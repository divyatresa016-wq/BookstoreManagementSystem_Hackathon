

package Model;

import java.time.LocalDate;

public class Rental {

    private int rentalId;
    private int customerId;
    private int bookId;

    private String customerName;
    private String bookTitle;

    private LocalDate rentalDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    private int rentalDays;
    private double rentalCharge;
    private double overdueFine;
    private String status;

    public Rental(int rentalId, int customerId, int bookId,
                  String customerName, String bookTitle,
                  LocalDate rentalDate, LocalDate dueDate,
                  LocalDate returnDate, int rentalDays,
                  double rentalCharge, double overdueFine,
                  String status) {

        this.rentalId = rentalId;
        this.customerId = customerId;
        this.bookId = bookId;
        this.customerName = customerName;
        this.bookTitle = bookTitle;
        this.rentalDate = rentalDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.rentalDays = rentalDays;
        this.rentalCharge = rentalCharge;
        this.overdueFine = overdueFine;
        this.status = status;
    }

    public int getRentalId() {
        return rentalId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getBookId() {
        return bookId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public int getRentalDays() {
        return rentalDays;
    }

    public double getRentalCharge() {
        return rentalCharge;
    }

    public double getOverdueFine() {
        return overdueFine;
    }

    public String getStatus() {
        return status;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void setOverdueFine(double overdueFine) {
        this.overdueFine = overdueFine;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
