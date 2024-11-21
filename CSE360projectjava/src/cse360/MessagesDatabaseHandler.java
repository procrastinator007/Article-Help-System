package cse360;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessagesDatabaseHandler {

    private static final String DATABASE_FILE = "database.db";
    private static Connection connection = null;

    // Method to get a database connection
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);
            }
        } catch (SQLException e) {
            System.err.println("Error establishing database connection: " + e.getMessage());
        }
        return connection;
    }

    // Method to add a new message
    public static boolean addMessage(String fromUser, String toUser, String groupName, String content) {
        String insertMessage = "INSERT INTO messages (from_user, to_user, group_name, content, status) " +
                               "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(insertMessage)) {
            pstmt.setString(1, fromUser);
            pstmt.setString(2, toUser ); // Ensure :admin suffix
            pstmt.setString(3, groupName);
            pstmt.setString(4, content);
            pstmt.setString(5, "received"); // Default status
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding message: " + e.getMessage());
            return false;
        }
    }

    
    public static List<String> getMessagesFromUser(String fromUser) {
        List<String> formattedMessages = new ArrayList<>();
        String query = "SELECT from_user, to_user, content, status " +
                       "FROM messages WHERE from_user = ? " +
                       "ORDER BY id DESC";

        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // Set the parameter for the from_user filter
            pstmt.setString(1, fromUser);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String sender = rs.getString("from_user");
                String recipient = rs.getString("to_user");
                String content = rs.getString("content");
                String status = rs.getString("status");

                String formattedMessage = "From: " + sender + "\n" +
                                          "To: " + recipient + "\n" +
                                          "Message: " + content + "\n" +
                                          "Status: " + (status != null ? status : "received");

                formattedMessages.add(formattedMessage);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving and formatting messages from user " + fromUser + ": " + e.getMessage());
        }

        return formattedMessages;
    }

    public static boolean deleteMessage(int messageId) {
        String deleteQuery = "DELETE FROM messages WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, messageId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Return true if a message was deleted
        } catch (SQLException e) {
            System.err.println("Error deleting message: " + e.getMessage());
            return false;
        }
    }

    // Method to retrieve all messages in a formatted string
    public static List<String> getAllMessagesFormatted(String groupName) {
        List<String> formattedMessages = new ArrayList<>();
        String query = "SELECT from_user, to_user, content, status " +
                       "FROM messages WHERE to_user = ? " +
                       "ORDER BY id DESC";

        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // Ensure groupName includes ":admin"
            pstmt.setString(1, groupName + ":admin");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String fromUser = rs.getString("from_user");
                String toUser = rs.getString("to_user");
                String content = rs.getString("content");
                String status = rs.getString("status");

                String formattedMessage = "From: " + fromUser + "\n" +
                                          "To: " + toUser + "\n" +
                                          "Message: " + content + "\n" +
                                          "Status: " + (status != null ? status : "received");

                formattedMessages.add(formattedMessage);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving and formatting messages for group " + groupName + ": " + e.getMessage());
        }

        return formattedMessages;
    }



    
    public static boolean updateMessageStatus(int messageId, String status) {
        String updateStatus = "UPDATE messages SET status = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(updateStatus)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, messageId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating message status: " + e.getMessage());
            return false;
        }
    }
    
    public static int getMessageId(String fromUser, String toUser, String content) {
        String query = "SELECT id FROM messages WHERE from_user = ? AND to_user = ? AND content = ?";
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, fromUser);
            pstmt.setString(2, toUser);
            pstmt.setString(3, content);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id"); // Return the message ID
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving message ID: " + e.getMessage());
        }
        return -1; // Return -1 if the message ID is not found
    }
}
