package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class WelcomePageController {
	
	@FXML private AnchorPane WelcomePage;

    // Navigation Buttons
    @FXML private Button NavShopLogin;
    @FXML private Button NavLoginButton;
    @FXML private Button NavRegisterButton;

    // Main Banner Button
    @FXML private Button LoginShopButton;

    /**
     * Initializes the controller after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        // Any additional setup or bindings can be done here.
        // Keeping this empty if no dynamic initialization is needed.
    }

 
  
    /**
     * Navigates to the Register page when the "Register" button is clicked.
     */
    @FXML
    void handleNavRegisterButton(ActionEvent event) {
        navigateToPage("Register.fxml", event);
    }

    /**
     * Navigates to the Login page when the "Shop Now" button is clicked.
     */
    @FXML
    void handleLoginShopButton(ActionEvent event) {
        navigateToPage("Login.fxml", event);
    }

    /**
     * Navigates to a specified page by loading the corresponding FXML file.
     *
     * @param fxmlFileName The name of the FXML file to navigate to.
     * @param event        The action event triggered by a button click.
     */
    private void navigateToPage(String fxmlFileName, ActionEvent event) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFileName));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Show the updated stage
            stage.show();
        } catch (IOException e) {
            // Show an error alert if the page fails to load
            showError("Unable to load the page: " + fxmlFileName + "\nError: " + e.getMessage());
        }
    }

    /**
     * Displays an error alert with a specified message.
     *
     * @param message The error message to display.
     */
    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Navigation Error");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Displays an informational alert with a specified message.
     *
     * @param message The information message to display.
     */
    private void showInfo(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
