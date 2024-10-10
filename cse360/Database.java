
package cse360;

import java.io.*;
import java.util.ArrayList;

public class Database {
    private static final String DATA_FILE = "database.ser";
    private static ArrayList<Admin> admins = new ArrayList<>();
    private static ArrayList<Teacher> teachers = new ArrayList<>();
    private static ArrayList<Student> students = new ArrayList<>();

    static {
        // Load data from file at the start
        loadData();
    }

    // Get all users for Admin to view
    public static ArrayList<User> getAllUsers() {
        ArrayList<User> allUsers = new ArrayList<>();
        allUsers.addAll(admins);
        allUsers.addAll(teachers);
        allUsers.addAll(students);
        return allUsers;
    }

    // Check if the database is empty
    public static boolean isEmpty() {
        return admins.isEmpty() && teachers.isEmpty() && students.isEmpty();
    }

    // Add user to the correct role-based database
    public static void addUser(User user) {
        if (user instanceof Admin) {
            admins.add((Admin) user);
        } else if (user instanceof Teacher) {
            teachers.add((Teacher) user);
        } else if (user instanceof Student) {
            students.add((Student) user);
        }
        saveData();  // Save data after every update
    }

    // Delete user
    public static void deleteUser(String username) {
        admins.removeIf(admin -> admin.getUsername().equals(username));
        teachers.removeIf(teacher -> teacher.getUsername().equals(username));
        students.removeIf(student -> student.getUsername().equals(username));
        saveData();  // Save data after every update
    }

    // Get user by username for login
    public static User getUser(String username) {
        for (Admin admin : admins) {
            if (admin.getUsername().equals(username)) return admin;
        }
        for (Teacher teacher : teachers) {
            if (teacher.getUsername().equals(username)) return teacher;
        }
        for (Student student : students) {
            if (student.getUsername().equals(username)) return student;
        }
        return null;
    }

    // Save data to file
    private static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(admins);
            oos.writeObject(teachers);
            oos.writeObject(students);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    // Load data from file
    private static void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists() || file.length() == 0) {
            System.out.println("No existing data found, starting fresh.");
            saveData();  // Save an empty database if no file exists
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            admins = (ArrayList<Admin>) ois.readObject();
            teachers = (ArrayList<Teacher>) ois.readObject();
            students = (ArrayList<Student>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }
}
