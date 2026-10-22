

package view;

import dao.CustomerDAO;

import Model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomerManagement extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;

    private JTable customerTable;
    private DefaultTableModel tableModel;

    private CustomerDAO customerDAO;

    private int selectedCustomerId = -1;

    public CustomerManagement() {
        customerDAO = new CustomerDAO();

        setTitle("Customer Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 8, 8));

        formPanel.setBackground(new Color(248, 247, 241));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(8, 12, 8, 12),
                BorderFactory.createTitledBorder("Customer Details")
        ));

        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        add(formPanel, BorderLayout.NORTH);

        // Customer table
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Email", "Phone"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        // Modern table appearance
        customerTable.setRowHeight(30);
        customerTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        customerTable.setForeground(new Color(65, 55, 45));
        customerTable.setBackground(new Color(255, 253, 248));
        customerTable.setSelectionBackground(new Color(232, 220, 200));
        customerTable.setSelectionForeground(new Color(65, 55, 45));

        // Visible borders between rows and columns
        customerTable.setGridColor(new Color(205, 195, 180));
        customerTable.setShowGrid(true);
        customerTable.setShowVerticalLines(true);
        customerTable.setShowHorizontalLines(true);
        customerTable.setIntercellSpacing(new Dimension(1, 1));

        // Table header appearance
        customerTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );
        customerTable.getTableHeader().setBackground(
                new Color(232, 220, 200)
        );
        customerTable.getTableHeader().setForeground(
                new Color(65, 55, 45)
        );
        customerTable.getTableHeader().setPreferredSize(
                new Dimension(0, 36)
        );

        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {

                int selectedRow = customerTable.getSelectedRow();

                if (selectedRow != -1) {
                    selectedCustomerId = (int) tableModel.getValueAt(
                            selectedRow, 0
                    );

                    nameField.setText(
                            tableModel.getValueAt(selectedRow, 1).toString()
                    );

                    emailField.setText(
                            tableModel.getValueAt(selectedRow, 2).toString()
                    );

                    phoneField.setText(
                            tableModel.getValueAt(selectedRow, 3).toString()
                    );
                }
            }
        });

        // Scroll pane styling
        JScrollPane customerScrollPane = new JScrollPane(customerTable);
        customerScrollPane.setBorder(
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
        customerScrollPane.getViewport().setBackground(
                new Color(255, 253, 248)
        );

        add(customerScrollPane, BorderLayout.CENTER);

        // Customer menu buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Add Customer");
        JButton viewButton = new JButton("View Customers");
        JButton searchButton = new JButton("Search by ID");
        JButton updateButton = new JButton("Update Customer");
        JButton deleteButton = new JButton("Delete Customer");
        JButton clearButton = new JButton("Clear Fields");

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Menu actions
        addButton.addActionListener(e -> addCustomer());
        viewButton.addActionListener(e -> loadCustomers());
        searchButton.addActionListener(e -> searchCustomer());
        updateButton.addActionListener(e -> updateCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());
        clearButton.addActionListener(e -> clearFields());

        loadCustomers();
    }

    // Add a new customer
    private void addCustomer() {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Name is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            nameField.requestFocusInWindow();
            return;
        }

        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Phone number is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            phoneField.requestFocusInWindow();
            return;
        }

        try {
            // Prevent duplicate customer names
            if (customerDAO.customerNameExists(name)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Customer already exists.",
                        "Duplicate Customer",
                        JOptionPane.WARNING_MESSAGE
                );
                nameField.requestFocusInWindow();
                return;
            }

            Customer customer = new Customer(0, name, email, phone);

            boolean success = customerDAO.addCustomer(customer);

            if (success) {
                JOptionPane.showMessageDialog(
                        this, "Customer added successfully!"
                );

                clearFields();
                loadCustomers();
            } else {
                JOptionPane.showMessageDialog(
                        this, "Customer could not be added."
                );
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    // Retrieve and display all customers
    private void loadCustomers() {

        try {
            List<Customer> customers = customerDAO.getAllCustomers();

            tableModel.setRowCount(0);

            for (Customer customer : customers) {
                tableModel.addRow(new Object[]{
                        customer.getCustomerId(),
                        customer.getName(),
                        customer.getEmail(),
                        customer.getPhone()
                });
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    // Search customer by ID
    private void searchCustomer() {

        String input = JOptionPane.showInputDialog(
                this, "Enter Customer ID:"
        );

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            int id = Integer.parseInt(input.trim());

            if (id <= 0) {
                JOptionPane.showMessageDialog(
                        this, "Customer ID must be a positive number."
                );
                return;
            }

            Customer customer = customerDAO.searchCustomerById(id);

            if (customer == null) {
                JOptionPane.showMessageDialog(
                        this, "Customer not found."
                );
                return;
            }

            nameField.setText(customer.getName());
            emailField.setText(customer.getEmail());
            phoneField.setText(customer.getPhone());

            // Highlight the matching customer in the table
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int tableId = (int) tableModel.getValueAt(i, 0);

                if (tableId == id) {
                    customerTable.setRowSelectionInterval(i, i);
                    customerTable.scrollRectToVisible(
                            customerTable.getCellRect(i, 0, true)
                    );
                    break;
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "Enter a valid Customer ID."
            );
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    // Update selected customer
    private void updateCustomer() {

        // Check whether a customer is selected
        if (selectedCustomerId == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a customer from the table first."
            );
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        // Validate customer name
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Name is required."
            );
            nameField.requestFocusInWindow();
            return;
        }

        // Validate phone number
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Phone number is required."
            );
            phoneField.requestFocusInWindow();
            return;
        }

        try {
            // Check whether another customer already has this name.
            // The selected customer's own ID is excluded.
            if (customerDAO.customerNameExists(name, selectedCustomerId)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Another customer with this name already exists.",
                        "Duplicate Customer",
                        JOptionPane.WARNING_MESSAGE
                );
                nameField.requestFocusInWindow();
                return;
            }

            Customer customer = new Customer(
                    selectedCustomerId, name, email, phone
            );

            boolean success = customerDAO.updateCustomer(customer);

            if (success) {
                JOptionPane.showMessageDialog(
                        this, "Customer updated successfully!"
                );

                loadCustomers();
                clearFields();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Customer could not be updated. Please refresh the table."
                );
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    // Delete customer by ID
    private void deleteCustomer() {

        String input = JOptionPane.showInputDialog(
                this, "Enter Customer ID to delete:"
        );

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            int id = Integer.parseInt(input.trim());

            if (id <= 0) {
                JOptionPane.showMessageDialog(
                        this, "Customer ID must be a positive number."
                );
                return;
            }

            // Verify that the customer exists before asking for confirmation
            Customer customer = customerDAO.searchCustomerById(id);

            if (customer == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Customer ID not found. No deletion performed."
                );
                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete Customer ID "
                            + id + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {

                boolean success = customerDAO.deleteCustomer(id);

                if (success) {
                    JOptionPane.showMessageDialog(
                            this, "Customer deleted successfully!"
                    );

                    loadCustomers();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Customer could not be deleted."
                    );
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "Enter a valid Customer ID."
            );
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    // Clear input fields and table selection
    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");

        customerTable.clearSelection();
        selectedCustomerId = -1;
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
}
