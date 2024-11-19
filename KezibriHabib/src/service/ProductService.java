package service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Product;
import mySQLdb.DBConnect;

public class ProductService {

    public void addProduct(Product product) throws SQLException {
        String query = "INSERT INTO e_Product (name, description, price, imagePath, stockQuantity ) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setString(4, product.getPimage());
            stmt.setInt(5, product.getStockQuantity());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to add product, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error adding product: " + e.getMessage(), e);
        }
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM e_Product";

        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("imagePath"),
                        rs.getInt("stockQuantity")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching products: " + e.getMessage(), e);
        }
        return products;
    }

    public Product getProductById(int productId) throws SQLException {
        String query = "SELECT * FROM e_Product WHERE product_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("imagePath"),
                        rs.getInt("stockQuantity")
                );
            } else {
                throw new SQLException("Product not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching product by ID: " + e.getMessage(), e);
        }
    }
    public Product getProductByName(String name) throws SQLException {
        String query = "SELECT * FROM e_Product WHERE name = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("imagePath"),
                        rs.getInt("stockQuantity")
                );
            } else {
                throw new SQLException("Product not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching product by ID: " + e.getMessage(), e);
        }
    }
    public List<String> getAllProductNames() throws SQLException {
        String query = "SELECT name FROM e_Product"; // Query only the product names
        List<String> productNames = new ArrayList<>();

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                productNames.add(rs.getString("name")); // Add the product name to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching product names: " + e.getMessage(), e);
        }

        return productNames;
    }


    public void updateProduct(Product product) throws SQLException {
        String query = "UPDATE e_Product SET name = ?, description = ?, price = ?, stockQuantity = ?, imagePath = ? WHERE product_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getStockQuantity());
            stmt.setString(5, product.getPimage());
            stmt.setInt(6, product.getIdProduct());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update product, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error updating product: " + e.getMessage(), e);
        }
    }
    public void updateProductStock(int productId, int quantityChange) throws SQLException {
        String query = "UPDATE e_Product SET stockQuantity = stockQuantity + ? WHERE product_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, quantityChange);
            stmt.setInt(2, productId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update product stock, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error updating product stock: " + e.getMessage(), e);
        }
    }
    
    


    public void deleteProduct(int productId) throws SQLException {
        String query = "DELETE FROM e_Product WHERE product_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to delete product, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error deleting product: " + e.getMessage(), e);
        }
    }
}
