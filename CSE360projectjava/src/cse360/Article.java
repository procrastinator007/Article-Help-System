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

    public Article(String title, String author, String articleAbstract, String keywords, String body, String references) {
        this.title = title;
        this.author = author;
        this.articleAbstract = articleAbstract;
        this.keywords = keywords;
        this.body = body;
        this.references = references;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Title: " + title + "\n" +
               "Author: " + author + "\n" +
               "Abstract: " + articleAbstract + "\n" +
               "Keywords: " + keywords + "\n" +
               "Body: " + body + "\n" +
               "References: " + references;
    }
}
