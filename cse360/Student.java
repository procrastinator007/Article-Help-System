
package cse360;

public class Student extends User {
    private String className;  // Add class information

    public Student(String firstName, String middleName, String lastName, String username, String passwordHash, String email, String inviteCode) {
        super(firstName, middleName, lastName, username, passwordHash, email);
    }

    @Override
    public String getRole() {
        return "Student";
    }
}
