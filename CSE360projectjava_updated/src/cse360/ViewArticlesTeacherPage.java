package cse360;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class ViewArticlesTeacherPage {
    private maincontroller controller;
    private TextArea articleDetails = new TextArea();
    private ListView<String> articleList = new ListView<>();
    private TextField searchField = new TextField(); // TextField for keyword search
    private CheckBox showHiddenCheckBox = new CheckBox("Show Hidden Articles"); // Checkbox to toggle hidden articles
    private ArticleDatabaseHandler articleDatabaseHandler = new ArticleDatabaseHandler();

    public ViewArticlesTeacherPage(maincontroller controller) {
        this.controller = controller;
    }

    public Parent getPage() {
        // Set up the article list with initial articles
        updateArticleList("");

        // Search field and button
        searchField.setPromptText("Enter keyword to search");
        Button btnSearch = new Button("Search");
        btnSearch.setOnAction(e -> performSearch());

        // Show hidden articles checkbox
        showHiddenCheckBox.setOnAction(e -> updateArticleList(searchField.getText().trim()));

        // Layout for search field, button, and checkbox at top right corner
        HBox searchLayout = new HBox(10, searchField, btnSearch, showHiddenCheckBox);
        searchLayout.setAlignment(Pos.CENTER_RIGHT);
        searchLayout.setPadding(new Insets(10));

        // Display article details when an article is selected
        articleList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Article article = articleDatabaseHandler.getArticleByTitle(newValue);
                articleDetails.setText(article != null ? article.toString() : "Article details not found.");
            }
        });
        articleDetails.setEditable(false);

        // Hide/Unhide button
        Button btnHideUnhide = new Button("Hide/Unhide");
        btnHideUnhide.setOnAction(e -> {
            String selectedTitle = articleList.getSelectionModel().getSelectedItem();
            if (selectedTitle != null) {
                Article article = articleDatabaseHandler.getArticleByTitle(selectedTitle);
                if (article != null) {
                    article.setHidden(!article.isHidden()); // Toggle hidden status
                    articleDatabaseHandler.saveArticle(article); // Update article in database
                    updateArticleList(searchField.getText().trim()); // Refresh list based on current search/filter
                }
            } else {
                showAlert("No Selection", "Please select an article to hide/unhide.");
            }
        });

        // Delete button
        Button btnDelete = new Button("Delete");
        btnDelete.setOnAction(e -> {
            String selectedTitle = articleList.getSelectionModel().getSelectedItem();
            if (selectedTitle != null) {
                articleDatabaseHandler.deleteArticle(selectedTitle); // Delete from database
                updateArticleList(searchField.getText().trim()); // Refresh list based on current search/filter
            } else {
                showAlert("No Selection", "Please select an article to delete.");
            }
        });

        // Create Article button
        Button btnCreateArticle = new Button("Create Article");
        btnCreateArticle.setOnAction(e -> controller.showCreateArticlePage());

        // Logout button
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> controller.logout());

        // Back button to return to the entry page
        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> controller.showEntryPage());

        // Layout configuration
        VBox layout = new VBox(10, articleList, articleDetails, btnHideUnhide, btnDelete, btnCreateArticle, btnLogout, btnBack);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(searchLayout); // Add search layout to the top
        borderPane.setCenter(layout);

        return borderPane;
    }

    // Method to update the article list based on a keyword filter and hidden checkbox status
    private void updateArticleList(String keyword) {
        articleList.getItems().clear();
        for (Article article : articleDatabaseHandler.getArticles()) {
            // Check if the article matches the search keyword and whether to show hidden articles
            if ((showHiddenCheckBox.isSelected() || !article.isHidden()) &&
                (keyword.isEmpty() || article.getKeywords().toLowerCase().contains(keyword.toLowerCase()))) {

                // Add "(Hidden)" label if the article is hidden
                String title = article.getTitle() + (article.isHidden() ? " (Hidden)" : "");
                articleList.getItems().add(title);
            }
        }
    }

    // Method to perform the search based on the entered keyword
    private void performSearch() {
        String keyword = searchField.getText().trim();
        updateArticleList(keyword);
    }

    // Utility method to show an alert
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
