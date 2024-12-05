package cse360;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class BackupRestorationPageTest {

    private ArticleDatabaseHandler dbHandler;

    @Before
    public void setUp() throws Exception {
        // Initialize ArticleDatabaseHandler with test database files
        dbHandler = new ArticleDatabaseHandler("test_main.db", "test_backup.db");

        // Insert sample articles for testing
        dbHandler.saveArticle(new Article(
            "article1-uid", "Sample Article 1", "Author 1", "Abstract 1", 
            "Keyword1", "Body 1", "Bibliography 1", "Beginner", false, "general"
        ));
        dbHandler.saveArticle(new Article(
            "article2-uid", "Sample Article 2", "Author 2", "Abstract 2", 
            "Keyword2", "Body 2", "Bibliography 2", "Intermediate", false, "general"
        ));
    }

    @After
    public void tearDown() throws Exception {
        // Clean up the test databases
        dbHandler.closeConnections();
    }

    @Test
    public void testBackupFunctionality() {
        // Inputs: List of article titles to back up
        List<String> selectedTitles = Arrays.asList("Sample Article 1", "Sample Article 2");

        // Perform backup
        dbHandler.backupSelectedArticles(selectedTitles);

        // Outputs: Verify backup file contains the articles
        List<String> backupTitles = dbHandler.getBackupArticleTitles();
        assertEquals(2, backupTitles.size());
        assertTrue(backupTitles.contains("Sample Article 1"));
        assertTrue(backupTitles.contains("Sample Article 2"));
    }

    @Test
    public void testRestoreFunctionality() {
        // Delete an article to simulate restoration scenario
        dbHandler.deleteArticle("article1-uid");

        // Inputs: Restore the deleted article from backup
        Article backupArticle = dbHandler.getBackupArticleByTitle("Sample Article 1");
        boolean restored = dbHandler.restoreArticle(backupArticle);

        // Outputs: Verify the article is restored successfully
        assertTrue(restored);

        // Ensure no duplicates are created
        List<Article> articles = dbHandler.getArticles();
        long count = articles.stream().filter(a -> a.getTitle().equals("Sample Article 1")).count();
        assertEquals(1, count);
    }
}
