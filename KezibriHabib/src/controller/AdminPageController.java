
package controller;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import javafx.scene.text.Text;
import javafx.beans.property.ReadOnlyObjectWrapper;

import javafx.collections.FXCollections;
import javafx.stage.FileChooser;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import model.Client;
import model.Order;
import model.Product;
import model.Admin;
import model.User;
import util.OrderStatus;
import util.UserInputValidator;
import service.OrderService;
import service.UserService;
import service.ProductService;
import service.UserService;
import service.SearchService;
import service.AuthentificationService;


import java.sql.SQLException;
import java.util.List;
import java.io.File;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class AdminPageController {

    // UI Components
    @FXML private Button adminLogout;
    @FXML private Button adminAccount;


    // Orders Tab
    @FXML private Tab OrdersTab;
    @FXML private TableView<Order> OrdersTableView;
    @FXML private TableColumn<Order, Integer> OrderIDCol;
    @FXML private TableColumn<Order, Integer> orderClientIDCol;
    @FXML private TableColumn<Order, String> orderNameCol;
    @FXML private TableColumn<Order, String> orderAddressCol;
    @FXML private TableColumn<Order, Double> orderPriceCol;
    @FXML private TableColumn<Order, String> orderStatusCol;
    @FXML private TableColumn<Order, String> orderDateCol;
    @FXML private Button OrderUpdateButton;
    @FXML private Button orderDelButton;

    // Clients Tab
    @FXML private Tab customerTab;
    @FXML private TableView<Client> customerTableView;
    @FXML private TableColumn<Client, Integer> customerIdCol;
    @FXML private TableColumn<Client, String> customerNameCol;
    @FXML private TableColumn<Client, String> customerEmailCol;
    @FXML private TableColumn<Client, String> customerPhoneCol;
    @FXML private TableColumn<Client, String> customerAddressCol;
    @FXML private TableColumn<Client, String> customerPassCol;
    @FXML private Button customerAddButton;
    @FXML private Button customerDelButton;
    @FXML private Button customerUpdateButton;
    @FXML private TextField addClientNameText;
    @FXML private TextField addClientAddressText;
    @FXML private TextField addClientEmailText;
    @FXML private TextField addClientPhoneText;
    @FXML private TextField addClientPassText;
    @FXML private TextField addClientConfirmText;
    
    // Products Tab
    @FXML private Tab productsTab;
    @FXML private TableView<Product> productTableView;
    @FXML private TableColumn<Product, Integer> productIdCol;
    @FXML private TableColumn<Product, String> productNameCol;
    @FXML private TableColumn<Product, String> productDesripCol;
    @FXML private TableColumn<Product, Double> productPriceCol;
    @FXML private TableColumn<Product, Integer> productStockCol;
    @FXML private TableColumn<Product, String> productImageCol;
    @FXML private Button productAddButton;
    @FXML private Button productDellButton;
    @FXML private Button productUpdateButoon;
    @FXML private Button productImageButton;
    @FXML private TextField productNameText;
    @FXML private TextField productDescripText;
    @FXML private TextField productStockText;
    @FXML private TextField productPriceText;
    @FXML private TextField imgPath;
    
    // Staff Tab
    @FXML private Tab staffTab;
    @FXML private TableView<Admin> StaffTableView;
    @FXML private TableColumn<Admin, Integer> staffIDCol;
    @FXML private TableColumn<Admin, String> staffUsernameCol;
    @FXML private TableColumn<Admin, String> staffPasswordCol;
    @FXML private Button StaffAddButton;
    @FXML private Button staffDelButton;
    @FXML private Button staffUpdateButton;
    @FXML private TextField addStaffUsernameText;
    @FXML private TextField addStaffPassText;
    @FXML private TextField addStaffConfirmText;
    
    // Search Tab
    @FXML private Text searchResult;

    // Services
    private final OrderService orderService = new OrderService();
    private final UserService userService = new UserService();
    private final ProductService productService = new ProductService();
    private final SearchService searchService = new SearchService();
    private final AuthentificationService  authService = new AuthentificationService(); 
    private final ObservableList<Product> products = FXCollections.observableArrayList();

    
    

    @FXML
    public void initialize() {
        initializeOrderTable();
        initializeClientTable();
        initializeProductTable();
        initializeStaffTable();
        loadOrderData();
        loadClientData();
        loadProductData();
        loadStaffData();
       
    }

    private void initializeOrderTable() {
        OrderIDCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getOrderId()));
        orderClientIDCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getClientId()));

        // Dynamically fetch client name
        orderNameCol.setCellValueFactory(data -> {
            int clientId = data.getValue().getClientId();
            Client client = fetchClientById(clientId);
            return new ReadOnlyObjectWrapper<>(client != null ? client.getUsername() : "Unknown");
        });

        // Dynamically fetch client address
        orderAddressCol.setCellValueFactory(data -> {
            int clientId = data.getValue().getClientId();
            Client client = fetchClientById(clientId);
            return new ReadOnlyObjectWrapper<>(client != null ? client.getAddress() : "Unknown");
        });

        orderPriceCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTotalAmount()));
        orderStatusCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getStatus().name()));
     // Format and display the date
        orderDateCol.setCellValueFactory(data -> {
            LocalDate orderDate = data.getValue().getOrderDate();
            if (orderDate != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return new ReadOnlyObjectWrapper<>(orderDate.format(formatter));
            }
            return new ReadOnlyObjectWrapper<>(""); // Return an empty string if the date is null
        }); 
    }

    private void initializeClientTable() {
    	customerTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateCustomerForm(newValue);
            }
        });
             
        customerIdCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getClientId()));
    	customerNameCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getUsername()));
    	customerAddressCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getAddress()));
    	customerEmailCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getEmail()));
    	customerPassCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPassword()));
        customerPhoneCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPhone()));
       
    }
    
    private void initializeStaffTable() {
        staffIDCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getUserId()));
        staffUsernameCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getUsername()));
        staffPasswordCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPassword()));
    }
     
    private void initializeProductTable() {
    	// Add a listener to the TableView selection model
        productTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateProductForm(newValue);
            }
        });
        productTableView.setItems(products); // Link ObservableList to TableView
        loadProductData();
        // Set cell value factories for each column in the product table
        productIdCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getIdProduct()));
        productNameCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getProductName()));
        productDesripCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getDescription()));
        productPriceCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPrice()));
        productStockCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getStockQuantity()));

        // For displaying images
        productImageCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPimage()));

        productImageCol.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isBlank()) {
                    setGraphic(null); // Clear cell
                } else {
                    Platform.runLater(() -> {
                        try {
                            Image image = imagePath.startsWith("http")
                                ? new Image(imagePath, 50, 50, true, true)
                                : new Image(new File(imagePath).toURI().toString(), 50, 50, true, true);
                            imageView.setImage(image);
                        } catch (Exception e) {
                            System.err.println("Error loading image: " + e.getMessage());
                            imageView.setImage(null); // Clear image on error
                        }
                        setGraphic(imageView);
                    });
                }
            }
        });


    }


    @FXML
    public void handleTabSelection(Event event) {
        Tab selectedTab = (Tab) event.getSource();
        System.out.println("Tab selected: " + selectedTab.getText());

        // Perform different actions based on the selected tab
        if (selectedTab == OrdersTab) {
            System.out.println("Orders tab selected. Load orders data.");
            loadOrderData();
        } else if (selectedTab == customerTab) {
            System.out.println("Customer tab selected. Load customers data.");
            loadClientData();
        } else if (selectedTab == productsTab) {
            System.out.println("Products tab selected. Load products data.");
            loadProductData();
        } else if (selectedTab == staffTab) {
            System.out.println("Staff tab selected. Load staff data.");
            loadStaffData();
        }
    }


    private void loadOrderData() {
        try {
            List<Order> orders = orderService.getAllOrders();
            ObservableList<Order> orderList = FXCollections.observableArrayList(orders);
            OrdersTableView.setItems(orderList);
        } catch (SQLException e) {
            showError("Failed to load orders: " + e.getMessage());
        }
    }

    private void loadClientData() {
        try {
            List<Client> clients = userService.getAllClients();
            ObservableList<Client> clientList = FXCollections.observableArrayList(clients);
            customerTableView.setItems(clientList);
        } catch (SQLException e) {
            showError("Failed to load clients: " + e.getMessage());
        }
    }
   
    private void loadStaffData() {
        try {
            // Retrieve all admins from the database
            List<Admin> admins = userService.getAllAdmins();
            ObservableList<Admin> staffList = FXCollections.observableArrayList(admins);

            // Populate the TableView with the retrieved staff data
            StaffTableView.setItems(staffList);
        } catch (SQLException e) {
            showError("Failed to load staff data: " + e.getMessage());
        }
    }
    
    private void loadProductData() {
        try {
            // Fetch all products using the ProductService
            ProductService productService = new ProductService();
            List<Product> products = productService.getAllProducts();

            // Convert the list of products to an observable list
            ObservableList<Product> productList = FXCollections.observableArrayList(products);

            // Set the items in the TableView
            productTableView.setItems(productList);
        } catch (SQLException e) {
            showError("Failed to load products: " + e.getMessage());
        }
    }

