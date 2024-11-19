package model;

import service.ProductService;
import java.sql.SQLException;

public class OrderItem {
    private int orderItemId;   // Auto-generated (primary key)
    private int orderId;       // Foreign key to the Order
    private int productId;     // Foreign key to the Product table
    private int quantity;      // Quantity of the product
    private double itemPrice;  // Unit price of the product
    private ProductService productService; // To fetch product details

    // Constructor
    public OrderItem(int orderId, int productId, int quantity, double itemPrice) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.itemPrice = itemPrice; // Pass unit price directly
    }

    // Overloaded constructor with ProductService
    public OrderItem(int orderId, int productId, int quantity, ProductService productService) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.productService = productService;

        // Fetch the item price using ProductService
        this.itemPrice = fetchItemPrice();
    }

    // Getter and Setter Methods
    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = quantity;
    }

    public double getItemPrice() {
        return itemPrice; // Unit price of the product
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    // Fetch the product price using ProductService
    private double fetchItemPrice() {
        try {
            Product product = productService.getProductById(this.productId);
            if (product != null) {
                return product.getPrice();
            } else {
                throw new IllegalArgumentException("Product not found for productId: " + this.productId);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product price: " + e.getMessage());
            return 0.0; // Default to 0.0 if an error occurs
        }
    }

    // Calculate total price for this order item
    public double TotalPrice() {
        return this.itemPrice * this.quantity; // Unit price * quantity
    }
}
