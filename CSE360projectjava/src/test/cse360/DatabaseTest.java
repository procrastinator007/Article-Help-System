package cse360;

import org.junit.*;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;


public class DatabaseTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // Initialize the database for testing
        Database.initializeDatabase();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        // Close database after testing
        Database.close();
    }

    @Before
    public void setUp() throws Exception {
        // Ensure the database is clean before each test
        List<User> users = Database.getAllUsers();
        for (User user : users) {
            Database.deleteUser(user.getUsername());
        }
    }
    

    @Test
    public void testAddUserToDatabase() {
        User user = new User("John", "M", "Doe", "john.doe@example.com", "johndoe", "password123", "Student");
        boolean result = Database.addUserToDatabase(user);

        assertTrue("User should be added to the database successfully", result);
        User retrievedUser = Database.getUser("johndoe");
        assertNotNull("User should be retrievable from the database", retrievedUser);
        assertEquals("User's first name should match", "John", retrievedUser.getFirstName());
        assertEquals("User's email should match", "john.doe@example.com", retrievedUser.getEmail());
    }

    @Test
    public void testIsEmpty() {
        // Database should be empty after setup
        assertTrue("Database should be empty initially", Database.isEmpty());

        // Add a user and check again
        User user = new User("Admin", null, null, "admin@example.com", "admin", "adminpass", "Admin");
        Database.addUserToDatabase(user);
        assertFalse("Database should not be empty after adding a user", Database.isEmpty());
    }

    @Test
    public void testGetUser() {
        User user = new User("Jane", "A", "Smith", "jane.smith@example.com", "janesmith", "password456", "Teacher");
        Database.addUserToDatabase(user);

        User retrievedUser = Database.getUser("janesmith");
        assertNotNull("User should be retrievable from the database", retrievedUser);
        assertEquals("Username should match", "janesmith", retrievedUser.getUsername());
        assertEquals("Role should match", "Teacher", retrievedUser.getRole());
    }

    @Test
    public void testAddUserToGroup() {
        User user = new User("Emily", null, null, "emily@example.com", "emily", "password789", "Student");
        Database.addUserToDatabase(user);

        int userId = Database.getUserId("emily");
        boolean result = Database.addUserToGroup(userId, "CS360", true);

        assertTrue("User should be added to the group successfully", result);
        List<String> specialGroups = Database.getUserSpecialGroups("emily");
        assertTrue("User should belong to the CS360 group", specialGroups.contains("CS360"));
    }

    @Test
    public void testDeleteGroup() {
        User user = new User("Mark", null, null, "mark@example.com", "mark", "password999", "Teacher");
        Database.addUserToDatabase(user);

        int userId = Database.getUserId("mark");
        Database.addUserToGroup(userId, "Math101", false);

        boolean result = Database.deleteGroup("Math101");
        assertTrue("Group should be deleted successfully", result);

        List<String> specialGroups = Database.getUserSpecialGroups("mark");
        assertFalse("User should no longer belong to Math101 group", specialGroups.contains("Math101"));
    }

    @Test
    public void testDeleteUser() {
        User user = new User("Charlie", null, null, "charlie@example.com", "charlie", "password123", "Student");
        Database.addUserToDatabase(user);

        boolean result = Database.deleteUser("charlie");
        assertTrue("User should be deleted successfully", result);

        User deletedUser = Database.getUser("charlie");
        assertNull("Deleted user should not exist in the database", deletedUser);
    }

    @Test
    public void testGetUsersByRole() {
        User student = new User("Student1", null, null, "student1@example.com", "student1", "password1", "Student");
        User teacher = new User("Teacher1", null, null, "teacher1@example.com", "teacher1", "password2", "Teacher");

        Database.addUserToDatabase(student);
        Database.addUserToDatabase(teacher);

        List<String> students = Database.getUsersByRole("Student");
        assertTrue("Students list should contain Student1", students.contains("student1"));

        List<String> teachers = Database.getUsersByRole("Teacher");
        assertTrue("Teachers list should contain Teacher1", teachers.contains("teacher1"));
    }

    @Test
    public void testValidateInviteCode() throws SQLException {
        String inviteCode = Database.generateInviteCode();

        boolean isValid = Database.validateInviteCode(inviteCode);
        assertTrue("Invite code should be valid", isValid);

        boolean isInvalid = Database.validateInviteCode(inviteCode);
        assertFalse("Invite code should not be valid after being consumed", isInvalid);
    }

    @Test
    public void testGetUsersNotInGroup() throws SQLException {
        User user1 = new User("Alice", null, null, "alice@example.com", "alice", "pass1", "Student");
        User user2 = new User("Bob", null, null, "bob@example.com", "bob", "pass2", "Student");

        Database.addUserToDatabase(user1);
        Database.addUserToDatabase(user2);

        int userId1 = Database.getUserId("alice");
        Database.addUserToGroup(userId1, "CS360", false);

        List<User> usersNotInGroup = Database.getUsersNotInGroup("CS360");
        assertEquals("Only one user should not be in the group", 1, usersNotInGroup.size());
        assertEquals("User not in the group should be Bob", "bob", usersNotInGroup.get(0).getUsername());
    }
}
