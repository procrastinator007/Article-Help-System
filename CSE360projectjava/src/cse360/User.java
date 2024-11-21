package cse360;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class User {
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String role;
    private List<SpecialGroup> specialGroups;

    // Constructor
    public User(String firstName, String middleName, String lastName, String email, String username, String password, String role) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;

        // Initialize the specialGroups and add the "general" group with default settings
        this.specialGroups = new ArrayList<>();
        this.specialGroups.add(new SpecialGroup("general", role.equals("Admin"))); // Head admin is an admin in "general" group
    }

    // Getters
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public List<SpecialGroup> getSpecialGroups() { return specialGroups; }

    // Add a new special group to the user
    public void addSpecialGroup(String groupName, boolean isAdmin) {
        specialGroups.add(new SpecialGroup(groupName, isAdmin));
    }

    // Method to get a comma-separated list of group names
    public String getGroupNames() {
        return specialGroups.stream()
                .map(SpecialGroup::getGroupName)
                .collect(Collectors.joining(", "));
    }
    public List<String> getGroupNamesList() {
        List<String> groupNames = specialGroups.stream()
                    .map(SpecialGroup::getGroupName)
                    .collect(Collectors.toList());
        System.out.println("User groups: " + groupNames);
        return groupNames;
    }


    // Method to get admin status in each group as a comma-separated list
    public String getAdminStatus() {
        return specialGroups.stream()
                .map(group -> group.getGroupName() + ": " + (group.isAdmin() ? "Admin" : "User"))
                .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return firstName + " " + middleName + " " + lastName + " (" + role + ")";
    }

    // Nested class representing a special group
    public static class SpecialGroup {
        private String groupName;
        private boolean isAdmin;

        public SpecialGroup(String groupName, boolean isAdmin) {
            this.groupName = groupName;
            this.isAdmin = isAdmin;
        }

        public String getGroupName() { return groupName; }
        public boolean isAdmin() { return isAdmin; }
    }
}
