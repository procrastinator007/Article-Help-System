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

    private void loadArticles() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATABASE_FILE))) {
            articles = (List<Article>) ois.readObject();
        } catch (FileNotFoundException e) {
            articles = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            articles = new ArrayList<>();
        }
    }

    private void saveArticles() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATABASE_FILE))) {
            oos.writeObject(articles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveArticle(Article article) {
        articles.add(article);
        saveArticles();
    }

    public List<String> listArticleTitles() {
        List<String> titles = new ArrayList<>();
        for (Article article : articles) {
            titles.add(article.getTitle());
        }
        return titles;
    }

    public Article getArticleByTitle(String title) {
        for (Article article : articles) {
            if (article.getTitle().equals(title)) {
                return article;
            }
        }
        return null;
    }
}
