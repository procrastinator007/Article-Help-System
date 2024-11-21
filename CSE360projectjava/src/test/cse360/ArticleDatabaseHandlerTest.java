package cse360;

import static org.junit.Assert.*;

import org.junit.*;
import java.util.List;

public class ArticleDatabaseHandlerTest {
    private ArticleDatabaseHandler handler;

    @Before
    public void setUp() {
        handler = new ArticleDatabaseHandler();
    }

    @After
    public void tearDown() {
        // Clean up the database to ensure tests don't interfere with each other
        List<Article> articles = handler.getArticles();
        for (Article article : articles) {
            handler.deleteArticle(article.getUid());
        }
    }

    @Test
    public void testSaveAndRetrieveArticle() {
        // Create a test article
        Article article = new Article(
                "Test Title",
                "Test Author",
                "Test Abstract",
                "Keyword1, Keyword2",
                "Test Body",
                "Test Bibliography",
                "Intermediate",
                "general"
        );

        // Save the article
        handler.saveArticle(article);

        // Retrieve the article by title
        Article retrieved = handler.getArticleByTitle("Test Title");

        // Assertions to verify the article was saved and retrieved correctly
        assertNotNull("Article should not be null", retrieved);
        assertEquals("Title should match", "Test Title", retrieved.getTitle());
        assertEquals("Author should match", "Test Author", retrieved.getAuthor());
        assertEquals("Level should match", "Intermediate", retrieved.getLevel());
        assertEquals("Special Groups should match", "general", retrieved.getSpecialGroups());
    }

    @Test
    public void testGetArticles() {
        // Verify initial state
        assertTrue("Articles list should be empty initially", handler.getArticles().isEmpty());

        // Add an article
        Article article = new Article(
                "Another Title",
                "Another Author",
                "Another Abstract",
                "Another Keyword",
                "Another Body",
                "Another Bibliography",
                "Beginner",
                "group1"
        );
        handler.saveArticle(article);

        // Verify the article list is not empty
        List<Article> articles = handler.getArticles();
        assertFalse("Articles list should not be empty", articles.isEmpty());
        assertEquals("Articles list should contain one article", 1, articles.size());
        assertEquals("First article title should match", "Another Title", articles.get(0).getTitle());
    }

    @Test
    public void testDeleteArticle() {
        // Create a test article
        Article article = new Article(
            "Delete Test", 
            "Test Author", 
            "Test Abstract", 
            "Test Keywords", 
            "Test Body", 
            "Test References", 
            "Beginner", 
            "general"
        );

        // Save the article
        handler.saveArticle(article);

        // Verify the article is saved
        Article retrieved = handler.getArticleByTitle("Delete Test");
        assertNotNull("Article should exist before deletion", retrieved);

        // Delete the article
        handler.deleteArticle(retrieved.getUid());

        // Verify the article is deleted
        Article deletedArticle = handler.getArticleByTitle("Delete Test");
        assertNull("Article should not exist after deletion", deletedArticle);
    }

            @Test
            public void testUpdateArticle() {
                // Add an article
                Article article = new Article(
                        "Update Test",
                        "Update Author",
                        "Update Abstract",
                        "Update Keyword",
                        "Update Body",
                        "Update Bibliography",
                        "Advanced",
                        "group3"
                );
                handler.saveArticle(article);

                // Retrieve and update the article
                Article retrieved = handler.getArticleByTitle("Update Test");
                assertNotNull("Article should exist for update", retrieved);

                retrieved.setTitle("Updated Title");
                retrieved.setAuthor("Updated Author");
                handler.saveArticle(retrieved);

                // Verify the updated article
                Article updated = handler.getArticleByTitle("Updated Title");
                assertNotNull("Updated article should exist", updated);
                assertEquals("Updated title should match", "Updated Title", updated.getTitle());
                assertEquals("Updated author should match", "Updated Author", updated.getAuthor());
            }

            
            @Test
            public void testSpecialGroups() {
                // Add an article to a special group
                Article specialGroupArticle = new Article(
                        "Special Group Test",
                        "Special Author",
                        "Special Abstract",
                        "Special Keyword",
                        "Special Body",
                        "Special Bibliography",
                        "Expert",
                        "group_special"
                );
                handler.saveArticle(specialGroupArticle);

                // Retrieve the article and verify the group
                Article retrieved = handler.getArticleByTitle("Special Group Test");
                assertNotNull("Article should exist", retrieved);
                assertEquals("Special group should match", "group_special", retrieved.getSpecialGroups());
            }

            @Test
            public void testListArticleTitles() {
                // Add multiple articles
                handler.saveArticle(new Article("Title1", "Author1", "Abstract1", "Keyword1", "Body1", "Bibliography1", "Beginner", "general"));
                handler.saveArticle(new Article("Title2", "Author2", "Abstract2", "Keyword2", "Body2", "Bibliography2", "Intermediate", "group1"));

                // Fetch article titles
                List<String> titles = handler.listArticleTitles("", false);
                assertEquals("There should be two articles", 2, titles.size());
                assertTrue("Titles should contain Title1", titles.contains("Title1"));
                assertTrue("Titles should contain Title2", titles.contains("Title2"));
            }
        }

