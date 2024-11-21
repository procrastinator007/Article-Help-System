package cse360;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.Arrays;
import java.util.List;

public class Article {
    private String uid; // Unique identifier
    private String title;
    private String author;
    private String articleAbstract;
    private String keywords;
    private String body;
    private String bibliography; // Changed from "references" to "bibliography"
    private String level;
    private boolean hidden; // Field to track if the article is hidden
    private String specialGroups; // New field for special groups

    // Constructor with UID assignment
    public Article(String title, String author, String articleAbstract, String keywords, String body, String references, String level, String specialGroups) {
        this.uid = UUID.randomUUID().toString(); // Assign a random UID
        this.title = title;
        this.author = author;
        this.articleAbstract = articleAbstract;
        this.keywords = keywords;
        this.body = body;
        this.bibliography = references;
        this.level = level;
        this.hidden = false; // Default is not hidden
        this.specialGroups = specialGroups != null ? specialGroups : "general"; // Default to "general" if not provided
    }

    // Constructor to load an article with a specific UID (for database use)
    public Article(String uid, String title, String author, String articleAbstract, String keywords, String body, String references, String level, boolean hidden, String specialGroups) {
        this.uid = uid;
        this.title = title;
        this.author = author;
        this.articleAbstract = articleAbstract;
        this.keywords = keywords;
        this.body = body;
        this.bibliography = references;
        this.level = level;
        this.hidden = hidden;
        this.specialGroups = specialGroups != null ? specialGroups : "general"; // Default to "general" if not provided
    }

    // Factory method to create an Article from a ResultSet (for database loading)
    public static Article fromResultSet(ResultSet rs) throws SQLException {
        String uid = rs.getString("uid");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String articleAbstract = rs.getString("articleAbstract");
        String keywords = rs.getString("keywords");
        String body = rs.getString("body");
        String references = rs.getString("references");
        String level = rs.getString("level");
        boolean hidden = rs.getInt("hidden") == 1;
        String specialGroups = rs.getString("special_groups");

        return new Article(uid, title, author, articleAbstract, keywords, body, references, level, hidden, specialGroups);
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getArticleAbstract() { return articleAbstract; }
    public String getKeywords() { return keywords; }
    public String getBody() { return body; }
    public String getBibliography() { return bibliography; }
    public String getLevel() { return level; }
    public boolean isHidden() { return hidden; }
    public String getSpecialGroups() { return specialGroups; }
    
    public List<String> getGroups() {
        return Arrays.asList(specialGroups.split(","));
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    // Utility method to generate a string array of values for SQL insertion or updating
    public Object[] toSqlValues() {
        return new Object[]{
                uid, title, author, articleAbstract, keywords, body, bibliography, level, hidden ? 1 : 0, specialGroups
        };
    }

    @Override
    public String toString() {
        return "UID: " + uid + "\n"
                + "Title: " + title + "\n"
                + "Author: " + author + "\n"
                + "Abstract: " + articleAbstract + "\n"
                + "Keywords: " + keywords + "\n"
                + "Body: " + body + "\n"
                + "References: " + bibliography + "\n"
                + "Level: " + level + "\n"
                + "Hidden: " + (hidden ? "Yes" : "No") + "\n"
                + "Special Groups: " + specialGroups;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setArticleAbstract(String articleAbstract) {
        this.articleAbstract = articleAbstract;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setBibliography(String bibliography) {
        this.bibliography = bibliography;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setSpecialGroups(String specialGroups) {
        this.specialGroups = specialGroups;
    }

}
