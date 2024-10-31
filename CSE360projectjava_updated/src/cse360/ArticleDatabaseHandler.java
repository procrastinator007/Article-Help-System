package cse360;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArticleDatabaseHandler {
    private static final String DATABASE_FILE = "database2.db";
    private static final String BACKUP_FILE = "database_backup.db";
    private static final String BACKUP_DIRECTORY = "backups"; // Directory for backups
    private Connection connection;
    private Connection backupConnection;

    public ArticleDatabaseHandler() {
        connectToDatabases();
        createArticlesTable();
    }

    // Connect to both databases (main and backup)
    private void connectToDatabases() {
        try {
            // Connect to main database
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);

            // Ensure backup directory exists
            File backupDir = new File(BACKUP_DIRECTORY);
            if (!backupDir.exists()) {
                backupDir.mkdir();
            }

            // Connect to backup database
            backupConnection = DriverManager.getConnection("jdbc:sqlite:" + BACKUP_FILE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create articles table if it doesn't exist
    private void createArticlesTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS articles (" +
                "uid TEXT PRIMARY KEY, " +
                "title TEXT UNIQUE NOT NULL, " +
                "author TEXT, " +
                "articleAbstract TEXT, " +
                "keywords TEXT, " +
                "body TEXT, " +
                "bibliography TEXT, " +
                "level TEXT, " +
                "hidden INTEGER DEFAULT 0" +
                ")";
        try (Statement stmt = connection.createStatement();
             Statement backupStmt = backupConnection.createStatement()) {
            // Create table in both databases if they don't exist
            stmt.execute(createTableSQL);
            backupStmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Save a new article
    public void saveArticle(Article article) {
        String insertSQL = "INSERT OR REPLACE INTO articles (uid, title, author, articleAbstract, keywords, body, bibliography, level, hidden) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            String uid = article.getUid() != null ? article.getUid() : UUID.randomUUID().toString();
            pstmt.setString(1, uid);
            pstmt.setString(2, article.getTitle());
            pstmt.setString(3, article.getAuthor());
            pstmt.setString(4, article.getArticleAbstract());
            pstmt.setString(5, article.getKeywords());
            pstmt.setString(6, article.getBody());
            pstmt.setString(7, article.getBibliography());
            pstmt.setString(8, article.getLevel());
            pstmt.setInt(9, article.isHidden() ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load all articles from the main database
    public List<Article> getArticles() {
        List<Article> articles = new ArrayList<>();
        String selectSQL = "SELECT * FROM articles";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                Article article = new Article(
                        rs.getString("uid"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("articleAbstract"),
                        rs.getString("keywords"),
                        rs.getString("body"),
                        rs.getString("bibliography"),
                        rs.getString("level"),
                        rs.getInt("hidden") == 1
                );
                articles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

    // Retrieve an article by title from the main database
    public Article getArticleByTitle(String title) {
        String selectSQL = "SELECT * FROM articles WHERE title = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, title.replace(" (Hidden)", ""));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Article(
                        rs.getString("uid"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("articleAbstract"),
                        rs.getString("keywords"),
                        rs.getString("body"),
                        rs.getString("bibliography"),
                        rs.getString("level"),
                        rs.getInt("hidden") == 1
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Retrieve an article by title from the backup database
    public Article getBackupArticleByTitle(String title) {
        String selectSQL = "SELECT * FROM articles WHERE title = ?";
        try (PreparedStatement pstmt = backupConnection.prepareStatement(selectSQL)) {
            pstmt.setString(1, title);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Article(
                        rs.getString("uid"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("articleAbstract"),
                        rs.getString("keywords"),
                        rs.getString("body"),
                        rs.getString("bibliography"),
                        rs.getString("level"),
                        rs.getInt("hidden") == 1
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Delete an article by UID from the main database
    public void deleteArticle(String uid) {
        String deleteSQL = "DELETE FROM articles WHERE uid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, uid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Restore an article to the main database if it doesn't already exist
    public boolean restoreArticle(Article article) {
        // Check if the article already exists in the main database
        if (articleExists(article.getUid(), connection)) {
            return false; // Article already exists, no need to restore
        }

        // Insert the article into the main database
        String insertSQL = "INSERT OR REPLACE INTO articles (uid, title, author, articleAbstract, keywords, body, bibliography, level, hidden) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, article.getUid());
            pstmt.setString(2, article.getTitle());
            pstmt.setString(3, article.getAuthor());
            pstmt.setString(4, article.getArticleAbstract());
            pstmt.setString(5, article.getKeywords());
            pstmt.setString(6, article.getBody());
            pstmt.setString(7, article.getBibliography());
            pstmt.setString(8, article.getLevel());
            pstmt.setInt(9, article.isHidden() ? 1 : 0);
            pstmt.executeUpdate();
            return true; // Article restored successfully
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check if an article exists in a given database connection by UID
    private boolean articleExists(String uid, Connection dbConnection) {
        String selectSQL = "SELECT uid FROM articles WHERE uid = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(selectSQL)) {
            pstmt.setString(1, uid);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to back up selected articles
    public void backupSelectedArticles(List<String> selectedTitles) {
        String selectSQL = "SELECT * FROM articles WHERE title = ?";
        String insertSQL = "INSERT OR REPLACE INTO articles (uid, title, author, articleAbstract, keywords, body, bibliography, level, hidden) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectSQL);
             PreparedStatement insertStmt = backupConnection.prepareStatement(insertSQL)) {
            
            for (String title : selectedTitles) {
                selectStmt.setString(1, title.replace(" (Hidden)", ""));
                ResultSet rs = selectStmt.executeQuery();
                
                if (rs.next()) {
                    // Check if the article already exists in the backup database
                    if (!articleExists(rs.getString("uid"), backupConnection)) {
                        // If not, insert it into the backup database
                        insertStmt.setString(1, rs.getString("uid"));
                        insertStmt.setString(2, rs.getString("title"));
                        insertStmt.setString(3, rs.getString("author"));
                        insertStmt.setString(4, rs.getString("articleAbstract"));
                        insertStmt.setString(5, rs.getString("keywords"));
                        insertStmt.setString(6, rs.getString("body"));
                        insertStmt.setString(7, rs.getString("bibliography"));
                        insertStmt.setString(8, rs.getString("level"));
                        insertStmt.setInt(9, rs.getInt("hidden"));
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // List all article titles based on a keyword filter and hidden status
    public List<String> listArticleTitles(String keyword, boolean showHidden) {
        List<String> titles = new ArrayList<>();
        String selectSQL = "SELECT title, hidden FROM articles WHERE (hidden = 0 OR ? = 1) AND (keywords LIKE ? OR ? = '')";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setInt(1, showHidden ? 1 : 0);
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, keyword);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                if (rs.getInt("hidden") == 1) {
                    title += " (Hidden)";
                }
                titles.add(title);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return titles;
    }

    // List available backup articles in the backup database
    public List<String> getBackupArticleTitles() {
        List<String> titles = new ArrayList<>();
        String selectSQL = "SELECT title FROM articles";
        try (Statement stmt = backupConnection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                titles.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return titles;
    }
}
