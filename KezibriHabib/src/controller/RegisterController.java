package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Client;
import service.UserService;
import util.Role;

import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField RegisterAddressField;

    @FXML
    private Button RegisterButton;

    @FXML
    private PasswordField RegisterConfirmField;

    @FXML
    private TextField RegisterEmailField;

    @FXML
    private VBox RegisterPage;

    @FXML
    private TextField RegisterPhoneField;

    @FXML
    private TextField RegisterUsernameField;

    @FXML
    private PasswordField RegisterpasswordField;

    @FXML
    private Hyperlink backToLogin;

    @FXML
    private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    void handleRegister(ActionEvent event) {
        // Reset error label
        errorLabel.setText("");
        errorLabel.setStyle(""); // Reset label style

        // Retrieve inputs
        String username = RegisterUsernameField.getText().trim();
        String password = RegisterpasswordField.getText().trim();
        String confirmPassword = RegisterConfirmField.getText().trim();
        String email = RegisterEmailField.getText().trim();
        String phone = RegisterPhoneField.getText().trim();
        String address = RegisterAddressField.getText().trim();

        // Validate inputs
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            setError("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            setError("Passwords do not match.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            setError("Invalid email format.");
            return;
        }

        if (!phone.matches("\\d+")) {
            setError("Phone number must be numeric.");
            return;
        }

        try {
            // Create a new Client object
            Client newClient = new Client(username, password, email, phone, address);
            newClient.setRole(Role.client); // Explicitly set role as client

            // Call UserService to create the user
            userService.createUser(newClient);

            // Clear the form after successful registration
            clearForm();

            // Notify the user
            setSuccess("Registration successful. You can now log in.");

        } catch (SQLException e) {
            setError("Failed to register: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleBackToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginPage.fxml"));
            System.out.println("Loading FXML: /view/Login.fxml"); //
            Parent root = loader.load();

            Stage currentStage = (Stage) backToLogin.getScene().getWindow(); // Use the Hyperlink
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Login");
            currentStage.show();
        } catch (Exception e) {
            setError("Failed to navigate to the login page.");
            e.printStackTrace();
        }
    }


    private void clearForm() {
        RegisterUsernameField.clear();
        RegisterpasswordField.clear();
        RegisterConfirmField.clear();
        RegisterEmailField.clear();
        RegisterPhoneField.clear();
        RegisterAddressField.clear();
    }

    private void setError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    private void setSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: green;");
    }
}
