package cse360;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

public class Database {
    private static final String DATA_FILE = "database.ser";
    private static ArrayList<User> users = new ArrayList<>();
    private static String inviteCode;

    static {
        // Load data from file at the start
        loadData();
    }

    // Load users and invite code from the serialized file
    private static void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists() || file.length() == 0) {
            System.out.println("No existing data found, starting fresh.");
            saveData();  // Save an empty database if no file exists
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            users = (ArrayList<User>) ois.readObject();
            inviteCode = (String) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    // Save users and invite code to the serialized file
    private static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
            oos.writeObject(inviteCode);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    // Add a new user to the database
    public static void addUser(User user) {
        users.add(user);
        saveData();  // Save data after adding a new user
    }

    // Delete a user from the database
    public static void deleteUser(String username) {
        users.removeIf(user -> user.getUsername().equals(username));
        saveData();  // Save data after deleting the user
    }

    // Get user by username for login validation
    public static User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;  // Return null if user not found
    }

    // Method to get all users (useful for admin viewing purposes)
    public static ArrayList<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    // Generate a one-time invite code
    public static String generateInviteCode() {
        inviteCode = UUID.randomUUID().toString();
        saveData();  // Save the new invite code
        return inviteCode;
    }

    // Validate and consume the invite code
    public static boolean validateInviteCode(String code) {
        if (inviteCode != null && inviteCode.equals(code)) {
            inviteCode = null;  // Consume the code
            saveData();  // Save the change
            return true;
        }
        return false;
    }

    // Check if the database is empty
    public static boolean isEmpty() {
        return users.isEmpty();
    }
}
