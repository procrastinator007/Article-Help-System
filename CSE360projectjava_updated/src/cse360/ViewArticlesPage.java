package cse360;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.List;

public class ViewArticlesPage {
    private maincontroller controller;
    private TextArea articleDetails = new TextArea();
    private ListView<String> articleList = new ListView<>();
    private TextField searchField = new TextField(); // TextField for keyword search
    private CheckBox showHiddenCheckBox = new CheckBox("Show Hidden Articles"); // Checkbox to toggle hidden articles
    private ArticleDatabaseHandler articleDatabaseHandler = new ArticleDatabaseHandler();

    public ViewArticlesPage(maincontroller controller) {
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

        // Enable multiple selection in the ListView
        articleList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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
                // Retrieve the article object using the title
                Article article = articleDatabaseHandler.getArticleByTitle(selectedTitle.replace(" (Hidden)", ""));
                if (article != null) {
                    // Delete the article using its uid
                    articleDatabaseHandler.deleteArticle(article.getUid());
                    updateArticleList(searchField.getText().trim()); // Refresh list based on current search/filter
                } else {
                    showAlert("Error", "Unable to find the article for deletion.");
                }
            } else {
                showAlert("No Selection", "Please select an article to delete.");
            }
        });

        // Backup selected articles button
        Button btnBackupSelected = new Button("Backup Selected Articles");
        btnBackupSelected.setOnAction(e -> {
            List<String> selectedArticles = articleList.getSelectionModel().getSelectedItems();
            if (selectedArticles != null && !selectedArticles.isEmpty()) {
                articleDatabaseHandler.backupSelectedArticles(selectedArticles);
                showAlert("Success", "Selected articles backed up successfully.");
            } else {
                showAlert("No Selection", "Please select one or more articles to back up.");
            }
        });

        // View and restore from backup button
        Button btnViewAndRestore = new Button("View and Restore Backup");
        btnViewAndRestore.setOnAction(e -> controller.showBackupRestorationPage());

        // Additional admin buttons
        Button btnDisplayUsers = new Button("User Database");
        btnDisplayUsers.setOnAction(e -> controller.showDisplayUserDatabasePage());

        Button btnCreateArticle = new Button("Create Article");
        btnCreateArticle.setOnAction(e -> controller.showCreateArticlePage());

        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> controller.logout());

        // Back button to return to the entry page
        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> controller.showEntryPage());

        // Layout configuration
        VBox layout = new VBox(10, articleList, articleDetails, btnHideUnhide, btnDelete, btnBackupSelected, btnViewAndRestore, btnDisplayUsers, btnCreateArticle, btnLogout, btnBack);
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
