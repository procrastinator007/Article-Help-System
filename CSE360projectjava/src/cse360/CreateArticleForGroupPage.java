package cse360;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class CreateArticleForGroupPage {
    private ArticleDatabaseHandler databaseHandler = new ArticleDatabaseHandler();
    private maincontroller controller;
    private User currentUser;
    private String groupName; // Group name for which the article is being created
    private Article articleToEdit; // For editing existing articles

    public CreateArticleForGroupPage(maincontroller controller, User currentUser, String groupName, Article articleToEdit) {
        this.controller = controller;
        this.currentUser = currentUser;
        this.groupName = groupName; // Group for which the article is being created
        this.articleToEdit = articleToEdit; // Pass in the article to edit if applicable
    }

    public Parent getPage() {
        TextField titleField = new TextField();
        titleField.setPromptText("Article Title");

        TextField authorField = new TextField();
        authorField.setPromptText("Author");

        TextArea abstractField = new TextArea();
        abstractField.setPromptText("Abstract");

        TextField keywordsField = new TextField();
        keywordsField.setPromptText("Keywords");

        TextArea bodyField = new TextArea();
        bodyField.setPromptText("Body");

        TextField referencesField = new TextField();
        referencesField.setPromptText("References");

        ComboBox<String> levelComboBox = new ComboBox<>();
        levelComboBox.getItems().addAll("Beginner", "Intermediate", "Advanced", "Expert");
        levelComboBox.setPromptText("Select Level");

        if (articleToEdit != null) {
            // Populate fields with article data for editing
            titleField.setText(articleToEdit.getTitle());
            authorField.setText(articleToEdit.getAuthor());
            abstractField.setText(articleToEdit.getArticleAbstract());
            keywordsField.setText(articleToEdit.getKeywords());
            bodyField.setText(articleToEdit.getBody());
            referencesField.setText(articleToEdit.getBibliography());
            levelComboBox.setValue(articleToEdit.getLevel());
        }

        Button btnSave = new Button("Save Article");
        btnSave.setOnAction(e -> {
            String title = titleField.getText();
            String author = authorField.getText();
            String articleAbstract = abstractField.getText();
            String keywords = keywordsField.getText();
            String body = bodyField.getText();
            String references = referencesField.getText();
            String level = levelComboBox.getValue();

            if (level == null) {
                showAlert("Please select an article level.");
                return;
            }

            if (articleToEdit == null) {
                // Create new article for the specified group
                Article article = new Article(title, author, articleAbstract, keywords, body, references, level, groupName);
                databaseHandler.saveArticle(article);
            } else {
                // Update existing article
                articleToEdit.setTitle(title);
                articleToEdit.setAuthor(author);
                articleToEdit.setArticleAbstract(articleAbstract);
                articleToEdit.setKeywords(keywords);
                articleToEdit.setBody(body);
                articleToEdit.setBibliography(references);
                articleToEdit.setLevel(level);
                articleToEdit.setSpecialGroups(groupName); // Ensure the article is assigned to the correct group
                databaseHandler.saveArticle(articleToEdit); // Save updated article
            }

            showAlert("Article saved successfully!");
            controller.showViewArticlesTeacherPage(currentUser, groupName);
        });

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> controller.showViewArticlesTeacherPage(currentUser, groupName));

        VBox layout = new VBox(10, titleField, authorField, abstractField, keywordsField, bodyField, referencesField, levelComboBox, btnSave, btnBack);
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
