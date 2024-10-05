package cse360;
public class Admin extends User {
    public Admin(String firstName, String middleName, String lastName, String username, String passwordHash, String email) {
        super(firstName, middleName, lastName, username, passwordHash, email);
    }
    
    @Override
    public String getRole() {
        return "Admin";
    }
}
