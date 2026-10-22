package dao;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RentalFineTest {

    public static void main(String[] args) {

        LocalDate dueDate = LocalDate.of(2026, 10, 25);

        LocalDate returnDate = LocalDate.of(2026, 10, 28);

        long overdueDays = Math.max(
                0, ChronoUnit.DAYS.between(dueDate, returnDate));

        double overdueFine = overdueDays * 10.0;

        System.out.println("Due Date: " + dueDate);
        System.out.println("Return Date: " + returnDate);
        System.out.println("Overdue Days: " + overdueDays);
        System.out.println("Overdue Fine: Rs. " + overdueFine);
    }
}

