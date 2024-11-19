package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Admin;
import model.Order;
import model.OrderItem;
import model.Product;
import model.User;
import service.AuthentificationService;
import service.OrderService;
import service.ProductService;
import util.OrderStatus;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientShoppingController {

    @FXML private ImageView Img;
    @FXML private Button addCartButton;
    @FXML private TableColumn<OrderItem, String> itemCol;
    @FXML private TableColumn<OrderItem, Integer> qtyCol;
    @FXML private TableColumn<OrderItem, Double> costCol;
    @FXML private TableColumn<OrderItem, Double> amountCol;
    @FXML private TableView<OrderItem> cartTable;
    @FXML private ChoiceBox<Product> productNameChoice;
    @FXML private Label productPrice;
    @FXML private Spinner<Integer> productQty;
    @FXML private Label totalLabel;
    @FXML private Button removeBtn;
    @FXML private Button checkoutBtn;
    @FXML private Button clientLogout;
    @FXML private Button clientAccount;

    private final ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
    private final ProductService productService = new ProductService();
    private final OrderService orderService = new OrderService();
    private Order currentOrder;

    @FXML
    public void initialize() {
        setupProductChoiceBox();
        setupTableView();
        setupSpinner();
        initializeOrder();
        loadProductChoices();
    }

    /**
     * Sets up the Product ChoiceBox to display product names and handle selection events.
     */
    private void setupProductChoiceBox() {
        productNameChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                displayProductImage(newVal);
            }
        });
    }

    /**
     * Configures the TableView columns for displaying cart items.
     */
    private void setupTableView() {
        // Unit price (cost) column - expects Double
        costCol.setCellValueFactory(data -> 
            new ReadOnlyObjectWrapper<>(data.getValue().getItemPrice())); // Use Double directly

        // Total cost (amount = productPrice * quantity) column - expects Double
        amountCol.setCellValueFactory(data -> 
            new ReadOnlyObjectWrapper<>(data.getValue().TotalPrice())); // Use Double directly

        // Item name column - expects String
        itemCol.setCellValueFactory(data -> 
            new ReadOnlyObjectWrapper<>(getProductNameById(data.getValue().getProductId())));

        // Quantity column - expects Integer
        qtyCol.setCellValueFactory(data -> 
            new ReadOnlyObjectWrapper<>(data.getValue().getQuantity()));

        // Set items for the TableView
        cartTable.setItems(orderItems);

        // Add custom formatting for the cost and amount columns (optional)
        formatPriceColumns();
    }





    /**
     * Configures the quantity spinner for product selection.
     */
    private void setupSpinner() {
        productQty.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
    }

    /**
     * Initializes a new order for the client.
     */
    private void initializeOrder() {
        try {
            currentOrder = new Order(1, OrderStatus.in_progress, new ArrayList<>()); // Replace 1 with the actual client ID
            orderService.createOrder(currentOrder);
        } catch (SQLException e) {
            showError("Error initializing order: " + e.getMessage());
        }
    }

    /**
     * Loads products from the database into the ChoiceBox.
     */
    private void loadProductChoices() {
        try {
            List<Product> products = productService.getAllProducts();
            productNameChoice.setItems(FXCollections.observableArrayList(products));
        } catch (SQLException e) {
            showError("Failed to load products: " + e.getMessage());
        }
    }

    /**
     * Displays the details of the selected product, including price and image.
     */
    private void displayProductImage(Product product) {
        if (product != null) {
            productPrice.setText(String.format("$%.2f", product.getPrice()));

            String imagePath = product.getPimage();
            System.out.println("Image path: " + imagePath); // Debugging

            try {
                if (imagePath != null) {
                    if (imagePath.startsWith("http") || imagePath.startsWith("https")) {
                        // Load remote image
                        System.out.println("Loading remote image: " + imagePath);
                        Img.setImage(new Image(imagePath, true));
                    } else {
                        // Load local image
                        System.out.println("Loading local image: " + imagePath);
                        Img.setImage(new Image("file:" + imagePath, true));
                    }
                } else {
                    throw new IllegalArgumentException("Image path is null or empty");
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                Img.setImage(new Image("images/cream.png")); // Default fallback
            }
        }
    }
    @FXML
    public void handleproductNameChoice(javafx.scene.input.MouseEvent event) {
    	Product selectedProduct = productNameChoice.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            productPrice.setText(String.format("$%.2f", selectedProduct.getPrice()));
            productQty.getValueFactory().setValue(1); // Reset quantity to 1
        } else {
            productPrice.setText(""); // Clear the price if no product is selected
        }
    }

    @FXML
    public void handImgChoice(javafx.scene.input.MouseEvent event) {
        // Get the selected product from the ChoiceBox
        Product selectedProduct = productNameChoice.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            // Display the product image and other details
            displayProductImage(selectedProduct);
        } else {
            System.err.println("No product selected.");
            Img.setImage(new Image("/images/placeholder.png")); // Fallback placeholder
        }
    }


    /**
     * Handles adding the selected product to the cart.
     */
    @FXML
    void handleAddToCart(ActionEvent event) {
        Product selectedProduct = productNameChoice.getSelectionModel().getSelectedItem();
        int quantity = productQty.getValue();

        if (selectedProduct == null || quantity <= 0) {
            showError("Please select a valid product and quantity.");
            return;
        }

        if (selectedProduct.getStockQuantity() < quantity) {
            showError("Not enough stock available.");
            return;
        }

        // Check if the product is already in the cart
        OrderItem existingItem = orderItems.stream()
                .filter(item -> item.getProductId() == selectedProduct.getIdProduct())
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            orderItems.add(new OrderItem(
                currentOrder.getOrderId(),
                selectedProduct.getIdProduct(),
                quantity,
                selectedProduct.getPrice()
            ));
        }

        try {
            productService.updateProductStock(selectedProduct.getIdProduct(), -quantity);
            updateTotalCost();
            cartTable.refresh();
        } catch (SQLException e) {
            showError("Failed to update cart: " + e.getMessage());
        }
    }

    /**
     * Handles removing an item from the cart.
     */
    @FXML
    void handleRemoveButton(ActionEvent event) {
        OrderItem selectedItem = cartTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            showError("Please select an item to remove.");
            return;
        }

        orderItems.remove(selectedItem);

        try {
            productService.updateProductStock(selectedItem.getProductId(), selectedItem.getQuantity());
            updateTotalCost();
            showInfo("Item removed from the cart.");
        } catch (SQLException e) {
            showError("Failed to remove product from cart: " + e.getMessage());
        }
    }

    /**
     * Handles the checkout process.
     */
    @FXML
    void handleCheckout(ActionEvent event) {
        if (orderItems.isEmpty()) {
            showError("Your cart is empty. Please add items before checking out.");
            return;
        }

        try {
            // Update order status to "validated"
            orderService.updateOrderStatus(currentOrder.getOrderId(), OrderStatus.validated);

            // Clear the cart and reset totals
            orderItems.clear();
            updateTotalCost();
            cartTable.refresh(); // Refresh table to clear items

            // Reset ChoiceBox and quantity spinner
            productNameChoice.getSelectionModel().clearSelection(); // Clear selected product
            productQty.getValueFactory().setValue(1); // Reset quantity to 1
            productPrice.setText(""); // Clear displayed price
            // Remove any image from the ImageView
            Img.setImage(null);

            // Initialize a new order
            initializeOrder();

            showInfo("Checkout successful! Your order has been validated.");
        } catch (SQLException e) {
            showError("Failed to complete checkout: " + e.getMessage());
        }
    }




    /**
     * Updates the total cost of the cart.
     */
    private void updateTotalCost() {
        double totalCost = orderItems.stream()
                .mapToDouble(OrderItem::TotalPrice) // Sum of all total prices
                .sum();

        // Update the total label
        totalLabel.setText(String.format("Total: $%.2f", totalCost));
    }

    


    /**
     * Retrieves the product name by its ID.
     */
    private String getProductNameById(int productId) {
        try {
            Product product = productService.getProductById(productId);
            return product != null ? product.getProductName() : "Unknown Product";
        } catch (SQLException e) {
            return "Unknown Product";
        }
    }
    
    private void formatPriceColumns() {
        // Format the cost column
        costCol.setCellFactory(column -> new TableCell<OrderItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item)); // Format as currency
                }
            }
        });

        // Format the amount column
        amountCol.setCellFactory(column -> new TableCell<OrderItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item)); // Format as currency
                }
            }
        });
    }
    private User loggedInUser; // Store the logged-in user

    @FXML
    public void setLoggedInUser(User user) {
        AuthentificationService authService = new AuthentificationService();

       
            // Store the logged-in user
            this.loggedInUser = user;

            // Personalize the button text with the user's full name and role
            String userRole = loggedInUser instanceof Admin ? "Admin" : "Client";
            clientAccount.setText(user.getUsername());

            // Optionally, perform further actions such as redirecting the user to another page
            System.out.println("Login successful for: " + user.getUsername() + " (" + userRole + ")");
       
    }
    
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
                Stage stage = (Stage) clientLogout.getScene().getWindow();
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


    /**
     * Displays an error alert with the specified message.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an information alert with the specified message.
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
