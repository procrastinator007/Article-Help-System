package cse360;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArticleDatabaseHandler {
    private static final String DATABASE_FILE = "database2.db";
    private static final String BACKUP_FILE = "database_backup.db"; // Direct file for backup
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

            // Initialize backup connection
            backupConnection = DriverManager.getConnection("jdbc:sqlite:" + BACKUP_FILE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create articles table if it doesn't exist in both databases
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
                "hidden INTEGER DEFAULT 0, " +
                "special_groups TEXT DEFAULT 'general'" + // Field to store group information
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
    	String insertSQL = "INSERT OR REPLACE INTO articles (uid, title, author, articleAbstract, keywords, body, bibliography, level, hidden, special_groups) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            pstmt.setString(10, String.join(",", article.getSpecialGroups())); // Store groups as comma-separated values
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

 // Load all articles from the main database
    public List<Article> getArticles() {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT * FROM articles"; // Select all columns from the articles table
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
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
                        rs.getInt("hidden") == 1,
                        rs.getString("special_groups")
                );
                articles.add(article);
                System.out.println("Retrieved article: " + article.getTitle());
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
                        rs.getInt("hidden") == 1,
                        rs.getString("special_groups")
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

    // Back up selected articles
 // Back up selected articles, replacing if the UID is the same but content differs
    public void backupSelectedArticles(List<String> selectedTitles) {
        String selectSQL = "SELECT * FROM articles WHERE title = ?";
        String checkBackupSQL = "SELECT * FROM articles WHERE uid = ?";
        String insertOrUpdateSQL = "INSERT OR REPLACE INTO articles (uid, title, author, articleAbstract, keywords, body, bibliography, level, hidden, special_groups) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectSQL);
             PreparedStatement checkBackupStmt = backupConnection.prepareStatement(checkBackupSQL);
             PreparedStatement insertOrUpdateStmt = backupConnection.prepareStatement(insertOrUpdateSQL)) {
            
            for (String title : selectedTitles) {
                selectStmt.setString(1, title.replace(" (Hidden)", ""));
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    String uid = rs.getString("uid");

                    // Check if article with same UID exists in the backup
                    checkBackupStmt.setString(1, uid);
                    ResultSet backupRs = checkBackupStmt.executeQuery();

                    boolean contentDiffers = true;
                    if (backupRs.next()) {
                        // Check if any fields differ
                        contentDiffers = !(rs.getString("title").equals(backupRs.getString("title")) &&
                                           rs.getString("author").equals(backupRs.getString("author")) &&
                                           rs.getString("articleAbstract").equals(backupRs.getString("articleAbstract")) &&
                                           rs.getString("keywords").equals(backupRs.getString("keywords")) &&
                                           rs.getString("body").equals(backupRs.getString("body")) &&
                                           rs.getString("bibliography").equals(backupRs.getString("bibliography")) &&
                                           rs.getString("level").equals(backupRs.getString("level")) &&
                                           rs.getInt("hidden") == backupRs.getInt("hidden") &&
                                           rs.getString("special_groups").equals(backupRs.getString("special_groups")));
                    }

                    // If no backup exists or contents differ, insert or replace
                    if (!backupRs.next() || contentDiffers) {
                        insertOrUpdateStmt.setString(1, uid);
                        insertOrUpdateStmt.setString(2, rs.getString("title"));
                        insertOrUpdateStmt.setString(3, rs.getString("author"));
                        insertOrUpdateStmt.setString(4, rs.getString("articleAbstract"));
                        insertOrUpdateStmt.setString(5, rs.getString("keywords"));
                        insertOrUpdateStmt.setString(6, rs.getString("body"));
                        insertOrUpdateStmt.setString(7, rs.getString("bibliography"));
                        insertOrUpdateStmt.setString(8, rs.getString("level"));
                        insertOrUpdateStmt.setInt(9, rs.getInt("hidden"));
                        insertOrUpdateStmt.setString(10, rs.getString("special_groups"));
                        insertOrUpdateStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    // Retrieve articles by a specific level
    public List<Article> getArticlesByLevel(String level) {
        List<Article> articles = new ArrayList<>();
        String selectSQL = "SELECT * FROM articles WHERE level = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, level);
            ResultSet rs = pstmt.executeQuery();
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
                        rs.getInt("hidden") == 1,
                        rs.getString("special_groups") // Add special_groups field
                );
                articles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

    // List available backup articles in the backup database
    public List<String> getBackupArticleTitles() {
        List<String> titles = new ArrayList<>();
        String selectSQL = "SELECT title FROM articles";
        
        // Ensure backup connection exists
        if (backupConnection != null) {
            try (Statement stmt = backupConnection.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSQL)) {
                while (rs.next()) {
                    titles.add(rs.getString("title"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Backup connection is not available.");
        }
        return titles;
    }
    
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
                    rs.getInt("hidden") == 1,
                    rs.getString("special_groups")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
 // Restore an article to the main database if it doesn't already exist
    public boolean restoreArticle(Article article) {
        // Check if the article already exists in the main database
        if (articleExists(article.getUid(), connection)) {
            return false; // Article already exists, no need to restore
        }

        // Insert the article into the main database
        String insertSQL = "INSERT INTO articles (uid, title, author, articleAbstract, keywords, body, bibliography, level, hidden, special_groups) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            pstmt.setString(10, article.getSpecialGroups()); // Add special_groups field
            pstmt.executeUpdate();
            return true; // Article restored successfully
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



}
