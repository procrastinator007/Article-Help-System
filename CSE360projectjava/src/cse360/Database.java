package cse360;
import cse360.Database;


import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

public class Database {
    // Constant representing the file name where the data is stored
    private static final String DATA_FILE = "database.ser";
    
    // List to store user objects
    private static ArrayList<User> users = new ArrayList<>();
    
    // String to store the one-time invite code
    private static String inviteCode;

    // Static block to initialize the database by loading existing data from the file
    static {
        // Load data from the file when the class is loaded
        loadData();
    }

    // Method to load users and invite code from the serialized file
    private static void loadData() {
        File file = new File(DATA_FILE);
        // Check if the file exists and is not empty
        if (!file.exists() || file.length() == 0) {
            System.out.println("No existing data found, starting fresh.");
            saveData();  // Save an empty database if no file exists
            return;
        }

        // Try to read the data from the file
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            // Deserialize the list of users and invite code
            users = (ArrayList<User>) ois.readObject();
            inviteCode = (String) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    // Method to save users and invite code to the serialized file
    private static void saveData() {
        // Try to write the data to the file
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            // Serialize the list of users and invite code
            oos.writeObject(users);
            oos.writeObject(inviteCode);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    // Method to add a new user to the database
    public static void addUser(User user) {
        users.add(user);  // Add the user to the list
        saveData();  // Save data after adding a new user to persist changes
    }

    // Method to delete a user from the database by username
    public static void deleteUser(String username) {
        // Remove user if the username matches
        users.removeIf(user -> user.getUsername().equals(username));
        saveData();  // Save data after deleting the user to persist changes
    }

    // Method to get a user by username for login validation
    public static User getUser(String username) {
        // Iterate through the list of users to find a match
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;  // Return the user if found
            }
        }
        return null;  // Return null if the user is not found
    }

    // Method to get a list of all users (useful for admin purposes)
    public static ArrayList<User> getAllUsers() {
        return new ArrayList<>(users);  // Return a copy of the user list
    }

    // Method to generate a new one-time invite code
    public static String generateInviteCode() {
        inviteCode = UUID.randomUUID().toString();  // Generate a new unique code
        saveData();  // Save the new invite code to persist it
        return inviteCode;
    }

    // Method to validate and consume the invite code
    public static boolean validateInviteCode(String code) {
        // Check if the invite code matches the stored code
        if (inviteCode != null && inviteCode.equals(code)) {
            inviteCode = null;  // Consume the code by setting it to null
            saveData();  // Save the change to persist the update
            return true;  // Return true if the code was valid
        }
        return false;  // Return false if the code was invalid
    }

    // Method to check if the database is empty (no users present)
    public static boolean isEmpty() {
        return users.isEmpty();  // Return true if the user list is empty
    }
}