///////////////////////////////////////CRUD//////////////////////////////////////////////
    //********************************************ORDERS*****************************************************

    @FXML
    void handleOrderUpdateButton(ActionEvent event) {
        // Get the selected order from the table
        Order selectedOrder = OrdersTableView.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            showError("No order selected.");
            return;
        }

        // Create a choice dialog for updating the order status
        ChoiceDialog<String> dialog = new ChoiceDialog<>(
            selectedOrder.getStatus().name(), 
            "in_progress", "validated", "delivered"
        );
        dialog.setTitle("Update Order Status");
        dialog.setHeaderText("Change the status of the selected order");
        dialog.setContentText("Select new status:");

        // Show the dialog and wait for the user's input
        dialog.showAndWait().ifPresent(newStatus -> {
            try {
                // Update the order's status in the database
                selectedOrder.setStatus(OrderStatus.valueOf(newStatus));
                orderService.updateOrderStatus(selectedOrder.getOrderId(), OrderStatus.valueOf(newStatus));

                // Reload the table data to reflect the changes
                loadOrderData();
                showInfo("Order status updated successfully.");
            } catch (IllegalArgumentException e) {
                showError("Invalid status selected.");
            } catch (SQLException e) {
                showError("Failed to update order status: " + e.getMessage());
            }
        });
    }
    @FXML
    void handleOrderDeleteButton(ActionEvent event) {
        Order selectedOrder = OrdersTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showError("No order selected.");
            return;
        }

        try {
            orderService.deleteOrder(selectedOrder.getOrderId());
            loadOrderData();
            showInfo("Order deleted successfully.");
        } catch (SQLException e) {
            showError("Failed to delete order: " + e.getMessage());
        }
    }
   
    //********************************************CLIENT*****************************************************
    @FXML
    private void onClientSelected() {
        Client selectedClient = customerTableView.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            addClientNameText.setText(selectedClient.getUsername());
            addClientAddressText.setText(selectedClient.getAddress());
            addClientEmailText.setText(selectedClient.getEmail());
            addClientPhoneText.setText(selectedClient.getPhone());
        }
    }
   
    private void populateCustomerForm(Client client) {
        if (client == null) {
            // Clear the form fields if no client is selected
        	clearClientForm();
        } else {
            try {
                // Populate text fields with the client's details
                addClientNameText.setText(client.getUsername());
                addClientAddressText.setText(client.getAddress());
                addClientEmailText.setText(client.getEmail());
               addClientPhoneText.setText(client.getPhone());

                // Optionally, display the password (if allowed, or leave it blank for security)
               addClientPassText.setText(client.getPassword());
            } catch (Exception e) {
                showError("Error populating client form: " + e.getMessage());
            }
        }
    }
    @FXML
    void handleClientUpdateButton(ActionEvent event) {
        // Get the selected client from the table
        Client selectedClient = customerTableView.getSelectionModel().getSelectedItem();

        if (selectedClient == null) {
            showError("No client selected.");
            return;
        }

        // Validate the updated input fields
        String updatedName = addClientNameText.getText().trim();
        String updatedAddress = addClientAddressText.getText().trim();
        String updatedEmail = addClientEmailText.getText().trim();
        String updatedPhone = addClientPhoneText.getText().trim();

        if (updatedName.isEmpty() || updatedAddress.isEmpty() || updatedEmail.isEmpty() || updatedPhone.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        if (!UserInputValidator.isValidUsername(updatedName)) {
            showError("Invalid name. It must consist of two words separated by a space.");
            return;
        }

        if (!UserInputValidator.isValidEmail(updatedEmail)) {
            showError("Invalid email format.");
            return;
        }

        if (!UserInputValidator.isValidPhoneNumber(updatedPhone)) {
            showError("Invalid phone number format.");
            return;
        }

        try {
            // Update the client object with new details
            selectedClient.setUsername(updatedName);
            selectedClient.setAddress(updatedAddress);
            selectedClient.setEmail(updatedEmail);
            selectedClient.setPhone(updatedPhone);

            // Update the client in the database
            userService.updateClient(selectedClient);

            // Reload the client table to reflect changes
            loadClientData();
            showInfo("Client updated successfully.");
        } catch (SQLException e) {
            showError("Failed to update client: " + e.getMessage());
        }
    }

    @FXML
    void handleClientDeleteButton(ActionEvent event) {
        Client selectedClient = customerTableView.getSelectionModel().getSelectedItem();
        if (selectedClient == null) {
            showError("No client selected.");
            return;
        }

        try {
            userService.deleteUser(selectedClient.getUserId());
            loadClientData();
            showInfo("Client deleted successfully.");
        } catch (SQLException e) {
            showError("Failed to delete client: " + e.getMessage());
        }
    }


    private Client fetchClientById(int clientId) {
        try {
            return userService.getClientById(clientId);
        } catch (SQLException e) {
            System.err.println("Error fetching client: " + e.getMessage());
            return null;
        }
    }
    
    @FXML
    void handleClientAddButton(ActionEvent event) {
        // Retrieve values from the text fields
        String name = addClientNameText.getText().trim();
        String address = addClientAddressText.getText().trim();
        String email = addClientEmailText.getText().trim();
        String phone = addClientPhoneText.getText().trim();
        String password = addClientPassText.getText().trim();
        String confirmPassword = addClientConfirmText.getText().trim();

        try {
            // Validate input
            if (name.isEmpty() || address.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                throw new IllegalArgumentException("All fields are required.");
            }

            if (!UserInputValidator.isValidUsername(name)) {
                throw new IllegalArgumentException("Invalid name. It must consist of two words separated by a space.");
            }

            if (!UserInputValidator.isValidEmail(email)) {
                throw new IllegalArgumentException("Invalid email format.");
            }

            if (!UserInputValidator.isValidPhoneNumber(phone)) {
                throw new IllegalArgumentException("Invalid phone number format.");
            }

            if (!UserInputValidator.isValidPassword(password)) {
                throw new IllegalArgumentException("Password must be at least 8 characters long and contain both letters and numbers.");
            }

            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Passwords do not match.");
            }

            // Create a new client
            Client newClient = new Client(name, password, email, phone, address);

            // Save the client using the UserService
            userService.createUser(newClient);

            // Reload the client data to reflect the changes
            loadClientData();

            // Clear input fields
            clearClientForm();

            // Show success message
            showInfo("Client added successfully.");

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Failed to add client: " + e.getMessage());
        }
    }

    /**
     * Clears the input fields in the client form.
     */
    private void clearClientForm() {
        addClientNameText.clear();
        addClientAddressText.clear();
        addClientEmailText.clear();
        addClientPhoneText.clear();
        addClientPassText.clear();
        addClientConfirmText.clear();
    }
    
    //********************************************ADMIN STAFF*************************************************
    @FXML
    void handleStaffAddButton(ActionEvent event) {
        // Retrieve input from the text fields
        String username = addStaffUsernameText.getText().trim();
        String password = addStaffPassText.getText().trim();
        String confirmPassword = addStaffConfirmText.getText().trim();

        // Validate inputs
        try {
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                throw new IllegalArgumentException("All fields are required.");
            }

            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Passwords do not match.");
            }

            if (!UserInputValidator.isValidUsername(username)) {
                throw new IllegalArgumentException("Invalid username. It must consist of a first and last name separated by a space.");
            }

            if (!UserInputValidator.isValidPassword(password)) {
                throw new IllegalArgumentException("Invalid password. It must be at least 8 characters long and contain both letters and numbers.");
            }

            // Create a new Admin object
            Admin newAdmin = new Admin(username, password);

            // Add the new admin to the database
            UserService userService = new UserService();
            userService.createUser(newAdmin);

            // Reload staff data to reflect the new addition
            loadStaffData();

            // Clear input fields
            clearStaffForm();

            // Show success message
            showInfo("Staff added successfully.");

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Failed to add staff: " + e.getMessage());
        }
    }
    @FXML
    void handleStaffUpdateButton(ActionEvent event) {
        // Get the selected staff from the TableView
        Admin selectedStaff = StaffTableView.getSelectionModel().getSelectedItem();

        if (selectedStaff == null) {
            showError("No staff selected for update.");
            return;
        }

        // Retrieve input from the text fields
        String updatedUsername = addStaffUsernameText.getText().trim();
        String updatedPassword = addStaffPassText.getText().trim();
        String confirmPassword = addStaffConfirmText.getText().trim();

        // Validate inputs
        try {
            if (updatedUsername.isEmpty() || updatedPassword.isEmpty() || confirmPassword.isEmpty()) {
                throw new IllegalArgumentException("All fields are required.");
            }

            if (!updatedPassword.equals(confirmPassword)) {
                throw new IllegalArgumentException("Passwords do not match.");
            }

            if (!UserInputValidator.isValidUsername(updatedUsername)) {
                throw new IllegalArgumentException("Invalid username. It must consist of a first and last name separated by a space.");
            }

            if (!UserInputValidator.isValidPassword(updatedPassword)) {
                throw new IllegalArgumentException("Invalid password. It must be at least 8 characters long and contain both letters and numbers.");
            }

            // Update the staff in the database
            UserService userService = new UserService();
            selectedStaff.setUsername(updatedUsername);
            selectedStaff.setPassword(updatedPassword);

            userService.updateUser(selectedStaff);

            // Reload staff data to reflect the updated details
            loadStaffData();

            // Clear input fields
            clearStaffForm();

            // Show success message
            showInfo("Staff updated successfully.");

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Failed to update staff: " + e.getMessage());
        }
    }

    /**
     * Clears the input fields in the staff form.
     */
    private void clearStaffForm() {
        addStaffUsernameText.clear();
        addStaffPassText.clear();
        addStaffConfirmText.clear();
    }
    @FXML
    void handleDeleteButton(ActionEvent event) {
        // Get the selected staff from the TableView
        Admin selectedStaff = StaffTableView.getSelectionModel().getSelectedItem();

        if (selectedStaff == null) {
            showError("No staff selected for deletion.");
            return;
        }

        // Confirm deletion
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Are you sure you want to delete the selected staff?");
        confirmationAlert.setContentText("This action cannot be undone.");
        confirmationAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        // Handle confirmation response
        ButtonType response = confirmationAlert.showAndWait().orElse(ButtonType.NO);
        if (response == ButtonType.NO) {
            return;
        }

        try {
            // Delete the staff from the database
            UserService userService = new UserService();
            userService.deleteUser(selectedStaff.getUserId());

            // Reload staff data to reflect the deletion
            loadStaffData();

            // Show success message
            showInfo("Staff deleted successfully.");
        } catch (SQLException e) {
            showError("Failed to delete staff: " + e.getMessage());
        }
    }


    //********************************************PRODUCT*****************************************************
    private Product currentProduct; // Represents the product being edited or added
    @FXML
    void handleProductDelButton(ActionEvent event) {
        // Get the selected product from the TableView
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showError("No product selected for deletion.");
            return;
        }

        // Confirm deletion with the user
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Delete Product");
        confirmationAlert.setContentText("Are you sure you want to delete the selected product?");
        var result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Delete the product from the database
                ProductService productService = new ProductService();
                productService.deleteProduct(selectedProduct.getIdProduct());

                // Reload product data to reflect the deletion
                loadProductData();

                // Show success message
                showInfo("Product deleted successfully.");
                clearProductForm();
            } catch (SQLException e) {
                showError("Failed to delete product: " + e.getMessage());
            }
        }
    }
    @FXML
    void handleProductAddButton(ActionEvent event) {
        // Retrieve input from the text fields
        String name = productNameText.getText().trim();
        String description = productDescripText.getText().trim();
        String stock = productStockText.getText().trim();
        String price = productPriceText.getText().trim();
        String imagePath = imgPath.getText().trim(); // Retrieve image path from TextField

        try {
            // Validate inputs
            if (name.isEmpty() || description.isEmpty() || stock.isEmpty() || price.isEmpty()) {
                throw new IllegalArgumentException("All fields are required.");
            }

            if (!stock.matches("\\d+")) {
                throw new IllegalArgumentException("Stock must be a valid positive integer.");
            }

            if (!price.matches("\\d+(\\.\\d{1,2})?")) {
                throw new IllegalArgumentException("Price must be a valid number with up to two decimal places.");
            }
         // Check if an image has been set in the ImageView
            //if (productImageView.getImage() != null) {
            	//imagePath = productImageView.getImage();
                
           // }

            // Create a new Product object
            Product newProduct = new Product();
            newProduct.setName(name);
            newProduct.setDescription(description);
            newProduct.setStockQuantity(Integer.parseInt(stock));
            newProduct.setPrice(Double.parseDouble(price));
            newProduct.setPimage(imagePath); 
            newProduct.setPimage(imagePath);
            
            // Add the product to the database
            ProductService productService = new ProductService();
            productService.addProduct(newProduct);

          
            // Show success message
            showInfo("Product added successfully.");
            // Add the new product directly to the ObservableList (assuming you have an ObservableList backing the TableView)
            products.add(newProduct); // Add to the ObservableList
            // Refresh the TableView (if needed)
            productTableView.refresh();

            // Clear input fields
            clearProductForm();


        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Failed to add product: " + e.getMessage());
        }
    }
    //Not used after the cleaning
    /**
     * Saves the updated image to the appropriate location and returns the path.
     * 
     * @param product The product being updated.
     * @param image   The image to be saved.
     * @return The relative path to the saved image, or null if the saving fails.
     */
    private String saveImageToDatabase(String productName, Image image) {
        try {
            File destinationFolder = new File("images");
            if (!destinationFolder.exists()) destinationFolder.mkdir();// change this

            // Use product name as part of the image filename to avoid conflicts
            String imageFileName = "product_" + productName + ".png";
            File destinationFile = new File(destinationFolder, imageFileName);

            // Convert JavaFX Image to file and save it
            if (destinationFile.exists()) {
                destinationFile.delete(); // Overwrite existing image
            }

            // Save the image and return the path
            return destinationFile.toURI().toString();
        } catch (Exception e) {
            showError("Failed to save product image: " + e.getMessage());
            return null;
        }
    }


    /*private void populateProductForm(Product product) {
    	if (product == null) {
            // Clear the form fields if no client is selected
        	clearProductForm();
        }
        productNameText.setText(product.getProductName());
        productDescripText.setText(product.getDescription());
        productStockText.setText(String.valueOf(product.getStockQuantity()));
        productPriceText.setText(String.valueOf(product.getPrice()));

        // Set the image in the ImageView if available
        if (product.getPimage() != null && !product.getPimage().isBlank()) {
            try {
                Image productImage;
                if (product.getPimage().startsWith("http")) {
                    productImage = new Image(product.getPimage());
                } else {
                    File imageFile = new File(product.getPimage());
                    if (imageFile.exists()) {
                        productImage = new Image(imageFile.toURI().toString());
                    } else {
                        productImage = new Image("default.png"); // Placeholder for missing images
                    }
                }
                productImageView.setImage(productImage);
            } catch (Exception e) {
            	showError("Error loading product image: " + e.getMessage());
                productImageView.setImage(new Image("default.png")); // Fallback image
            }
        } else {
            productImageView.setImage(null); // Clear the ImageView if no image is available
        }
    }*/
    /**
     * Populates the product form with the details of the selected product.
     */
    private void populateProductForm(Product product) {
        if (product == null) {
            clearProductForm();
            return;
        }

        productNameText.setText(product.getProductName());
        productDescripText.setText(product.getDescription());
        productStockText.setText(String.valueOf(product.getStockQuantity()));
        productPriceText.setText(String.valueOf(product.getPrice()));
        imgPath.setText(product.getPimage());

        // Validate and set the image
        if (product.getPimage() != null && !product.getPimage().isBlank()) {
            try {
                Image productImage;
                if (product.getPimage().startsWith("http")) {
                    // If it's a URL, try to load it
                    productImage = new Image(product.getPimage(), true);
                } else {
                    // If it's a local file, validate its existence
                    File imageFile = new File(product.getPimage());
                    if (imageFile.exists()) {
                        productImage = new Image(imageFile.toURI().toString());
                    } else {
                        throw new IllegalArgumentException("File not found: " + product.getPimage());
                    }
                }
                productImageView.setImage(productImage);
            } catch (Exception e) {
                // Log and use a placeholder image if the provided image is invalid
                System.err.println("Error loading image: " + e.getMessage());
                productImageView.setImage(new Image("default.png")); // Use a default placeholder image
            }
        } else {
            // Clear the ImageView if no image is available
            productImageView.setImage(null);
        }
    }



    
    @FXML
    void handleProductUpdateButton(ActionEvent event) {
        // Retrieve the selected product from the TableView
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showError("No product selected for update.");
            return;
        }

        // Retrieve updated values from the text fields
        String updatedName = productNameText.getText().trim();
        String updatedDescription = productDescripText.getText().trim();
        String updatedStock = productStockText.getText().trim();
        String updatedPrice = productPriceText.getText().trim();
        String updatedImagePath = imgPath.getText().trim();
        try {
            // Validate input
            if (updatedName.isEmpty() || updatedDescription.isEmpty() || updatedStock.isEmpty() || updatedPrice.isEmpty()) {
                throw new IllegalArgumentException("All fields are required.");
            }

            if (!updatedStock.matches("\\d+")) {
                throw new IllegalArgumentException("Stock must be a valid positive integer.");
            }

            if (!updatedPrice.matches("\\d+(\\.\\d{1,2})?")) {
                throw new IllegalArgumentException("Price must be a valid number with up to two decimal places.");
            }

            // Update product details
            selectedProduct.setName(updatedName);
            selectedProduct.setDescription(updatedDescription);
            selectedProduct.setStockQuantity(Integer.parseInt(updatedStock));
            selectedProduct.setPrice(Double.parseDouble(updatedPrice));
            selectedProduct.setPimage(updatedImagePath);
            // Check if the image is updated
            /*if (productImageView.getImage() != null) {
            	String updatedImagePath = saveImageToDatabase(selectedProduct.getProductName(), productImageView.getImage());
                if (updatedImagePath != null) {
                    selectedProduct.setPimage(updatedImagePath); // Update the image path
                }
            }*/

            // Update the product in the database
            productService.updateProduct(selectedProduct);

            // Reload product data to reflect changes
            loadProductData();

            // Clear the input fields
            clearProductForm();

            // Show success message
            showInfo("Product updated successfully.");

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Failed to update product: " + e.getMessage());
        }
    }
    
    //never used after the cleaning of the code
    private void onProductSelected(Product selectedProduct) {
        currentProduct = selectedProduct; // Assign the selected product
    }
    private void refreshProductTable() {
        try {
            List<Product> products = productService.getAllProducts(); // Fetch updated product list
            productTableView.setItems(FXCollections.observableArrayList(products)); // Update TableView
        } catch (SQLException e) {
            showError("Error loading products: " + e.getMessage());
        }
    }


    @FXML
    private ImageView productImageView;
    //never used after the cleaning of the code
    private void updateProductImage(String imagePath) {
        try {
            if (currentProduct != null) {
                currentProduct.setPimage(imagePath);
                productService.updateProduct(currentProduct);
                refreshProductTable();
                showInfo("Image successfully updated.");
            } else {
                showError("No product selected. Please select or create a product first.");
            }
        } catch (SQLException e) {
             showError("Updating Product Image" + e.getMessage());

        }
    }

   /* @FXML
    public void handleProductImageButton(ActionEvent event) {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Add Product Image");
        inputDialog.setHeaderText("Enter an HTTPS URL or select a local file");
        inputDialog.setContentText("Enter URL (or leave blank to select file):");

        String imageUrl = inputDialog.showAndWait().orElse("");
        if (!imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http")) {
            	productImageView.setImage(imageUrl);
            } else {
                showError("Invalid URL. Please provide a valid HTTPS URL.");
            }
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File selectedFile = fileChooser.showOpenDialog(productImageView.getScene().getWindow());

            if (selectedFile != null) {
                try {
                    File destinationFolder = new File("images");
                    if (!destinationFolder.exists()) destinationFolder.mkdir();

                    File destinationFile = new File(destinationFolder, selectedFile.getName());
                    Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    updateProductImage("images/" + selectedFile.getName());
                } catch (IOException e) {
                    handleException("Saving Image", e);
                }
            } else {
                showError("No file selected.");
            }
        }
    }*/

