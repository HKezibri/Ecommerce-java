package service;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import model.Invoice;
import model.OrderItem;
import model.Product;
import mySQLdb.DBConnect;

public class InvoiceService {

	// Generate invoice for an order
	public void CreateInvoice(int orderId) throws SQLException {
	    String query = "SELECT o.order_id, o.order_date, oi.product_id, oi.quantity, p.product_price " +
	                   "FROM e_Orders o " +
	                   "JOIN e_OrderItem oi ON o.order_id = oi.order_id " +
	                   "JOIN e_Products p ON oi.product_id = p.product_id " +
	                   "WHERE o.order_id = ?";

	    try (Connection conn = DBConnect.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setInt(1, orderId);
	        ResultSet rs = stmt.executeQuery();

	        // Check if the order exists and fetch order details
	        List<OrderItem> orderItems = new ArrayList<>();
	        double totalPrice = 0.0;
	        Date orderDate = null;

	        while (rs.next()) {
	            // Get order details
	            if (orderDate == null) {
	                orderDate = rs.getDate("order_date");
	            }

	            int productId = rs.getInt("product_id");
	            int quantity = rs.getInt("quantity");
	            double productPrice = rs.getDouble("product_price");

	            // Create OrderItem and calculate total price
	            OrderItem item = new OrderItem(orderId, productId, quantity, productPrice);
	            orderItems.add(item);
	            totalPrice += item.TotalPrice(); // Unit price * quantity
	        }

	        if (orderItems.isEmpty()) {
	            throw new SQLException("Order with ID " + orderId + " has no items.");
	        }

	        // Create Invoice
	        Invoice invoice = new Invoice(orderId, totalPrice, orderDate);
	        saveInvoice(invoice, conn);

	        System.out.println("Invoice generated successfully for Order ID: " + orderId);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new SQLException("Error generating invoice: " + e.getMessage(), e);
	    }
	}



    // Fetch product details by product ID
    private Product getProductById(int productId, Connection conn) throws SQLException {
        String query = "SELECT * FROM e_Product WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Validate data fields before creating product
                String name = rs.getString("name");
                if (name == null || name.isEmpty()) {
                    throw new SQLException("Product name is missing for product ID: " + productId);
                }
                return new Product(
                    productId,
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getString("imagePath"),
                    rs.getInt("stockQuantity")
                );
            } else {
                throw new SQLException("Product not found with ID: " + productId);
            }
        }
    }
    
    // Update an existing invoice's total in case of order modification
    public void updateInvoice(int orderId, double newTotalPrice) throws SQLException {
        String query = "UPDATE e_Invoice SET total = ? WHERE order_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDouble(1, newTotalPrice);
            stmt.setInt(2, orderId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Failed to update invoice for order ID " + orderId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error updating invoice: " + e.getMessage(), e);
        }
    }

    // Save generated invoice into the database
    private void saveInvoice(Invoice invoice, Connection conn) throws SQLException {
        String query = "INSERT INTO e_Invoice (order_id, total, invoice_date) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, invoice.getOrderId());
            stmt.setDouble(2, invoice.getTotalprice());
            stmt.setDate(3, new java.sql.Date(invoice.getInvoiceDate().getTime()));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to generate invoice, no rows affected.");
            }

            // Retrieve and set the generated ID for the invoice
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                invoice.setIdInvoice(generatedKeys.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error saving invoice: " + e.getMessage(), e);
        }
    }
    // Delete an invoice by its ID
    public void deleteInvoice(int idInvoice) throws SQLException {
        String query = "DELETE FROM e_Invoice WHERE invoice_id = ?";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idInvoice);
            int rowsAffected = stmt.executeUpdate();

            // Check if the delete was successful
            if (rowsAffected == 0) {
                throw new SQLException("No invoice found with ID: " + idInvoice + ". Deletion failed.");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error deleting invoice: " + e.getMessage(), e);
        }
    }

}
