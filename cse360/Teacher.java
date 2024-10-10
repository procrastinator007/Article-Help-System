
package cse360;

public class Teacher extends User {
    private String[] classes;  // Add classes information

    public Teacher(String firstName, String middleName, String lastName, String username, String passwordHash, String email) {
        super(firstName, middleName, lastName, username, passwordHash, email);
    }

    @Override
    public String getRole() {
        return "Teacher";
    }
}
