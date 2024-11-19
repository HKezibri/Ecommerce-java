package model;

//@Table(name = "e_Product")

public class Product {
	
	private int ProductId; //auto-generated
    private String name;
    private String description;
    private double price;
    private String imagePath ;
    private int stockQuantity;
    // private Date add_date private Date update_date
    
    //Constructor
    public Product() {
        this.name = "";
        this.description = "";
        this.price = 0.0;
        this.imagePath  = "";
        this.stockQuantity = 0;
    }
    public Product(String name, String descrip, double price, String pimage, int stock) {
    	if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        this.name = name;
        this.description = descrip;
        this.price = price;
        this.imagePath  = pimage;
        this.stockQuantity = stock;
    }
    public Product(int idProduct, String name, String description, double price, String pImage, int stockQuantity) {
    	if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        this.ProductId = idProduct;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imagePath  = pImage;
        this.stockQuantity = stockQuantity;
    }

    // Getters & setters
    public int getIdProduct() {
        return ProductId;
    }

    public void setIdProduct(int idProduct) {
        this.ProductId = idProduct;
    }

    public String getProductName() {
        return name;
    }

    public void setName(String nom) {
        this.name = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getPimage() {
		return imagePath ;
	}
	public void setPimage(String pimage) {
		this.imagePath  = pimage;
	}

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stock) {
        this.stockQuantity = stock;
    }
    
    // Method to reduce stock quantity when an order is placed
    public boolean reduceStock(int quantity) {
        if (this.stockQuantity >= quantity) {
            this.stockQuantity -= quantity;
            return true; // Stock reduced successfully
        }
        return false; // System.out.println("Not enough stock for the requested quantity.");
    }
    
    // Method to return product information as a string
   /* @Override
    public String toString() {
        return "Product{" +
                "idProduct=" + this.getIdProduct() +
                ", name='" + this.getProductName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", price=" + this.getPrice() +
                ", stockQuantity=" + this.getStockQuantity() +
                '}';
    }*/
    @Override
    public String toString() {
        return this.getProductName(); // Return only the product name
    }

 


}
