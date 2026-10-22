

package dao;

import database.DBConnection;
import Model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // Add a new book
    public boolean addBook(Book book) throws SQLException {

        String sql = "INSERT INTO books "
                + "(title, author, category, price, stock) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setDouble(4, book.getPrice());
            ps.setInt(5, book.getStock());

            return ps.executeUpdate() > 0;
        }
    }

    // Retrieve all books
    public List<Book> getAllBooks() throws SQLException {

        List<Book> books = new ArrayList<>();

        String sql = "SELECT * FROM books ORDER BY book_id";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                );

                books.add(book);
            }
        }

        return books;
    }

    // Search for a book using its ID
    public Book searchBookById(int bookId) throws SQLException {

        String sql = "SELECT * FROM books WHERE book_id = ?";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Book(
                            rs.getInt("book_id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("category"),
                            rs.getDouble("price"),
                            rs.getInt("stock")
                    );
                }
            }
        }

        return null;
    }

    // Search for books by exact or partial title
    public List<Book> searchBooksByTitle(String title)
            throws SQLException {

        List<Book> books = new ArrayList<>();

        String sql = "SELECT * FROM books "
                + "WHERE LOWER(title) LIKE LOWER(?) "
                + "ORDER BY title";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + title.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Book book = new Book(
                            rs.getInt("book_id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("category"),
                            rs.getDouble("price"),
                            rs.getInt("stock")
                    );

                    books.add(book);
                }
            }
        }

        return books;
    }

    // Update the stock of a book
    public boolean updateStock(int bookId, int newStock)
            throws SQLException {

        String sql = "UPDATE books SET stock = ? WHERE book_id = ?";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, newStock);
            ps.setInt(2, bookId);

            return ps.executeUpdate() > 0;
        }
    }

    // Delete a book
    public boolean deleteBook(int bookId) throws SQLException {

        String sql = "DELETE FROM books WHERE book_id = ?";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bookId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean bookTitleExists(String title) throws SQLException {

        String sql = "SELECT COUNT(*) FROM books "
            + "WHERE LOWER(TRIM(title)) = LOWER(TRIM(?))";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

             ps.setString(1, title);

             try (ResultSet rs = ps.executeQuery()) {
               if (rs.next()) {
                 return rs.getInt(1) > 0;
            }
        }
    }

    return false;
}

}

