package cse360;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.List;

public class HelpArticleGroupsTest {

    private ArticleDatabaseHandler dbHandler;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // Initialize the database for testing
        Database.initializeDatabase();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        // Close the database after all tests
        Database.close();
    }

    @Before
    public void setUp() throws Exception {
        // Initialize the ArticleDatabaseHandler and clean up the database
        dbHandler = new ArticleDatabaseHandler("test_main.db", "test_backup.db");

        // Clear articles table before each test
        dbHandler.getArticles().forEach(article -> dbHandler.deleteArticle(article.getUid()));
    }

    @After
    public void tearDown() throws Exception {
        // Close the database connections after each test
        dbHandler.closeConnections();
    }

    @Test
    public void testAddArticleToGroup() {
        // Create and save an article in a group
        Article article = new Article("uid1", "Eclipse IDE", "John Doe", "An article about Eclipse",
                "IDE, Java", "Eclipse content", "References", "Beginner", false, "IDE");
        dbHandler.saveArticle(article);

        // Retrieve the article and verify it belongs to the correct group
        Article retrievedArticle = dbHandler.getArticleByTitle("Eclipse IDE");
        assertNotNull("Article should be retrievable from the database", retrievedArticle);
        assertEquals("Group should be IDE", "IDE", retrievedArticle.getSpecialGroups());
    }

    @Test
  /*  public void testRetrieveArticlesByGroup() {
        // Add multiple articles to a group
        Article article1 = new Article("uid1", "Eclipse IDE", "John Doe", "Eclipse article",
                "Java, IDE", "Eclipse content", "References", "Intermediate", false, "IDE");
        Article article2 = new Article("uid2", "IntelliJ IDE", "Jane Doe", "IntelliJ article",
                "Java, IDE", "IntelliJ content", "References", "Advanced", false, "IDE");
        Article article3 = new Article("uid3", "Data Structures", "Alice", "Data Structures article",
                "DSA, Algorithms", "DS content", "References", "Beginner", false, "Algorithms");

        dbHandler.saveArticle(article1);
        dbHandler.saveArticle(article2);
        dbHandler.saveArticle(article3);

        // Retrieve articles by the group "IDE"
        List<Article> ideArticles = dbHandler.getArticlesByLevel("IDE");
        assertEquals("There should be 2 articles in the IDE group", 2, ideArticles.size());
    }
*/
 
    public void testArticleBelongsToMultipleGroups() {
        // Create an article that belongs to multiple groups
        Article article = new Article("uid4", "Eclipse IntelliJ Comparison", "John Doe",
                "Comparison of IDEs", "IDE, Java, IntelliJ, Eclipse",
                "Comparison content", "References", "Advanced", false, "IDE, Comparison");

        dbHandler.saveArticle(article);

        // Retrieve the article and verify its group memberships
        Article retrievedArticle = dbHandler.getArticleByTitle("Eclipse IntelliJ Comparison");
        assertNotNull("Article should be retrievable from the database", retrievedArticle);
        assertTrue("Article should belong to the IDE group",
                retrievedArticle.getSpecialGroups().contains("IDE"));
        assertTrue("Article should belong to the Comparison group",
                retrievedArticle.getSpecialGroups().contains("Comparison"));
    }
}
