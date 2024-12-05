package cse360;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.List;

public class PhaseThreeContinuityTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // Initialize the database before running the tests
        Database.initializeDatabase();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        // Close the database connection after all tests
        Database.close();
    }

    @Before
    public void setUp() throws Exception {
        // Clean up the database before each test
        List<User> users = Database.getAllUsers();
        for (User user : users) {
            Database.deleteUser(user.getUsername());
        }
    }

    @After
    public void tearDown() throws Exception {
        // Optional cleanup after each test if necessary
    }

    @Test
    public void testAddUserToDatabase() {
        // Test adding a user to the database
        User user = new User("John", "Doe", "Admin", "john.doe@example.com", "johndoe", "password123", "Admin");
        boolean result = Database.addUserToDatabase(user);

        assertTrue("User should be added successfully", result);
        User retrievedUser = Database.getUser("johndoe");
        assertNotNull("User should be retrievable from the database", retrievedUser);
        assertEquals("User's role should match", "Admin", retrievedUser.getRole());
    }

    @Test
    public void testRoleAssignmentAndRetrieval() {
        // Test assigning roles to users and retrieving them
        User admin = new User("Jane", "Smith", "Admin", "jane.smith@example.com", "janesmith", "password456", "Admin");
        User student = new User("Emily", "Brown", "Student", "emily.brown@example.com", "emilybrown", "password789", "Student");

        Database.addUserToDatabase(admin);
        Database.addUserToDatabase(student);

        List<String> admins = Database.getUsersByRole("Admin");
        List<String> students = Database.getUsersByRole("Student");

        assertTrue("Admin list should contain Jane", admins.contains("janesmith"));
        assertTrue("Student list should contain Emily", students.contains("emilybrown"));
    }

    @Test
    public void testDatabaseIsNotEmpty() {
        // Test if the database is not empty after adding a user
        User user = new User("Mark", "Taylor", "Teacher", "mark.taylor@example.com", "marktaylor", "password", "Teacher");
        Database.addUserToDatabase(user);

        assertFalse("Database should not be empty after adding a user", Database.isEmpty());
    }
}
