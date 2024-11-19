package model;

//@Table(name = "e_Admin")
import util.Role;

public class Admin extends User {
	
	// private Date add_date private Date update_date
    public Admin(String username, String password) {
        super(username, password, Role.admin);
    }
    
    

    @Override
    public void displayInfo() {
        System.out.println("Admin Username: " + getUsername());
       
    }
    
}
