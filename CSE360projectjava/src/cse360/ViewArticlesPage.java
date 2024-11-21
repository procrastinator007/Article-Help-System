package cse360;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ViewArticlesPage {
    private maincontroller controller;
    private User loggedInUser;

    private TextArea articleDetails = new TextArea();
    private ListView<String> articleList = new ListView<>();
    private TextField searchField = new TextField();
    private CheckBox showHiddenCheckBox = new CheckBox("Show Hidden Articles");
    private ComboBox<String> searchTypeComboBox = new ComboBox<>();
    private ComboBox<String> levelComboBox;
    private Button filterButton;
    private ArticleDatabaseHandler articleDatabaseHandler = new ArticleDatabaseHandler();

    public ViewArticlesPage(maincontroller controller, User user) {
        this.controller = controller;
        this.loggedInUser = user;
    }

    public Parent getPage() {
        updateArticleList("", "All");
        initializeFilterComponents();

        // Initialize search type combo box
        searchTypeComboBox.getItems().addAll("All", "Title", "Author", "Abstract", "Description", "Keywords");
        searchTypeComboBox.setValue("All");

        // Search bar and layout
        Button btnSearch = new Button("Search");
        btnSearch.setOnAction(e -> performSearch());
        showHiddenCheckBox.setOnAction(e -> updateArticleList(searchField.getText().trim(), searchTypeComboBox.getValue()));

        HBox searchLayout = new HBox(10, searchTypeComboBox, searchField, btnSearch, showHiddenCheckBox, levelComboBox, filterButton);
        searchLayout.setAlignment(Pos.CENTER);
        searchLayout.setPadding(new Insets(10));

        // Article list layout
        articleList.setPrefWidth(300);
        articleList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Article article = articleDatabaseHandler.getArticleByTitle(newValue.replace(" (Hidden)", ""));
                articleDetails.setText(article != null ? article.toString() : "Article details not found.");
            }
        });

        // Article details layout
        articleDetails.setEditable(false);
        articleDetails.setWrapText(true);

        // Buttons for actions
        Button btnHideUnhide = new Button("Hide/Unhide");
        btnHideUnhide.setOnAction(e -> toggleHideArticle());

        Button btnDelete = new Button("Delete");
        btnDelete.setOnAction(e -> deleteArticle());

        Button btnEditArticle = new Button("Edit Article");
        btnEditArticle.setOnAction(e -> editArticle());

        Button btnCreateArticle = new Button("Create Article");
        btnCreateArticle.setOnAction(e -> controller.showCreateArticlePage(loggedInUser));

        Button btnBackupSelected = new Button("Backup Selected Articles");
        btnBackupSelected.setOnAction(e -> backupSelectedArticles());

        Button btnViewAndRestore = new Button("View and Restore Backup");
        btnViewAndRestore.setOnAction(e -> controller.showBackupRestorationPage(loggedInUser));

        Button btnDisplayUsers = new Button("User Database");
        btnDisplayUsers.setOnAction(e -> controller.showDisplayUserDatabasePage(loggedInUser));

        Button btnSpecialGroup = new Button("Special Group");
        btnSpecialGroup.setOnAction(e -> controller.showSpecialGroupPage(loggedInUser));

        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> controller.logout());

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> controller.showEntryPage());

        Button btnViewHelpRequests = new Button("View Help Requests");
        btnViewHelpRequests.setOnAction(e -> controller.showHelpRequestPage(loggedInUser));

        // Organize buttons
        VBox buttonLayout = new VBox(10, btnHideUnhide, btnDelete, btnEditArticle, btnCreateArticle, btnBackupSelected,
                btnViewAndRestore, btnDisplayUsers, btnSpecialGroup, btnViewHelpRequests, btnLogout, btnBack);
        buttonLayout.setAlignment(Pos.TOP_CENTER);
        buttonLayout.setPadding(new Insets(10));

        // Layout organization
        HBox mainLayout = new HBox(10, articleList, articleDetails, buttonLayout);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setHgrow(articleDetails, Priority.ALWAYS);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(searchLayout);
        borderPane.setCenter(mainLayout);

        return borderPane;
    }

    private void updateArticleList(String keyword, String searchType) {
        articleList.getItems().clear();
        for (Article article : articleDatabaseHandler.getArticles()) {
            if ((showHiddenCheckBox.isSelected() || !article.isHidden()) &&
                matchesSearchType(article, keyword, searchType)) {

                String title = article.getTitle() + (article.isHidden() ? " (Hidden)" : "");
                articleList.getItems().add(title);
            }
        }
    }

    private boolean matchesSearchType(Article article, String keyword, String searchType) {
        if (keyword.isEmpty()) {
            return true;
        }
        keyword = keyword.toLowerCase();
        switch (searchType) {
            case "Title":
                return article.getTitle().toLowerCase().contains(keyword);
            case "Author":
                return article.getAuthor().toLowerCase().contains(keyword);
            case "Abstract":
                return article.getArticleAbstract().toLowerCase().contains(keyword);
            case "Description":
                return article.getBody().toLowerCase().contains(keyword);
            case "Keywords":
                return article.getKeywords().toLowerCase().contains(keyword);
            default: // "All"
                return article.getTitle().toLowerCase().contains(keyword) ||
                       article.getAuthor().toLowerCase().contains(keyword) ||
                       article.getBody().toLowerCase().contains(keyword) ||
                       article.getKeywords().toLowerCase().contains(keyword);
        }
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        String searchType = searchTypeComboBox.getValue();
        updateArticleList(keyword, searchType);
    }

    private void toggleHideArticle() {
        String selectedTitle = articleList.getSelectionModel().getSelectedItem();
        if (selectedTitle != null) {
            Article article = articleDatabaseHandler.getArticleByTitle(selectedTitle.replace(" (Hidden)", ""));
            if (article != null) {
                article.setHidden(!article.isHidden());
                articleDatabaseHandler.saveArticle(article);
                performSearch();
            } else {
                showAlert("Error", "Unable to find the article.");
            }
        } else {
            showAlert("No Selection", "Please select an article to hide/unhide.");
        }
    }

    private void deleteArticle() {
        String selectedTitle = articleList.getSelectionModel().getSelectedItem();
        if (selectedTitle != null) {
            Article article = articleDatabaseHandler.getArticleByTitle(selectedTitle.replace(" (Hidden)", ""));
            if (article != null) {
                articleDatabaseHandler.deleteArticle(article.getUid());
                performSearch();
            } else {
                showAlert("Error", "Unable to find the article for deletion.");
            }
        } else {
            showAlert("No Selection", "Please select an article to delete.");
        }
    }

    private void backupSelectedArticles() {
        List<String> selectedArticles = articleList.getSelectionModel().getSelectedItems();
        if (selectedArticles != null && !selectedArticles.isEmpty()) {
            articleDatabaseHandler.backupSelectedArticles(selectedArticles);
            showAlert("Success", "Selected articles backed up successfully.");
        } else {
            showAlert("No Selection", "Please select one or more articles to back up.");
        }
    }

    private void editArticle() {
        String selectedTitle = articleList.getSelectionModel().getSelectedItem();
        if (selectedTitle != null) {
            Article article = articleDatabaseHandler.getArticleByTitle(selectedTitle.replace(" (Hidden)", ""));
            if (article != null) {
                controller.showEditArticlePage(article, loggedInUser);
            } else {
                showAlert("Error", "Unable to find the article for editing.");
            }
        } else {
            showAlert("No Selection", "Please select an article to edit.");
        }
    }

    private void initializeFilterComponents() {
        levelComboBox = new ComboBox<>();
        levelComboBox.getItems().addAll("All", "Beginner", "Intermediate", "Advanced", "Expert");
        levelComboBox.setValue("All");

        filterButton = new Button("Filter by Level");
        filterButton.setOnAction(e -> filterArticlesByLevel());
    }

    private void filterArticlesByLevel() {
        String selectedLevel = levelComboBox.getValue();
        List<Article> articles = selectedLevel.equals("All") ?
                articleDatabaseHandler.getArticles() : articleDatabaseHandler.getArticlesByLevel(selectedLevel);

        articleList.getItems().clear();
        for (Article article : articles) {
            articleList.getItems().add(article.getTitle() + (article.isHidden() ? " (Hidden)" : ""));
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
