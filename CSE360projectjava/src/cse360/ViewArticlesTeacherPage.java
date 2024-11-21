package cse360;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ViewArticlesTeacherPage {
    private maincontroller controller;
    private User loggedInUser;
    private String groupName;

    private TextArea articleDetails = new TextArea();
    private ListView<String> articleList = new ListView<>();
    private TextField searchField = new TextField();
    private ComboBox<String> searchTypeComboBox;
    private CheckBox showHiddenCheckBox = new CheckBox("Show Hidden Articles");
    private ComboBox<String> levelComboBox;
    private Button filterButton;
    private ArticleDatabaseHandler articleDatabaseHandler = new ArticleDatabaseHandler();

    public ViewArticlesTeacherPage(maincontroller controller, User loggedInUser, String groupName) {
        this.controller = controller;
        this.loggedInUser = loggedInUser;
        this.groupName = groupName;
    }

    public Parent getPage() {
        updateArticleList("", "All");
        initializeFilterComponents();

        // Search type ComboBox
        searchTypeComboBox = new ComboBox<>();
        searchTypeComboBox.getItems().addAll("All", "Title", "Author", "Abstract", "Description", "Keywords");
        searchTypeComboBox.setValue("All");

        // Search bar
        searchField.setPromptText("Enter keyword to search");
        Button btnSearch = new Button("Search");
        btnSearch.setOnAction(e -> performSearch());
        showHiddenCheckBox.setOnAction(e -> updateArticleList(searchField.getText().trim(), searchTypeComboBox.getValue()));

        HBox searchLayout = new HBox(10, searchTypeComboBox, searchField, btnSearch, showHiddenCheckBox, levelComboBox, filterButton);
        searchLayout.setAlignment(Pos.CENTER);
        searchLayout.setPadding(new Insets(10));

        // Article list
        articleList.setPrefWidth(300);
        articleList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Article article = articleDatabaseHandler.getArticleByTitle(newValue.replace(" (Hidden)", ""));
                articleDetails.setText(article != null ? article.toString() : "Article details not found.");
            }
        });

        // Article details
        articleDetails.setEditable(false);
        articleDetails.setWrapText(true);

        // Action buttons
        Button btnHideUnhide = new Button("Hide/Unhide");
        btnHideUnhide.setOnAction(e -> toggleHideArticle());

        Button btnDelete = new Button("Delete");
        btnDelete.setOnAction(e -> deleteArticle());

        Button btnEditArticle = new Button("Edit Article");
        btnEditArticle.setOnAction(e -> editArticle());

        Button btnCreateArticle = new Button("Create Article");
        btnCreateArticle.setOnAction(e -> controller.showCreateArticleForGroupPage(loggedInUser, groupName, null));

        Button btnBackup = new Button("Backup");
        btnBackup.setOnAction(e -> backupSelectedArticle());

        Button btnManageUsers = new Button("Manage Group Users");
        btnManageUsers.setOnAction(e -> controller.showGroupUserManagementPage(loggedInUser, groupName));

        Button btnHelpTeacher = new Button("Help Teacher Page");
        btnHelpTeacher.setOnAction(e -> controller.showHelpTeacherPage(loggedInUser, groupName));

        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> controller.logout());

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> controller.showListOfGroupsPage(loggedInUser));

        VBox buttonLayout = new VBox(10, btnHideUnhide, btnDelete, btnEditArticle, btnCreateArticle, btnBackup, btnManageUsers,
                btnHelpTeacher, btnLogout, btnBack);
        buttonLayout.setAlignment(Pos.TOP_CENTER);
        buttonLayout.setPadding(new Insets(10));

        // Main layout
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
            if (article.getSpecialGroups().contains(groupName) &&
                (showHiddenCheckBox.isSelected() || !article.isHidden())) {

                boolean matches = switch (searchType) {
                    case "Title" -> article.getTitle().toLowerCase().contains(keyword.toLowerCase());
                    case "Author" -> article.getAuthor().toLowerCase().contains(keyword.toLowerCase());
                    case "Abstract" -> article.getArticleAbstract().toLowerCase().contains(keyword.toLowerCase());
                    case "Description" -> article.getBody().toLowerCase().contains(keyword.toLowerCase());
                    case "Keywords" -> article.getKeywords().toLowerCase().contains(keyword.toLowerCase());
                    default -> article.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                            article.getAuthor().toLowerCase().contains(keyword.toLowerCase()) ||
                            article.getArticleAbstract().toLowerCase().contains(keyword.toLowerCase()) ||
                            article.getBody().toLowerCase().contains(keyword.toLowerCase()) ||
                            article.getKeywords().toLowerCase().contains(keyword.toLowerCase());
                };

                if (matches) {
                    String title = article.getTitle() + (article.isHidden() ? " (Hidden)" : "");
                    articleList.getItems().add(title);
                }
            }
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
                articleDatabaseHandler.getArticles() :
                articleDatabaseHandler.getArticlesByLevel(selectedLevel);

        articleList.getItems().clear();
        for (Article article : articles) {
            if (article.getSpecialGroups().contains(groupName)) {
                articleList.getItems().add(article.getTitle() + (article.isHidden() ? " (Hidden)" : ""));
            }
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

    private void editArticle() {
        String selectedTitle = articleList.getSelectionModel().getSelectedItem();
        if (selectedTitle != null) {
            Article article = articleDatabaseHandler.getArticleByTitle(selectedTitle.replace(" (Hidden)", ""));
            if (article != null) {
                controller.showCreateArticleForGroupPage(loggedInUser, groupName, article);
            } else {
                showAlert("Error", "Unable to find the article for editing.");
            }
        } else {
            showAlert("No Selection", "Please select an article to edit.");
        }
    }

    private void backupSelectedArticle() {
        String selectedTitle = articleList.getSelectionModel().getSelectedItem();
        if (selectedTitle != null) {
            articleDatabaseHandler.backupSelectedArticles(List.of(selectedTitle));
            showAlert("Backup Success", "Article backed up successfully.");
        } else {
            showAlert("No Selection", "Please select an article to back up.");
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