package model;

////@Table(name = "e_Client")
import util.Role;

public class Client extends User {
	private int clientId;
    private String email;
    private String phone;
    private String address;
 // private Date add_date private Date update_date

    public Client(String username, String password, String email, String phone, String address) {
        super(username,password, Role.client); // Set role using Role enum
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
    public Client(int clientId, String username, String password, String email, String phone, String address) {
        super(username,password, Role.client); // Set role using Role enum
        this.clientId = clientId;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
 // Constructor to initialize all attributes
    public Client(int clientId, int userId, String username, String password, String email, String phone, String address) {
        super(userId, username, password, Role.client); // Call the User constructor
        this.clientId = clientId;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    // Getters and setters
    public int getClientId() {return clientId;}
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    
    public void setClientId(int clientId) {  this.clientId = clientId;  }
    public void setEmail(String email) { this.email = email;  }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public void displayInfo() {
        System.out.println("Client Username: " + getUsername());
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);
        System.out.println("Address: " + address);
    }
}
