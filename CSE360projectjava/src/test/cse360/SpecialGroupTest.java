package cse360;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.List;
import java.sql.SQLException;

public class SpecialGroupTest {
    private ArticleDatabaseHandler handler;

    @Before
    public void setUp() {
        // Initialize the handler
        handler = new ArticleDatabaseHandler();
    }

    @After
    public void tearDown() throws SQLException {
        // Clean up articles to ensure no interference between tests
        List<Article> articles = handler.getArticles();
        for (Article article : articles) {
            handler.deleteArticle(article.getUid());
        }

        // Clean up users from the database
        List<User> users = Database.getAllUsers();
        for (User user : users) {
            Database.deleteUser(user.getUsername());
        }
    }

    // ======================= ARTICLE RELEVANT TESTS ======================= //

    @Test
    public void testAssignArticleToSpecialGroup() {
        // Passing test case
        Article article = new Article(
                "Special Group Article",
                "Author Name",
                "Abstract Info",
                "Keyword1, Keyword2",
                "Content Body",
                "Bibliography",
                "Intermediate",
                "SpecialGroup"
        );
        handler.saveArticle(article);

        // Retrieve and verify
        Article retrieved = handler.getArticleByTitle("Special Group Article");
        assertNotNull("Article should not be null after saving", retrieved);
        assertEquals("Title should match", "Special Group Article", retrieved.getTitle());
        assertEquals("Special group should match", "SpecialGroup", retrieved.getSpecialGroups());

        System.out.println("testAssignArticleToSpecialGroup passed.");
    }


    // ======================= USER RELEVANT TESTS ======================= //

    @Test
    public void testAddUserToSpecialGroup() {
        // Passing test case
        User user = new User("John", "M", "Doe", "john.doe@example.com", "johndoe", "password123", "Student");
        boolean userAdded = Database.addUserToDatabase(user);
        assertTrue("User should be added to the database", userAdded);

        int userId = Database.getUserId("johndoe");
        boolean groupAdded = Database.addUserToGroup(userId, "SpecialGroup", true);
        assertTrue("User should be added to the special group", groupAdded);

        List<String> specialGroups = Database.getUserSpecialGroups("johndoe");
        assertTrue("User should belong to 'SpecialGroup'", specialGroups.contains("SpecialGroup"));

        System.out.println("testAddUserToSpecialGroup passed.");
    }


    @Test
    public void testGetUsersBySpecialGroup() throws Exception {
        // Passing test case
        User user1 = new User("Alice", null, null, "alice@example.com", "alice", "password1", "Student");
        Database.addUserToDatabase(user1);
        int userId1 = Database.getUserId("alice");
        Database.addUserToGroup(userId1, "SpecialGroup", false);

        List<User> specialGroupUsers = Database.getUsersInGroup("SpecialGroup");
        assertEquals("There should be one user in the SpecialGroup", 1, specialGroupUsers.size());
        assertEquals("The user in the group should be Alice", "alice", specialGroupUsers.get(0).getUsername());

        System.out.println("testGetUsersBySpecialGroup passed.");
    }

    @Test
    public void testFailGetUsersBySpecialGroup() throws Exception {
        // Failing test case: Retrieve users from a non-existent group
        List<User> specialGroupUsers = Database.getUsersInGroup("NonExistentGroup");

        // Assert failure
        assertTrue("No users should be returned for a non-existent group", specialGroupUsers.isEmpty());
        System.out.println("testFailGetUsersBySpecialGroup failed as expected.");
    }
}
