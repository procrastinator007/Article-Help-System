package cse360;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleDatabaseHandler {
    private static final String DATABASE_FILE = "database2.ser";
    private List<Article> articles;

    public ArticleDatabaseHandler() {
        loadArticles();
    }

    // Load articles from the serialized file
    private void loadArticles() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATABASE_FILE))) {
            articles = (List<Article>) ois.readObject();
        } catch (FileNotFoundException e) {
            articles = new ArrayList<>(); // Initialize empty list if file not found
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            articles = new ArrayList<>(); // Initialize empty list on error
        }
    }

    // Save articles to the serialized file
    private void saveArticles() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATABASE_FILE))) {
            oos.writeObject(articles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save a new article
    public void saveArticle(Article article) {
        articles.add(article);
        saveArticles();
    }

    // List article titles based on keyword filter and visibility
    public List<String> listArticleTitles(String keyword, boolean showHidden) {
        List<String> titles = new ArrayList<>();
        for (Article article : articles) {
            if ((showHidden || !article.isHidden()) &&
                (keyword.isEmpty() || article.getKeywords().toLowerCase().contains(keyword.toLowerCase()))) {
                titles.add(article.getTitle() + (article.isHidden() ? " (Hidden)" : ""));
            }
        }
        return titles;
    }

    // Retrieve an article by title
    public Article getArticleByTitle(String title) {
        for (Article article : articles) {
            if (article.getTitle().equals(title.replace(" (Hidden)", ""))) {
                return article;
            }
        }
        return null;
    }

    // Delete an article by title
    public void deleteArticle(String title) {
        articles.removeIf(article -> article.getTitle().equals(title.replace(" (Hidden)", "")));
        saveArticles();
    }

    // Set article visibility by title
    public void setArticleVisibility(String title, boolean isVisible) {
        for (Article article : articles) {
            if (article.getTitle().equals(title.replace(" (Hidden)", ""))) {
                article.setHidden(!isVisible);
                break;
            }
        }
        saveArticles();
    }

    // Get all articles (for other classes if needed)
    public List<Article> getArticles() {
        return articles;
    
    }
    
}
