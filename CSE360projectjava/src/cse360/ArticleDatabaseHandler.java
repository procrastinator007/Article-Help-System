package cse360;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArticleDatabaseHandler {
    private static final String DATABASE_FILE = "database2.db";
    private Connection connection;

    public ArticleDatabaseHandler() {
        connectToDatabase();
        createArticlesTable();
    }

    // Connect to SQLite database
    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create articles table if it doesn't exist
    private void createArticlesTable() {
    	String createTableSQL = "CREATE TABLE IF NOT EXISTS articles (" +
                "uid TEXT PRIMARY KEY, " + // uid as PRIMARY KEY with TEXT type (not AUTOINCREMENT)
                "title TEXT UNIQUE NOT NULL, " +
                "author TEXT, " +
                "articleAbstract TEXT, " +
                "keywords TEXT, " +
                "body TEXT, " +
                "bibliography TEXT, " +
                "level TEXT, " +
                "hidden INTEGER DEFAULT 0" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL); // Create the new table with the updated schema
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
            pstmt.setString(7, article.getBibliography()); // Changed from getReferences() to getBibliography()
            pstmt.setString(8, article.getLevel());
            pstmt.setInt(9, article.isHidden() ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load all articles from the database
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
                        rs.getString("bibliography"), // Changed from "references" to "bibliography"
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

    // Retrieve an article by title
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
                        rs.getString("bibliography"), // Changed from "references" to "bibliography"
                        rs.getString("level"),
                        rs.getInt("hidden") == 1
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Other methods like deleteArticle and setArticleVisibility remain unchanged


    // List article titles based on keyword filter and visibility
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

    
    // Delete an article by UID
    public void deleteArticle(String uid) {
        String deleteSQL = "DELETE FROM articles WHERE uid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, uid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Set article visibility by UID
    public void setArticleVisibility(String uid, boolean isVisible) {
        String updateSQL = "UPDATE articles SET hidden = ? WHERE uid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setInt(1, isVisible ? 0 : 1);
            pstmt.setString(2, uid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