/*
    @FXML
    public void handleProductImageButton(ActionEvent event) {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Add Product Image");
        inputDialog.setHeaderText("Enter an HTTPS URL or select a local file");
        inputDialog.setContentText("Enter URL (or leave blank to select file):");

        String imageUrl = inputDialog.showAndWait().orElse("");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            // User provided a URL
            if (imageUrl.startsWith("http") || imageUrl.startsWith("https")) {
                try {
                    // Load the remote image
                    Image remoteImage = new Image(imageUrl, true);

                    if (remoteImage.isError()) {
                        showError("Failed to load the image from the provided URL.");
                        return;
                    }

                    // Update the ImageView
                    productImageView.setImage(remoteImage);

                    // Save the HTTPS URL directly in the database
                    if (currentProduct != null) {
                        currentProduct.setPimage(imageUrl); // Save HTTPS URL
                        productService.updateProduct(currentProduct); // Update in the database
                        refreshProductTable(); // Refresh the table to display updated data
                    } else {
                        showError("No product selected. Please select or create a product first.");
                    }

                    showInfo("Image successfully imported from URL.");
                } catch (Exception e) {
                    showError("Error loading image from URL: " + e.getMessage());
                }
            } else {
                showError("Invalid URL. Please provide a valid HTTPS URL.");
            }
        } else {
            // User wants to select a local file
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

            File selectedFile = fileChooser.showOpenDialog(productImageView.getScene().getWindow());

            if (selectedFile != null) {
                try {
                    File destinationFolder = new File("images");
                    if (!destinationFolder.exists()) destinationFolder.mkdir();

                    File destinationFile = new File(destinationFolder, selectedFile.getName());
                    Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    // Update the ImageView
                    Image importedImage = new Image(destinationFile.toURI().toString());
                    productImageView.setImage(importedImage);

                    // Save the relative path in the product object
                    if (currentProduct != null) {
                        currentProduct.setPimage("images/" + selectedFile.getName()); // Save this in DB
                        productService.updateProduct(currentProduct); // Save changes in the database
                        refreshProductTable(); // Refresh the table to display updated data
                    } else {
                        showError("No product selected. Please select or create a product first.");
                    }

                    showInfo("Image successfully imported.");
                } catch (IOException e) {
                    showError("Error saving image: " + e.getMessage());
                } catch (SQLException e) {
                    showError("Error updating product: " + e.getMessage());
                }
            } else {
                showError("No file selected.");
            }
        }
        
    }*/
    /**
     * Handles the image import button.
     */
    @FXML
    public void handleProductImageButton(ActionEvent event) {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Add Product Image");
        inputDialog.setHeaderText("Enter an HTTPS URL or select a local file");
        inputDialog.setContentText("Enter URL (or leave blank to select a file):");

        String imageUrl = inputDialog.showAndWait().orElse("");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            // User provided a URL
            if (imageUrl.startsWith("http") || imageUrl.startsWith("https")) {
                try {
                    Image remoteImage = new Image(imageUrl, true);

                    if (remoteImage.isError()) {
                        showError("Failed to load the image from the provided URL.");
                        return;
                    }

                    // Set the HTTPS URL in the imgPath TextField
                    imgPath.setText(imageUrl);

                    // Display the image in the ImageView
                    productImageView.setImage(remoteImage);

                    showInfo("Image successfully loaded from URL.");
                } catch (Exception e) {
                    showError("Error loading image from URL: " + e.getMessage());
                }
            } else {
                showError("Invalid URL. Please provide a valid HTTPS URL.");
            }
        } else {
            // User wants to select a local file
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

            File selectedFile = fileChooser.showOpenDialog(imgPath.getScene().getWindow());

            if (selectedFile != null) {
                try {
                    File destinationFolder = new File("images");
                    if (!destinationFolder.exists()) destinationFolder.mkdir();

                    File destinationFile = new File(destinationFolder, selectedFile.getName());
                    Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    // Set the local file's relative path in the imgPath TextField
                    imgPath.setText("images/" + selectedFile.getName());

                    // Display the image in the ImageView
                    productImageView.setImage(new Image(destinationFile.toURI().toString()));

                    showInfo("Image successfully loaded from file.");
                } catch (IOException e) {
                    showError("Error saving image: " + e.getMessage());
                }
            } else {
                showError("No file selected.");
            }
        }
    }


    /**
     * Clears the input fields in the product form.
     */
    private void clearProductForm() {
    	productNameText.clear();
        productDescripText.clear();
        productStockText.clear();
        productPriceText.clear();
        imgPath.clear();
        productImageView.setImage(null);
    }
    
    
    
    //**********************************************SEARCH**************************************
    @FXML
    private TextField SearchText; // Input field for search query
   
    /**
     * Handles search functionality across clients, products, or orders based on user input.
     */
    @FXML
    private void onSearch() {
        // Retrieve the search query from the text field
        String searchQuery = SearchText.getText().trim();

        // Check if the search query is empty
        if (searchQuery.isEmpty()) {
            searchResult.setText("Please enter a search term.");
            return;
        }

        try {
            // Search clients
            List<Client> matchingClients = searchService.searchUserByUsername(searchQuery).stream()
                .filter(user -> user instanceof Client)
                .map(user -> (Client) user)
                .toList();

            if (!matchingClients.isEmpty()) {
                StringBuilder resultText = new StringBuilder("Clients Found:\n");
                for (Client client : matchingClients) {
                    resultText.append(formatClientInfo(client)).append("\n");
                }
                searchResult.setText(resultText.toString());
                return;
            }

            // Search products
            List<Product> matchingProducts = searchService.searchProductsByName(searchQuery);
            if (!matchingProducts.isEmpty()) {
                StringBuilder resultText = new StringBuilder("Products Found:\n");
                for (Product product : matchingProducts) {
                    resultText.append(formatProductInfo(product)).append("\n");
                }
                searchResult.setText(resultText.toString());
                return;
            }

            // Search orders dynamically for a specific client
            searchOrders(searchQuery); // Reuse the optimized `searchOrders` method

            // No matches found
            searchResult.setText("No results found for the query: " + searchQuery);

        } catch (SQLException e) {
            searchResult.setText("Error during search: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //  Search Orders Function
    private void searchOrders(String searchQuery) {
        // Check if the search query is empty
        if (searchQuery.isEmpty()) {
            searchResult.setText("Please enter a value to search.");
            return;
        }

        try {
            // Call the search service using the username (searchQuery)
            List<Order> matchingOrders = searchService.searchOrders(searchQuery, searchQuery);

            if (!matchingOrders.isEmpty()) {
                // Build and display results
                String resultText = matchingOrders.stream()
                        .map(this::formatOrderInfo)
                        .reduce((a, b) -> a + "\n" + b)
                        .orElse(""); // Reduce results into a single formatted string
                searchResult.setText("Orders Found:\n" + resultText);
            } else {
                searchResult.setText("No orders found matching the search criteria.");
            }
        } catch (SQLException e) {
            searchResult.setText("Error searching orders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    

    //********************************************LOGIN********************************************
    private User loggedInUser; // Store the logged-in user

    @FXML
    public void setLoggedInUser(User user) {
        //AuthentificationService authService = new AuthentificationService();

       
            // Store the logged-in user
            this.loggedInUser = user;

            // Personalize the button text with the user's full name and role
            String userRole = loggedInUser instanceof Admin ? "Admin" : "Client";
            adminAccount.setText(user.getUsername());

            // Optionally, perform further actions such as redirecting the user to another page
            System.out.println("Login successful for: " + user.getUsername() + " (" + userRole + ")");
       
    }

    
    //********************************************LOGOUT********************************************
    @FXML
    public void handleLogout(ActionEvent event) {
        // Show confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to log out?");
        alert.setContentText("You will be redirected to the login page.");
        
        // Wait for user response
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("Admin logged out.");
            
            // Load the login page
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
                Parent loginPage = loader.load();

                // Get the current stage and set the login scene
                Stage stage = (Stage) adminLogout.getScene().getWindow();
                Scene scene = new Scene(loginPage);
                stage.setScene(scene);
                stage.setTitle("Login Page");
                stage.show();
            } catch (IOException e) {
                showError("Failed to load the login page: " + e.getMessage());
            }
        } else {
            // User cancelled logout
            System.out.println("Logout cancelled.");
        }
    }


    
    
    ////////////////////////////////////////////////////
    
    /**
     * Formats client information for display.
     *
     * @param client The client to format.
     * @return A formatted string with client details.
     */
    private String formatClientInfo(Client client) {
        return "ID: " + client.getClientId() + ", Name: " + client.getUsername() +
               ", Email: " + client.getEmail() + ", Phone: " + client.getPhone() +
               ", Address: " + client.getAddress();
    }

    /**
     * Formats product information for display.
     *
     * @param product The product to format.
     * @return A formatted string with product details.
     */
    private String formatProductInfo(Product product) {
        return "ID: " + product.getIdProduct() + ", Name: " + product.getProductName() +
               ", Price: $" + product.getPrice() + ", Stock: " + product.getStockQuantity() +
               ", Description: " + product.getDescription();
    }

    /**
     * Formats order information for display.
     *
     * @param order The order to format.
     * @return A formatted string with order details.
     */
    private String formatOrderInfo(Order order) {
        return "Order ID: " + order.getOrderId() + ", Client ID: " + order.getClientId() +
               ", Status: " + order.getStatus();
    }

 
     
    private void showError(String message) {
    	Platform.runLater(() -> {
	        Alert alert = new Alert(Alert.AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setContentText(message);
	        alert.showAndWait();
    	});
    }

    private void showInfo(String message) {
    	Platform.runLater(() -> {
	        Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        alert.setTitle("Information");
	        alert.setContentText(message);
	        alert.showAndWait();
    	});
    }
   

}
