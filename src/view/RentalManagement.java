
package view;

import Model.Rental;
import dao.RentalDAO;
import dao.CustomerDAO;
import dao.BookDAO;
import Model.Customer;
import Model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class RentalManagement extends JFrame {

    private JComboBox<String> customerCombo;
    private JComboBox<String> bookCombo;
    private JTextField rentalDaysField;

    private JTable rentalTable;
    private DefaultTableModel tableModel;

    private RentalDAO rentalDAO;
    private CustomerDAO customerDAO;
    private BookDAO bookDAO;

    public RentalManagement() {

        rentalDAO = new RentalDAO();
        customerDAO = new CustomerDAO();
        bookDAO = new BookDAO();

        setTitle("Rental Management");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel chargesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        chargesPanel.setBackground(new Color(248, 247, 241));
        chargesPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 12, 0, 12)
        );

        JLabel rentalRateLabel = new JLabel(
                "Rental Charge: ₹20 per day"
        );
        JLabel overdueFineLabel = new JLabel(
                "Overdue Fine: ₹10 per day"
        );

        Font rateFont = new Font("SansSerif", Font.BOLD, 15);

        rentalRateLabel.setFont(rateFont);
        overdueFineLabel.setFont(rateFont);

        rentalRateLabel.setForeground(new Color(65, 55, 45));
        overdueFineLabel.setForeground(new Color(65, 55, 45));

        chargesPanel.add(rentalRateLabel);
        chargesPanel.add(overdueFineLabel);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        formPanel.setBackground(new Color(248, 247, 241));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(8, 12, 8, 12),
                BorderFactory.createTitledBorder("Rental Details")
        ));

        customerCombo = new JComboBox<>();
        bookCombo = new JComboBox<>();
        rentalDaysField = new JTextField();

        formPanel.add(new JLabel("Customer Name:"));
        formPanel.add(customerCombo);

        formPanel.add(new JLabel("Book Title:"));
        formPanel.add(bookCombo);

        formPanel.add(new JLabel("Rental Duration (days):"));
        formPanel.add(rentalDaysField);

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(new Color(248, 247, 241));

        topPanel.add(chargesPanel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{
                        "Rental ID", "Customer", "Book",
                        "Rental Date", "Due Date", "Return Date",
                        "Days", "Rental Charge (₹)",
                        "Overdue Fine (₹)", "Status"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rentalTable = new JTable(tableModel);
        rentalTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        rentalTable.setRowHeight(28);
        rentalTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        rentalTable.setForeground(new Color(65, 55, 45));
        rentalTable.setBackground(new Color(255, 253, 248));
        rentalTable.setSelectionBackground(new Color(232, 220, 200));
        rentalTable.setSelectionForeground(new Color(65, 55, 45));

        rentalTable.setGridColor(new Color(205, 195, 180));
        rentalTable.setShowGrid(true);
        rentalTable.setShowVerticalLines(true);
        rentalTable.setShowHorizontalLines(true);
        rentalTable.setIntercellSpacing(new Dimension(1, 1));

        rentalTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        rentalTable.getTableHeader().setBackground(
                new Color(232, 220, 200)
        );
        rentalTable.getTableHeader().setForeground(
                new Color(65, 55, 45)
        );
        rentalTable.getTableHeader().setPreferredSize(
                new Dimension(0, 36)
        );

        JScrollPane scrollPane = new JScrollPane(rentalTable);
        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
        scrollPane.getViewport().setBackground(
                new Color(255, 253, 248)
        );

        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton rentButton = new JButton("Rent Book");
        JButton returnButton = new JButton("Return Selected Book");
        JButton refreshButton = new JButton("Refresh Rentals");
        JButton clearButton = new JButton("Clear Fields");

        buttonPanel.add(rentButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);

        rentButton.addActionListener(e -> rentBook());
        returnButton.addActionListener(e -> returnBook());
        refreshButton.addActionListener(e -> loadRentals());
        clearButton.addActionListener(e -> clearFields());

        loadCustomers();
        loadBooks();
        loadRentals();
    }

    private void loadCustomers() {
        try {
            customerCombo.removeAllItems();

            List<Customer> customers = customerDAO.getAllCustomers();

            for (Customer customer : customers) {
                customerCombo.addItem(customer.getName());
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }
    private void loadBooks() {
        try {
            bookCombo.removeAllItems();

            List<Book> books = bookDAO.getAllBooks();

            for (Book book : books) {
                if (book.getStock() > 0) {
                    bookCombo.addItem(book.getTitle());
                }
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void rentBook() {

        String customerName =
                (String) customerCombo.getSelectedItem();

        String bookTitle =
                (String) bookCombo.getSelectedItem();

        String daysInput = rentalDaysField.getText().trim();

        if (customerName == null) {
            JOptionPane.showMessageDialog(
                    this, "Please select a customer."
            );
            return;
        }

        if (bookTitle == null) {
            JOptionPane.showMessageDialog(
                    this, "No available book selected."
            );
            return;
        }

        if (daysInput.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Enter the rental duration."
            );
            rentalDaysField.requestFocusInWindow();
            return;
        }

        try {
            int days = Integer.parseInt(daysInput);

            if (days <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Rental duration must be at least one day."
                );
                rentalDaysField.requestFocusInWindow();
                return;
            }

            rentalDAO.rentBook(customerName, bookTitle, days);

            JOptionPane.showMessageDialog(
                    this,
                    "Book rented successfully!\n"
                    + "Customer: " + customerName + "\n"
                    + "Book: " + bookTitle + "\n"
                    + "Rental duration: " + days + " days\n"
                    + "Rental charge will be calculated when the book is returned."
            );

            clearFields();
            loadBooks();
            loadRentals();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid whole number for rental days."
            );
        } catch (IllegalArgumentException | SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Rental Error",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void returnBook() {

        int selectedRow = rentalTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a rental record from the table."
            );
            return;
        }

        String status = tableModel.getValueAt(
                selectedRow, 9
        ).toString();

        if (!"ACTIVE".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(
                    this,
                    "This book has already been returned."
            );
            return;
        }

        int rentalId = Integer.parseInt(
                tableModel.getValueAt(selectedRow, 0).toString()
        );

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Confirm return of this book?",
                "Return Book",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            double fine = rentalDAO.returnBook(rentalId);

            JOptionPane.showMessageDialog(
                    this,
                    "Book returned successfully!\n"
                    + "Overdue fine: ₹" + fine
            );

            loadBooks();
            loadRentals();

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void loadRentals() {
        try {
            tableModel.setRowCount(0);

            List<Rental> rentals = rentalDAO.getAllRentals();

            for (Rental rental : rentals) {
                tableModel.addRow(new Object[]{
                        rental.getRentalId(),
                        rental.getCustomerName(),
                        rental.getBookTitle(),
                        rental.getRentalDate(),
                        rental.getDueDate(),
                        rental.getReturnDate() == null
                                ? "-" : rental.getReturnDate(),
                        rental.getRentalDays(),
                        rental.getRentalCharge(),
                        rental.getOverdueFine(),
                        rental.getStatus()
                });
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void clearFields() {
        rentalDaysField.setText("");

        if (customerCombo.getItemCount() > 0) {
            customerCombo.setSelectedIndex(0);
        }

        if (bookCombo.getItemCount() > 0) {
            bookCombo.setSelectedIndex(0);
        }
    }

    private void showDatabaseError(SQLException ex) {
        JOptionPane.showMessageDialog(
                this,
                "Database error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
