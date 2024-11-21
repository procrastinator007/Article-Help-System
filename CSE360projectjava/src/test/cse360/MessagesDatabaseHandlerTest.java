package cse360;

import org.junit.*;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class MessagesDatabaseHandlerTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // Set up the test database
        Connection connection = MessagesDatabaseHandler.getConnection();
        try (Statement stmt = connection.createStatement()) {
            // Create the messages table for testing
            String createMessagesTable = "CREATE TABLE IF NOT EXISTS messages (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "from_user TEXT NOT NULL, " +
                    "to_user TEXT NOT NULL, " +
                    "group_name TEXT NOT NULL, " +
                    "content TEXT NOT NULL, " +
                    "status TEXT DEFAULT 'received')";
            stmt.executeUpdate(createMessagesTable);

            // Clean up any existing test data
            stmt.executeUpdate("DELETE FROM messages");
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        // Clean up the database after all tests
        Connection connection = MessagesDatabaseHandler.getConnection();
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM messages");
        }
    }

    @Before
    public void setUp() throws Exception {
        // Ensure the database is clean before each test
        Connection connection = MessagesDatabaseHandler.getConnection();
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM messages");
        }
    }

    @Test
    public void testAddMessage() {
        boolean result = MessagesDatabaseHandler.addMessage("User1", "User2", "TestGroup", "Hello, this is a test message!");
        assertTrue("Message should be added successfully", result);

        List<String> messages = MessagesDatabaseHandler.getMessagesFromUser("User1");
        assertEquals("There should be one message from User1", 1, messages.size());
        assertTrue("Message content should match", messages.get(0).contains("Hello, this is a test message!"));
    }

    @Test
    public void testGetMessagesFromUser() {
        MessagesDatabaseHandler.addMessage("User1", "User2", "TestGroup", "Message 1");
        MessagesDatabaseHandler.addMessage("User1", "User3", "TestGroup", "Message 2");

        List<String> messages = MessagesDatabaseHandler.getMessagesFromUser("User1");
        assertEquals("There should be two messages from User1", 2, messages.size());
        assertTrue("First message content should match", messages.get(0).contains("Message 2"));
        assertTrue("Second message content should match", messages.get(1).contains("Message 1"));
    }

    @Test
    public void testDeleteMessage() {
        MessagesDatabaseHandler.addMessage("User1", "User2", "TestGroup", "Message to be deleted");
        int messageId = MessagesDatabaseHandler.getMessageId("User1", "User2", "Message to be deleted");

        boolean result = MessagesDatabaseHandler.deleteMessage(messageId);
        assertTrue("Message should be deleted successfully", result);

        List<String> messages = MessagesDatabaseHandler.getMessagesFromUser("User1");
        assertTrue("There should be no messages from User1 after deletion", messages.isEmpty());
    }

    @Test
    public void testUpdateMessageStatus() {
        MessagesDatabaseHandler.addMessage("User1", "User2", "TestGroup", "Message for status update");
        int messageId = MessagesDatabaseHandler.getMessageId("User1", "User2", "Message for status update");

        boolean result = MessagesDatabaseHandler.updateMessageStatus(messageId, "resolved");
        assertTrue("Message status should be updated successfully", result);

        List<String> messages = MessagesDatabaseHandler.getMessagesFromUser("User1");
        assertTrue("Message status should match", messages.get(0).contains("Status: resolved"));
    }

    @Test
    public void testGetAllMessagesFormatted() {
        MessagesDatabaseHandler.addMessage("User1", "User2:admin", "TestGroup", "Message 1");
        MessagesDatabaseHandler.addMessage("User3", "User2:admin", "TestGroup", "Message 2");

        List<String> messages = MessagesDatabaseHandler.getAllMessagesFormatted("User2");
        assertEquals("There should be two messages for User2", 2, messages.size());
        assertTrue("First message content should match", messages.get(0).contains("Message 2"));
        assertTrue("Second message content should match", messages.get(1).contains("Message 1"));
    }
}
