
package dao;

import Model.Rental;
import database.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

    private static final double RENTAL_RATE_PER_DAY = 20.0;
    private static final double OVERDUE_FINE_PER_DAY = 10.0;

    // Rent a book using the customer name and book title.
    public void rentBook(String customerName, String bookTitle,
                         int rentalDays) throws SQLException {

        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a customer.");
        }

        if (bookTitle == null || bookTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a book.");
        }

        if (rentalDays <= 0) {
            throw new IllegalArgumentException(
                    "Rental duration must be at least one day.");
        }

        try (Connection con =
                     DBConnection.getInstance().getConnection()) {

            con.setAutoCommit(false);

            try {
                int customerId;

                String customerSql =
                        "SELECT customer_id FROM customers "
                      + "WHERE LOWER(TRIM(name)) = LOWER(TRIM(?))";

                try (PreparedStatement ps =
                             con.prepareStatement(customerSql)) {

                    ps.setString(1, customerName.trim());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Customer does not exist.");
                        }

                        customerId = rs.getInt("customer_id");

                        if (rs.next()) {
                            throw new SQLException(
                                    "Multiple customers have this name. "
                                  + "Please ensure customer names are unique.");
                        }
                    }
                }

                int bookId;

                String bookSql =
                        "SELECT book_id, stock FROM books "
                      + "WHERE LOWER(TRIM(title)) = LOWER(TRIM(?)) "
                      + "FOR UPDATE";

                try (PreparedStatement ps =
                             con.prepareStatement(bookSql)) {

                    ps.setString(1, bookTitle.trim());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Book does not exist.");
                        }

                        bookId = rs.getInt("book_id");

                        if (rs.getInt("stock") <= 0) {
                            throw new SQLException(
                                    "This book is currently unavailable.");
                        }

                        if (rs.next()) {
                            throw new SQLException(
                                    "Multiple books have this title. "
                                  + "Please ensure book titles are unique.");
                        }
                    }
                }

                LocalDate rentalDate = LocalDate.now();
                LocalDate dueDate = rentalDate.plusDays(rentalDays);
                double rentalCharge = 0.0;

                String rentalSql =
                        "INSERT INTO rentals "
                      + "(customer_id, book_id, rental_date, due_date, "
                      + "rental_days, rental_charge, overdue_fine, status) "
                      + "VALUES (?, ?, ?, ?, ?, ?, 0.00, 'ACTIVE')";

                try (PreparedStatement ps =
                             con.prepareStatement(
                                     rentalSql,
                                     Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, customerId);
                    ps.setInt(2, bookId);
                    ps.setDate(3, Date.valueOf(rentalDate));
                    ps.setDate(4, Date.valueOf(dueDate));
                    ps.setInt(5, rentalDays);
                    ps.setDouble(6, rentalCharge);

                    ps.executeUpdate();
                }

                String updateStockSql =
                        "UPDATE books SET stock = stock - 1 "
                      + "WHERE book_id = ? AND stock > 0";

                try (PreparedStatement ps =
                             con.prepareStatement(updateStockSql)) {

                    ps.setInt(1, bookId);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "Unable to update book availability.");
                    }
                }

                con.commit();

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // Retrieve all rentals, including customer and book names.
    public List<Rental> getAllRentals() throws SQLException {

        List<Rental> rentals = new ArrayList<>();

        String sql =
                "SELECT r.*, c.name AS customer_name, "
              + "b.title AS book_title "
              + "FROM rentals r "
              + "JOIN customers c ON r.customer_id = c.customer_id "
              + "JOIN books b ON r.book_id = b.book_id "
              + "ORDER BY r.rental_id DESC";

        try (Connection con =
                     DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Date returnSqlDate = rs.getDate("return_date");

                Rental rental = new Rental(
                        rs.getInt("rental_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("book_id"),
                        rs.getString("customer_name"),
                        rs.getString("book_title"),
                        rs.getDate("rental_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate(),
                        returnSqlDate == null
                                ? null : returnSqlDate.toLocalDate(),
                        rs.getInt("rental_days"),
                        rs.getDouble("rental_charge"),
                        rs.getDouble("overdue_fine"),
                        rs.getString("status")
                );

                rentals.add(rental);
            }
        }

        return rentals;
    }

    // Return a rented book and calculate any overdue fine.
    public double returnBook(int rentalId) throws SQLException {

        try (Connection con =
                     DBConnection.getInstance().getConnection()) {

            con.setAutoCommit(false);

            try {
            	int bookId;
            	LocalDate rentalDate;
            	LocalDate dueDate;
            	String status;

            	String rentalSql =
            	        "SELECT book_id, rental_date, due_date, status "
            	      + "FROM rentals WHERE rental_id = ? FOR UPDATE";

                try (PreparedStatement ps =
                             con.prepareStatement(rentalSql)) {

                    ps.setInt(1, rentalId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Rental record does not exist.");
                        }

                        bookId = rs.getInt("book_id");
                        rentalDate = rs.getDate("rental_date").toLocalDate();
                        dueDate = rs.getDate("due_date").toLocalDate();
                        status = rs.getString("status");
                    }
                }

                if ("RETURNED".equalsIgnoreCase(status)) {
                    throw new SQLException(
                            "This book has already been returned.");
                }

                LocalDate returnDate = LocalDate.now();
                long actualDaysKept = Math.max(
                        1, ChronoUnit.DAYS.between(rentalDate, returnDate));

                double rentalCharge =
                        actualDaysKept * RENTAL_RATE_PER_DAY;

                long overdueDays = Math.max(
                        0, ChronoUnit.DAYS.between(dueDate, returnDate));

                double overdueFine =
                        overdueDays * OVERDUE_FINE_PER_DAY;

                String updateRentalSql =
                        "UPDATE rentals SET return_date = ?, "
                      + "rental_charge = ?, overdue_fine = ?, "
                      + "status = 'RETURNED' "
                      + "WHERE rental_id = ? AND status = 'ACTIVE'";

                try (PreparedStatement ps =
                        con.prepareStatement(updateRentalSql)) {

               ps.setDate(1, Date.valueOf(returnDate));
               ps.setDouble(2, rentalCharge);
               ps.setDouble(3, overdueFine);
               ps.setInt(4, rentalId);

               if (ps.executeUpdate() != 1) {
                   throw new SQLException(
                           "Unable to update the rental record.");
               }
           }

                String updateStockSql =
                        "UPDATE books SET stock = stock + 1 "
                      + "WHERE book_id = ?";

                try (PreparedStatement ps =
                             con.prepareStatement(updateStockSql)) {

                    ps.setInt(1, bookId);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "Unable to update book stock.");
                    }
                }

                con.commit();
                return overdueFine;

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}

