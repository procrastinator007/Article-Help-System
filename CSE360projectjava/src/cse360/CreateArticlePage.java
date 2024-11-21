package cse360;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.List;

public class CreateArticlePage {
    private ArticleDatabaseHandler databaseHandler = new ArticleDatabaseHandler();
    private maincontroller controller;
    private ComboBox<String> groupComboBox;
    private User currentUser;
    private Article articleToEdit; // For editing existing articles

    public CreateArticlePage(maincontroller controller, User currentUser, Article articleToEdit) {
        this.controller = controller;
        this.currentUser = currentUser;
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

        groupComboBox = new ComboBox<>();
        groupComboBox.setPromptText("Select Group");
        populateGroupComboBox();

        if (articleToEdit != null) {
            // Populate fields with article data for editing
            titleField.setText(articleToEdit.getTitle());
            authorField.setText(articleToEdit.getAuthor());
            abstractField.setText(articleToEdit.getArticleAbstract());
            keywordsField.setText(articleToEdit.getKeywords());
            bodyField.setText(articleToEdit.getBody());
            referencesField.setText(articleToEdit.getBibliography());
            levelComboBox.setValue(articleToEdit.getLevel());
            groupComboBox.setValue(articleToEdit.getSpecialGroups());
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
            String selectedGroup = groupComboBox.getValue();

            if (level == null || selectedGroup == null) {
                showAlert("Please select an article level and group.");
                return;
            }

            if (articleToEdit == null) {
                // Create new article
                Article article = new Article(title, author, articleAbstract, keywords, body, references, level, selectedGroup);
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
                articleToEdit.setSpecialGroups(selectedGroup);
                databaseHandler.saveArticle(articleToEdit); // Save updated article
            }

            showAlert("Article saved successfully!");
            controller.showViewArticlesPage(currentUser);
        });

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> controller.showViewArticlesPage(currentUser));

        VBox layout = new VBox(10, titleField, authorField, abstractField, keywordsField, bodyField, referencesField, levelComboBox, groupComboBox, btnSave, btnBack);
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    private void populateGroupComboBox() {
        // Fetch admin groups for the current user
        List<String> userGroups = Database.getUserSpecialGroups(currentUser.getUsername());
        
        // Add "general" and some hard-coded groups for demonstration
        userGroups.add("general");  // Always add 'general'
        

        // Populate the combo box with the updated list
        groupComboBox.getItems().clear();
        groupComboBox.getItems().addAll(userGroups);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
