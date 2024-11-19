package service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.User;
import model.Client;
import model.Admin;
import mySQLdb.DBConnect;
import util.Role;
import util.UserInputValidator;

public class UserService {

    // Create a new user
    public void createUser(User user) throws SQLException {
        UserInputValidator.validateUserInput(user);

        String query = "INSERT INTO e_Users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().toString());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to create user, no rows affected.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    insertUserDetails(user, conn, userId);
                }
            }
        }
    }

    // Insert additional details for Clients or Admins
    private void insertUserDetails(User user, Connection conn, int userId) throws SQLException {
        if (user instanceof Client client) {
            String query = "INSERT INTO e_Clients (user_id, username, email, phone, address) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setString(2, client.getUsername()); // Insert username into e_Clients
                stmt.setString(3, client.getEmail());
                stmt.setString(4, client.getPhone());
                stmt.setString(5, client.getAddress());
                stmt.executeUpdate();
            }
        } else if (user instanceof Admin) {
            String query = "INSERT INTO e_Admin (user_id) VALUES (?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }
        }
    }


    // Retrieve a user by ID
    public User getUserById(int userId) throws SQLException {
        String query = """
                SELECT u.user_id, u.username, u.password, u.role, 
                       c.email, c.phone, c.address
                FROM e_Users u
                LEFT JOIN e_Clients c ON u.user_id = c.user_id
                WHERE u.user_id = ?
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role").toLowerCase();
                    if (Role.client.name().equals(role)) {
                        return new Client(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email"),
                                rs.getString("phone"),
                                rs.getString("address")
                        );
                    } else if (Role.admin.name().equals(role)) {
                        return new Admin(rs.getString("username"), rs.getString("password"));
                    }
                }
            }
        }
        throw new SQLException("User not found.");
    }

    // Retrieve a client by ID
    public Client getClientById(int clientId) throws SQLException {
        String query = """
                SELECT u.user_id, u.username, u.password, c.client_id, c.email, c.phone, c.address 
                FROM e_Users u
                JOIN e_Clients c ON u.user_id = c.user_id
                WHERE c.client_id = ?
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Client(
                            rs.getInt("client_id"),
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("address")
                    );
                }
            }
        }
        throw new SQLException("Client not found.");
    }
    
    //Retrieve all admins
    public List<Admin> getAllAdmins() throws SQLException {
        String query = "SELECT * FROM e_Admin a JOIN e_Users u ON a.user_id = u.user_id WHERE u.role = 'Admin'";
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            List<Admin> admins = new ArrayList<>();
            while (resultSet.next()) {
                Admin admin = new Admin(
                    resultSet.getString("username"),
                    resultSet.getString("password")
                );
                admin.setUserId(resultSet.getInt("user_id")); // Set additional properties if needed
                admins.add(admin);
            }
            return admins;
        }
    }
    // Retrieve all clients
    public List<Client> getAllClients() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String query = """
                SELECT u.user_id, u.username, u.password, c.client_id, c.email, c.phone, c.address 
                FROM e_Users u
                JOIN e_Clients c ON u.user_id = c.user_id
                WHERE u.role = 'Client'
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clients.add(new Client(
                        rs.getInt("client_id"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                ));
            }
        }
        return clients;
    }

    // Update a user
    public void updateUser(User user) throws SQLException {
        String query = "UPDATE e_Users SET username = ?, password = ?, role = ? WHERE user_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().toString());
            stmt.setInt(4, user.getUserId());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Failed to update user, no rows affected.");
            }
        }
    }

    // Update a client
    public void updateClient(Client client) throws SQLException {
        updateUser(client); // Update general user details
        String query = "UPDATE e_Clients SET email = ?, phone = ?, address = ? WHERE client_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, client.getEmail());
            stmt.setString(2, client.getPhone());
            stmt.setString(3, client.getAddress());
            stmt.setInt(4, client.getClientId());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Failed to update client, no rows affected.");
            }
        }
    }

    // Delete a user
    public void deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM e_Users WHERE user_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Failed to delete user, no rows affected.");
            }
        }
    }
}
