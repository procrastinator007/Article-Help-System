package cse360;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class BackupRestorationPage {
    private maincontroller controller;
    private User loggedInUser; // Added user context
    private ListView<String> backupListView = new ListView<>();
    private ArticleDatabaseHandler articleDatabaseHandler = new ArticleDatabaseHandler();

    public BackupRestorationPage(maincontroller controller, User user) { // Accept user as a parameter
        this.controller = controller;
        this.loggedInUser = user;
    }

    public Parent getPage() {
        // Load the list of available backups
        updateBackupList();

        // Button to restore selected articles
        Button btnRestore = new Button("Restore Selected Article");
        btnRestore.setOnAction(e -> restoreSelectedArticle());

        // Button to refresh the backup list
        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> updateBackupList());

        // Button to go back to the previous page
        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> controller.showViewArticlesPage(loggedInUser)); // Pass user context

        // Layout configuration
        VBox layout = new VBox(10, new Label("Available Backups:"), backupListView, btnRestore, btnRefresh, btnBack);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(layout);

        return borderPane;
    }

    // Method to update the list of available backups
    private void updateBackupList() {
        List<String> backupTitles = articleDatabaseHandler.getBackupArticleTitles();
        backupListView.getItems().clear();
        backupListView.getItems().addAll(backupTitles);
    }

    // Method to restore the selected article
    private void restoreSelectedArticle() {
        String selectedTitle = backupListView.getSelectionModel().getSelectedItem();
        if (selectedTitle != null) {
            Article backupArticle = articleDatabaseHandler.getBackupArticleByTitle(selectedTitle);
            if (backupArticle != null) {
                boolean restored = articleDatabaseHandler.restoreArticle(backupArticle);
                if (restored) {
                    showAlert("Success", "Article restored successfully.");
                } else {
                    showAlert("Info", "Article already exists in the main database.");
                }
            } else {
                showAlert("Error", "Failed to find the selected article in the backup database.");
            }
        } else {
            showAlert("No Selection", "Please select an article to restore.");
        }
    }

    // Utility method to show an alert dialog
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
