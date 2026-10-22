
package dao;

import database.DBConnection;
import Model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // Add a new customer
    public boolean addCustomer(Customer customer)
            throws SQLException {

        String sql = "INSERT INTO customers (name, email, phone) "
                   + "VALUES (?, ?, ?)";

        try (Connection con =
                 DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());

            return ps.executeUpdate() > 0;
        }
    }

    // Retrieve all customers
    public List<Customer> getAllCustomers()
            throws SQLException {

        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT * FROM customers";

        try (Connection con =
                 DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone")
                );

                customers.add(customer);
            }
        }

        return customers;
    }

    // Search customer by ID
    public Customer searchCustomerById(int customerId)
            throws SQLException {

        String sql =
            "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection con =
                 DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone")
                    );
                }
            }
        }

        return null;
    }

    // Update customer details
    public boolean updateCustomer(Customer customer)
            throws SQLException {

        String sql = "UPDATE customers "
                   + "SET name = ?, email = ?, phone = ? "
                   + "WHERE customer_id = ?";

        try (Connection con =
                 DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());
            ps.setInt(4, customer.getCustomerId());

            return ps.executeUpdate() > 0;
        }
    }

    // Delete customer by ID
    public boolean deleteCustomer(int customerId)
            throws SQLException {

        String sql =
            "DELETE FROM customers WHERE customer_id = ?";

        try (Connection con =
                 DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            return ps.executeUpdate() > 0;
        }
    }
    

    public boolean customerNameExists(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers "
            + "WHERE LOWER(TRIM(name)) = LOWER(TRIM(?))";

        try (Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

         ps.setString(1, name);

         try (ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}

    public boolean customerNameExists(String name, int excludeCustomerId)
        throws SQLException {

        String sql = "SELECT COUNT(*) FROM customers "
            + "WHERE LOWER(TRIM(name)) = LOWER(TRIM(?)) "
            + "AND customer_id <> ?";

        try (Connection con = DBConnection.getInstance().getConnection();
           PreparedStatement ps = con.prepareStatement(sql)) {

         ps.setString(1, name);
         ps.setInt(2, excludeCustomerId);
 
         try (ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}

}

