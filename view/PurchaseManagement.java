


package view;

import dao.BookDAO;
import dao.CustomerDAO;
import dao.PurchaseDAO;
import Model.Book;
import Model.Customer;
import Model.Purchase;
import Model.Bill;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PurchaseManagement extends JFrame {

    private JTextField customerNameField;
    private JTextField bookIdField;
    private JTextField quantityField;

    private JLabel totalLabel;

    private JTable cartTable;
    private DefaultTableModel cartModel;

    private JTable historyTable;
    private DefaultTableModel historyModel;

    private List<Bill> cart;

    private BookDAO bookDAO;
    private CustomerDAO customerDAO;
    private PurchaseDAO purchaseDAO;

    public PurchaseManagement() {

        bookDAO = new BookDAO();
        customerDAO = new CustomerDAO();
        purchaseDAO = new PurchaseDAO();

        cart = new ArrayList<>();

        setTitle("Purchase Management - Shopping Cart");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(248, 247, 241));

        // Customer details
        JPanel customerPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        customerPanel.setBackground(new Color(248, 247, 241));
        customerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(8, 12, 4, 12),
                BorderFactory.createTitledBorder("Customer Details")
        ));

        customerNameField = new JTextField();

        customerPanel.add(new JLabel("Customer Name:"));
        customerPanel.add(customerNameField);

        // Book selection form
        JPanel bookPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        bookPanel.setBackground(new Color(248, 247, 241));
        bookPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 12, 8, 12),
                BorderFactory.createTitledBorder("Add Book to Cart")
        ));

        bookIdField = new JTextField();
        quantityField = new JTextField();

        bookPanel.add(new JLabel("Book ID:"));
        bookPanel.add(bookIdField);

        bookPanel.add(new JLabel("Quantity:"));
        bookPanel.add(quantityField);

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(new Color(248, 247, 241));
        topPanel.add(customerPanel, BorderLayout.NORTH);
        topPanel.add(bookPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Shopping cart table
        cartModel = new DefaultTableModel(
                new String[]{
                        "Book ID", "Title", "Unit Price",
                        "Quantity", "Subtotal"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(cartModel);

        cartTable.setRowHeight(30);
        cartTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cartTable.setForeground(new Color(65, 55, 45));
        cartTable.setBackground(new Color(255, 253, 248));
        cartTable.setSelectionBackground(new Color(232, 220, 200));
        cartTable.setSelectionForeground(new Color(65, 55, 45));

        cartTable.setGridColor(new Color(205, 195, 180));
        cartTable.setShowGrid(true);
        cartTable.setShowVerticalLines(true);
        cartTable.setShowHorizontalLines(true);
        cartTable.setIntercellSpacing(new Dimension(1, 1));

        cartTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );
        cartTable.getTableHeader().setBackground(
                new Color(232, 220, 200)
        );
        cartTable.getTableHeader().setForeground(
                new Color(65, 55, 45)
        );
        cartTable.getTableHeader().setPreferredSize(
                new Dimension(0, 36)
        );

        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(6, 12, 6, 12),
                BorderFactory.createTitledBorder("Shopping Cart")
        ));
        cartScroll.getViewport().setBackground(new Color(255, 253, 248));

        // Purchase history table
        historyModel = new DefaultTableModel(
                new String[]{
                        "Purchase ID", "Customer ID",
                        "Purchase Date", "Total Amount"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(historyModel);

        historyTable.setRowHeight(30);
        historyTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        historyTable.setForeground(new Color(65, 55, 45));
        historyTable.setBackground(new Color(255, 253, 248));
        historyTable.setSelectionBackground(new Color(232, 220, 200));
        historyTable.setSelectionForeground(new Color(65, 55, 45));

        historyTable.setGridColor(new Color(205, 195, 180));
        historyTable.setShowGrid(true);
        historyTable.setShowVerticalLines(true);
        historyTable.setShowHorizontalLines(true);
        historyTable.setIntercellSpacing(new Dimension(1, 1));

        historyTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );
        historyTable.getTableHeader().setBackground(
                new Color(232, 220, 200)
        );
        historyTable.getTableHeader().setForeground(
                new Color(65, 55, 45)
        );
        historyTable.getTableHeader().setPreferredSize(
                new Dimension(0, 36)
        );

        JScrollPane historyScroll = new JScrollPane(historyTable);
        historyScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(6, 12, 6, 12),
                BorderFactory.createTitledBorder("Purchase History")
        ));
        historyScroll.getViewport().setBackground(new Color(255, 253, 248));

        // Display both tables vertically
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                cartScroll,
                historyScroll
        );

        splitPane.setResizeWeight(0.65);
        add(splitPane, BorderLayout.CENTER);

        // Buttons and total
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Add Book");
        JButton removeButton = new JButton("Remove Selected");
        JButton refreshButton = new JButton("Refresh History");
        JButton purchaseButton = new JButton("Complete Purchase");
        JButton clearButton = new JButton("Clear Cart");
        JButton detailsButton = new JButton("View Purchase Details");

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(purchaseButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(detailsButton);

        totalLabel = new JLabel("Cart Total: 0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );

        bottomPanel.add(totalLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> addBookToCart());
        removeButton.addActionListener(e -> removeSelectedBook());
        refreshButton.addActionListener(e -> loadPurchaseHistory());
        purchaseButton.addActionListener(e -> completePurchase());
        clearButton.addActionListener(e -> clearCart());
        detailsButton.addActionListener(e -> viewPurchaseDetails());

        loadPurchaseHistory();
    }

    // Add a book to the shopping cart
    private void addBookToCart() {

        try {
            int bookId =
                    Integer.parseInt(bookIdField.getText().trim());

            int quantity =
                    Integer.parseInt(quantityField.getText().trim());

            if (bookId <= 0 || quantity <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Book ID and quantity must be positive."
                );
                return;
            }

            Book book = bookDAO.searchBookById(bookId);

            if (book == null) {
                JOptionPane.showMessageDialog(
                        this, "Book not found."
                );
                return;
            }

            int existingRow = -1;
            int existingQuantity = 0;

            for (int i = 0; i < cartModel.getRowCount(); i++) {

                int currentBookId =
                        ((Number) cartModel.getValueAt(i, 0)).intValue();

                if (currentBookId == bookId) {
                    existingRow = i;
                    existingQuantity =
                            ((Number) cartModel.getValueAt(i, 3)).intValue();
                    break;
                }
            }

            if (quantity > Integer.MAX_VALUE - existingQuantity) {
                JOptionPane.showMessageDialog(
                        this, "Requested quantity is too large."
                );
                return;
            }

            int newQuantity = existingQuantity + quantity;

            if (newQuantity > book.getStock()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Insufficient stock for " + book.getTitle()
                                + ". Available stock: " + book.getStock()
                );
                return;
            }

            double subtotal = book.getPrice() * newQuantity;

            if (existingRow >= 0) {

                cartModel.setValueAt(newQuantity, existingRow, 3);
                cartModel.setValueAt(subtotal, existingRow, 4);

                cart.get(existingRow).setQuantity(newQuantity);

            } else {

                Bill item = new Bill(
                        0, 0, bookId, quantity, book.getPrice()
                );

                cart.add(item);

                cartModel.addRow(new Object[]{
                        book.getBookId(),
                        book.getTitle(),
                        book.getPrice(),
                        quantity,
                        book.getPrice() * quantity
                });
            }

            updateCartTotal();

            bookIdField.setText("");
            quantityField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "Enter valid numbers for Book ID and Quantity."
            );
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    // Find customers whose names match the entered name
    private List<Customer> findCustomersByName(String name)
            throws SQLException {

        List<Customer> matches = new ArrayList<>();
        List<Customer> customers = customerDAO.getAllCustomers();

        for (Customer customer : customers) {
            if (customer.getName() != null
                    && customer.getName().trim().equalsIgnoreCase(name)) {
                matches.add(customer);
            }
        }

        return matches;
    }

    // Remove the selected book from the cart
    private void removeSelectedBook() {

        int selectedRow = cartTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this, "Select a book from the cart first."
            );
            return;
        }

        cart.remove(selectedRow);
        cartModel.removeRow(selectedRow);

        updateCartTotal();
    }

    // Calculate total amount
    private void updateCartTotal() {

        double total = 0.0;

        for (int i = 0; i < cartModel.getRowCount(); i++) {
            total += ((Number) cartModel.getValueAt(i, 4)).doubleValue();
        }

        totalLabel.setText(
                String.format("Cart Total: %.2f", total)
        );
    }

    // Complete purchase using the customer name
    private void completePurchase() {

        String customerName = customerNameField.getText().trim();

        // Customer name is compulsory
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Customer name is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            customerNameField.requestFocusInWindow();
            return;
        }

        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Your shopping cart is empty."
            );
            return;
        }

        try {
            // Retrieve the customer from the database by name
            List<Customer> matches = findCustomersByName(customerName);

            if (matches.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Customer not found. Please enter a valid customer name.",
                        "Invalid Customer",
                        JOptionPane.WARNING_MESSAGE
                );
                customerNameField.requestFocusInWindow();
                return;
            }

            // Prevent purchases being assigned to the wrong customer
            if (matches.size() > 1) {
                StringBuilder message = new StringBuilder(
                        "Multiple customers have this name.\n"
                        + "Please use a unique customer name.\n\n"
                        + "Matching customers:\n"
                );

                for (Customer customer : matches) {
                    message.append("Customer ID: ")
                            .append(customer.getCustomerId())
                            .append(" - ")
                            .append(customer.getName())
                            .append("\n");
                }

                JOptionPane.showMessageDialog(
                        this,
                        message.toString(),
                        "Duplicate Customer Name",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Automatically retrieve the customer's ID
            Customer customer = matches.get(0);
            int customerId = customer.getCustomerId();

            updateCartTotal();

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Customer: " + customer.getName()
                            + "\nCustomer ID: " + customerId
                            + "\nNumber of distinct books: " + cart.size()
                            + "\n" + totalLabel.getText()
                            + "\n\nConfirm this purchase?",
                    "Confirm Purchase",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }

            // Save the purchase using the retrieved customer ID
            int purchaseId =
                    purchaseDAO.createPurchase(customerId, cart);

            JOptionPane.showMessageDialog(
                    this,
                    "Purchase completed successfully!"
                            + "\nPurchase ID: " + purchaseId
                            + "\nCustomer ID: " + customerId
                            + "\n" + totalLabel.getText()
            );

            clearCart();
            customerNameField.setText("");
            loadPurchaseHistory();

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    // Load purchase history
    private void loadPurchaseHistory() {

        try {
            List<Purchase> purchases =
                    purchaseDAO.getAllPurchases();

            historyModel.setRowCount(0);

            for (Purchase purchase : purchases) {
                historyModel.addRow(new Object[]{
                        purchase.getPurchaseId(),
                        purchase.getCustomerId(),
                        purchase.getPurchaseDate(),
                        purchase.getTotalAmount()
                });
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    // Clear the cart
    private void clearCart() {

        cart.clear();
        cartModel.setRowCount(0);

        updateCartTotal();
    }

    // Display database errors
    private void showDatabaseError(SQLException ex) {

        JOptionPane.showMessageDialog(
                this,
                "Database error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // Display details of the selected purchase
    private void viewPurchaseDetails() {

        int selectedRow = historyTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select a purchase from Purchase History first."
            );
            return;
        }

        try {
            int purchaseId = ((Number)
                    historyModel.getValueAt(selectedRow, 0)).intValue();

            List<Object[]> details =
                    purchaseDAO.getPurchaseDetails(purchaseId);

            if (details.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No items found for this purchase."
                );
                return;
            }

            String[] columns = {
                    "Book ID", "Title", "Quantity",
                    "Unit Price", "Subtotal"
            };

            DefaultTableModel detailModel =
                    new DefaultTableModel(columns, 0) {
                        @Override
                        public boolean isCellEditable(
                                int row, int column) {
                            return false;
                        }
                    };

            for (Object[] item : details) {
                detailModel.addRow(item);
            }

            JTable detailTable = new JTable(detailModel);

            detailTable.setRowHeight(30);
            detailTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
            detailTable.setForeground(new Color(65, 55, 45));
            detailTable.setBackground(new Color(255, 253, 248));
            detailTable.setSelectionBackground(new Color(232, 220, 200));
            detailTable.setSelectionForeground(new Color(65, 55, 45));

            detailTable.setGridColor(new Color(205, 195, 180));
            detailTable.setShowGrid(true);
            detailTable.setShowVerticalLines(true);
            detailTable.setShowHorizontalLines(true);
            detailTable.setIntercellSpacing(new Dimension(1, 1));

            detailTable.getTableHeader().setFont(
                    new Font("SansSerif", Font.BOLD, 14)
            );
            detailTable.getTableHeader().setBackground(
                    new Color(232, 220, 200)
            );
            detailTable.getTableHeader().setForeground(
                    new Color(65, 55, 45)
            );
            detailTable.getTableHeader().setPreferredSize(
                    new Dimension(0, 36)
            );

            JScrollPane scrollPane = new JScrollPane(detailTable);
            scrollPane.setPreferredSize(new Dimension(650, 250));

            JOptionPane.showMessageDialog(
                    this,
                    scrollPane,
                    "Purchase ID: " + purchaseId + " - Details",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }
}
