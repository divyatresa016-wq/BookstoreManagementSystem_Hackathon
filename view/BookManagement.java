


package view;

import dao.BookDAO;
import Model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class BookManagement extends JFrame {

    private JTextField titleField;
    private JTextField authorField;
    private JTextField categoryField;
    private JTextField priceField;
    private JTextField stockField;

    private JTextField searchTitleField;

    private JTable bookTable;
    private DefaultTableModel tableModel;

    private BookDAO bookDAO;

    public BookManagement() {

        bookDAO = new BookDAO();

        setTitle("Book Management");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Book input form
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 8, 8));

        formPanel.setBackground(new Color(248, 247, 241));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(8, 12, 8, 12),
                BorderFactory.createTitledBorder("Book Details")
        ));

        titleField = new JTextField();
        authorField = new JTextField();
        categoryField = new JTextField();
        priceField = new JTextField();
        stockField = new JTextField();

        formPanel.add(new JLabel("Book Title:"));
        formPanel.add(titleField);

        formPanel.add(new JLabel("Author:"));
        formPanel.add(authorField);

        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Price:"));
        formPanel.add(priceField);

        formPanel.add(new JLabel("Stock:"));
        formPanel.add(stockField);

        // Main top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(248, 247, 241));
        topPanel.add(formPanel, BorderLayout.CENTER);

        // Search by title panel
        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 8));

        searchPanel.setBackground(new Color(248, 247, 241));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(
                0, 12, 0, 12));

        searchTitleField = new JTextField(25);

        JButton searchTitleButton = new JButton("Search by Title");
        JButton showAllButton = new JButton("Show All Books");

        searchPanel.add(new JLabel("Search Title:"));
        searchPanel.add(searchTitleField);
        searchPanel.add(searchTitleButton);
        searchPanel.add(showAllButton);

        topPanel.add(searchPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Book table
        String[] columns = {
                "Book ID", "Title", "Author",
                "Category", "Price", "Stock"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        // Table appearance
        bookTable.setRowHeight(30);
        bookTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        bookTable.setForeground(new Color(65, 55, 45));
        bookTable.setBackground(new Color(255, 253, 248));
        bookTable.setSelectionBackground(new Color(232, 220, 200));
        bookTable.setSelectionForeground(new Color(65, 55, 45));

        bookTable.setGridColor(new Color(205, 195, 180));
        bookTable.setShowGrid(true);
        bookTable.setShowVerticalLines(true);
        bookTable.setShowHorizontalLines(true);
        bookTable.setIntercellSpacing(new Dimension(1, 1));

        bookTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 14));
        bookTable.getTableHeader().setBackground(
                new Color(232, 220, 200));
        bookTable.getTableHeader().setForeground(
                new Color(65, 55, 45));
        bookTable.getTableHeader().setPreferredSize(
                new Dimension(0, 36));

        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        bookScrollPane.setBorder(
                BorderFactory.createEmptyBorder(8, 12, 8, 12));
        bookScrollPane.getViewport().setBackground(
                new Color(255, 253, 248));

        add(bookScrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 8, 8));

        JButton addButton = new JButton("Add Book");
        JButton refreshButton = new JButton("Refresh Books");
        JButton searchButton = new JButton("Search by ID");
        JButton updateButton = new JButton("Update Stock");
        JButton deleteButton = new JButton("Delete Book");
        JButton clearButton = new JButton("Clear Fields");

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> addBook());
        refreshButton.addActionListener(e -> loadBooks());
        searchButton.addActionListener(e -> searchBook());
        updateButton.addActionListener(e -> updateStock());
        deleteButton.addActionListener(e -> deleteBook());
        clearButton.addActionListener(e -> clearFields());

        searchTitleButton.addActionListener(e -> searchByTitle());

        showAllButton.addActionListener(e -> {
            searchTitleField.setText("");
            loadBooks();
        });

        searchTitleField.addActionListener(e -> searchByTitle());

        loadBooks();

        setVisible(true);
    }

    // Add a new book
    private void addBook() {

        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String category = categoryField.getText().trim();

        // Validate required fields
        if (title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Book title and author are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        int stock;

        try {
            price = Double.parseDouble(priceField.getText().trim());
            stock = Integer.parseInt(stockField.getText().trim());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid numeric price and whole-number stock.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!Double.isFinite(price) || price < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Price must be a valid, non-negative number.",
                    "Invalid Price",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (stock < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Stock cannot be negative.",
                    "Invalid Stock",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Check for an existing title BEFORE inserting
            if (bookDAO.bookTitleExists(title)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Book already exists.",
                        "Duplicate Book",
                        JOptionPane.WARNING_MESSAGE);
                titleField.requestFocusInWindow();
                return;
            }

            Book book = new Book(
                    0, title, author, category, price, stock);

            boolean success = bookDAO.addBook(book);

            if (success) {
                JOptionPane.showMessageDialog(
                        this, "Book added successfully!");

                clearFields();
                searchTitleField.setText("");
                loadBooks();
            } else {
                JOptionPane.showMessageDialog(
                        this, "Book could not be added.");
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    // Display all books
    private void loadBooks() {

        try {
            List<Book> books = bookDAO.getAllBooks();

            tableModel.setRowCount(0);

            for (Book book : books) {
                tableModel.addRow(new Object[]{
                        book.getBookId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getCategory(),
                        book.getPrice(),
                        book.getStock()
                });
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    // Search for books by title
    private void searchByTitle() {

        String title = searchTitleField.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter a book title to search.",
                    "Search",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            List<Book> books = bookDAO.searchBooksByTitle(title);

            tableModel.setRowCount(0);

            for (Book book : books) {
                tableModel.addRow(new Object[]{
                        book.getBookId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getCategory(),
                        book.getPrice(),
                        book.getStock()
                });
            }

            if (books.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No books found matching: " + title,
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    // Search for a book using its ID
    private void searchBook() {

        String input = JOptionPane.showInputDialog(
                this, "Enter Book ID:");

        if (input == null) {
            return;
        }

        int bookId;

        try {
            bookId = Integer.parseInt(input.trim());

            if (bookId <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "Enter a valid positive Book ID.");
            return;
        }

        try {
            Book book = bookDAO.searchBookById(bookId);

            if (book == null) {
                JOptionPane.showMessageDialog(
                        this, "Book not found.");
                return;
            }

            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            categoryField.setText(book.getCategory());
            priceField.setText(String.valueOf(book.getPrice()));
            stockField.setText(String.valueOf(book.getStock()));

            tableModel.setRowCount(0);

            tableModel.addRow(new Object[]{
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getCategory(),
                    book.getPrice(),
                    book.getStock()
            });

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    // Update the stock of a selected book
    private void updateStock() {

        int selectedRow = bookTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select a book from the table first.");
            return;
        }

        int bookId = ((Number)
                tableModel.getValueAt(selectedRow, 0)).intValue();

        String input = JOptionPane.showInputDialog(
                this, "Enter the new stock quantity:");

        if (input == null) {
            return;
        }

        int newStock;

        try {
            newStock = Integer.parseInt(input.trim());

            if (newStock < 0) {
                JOptionPane.showMessageDialog(
                        this, "Stock cannot be negative.");
                return;
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "Enter a valid whole number for stock.");
            return;
        }

        try {
            boolean updated = bookDAO.updateStock(bookId, newStock);

            if (updated) {
                JOptionPane.showMessageDialog(
                        this, "Stock updated successfully!");
            } else {
                JOptionPane.showMessageDialog(
                        this, "Book not found.");
            }

            loadBooks();

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    // Delete a selected book
    private void deleteBook() {

        int selectedRow = bookTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select a book from the table first.");
            return;
        }

        int bookId = ((Number)
                tableModel.getValueAt(selectedRow, 0)).intValue();

        String title = String.valueOf(
                tableModel.getValueAt(selectedRow, 1));

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete \"" + title + "\"?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean deleted = bookDAO.deleteBook(bookId);

            if (deleted) {
                JOptionPane.showMessageDialog(
                        this, "Book deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(
                        this, "Book not found.");
            }

            clearFields();
            loadBooks();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not delete this book. It may be linked "
                            + "to an existing purchase.\n\n"
                            + ex.getMessage(),
                    "Deletion Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Clear all input fields
    private void clearFields() {

        titleField.setText("");
        authorField.setText("");
        categoryField.setText("");
        priceField.setText("");
        stockField.setText("");

        bookTable.clearSelection();
    }

    // Display database errors
    private void showDatabaseError(SQLException ex) {

        JOptionPane.showMessageDialog(
                this,
                "A database error occurred:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }
}

        