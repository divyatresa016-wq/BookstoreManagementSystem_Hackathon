

package dao;

import database.DBConnection;
import Model.Purchase;
import Model.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PurchaseDAO {

    // Create a purchase containing multiple books
    public int createPurchase(int customerId, List<Bill> items)
            throws SQLException {

        if (customerId <= 0) {
            throw new IllegalArgumentException(
                    "Customer ID must be positive.");
        }

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException(
                    "A purchase must contain at least one book.");
        }

        // Combine quantities for duplicate book IDs
        Map<Integer, Integer> quantities = new LinkedHashMap<>();

        for (Bill item : items) {

            if (item.getBookId() <= 0 || item.getQuantity() <= 0) {
                throw new IllegalArgumentException(
                        "Book ID and quantity must be positive.");
            }

            int bookId = item.getBookId();
            int quantity = item.getQuantity();

            int existingQuantity =
                    quantities.getOrDefault(bookId, 0);

            if (quantity > Integer.MAX_VALUE - existingQuantity) {
                throw new IllegalArgumentException(
                        "Total quantity is too large for book ID " + bookId);
            }

            quantities.put(bookId, existingQuantity + quantity);
        }

        try (Connection con =
                     DBConnection.getInstance().getConnection()) {

            con.setAutoCommit(false);

            try {

                // 1. Verify that the customer exists
                String customerSQL =
                        "SELECT customer_id FROM customers "
                        + "WHERE customer_id = ?";

                try (PreparedStatement ps =
                             con.prepareStatement(customerSQL)) {

                    ps.setInt(1, customerId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Customer ID " + customerId
                                    + " does not exist.");
                        }
                    }
                }

                // 2. Lock books, verify stock and calculate total
                Map<Integer, Double> prices = new LinkedHashMap<>();
                double totalAmount = 0.0;

                String bookSQL =
                        "SELECT price, stock FROM books "
                        + "WHERE book_id = ? FOR UPDATE";

                for (Map.Entry<Integer, Integer> entry
                        : quantities.entrySet()) {

                    int bookId = entry.getKey();
                    int quantity = entry.getValue();

                    try (PreparedStatement ps =
                                 con.prepareStatement(bookSQL)) {

                        ps.setInt(1, bookId);

                        try (ResultSet rs = ps.executeQuery()) {

                            if (!rs.next()) {
                                throw new SQLException(
                                        "Book ID " + bookId
                                        + " does not exist.");
                            }

                            double price = rs.getDouble("price");
                            int stock = rs.getInt("stock");

                            // Validate the book price
                            if (rs.wasNull() || !Double.isFinite(price) || price < 0) {
                                throw new SQLException(
                                        "Invalid price for book ID " + bookId);
                            }

                            // Validate available stock
                            if (stock < 0) {
                                throw new SQLException(
                                        "Invalid stock for book ID " + bookId);
                            }

                            if (stock < quantity) {
                                throw new SQLException(
                                        "Insufficient stock for book ID "
                                        + bookId
                                        + ". Available stock: " + stock);
                            }

                            // Calculate subtotal and validate it
                            double subtotal = price * quantity;

                            if (!Double.isFinite(subtotal)) {
                                throw new SQLException(
                                        "Invalid subtotal for book ID " + bookId);
                            }

                            prices.put(bookId, price);
                            totalAmount += subtotal;

                            // Validate the running total
                            if (!Double.isFinite(totalAmount)) {
                                throw new SQLException(
                                        "Purchase total is too large.");
                            }
                        }
                    }
                }
                if (totalAmount < 0 || !Double.isFinite(totalAmount)) {
                    throw new SQLException("Invalid purchase total.");
                }

                // 3. Insert the purchase record
                String purchaseSQL =
                        "INSERT INTO purchases "
                        + "(customer_id, total_amount) "
                        + "VALUES (?, ?)";

                int purchaseId;

                try (PreparedStatement ps = con.prepareStatement(
                        purchaseSQL, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, customerId);
                    ps.setDouble(2, totalAmount);

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {

                        if (!keys.next()) {
                            throw new SQLException(
                                    "Could not generate purchase ID.");
                        }

                        purchaseId = keys.getInt(1);
                    }
                }

                // 4. Insert purchase items and update stock
                String itemSQL =
                        "INSERT INTO purchase_items "
                        + "(purchase_id, book_id, quantity, unit_price) "
                        + "VALUES (?, ?, ?, ?)";

                String stockSQL =
                        "UPDATE books SET stock = stock - ? "
                        + "WHERE book_id = ? AND stock >= ?";

                try (PreparedStatement itemPS =
                             con.prepareStatement(itemSQL);
                     PreparedStatement stockPS =
                             con.prepareStatement(stockSQL)) {

                    for (Map.Entry<Integer, Integer> entry
                            : quantities.entrySet()) {

                        int bookId = entry.getKey();
                        int quantity = entry.getValue();
                        double price = prices.get(bookId);

                        // Save this book in the purchase
                        itemPS.setInt(1, purchaseId);
                        itemPS.setInt(2, bookId);
                        itemPS.setInt(3, quantity);
                        itemPS.setDouble(4, price);

                        itemPS.executeUpdate();

                        // Reduce the available stock
                        stockPS.setInt(1, quantity);
                        stockPS.setInt(2, bookId);
                        stockPS.setInt(3, quantity);

                        if (stockPS.executeUpdate() != 1) {
                            throw new SQLException(
                                    "Stock update failed for book ID "
                                    + bookId);
                        }
                    }
                }

                // 5. Commit all changes together
                con.commit();

                return purchaseId;

            } catch (SQLException | RuntimeException e) {

                con.rollback();
                throw e;

            } finally {

                con.setAutoCommit(true);
            }
        }
    }

    // Retrieve all purchases
    public List<Purchase> getAllPurchases() throws SQLException {

        List<Purchase> purchases = new ArrayList<>();

        String sql =
                "SELECT purchase_id, customer_id, "
                + "purchase_date, total_amount "
                + "FROM purchases ORDER BY purchase_id";

        try (Connection con =
                     DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Purchase purchase = new Purchase(
                        rs.getInt("purchase_id"),
                        rs.getInt("customer_id"),
                        rs.getString("purchase_date"),
                        rs.getDouble("total_amount")
                );

                purchases.add(purchase);
            }
        }

        return purchases;
    }

    // Retrieve the individual books in a purchase
    public List<Object[]> getPurchaseDetails(int purchaseId)
            throws SQLException {

        List<Object[]> details = new ArrayList<>();

        String sql =
                "SELECT pi.book_id, b.title, pi.quantity, "
                + "pi.unit_price, "
                + "(pi.quantity * pi.unit_price) AS subtotal "
                + "FROM purchase_items pi "
                + "JOIN books b ON pi.book_id = b.book_id "
                + "WHERE pi.purchase_id = ? "
                + "ORDER BY pi.item_id";

        try (Connection con =
                     DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, purchaseId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    details.add(new Object[]{
                            rs.getInt("book_id"),
                            rs.getString("title"),
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price"),
                            rs.getDouble("subtotal")
                    });
                }
            }
        }

        return details;
    }

}
