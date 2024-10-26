package cse360;

import java.io.Serializable;

public class Article implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String author;
    private String articleAbstract;
    private String keywords;
    private String body;
    private String references;
    private String level;
    private boolean hidden; // New field to track if the article is hidden

    // Constructor
    public Article(String title, String author, String articleAbstract, String keywords, String body, String references, String level) {
        this.title = title;
        this.author = author;
        this.articleAbstract = articleAbstract;
        this.keywords = keywords;
        this.body = body;
        this.references = references;
        this.level = level;
        this.hidden = false; // Default is not hidden
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getArticleAbstract() {
        return articleAbstract;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getBody() {
        return body;
    }

    public String getReferences() {
        return references;
    }

    public String getLevel() {
        return level;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public String toString() {
        return "Title: " + title + "\n"
                + "Author: " + author + "\n"
                + "Abstract: " + articleAbstract + "\n"
                + "Keywords: " + keywords + "\n"
                + "Body: " + body + "\n"
                + "References: " + references + "\n"
                + "Level: " + level + "\n"
                + "Hidden: " + (hidden ? "Yes" : "No");
    }
}
