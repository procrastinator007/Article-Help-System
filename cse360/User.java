package cse360;
public abstract class User {
    protected String firstName;
    protected String middleName;
    protected String lastName;
    protected String username;
    protected String passwordHash;
    protected String email;

    public User(String firstName, String middleName, String lastName, String username, String passwordHash, String email) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFullName() {
        return firstName + " " + middleName + " " + lastName;
    }

    public abstract String getRole();
}
