module test {
	
	requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;

    exports controller; // Makes the `controller` package accessible to other modules.
    opens controller to javafx.fxml; // Allows reflection for FXML controllers.
    opens application to javafx.graphics, javafx.fxml; // Optional, for the main app package.
}
