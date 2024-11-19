package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mySQLdb.DBConnect;
import model.User;
import model.Admin;
import model.Client;
import util.Role;

public class AuthentificationService {

    /**
     * Authenticates a user by their username and password.
     *
     * @param username The username provided by the user.
     * @param password The password provided by the user.
     * @return A User object representing the authenticated user.
     * @throws AuthenticationException If authentication fails.
     */
    public User authenticate(String username, String password) throws AuthenticationException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new AuthenticationException("Username and password must not be blank.");
        }

        String query = """
                SELECT u.user_id, u.username, u.password, u.role, 
                       c.email, c.phone, c.address
                FROM e_Users u
                LEFT JOIN e_Clients c ON u.user_id = c.user_id
                WHERE u.username = ?;
                """;

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String dbPassword = resultSet.getString("password");
                    String role = resultSet.getString("role");

                    // Verify password
                    if (!password.equals(dbPassword)) { // Replace with password hash comparison if needed
                        throw new AuthenticationException("Invalid password.");
                    }

                    // Handle roles
                    if (Role.admin.name().equalsIgnoreCase(role)) {
                        Admin admin = new Admin(username, password);
                        admin.setUsername(username); // Assuming Admin class has a setFullName method
                        return admin;
                    } else if (Role.client.name().equalsIgnoreCase(role)) {
                        String email = resultSet.getString("email");
                        String phone = resultSet.getString("phone");
                        String address = resultSet.getString("address");
                        Client client = new Client(username, password, email, phone, address);
                        client.setUsername(username); // Assuming Client class has a setFullName method
                        return client;
                    } else {
                        throw new AuthenticationException("Unknown user role: " + role);
                    }
                } else {
                    throw new AuthenticationException("User not found.");
                }
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Custom exception for authentication errors.
     */
    public static class AuthenticationException extends Exception {
        public AuthenticationException(String message) {
            super(message);
        }

        public AuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
