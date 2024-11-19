package model;

//@Table(name = "e_Users")
import util.Role;

public abstract class User {
    private int userId; // auto-generated
    private String username;
    private String password;
    private Role role;
 // private Date add_date private Date update_date

    public User(String password, Role role) {
        this.password = password;
        this.role = role;
    }
    public User(String u_name, String psw, Role role) {
    	this.username = u_name;
    	this.password = psw;
    	this.role = role;
    }
    public User(int userid, String u_name, String psw, Role role) {
    	this.userId = userid;
    	this.username = u_name;
    	this.password = psw;
    	this.role = role;
    }

    // Getters and setters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    
    public void setUsername(String username) { this.username = username;  }
    public void setUserId(int userid) { this.userId = userid;  }
    public String setPassword(String psw) { return this.password = psw; }
    public Role setRole (Role role) {return this.role = role;}

    public abstract void displayInfo(); // Method to be overridden by Admin and Client
}
