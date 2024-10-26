package cse360;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class Database {
    // SQLite database file path
    private static final String DATABASE_FILE = "database.db";
    private static Connection connection = null;

    static {
        // Initialize the database connection and tables
        initializeDatabase();
    }

    // Method to initialize the SQLite database and tables if not exists
    private static void initializeDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);

            // Create Users table if it does not exist
            String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "role TEXT NOT NULL, " +
                    "firstName TEXT, " +
                    "middleName TEXT, " +
                    "lastName TEXT, " +
                    "email TEXT NOT NULL UNIQUE" +
                    ")";
            executeUpdate(createUserTable);

            // Create InviteCodes table if it does not exist
            String createInviteCodeTable = "CREATE TABLE IF NOT EXISTS invite_codes (" +
                    "code TEXT PRIMARY KEY)";
            executeUpdate(createInviteCodeTable);

        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    // Execute an update query
    private static void executeUpdate(String query) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
        }
    }

    // Method to add a new user to the database (this matches addUserToDatabase in NewUser.java)
    public static boolean addUserToDatabase(User user) {
        String insertUser = "INSERT INTO users (username, password, role, firstName, middleName, lastName, email) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getMiddleName());
            pstmt.setString(6, user.getLastName());
            pstmt.setString(7, user.getEmail());
            pstmt.executeUpdate();
            return true; // Return true if the user was added successfully
        } catch (SQLException e) {
            System.err.println("Error adding user to database: " + e.getMessage());
            return false;
        }
    }

    // Method to check if an admin user already exists in the database
    public static boolean isExistingAdmin() {
        String query = "SELECT COUNT(*) AS total FROM users WHERE role = 'Admin'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("total") > 0; // Returns true if an admin exists
            }
        } catch (SQLException e) {
            System.err.println("Error checking for existing admin: " + e.getMessage());
        }
        return false;
    }

    // Method to delete a user from the database by username
    public static void deleteUser(String username) {
        String deleteUser = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteUser)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    // Method to get a user by username for login validation
    public static User getUser(String username) {
        String selectUser = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectUser)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("firstName"),
                        rs.getString("middleName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
        }
        return null;
    }

    // Method to get a list of all users (useful for admin purposes)
    public static ArrayList<User> getAllUsers() throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        String selectAllUsers = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectAllUsers)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("firstName"),
                        rs.getString("middleName"),
                        rs.getString("lastName"),
                        rs.getString("email")
                ));
            }
        }
        return users;
    }


    // Method to generate a new one-time invite code
    public static String generateInviteCode() throws SQLException {
        String code = UUID.randomUUID().toString();
        String insertCode = "INSERT OR REPLACE INTO invite_codes (code) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertCode)) {
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        }
        return code;
    }


    // Method to validate and consume the invite code
    public static boolean validateInviteCode(String code) {
        String selectCode = "SELECT * FROM invite_codes WHERE code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectCode)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                deleteInviteCode(code);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error validating invite code: " + e.getMessage());
        }
        return false;
    }

    // Delete invite code after use
    private static void deleteInviteCode(String code) {
        String deleteCode = "DELETE FROM invite_codes WHERE code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteCode)) {
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting invite code: " + e.getMessage());
        }
    }

    // Method to check if the database is empty (no users present)
    public static boolean isEmpty() {
        String countUsers = "SELECT COUNT(*) AS total FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(countUsers)) {
            if (rs.next()) {
                return rs.getInt("total") == 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if database is empty: " + e.getMessage());
        }
        return true;
    }

    // Close database connection
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
