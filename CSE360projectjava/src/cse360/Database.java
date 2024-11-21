package cse360;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Database {
    private static final String DATABASE_FILE = "database.db";
    private static Connection connection = null;

    static {
        initializeDatabase();
    }

    // Initialize the database connection and create tables if needed
    private static void initializeDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);

            // Create users table with groups column instead of a separate groups table
            String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "role TEXT NOT NULL, " +
                    "firstName TEXT, " +
                    "middleName TEXT, " +
                    "lastName TEXT, " +
                    "email TEXT NOT NULL UNIQUE, " +
                    "groups TEXT DEFAULT 'general:false'" + // List of groups with admin status
                    ")";
            executeUpdate(createUserTable);

            String createInviteCodeTable = "CREATE TABLE IF NOT EXISTS invite_codes (" +
                    "code TEXT PRIMARY KEY)";
            executeUpdate(createInviteCodeTable);
            
            String createMessagesTable = "CREATE TABLE IF NOT EXISTS messages (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "from_user TEXT NOT NULL, " +
                    "to_user TEXT NOT NULL, " + // Represents groupName:true
                    "group_name TEXT NOT NULL, " +
                    "content TEXT NOT NULL, " +
                    "status TEXT DEFAULT 'received', " + // Status: received, work-in-progress, resolved
                    "FOREIGN KEY(from_user) REFERENCES users(username)" + // Only `from_user` references users
                    ")";
            executeUpdate(createMessagesTable);


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

    // Check if the database is empty
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

    // Method to add a new user to the database
    public static boolean addUserToDatabase(User user) {
        boolean isAdmin = user.getRole().equals("Admin") || isEmpty(); // First user is admin if database is empty
        String groups = "general:" + (isAdmin ? "true" : "false"); // Default group with admin status

        String insertUser = "INSERT INTO users (username, password, role, firstName, middleName, lastName, email, groups) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getMiddleName());
            pstmt.setString(6, user.getLastName());
            pstmt.setString(7, user.getEmail());
            pstmt.setString(8, groups);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding user to database: " + e.getMessage());
            return false;
        }
    }

    // Method to get the user ID by username
    public static int getUserId(String username) {
        String query = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user ID: " + e.getMessage());
        }
        return -1; // Return -1 if user ID is not found
    }
    
    // Method to add a user to a specific group
    public static boolean addUserToGroup(int userId, String groupName, boolean isAdmin) {
        String getGroupsQuery = "SELECT groups FROM users WHERE id = ?";
        String updateGroupsQuery = "UPDATE users SET groups = ? WHERE id = ?";
        try (PreparedStatement pstmtGet = connection.prepareStatement(getGroupsQuery);
             PreparedStatement pstmtUpdate = connection.prepareStatement(updateGroupsQuery)) {

            // Fetch current groups
            pstmtGet.setInt(1, userId);
            ResultSet rs = pstmtGet.executeQuery();
            if (rs.next()) {
                String currentGroups = rs.getString("groups");
                List<String> groupList = new ArrayList<>(Arrays.asList(currentGroups.split(",")));
                String newGroupEntry = groupName + ":" + (isAdmin ? "true" : "false");
                
                // Add the new group if it doesn't exist
                boolean groupExists = groupList.stream().anyMatch(g -> g.startsWith(groupName + ":"));
                if (!groupExists) {
                    groupList.add(newGroupEntry);
                    String updatedGroups = String.join(",", groupList);
                    pstmtUpdate.setString(1, updatedGroups);
                    pstmtUpdate.setInt(2, userId);
                    pstmtUpdate.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding user to group: " + e.getMessage());
            return false;
        }
    }
    
    public static List<User> getUsersInGroup(String groupName) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT firstName, middleName, lastName, email, username, role FROM users WHERE groups LIKE ?";
        
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db");
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "%" + groupName + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                User user = new User(
                    rs.getString("firstName"),
                    rs.getString("middleName"),
                    rs.getString("lastName"),
                    rs.getString("email"),
                    rs.getString("username"),
                    null, // Skip password
                    rs.getString("role")
                );
                users.add(user);
            }
        }
        return users;
    }
    
 // Method to delete a group and remove it from all users
    public static boolean deleteGroup(String groupName) {
        String getUsersQuery = "SELECT id, groups FROM users";
        String updateUserGroupsQuery = "UPDATE users SET groups = ? WHERE id = ?";

        try (PreparedStatement pstmtGetUsers = connection.prepareStatement(getUsersQuery);
             PreparedStatement pstmtUpdateUser = connection.prepareStatement(updateUserGroupsQuery)) {

            // Retrieve all users and their groups
            ResultSet rs = pstmtGetUsers.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("id");
                String groups = rs.getString("groups");

                if (groups != null && !groups.isEmpty()) {
                    String[] groupArray = groups.split(",");
                    StringBuilder updatedGroups = new StringBuilder();

                    for (String group : groupArray) {
                        // Skip the group being deleted
                        if (!group.startsWith(groupName + ":")) {
                            if (updatedGroups.length() > 0) {
                                updatedGroups.append(",");
                            }
                            updatedGroups.append(group);
                        }
                    }

                    // Update the user's groups in the database
                    pstmtUpdateUser.setString(1, updatedGroups.toString());
                    pstmtUpdateUser.setInt(2, userId);
                    pstmtUpdateUser.executeUpdate();
                }
            }

            return true; // Group deletion completed successfully for all users
        } catch (SQLException e) {
            System.err.println("Error deleting group: " + groupName + ". Details: " + e.getMessage());
            return false;
        }
    }


 // Method to retrieve users by their role (e.g., "Teacher" or "Student")
    public static List<String> getUsersByRole(String role) {
        List<String> users = new ArrayList<>();
        String query = "SELECT username FROM users WHERE role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users by role: " + e.getMessage());
        }
        return users;
    }

    // Method to retrieve users not in a specific group
    public static List<User> getUsersNotInGroup(String groupName) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT firstName, middleName, lastName, email, username, role FROM users WHERE groups NOT LIKE ?";
        
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db");
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "%" + groupName + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                User user = new User(
                    rs.getString("firstName"),
                    rs.getString("middleName"),
                    rs.getString("lastName"),
                    rs.getString("email"),
                    rs.getString("username"),
                    null, // Skip password
                    rs.getString("role")
                );
                users.add(user);
            }
        }
        return users;
    }


   

    // Method to remove a user from a specific group
    public static void removeUserFromGroup(String username, String groupName) throws SQLException {
        String query = "SELECT groups FROM users WHERE username = ?";
        
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db");
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String groups = rs.getString("groups");
                // Remove the specified group
                String updatedGroups = groups.replaceAll(",?\\s*" + groupName + ":(true|false)", "").trim();
                
                // Update the user's groups
                try (PreparedStatement updateStmt = connection.prepareStatement("UPDATE users SET groups = ? WHERE username = ?")) {
                    updateStmt.setString(1, updatedGroups);
                    updateStmt.setString(2, username);
                    updateStmt.executeUpdate();
                }
            }
        }
    }

    // Method to check if an admin user already exists
    public static boolean isExistingAdmin() {
        String query = "SELECT COUNT(*) AS total FROM users WHERE groups LIKE '%general:true%'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() && rs.getInt("total") > 0;
        } catch (SQLException e) {
            System.err.println("Error checking for existing admin: " + e.getMessage());
            return false;
        }
    }

    // Method to get a user by username
    public static User getUser(String username) {
        String selectUser = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectUser)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getString("firstName"),
                        rs.getString("middleName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                String[] groups = rs.getString("groups").split(",");
                for (String group : groups) {
                    String[] parts = group.split(":");
                    if (parts.length == 2) {
                        user.addSpecialGroup(parts[0], Boolean.parseBoolean(parts[1]));
                    }
                }
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
        }
        return null;
    }

    // Method to generate a new invite code
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
 // Add this method to Database.java
    public static ArrayList<User> getAllUsers() throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        String selectAllUsers = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectAllUsers)) {
            while (rs.next()) {
                User user = new User(
                        rs.getString("firstName"),
                        rs.getString("middleName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                String[] groups = rs.getString("groups").split(",");
                for (String group : groups) {
                    String[] parts = group.split(":");
                    if (parts.length == 2) {
                        user.addSpecialGroup(parts[0], Boolean.parseBoolean(parts[1]));
                    }
                }
                users.add(user);
            }
        }
        return users;
    }
 // Method to delete a user from the database by username
    public static boolean deleteUser(String username) {
        String deleteUserQuery = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteUserQuery)) {
            pstmt.setString(1, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Return true if a user was deleted successfully
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    public static List<String> getUserAdminGroups(String username) {
        List<String> adminGroups = new ArrayList<>();
        String query = "SELECT group_name FROM special_groups WHERE user_id = (SELECT id FROM users WHERE username = ?) AND is_admin = 1";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                adminGroups.add(rs.getString("group_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving admin groups for user: " + e.getMessage());
        }
        return adminGroups;
    }
    public static List<String> getUserSpecialGroups(String username) {
        List<String> specialGroups = new ArrayList<>();
        String query = "SELECT groups FROM users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String groupsField = rs.getString("groups"); // Get the `groups` column
                if (groupsField != null && !groupsField.isEmpty()) {
                    // Split and filter out `general` group
                    String[] groups = groupsField.split(",");
                    for (String group : groups) {
                        String groupName = group.split(":")[0]; // Get group name
                        if (!groupName.equalsIgnoreCase("general")) {
                            specialGroups.add(groupName);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving special groups for user: " + e.getMessage());
        }
        return specialGroups;
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
