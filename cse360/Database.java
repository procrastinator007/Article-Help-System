package cse360;
import java.util.ArrayList;

public class Database {
    private static ArrayList<Admin> admins = new ArrayList<>();
    private static ArrayList<Teacher> teachers = new ArrayList<>();
    private static ArrayList<Student> students = new ArrayList<>();

    // Get all users for Admin to view
    public static ArrayList<User> getAllUsers() {
        ArrayList<User> allUsers = new ArrayList<>();
        allUsers.addAll(admins);
        allUsers.addAll(teachers);
        allUsers.addAll(students);
        return allUsers;
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
    }

    // Delete user
    public static void deleteUser(String username) {
        admins.removeIf(admin -> admin.getUsername().equals(username));
        teachers.removeIf(teacher -> teacher.getUsername().equals(username));
        students.removeIf(student -> student.getUsername().equals(username));
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
        return null; // User not found
    }

    // Check if the database is empty (first registration)
    public static boolean isEmpty() {
        return admins.isEmpty() && teachers.isEmpty() && students.isEmpty();
    }
}
