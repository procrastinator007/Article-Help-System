package cse360;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class InstructorRoleEnhancement {

    private ArticleDatabaseHandler dbHandler;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // Initialize database for testing before all tests
        System.out.println("Setting up before all tests...");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        // Cleanup resources after all tests
        System.out.println("Tearing down after all tests...");
    }

    @Before
    public void setUp() throws Exception {
        // Initialize the ArticleDatabaseHandler with test database files
        dbHandler = new ArticleDatabaseHandler("test_main.db", "test_backup.db");
        
        // Clear any existing articles in the database
        for (Article article : dbHandler.getArticles()) {
            dbHandler.deleteArticle(article.getUid());
        }
        System.out.println("Setting up before each test...");
    }

    @After
    public void tearDown() throws Exception {
        // Close database connections after each test
        dbHandler.closeConnections();
        System.out.println("Tearing down after each test...");
    }

    @Test
    public void testCreateArticle() {
        // Inputs: Create a new article
        Article newArticle = new Article(
                "inheritance-uid",
                "Inheritance",
                "Instructor A",
                "Understanding inheritance in OOP.",
                "OOP,Inheritance",
                "Inheritance is a fundamental principle in OOP.",
                "Bibliography here.",
                "Intermediate",
                false,
                "general"
        );

        dbHandler.saveArticle(newArticle);

        // Outputs: Verify the article is saved
        Article retrievedArticle = dbHandler.getArticleByTitle("Inheritance");
        assertNotNull("Article should be created and retrievable from the database", retrievedArticle);
        assertEquals("Title should match", "Inheritance", retrievedArticle.getTitle());
        System.out.println("testCreateArticle passed!");
    }

    @Test
    public void testUpdateArticle() {
        // Inputs: Create a new article and update its content
        Article article = new Article(
                "inheritance-uid",
                "Inheritance",
                "Instructor A",
                "Understanding inheritance in OOP.",
                "OOP,Inheritance",
                "Inheritance is a fundamental principle in OOP.",
                "Bibliography here.",
                "Intermediate",
                false,
                "general"
        );
        dbHandler.saveArticle(article);

        // Update article content
        article.setBody("Updated body for Inheritance article.");
        dbHandler.saveArticle(article);

        // Outputs: Verify the article is updated
        Article updatedArticle = dbHandler.getArticleByTitle("Inheritance");
        assertNotNull("Updated article should exist in the database", updatedArticle);
        assertEquals("Updated body should match", "Updated body for Inheritance article.", updatedArticle.getBody());
        System.out.println("testUpdateArticle passed!");
    }

    @Test
    public void testDeleteArticle() {
        // Inputs: Create and delete an article
        Article article = new Article(
                "inheritance-uid",
                "Inheritance",
                "Instructor A",
                "Understanding inheritance in OOP.",
                "OOP,Inheritance",
                "Inheritance is a fundamental principle in OOP.",
                "Bibliography here.",
                "Intermediate",
                false,
                "general"
        );
        dbHandler.saveArticle(article);

        // Delete the article
        dbHandler.deleteArticle(article.getUid());

        // Outputs: Verify the article no longer exists
        Article deletedArticle = dbHandler.getArticleByTitle("Inheritance");
        assertNull("Article should be deleted from the database", deletedArticle);
        System.out.println("testDeleteArticle passed!");
    }
}
