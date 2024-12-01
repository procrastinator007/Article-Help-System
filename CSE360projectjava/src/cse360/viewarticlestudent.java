package cse360;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class viewarticlestudent {
    private maincontroller controller;
    private User loggedInUser;
    private String groupName;
    private TextArea articleDetails = new TextArea();
    private ListView<String> articleList = new ListView<>();
    private TextField searchField = new TextField();
    private ComboBox<String> searchTypeComboBox;
    private ComboBox<String> levelComboBox;
    private Button filterButton;
    private ArticleDatabaseHandler articleDatabaseHandler = new ArticleDatabaseHandler();

    public viewarticlestudent(maincontroller controller, User loggedInUser, String groupName) {
        this.controller = controller;
        this.loggedInUser = loggedInUser;
        this.groupName = groupName;
    }

    public Parent getPage() {
        // Set up the article list with initial articles
        updateArticleList("", "All");

        // Initialize filter components
        initializeFilterComponents();

        // Initialize search type ComboBox
        searchTypeComboBox = new ComboBox<>();
        searchTypeComboBox.getItems().addAll("All", "Title", "Author", "Abstract", "Description", "Keywords");
        searchTypeComboBox.setValue("All");

        // Search bar
        searchField.setPromptText("Enter search term");
        Button btnSearch = new Button("Search");
        btnSearch.setOnAction(e -> performSearch());
        HBox searchLayout = new HBox(10, searchTypeComboBox, searchField, btnSearch, levelComboBox, filterButton);
        searchLayout.setAlignment(Pos.CENTER);
        searchLayout.setPadding(new Insets(10));

        // Article list
        articleList.setPrefWidth(300);
        articleList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Article article = articleDatabaseHandler.getArticleByTitle(newValue);
                articleDetails.setText(article != null ? article.toString() : "Article details not found.");
            }
        });

        // Article details
        articleDetails.setEditable(false);
        articleDetails.setWrapText(true);

        // Action buttons
        Button btnGetHelp = new Button("Get Help");
        btnGetHelp.setOnAction(e -> controller.showGetHelpPage(loggedInUser, groupName));

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> controller.showListOfGroupsPage(loggedInUser));

        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> controller.logout());

        VBox buttonLayout = new VBox(10, btnGetHelp, btnBack, btnLogout);
        buttonLayout.setAlignment(Pos.TOP_CENTER);
        buttonLayout.setPadding(new Insets(10));

        // Main layout
        HBox mainLayout = new HBox(10, articleList, articleDetails, buttonLayout);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setHgrow(articleDetails, Priority.ALWAYS);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(searchLayout); // Add search layout to the top
        borderPane.setCenter(mainLayout);

        return borderPane;
    }

    // Method to initialize the filter components
    private void initializeFilterComponents() {
        levelComboBox = new ComboBox<>();
        levelComboBox.getItems().addAll("All", "Beginner", "Intermediate", "Advanced", "Expert");
        levelComboBox.setValue("All");

        filterButton = new Button("Filter by Level");
        filterButton.setOnAction(e -> filterArticlesByLevel());
    }

    // Method to filter articles by level
    private void filterArticlesByLevel() {
        String selectedLevel = levelComboBox.getValue();
        List<Article> articles = selectedLevel.equals("All") ?
                articleDatabaseHandler.getArticles() :
                articleDatabaseHandler.getArticlesByLevel(selectedLevel);

        articleList.getItems().clear();
        for (Article article : articles) {
            if (article.getSpecialGroups().contains(groupName) && !article.isHidden()) {
                articleList.getItems().add(article.getTitle());
            }
        }
    }

    // Method to update the article list based on a keyword filter and search type
    private void updateArticleList(String keyword, String searchType) {
        articleList.getItems().clear();

        // Fetch all articles and filter based on the selected search type
        List<Article> articles = articleDatabaseHandler.getArticles();
        for (Article article : articles) {
            if (article.getSpecialGroups().contains(groupName) && !article.isHidden()) { // Exclude hidden articles
                boolean matches = false;

                switch (searchType) {
                    case "Title":
                        matches = article.getTitle().toLowerCase().contains(keyword.toLowerCase());
                        break;
                    case "Author":
                        matches = article.getAuthor().toLowerCase().contains(keyword.toLowerCase());
                        break;
                    case "Abstract":
                        matches = article.getArticleAbstract().toLowerCase().contains(keyword.toLowerCase());
                        break;
                    case "Description":
                        matches = article.getBody().toLowerCase().contains(keyword.toLowerCase());
                        break;
                    case "Keywords":
                        matches = article.getKeywords().toLowerCase().contains(keyword.toLowerCase());
                        break;
                    case "All":
                    default:
                        matches = article.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                                  article.getAuthor().toLowerCase().contains(keyword.toLowerCase()) ||
                                  article.getArticleAbstract().toLowerCase().contains(keyword.toLowerCase()) ||
                                  article.getBody().toLowerCase().contains(keyword.toLowerCase()) ||
                                  article.getKeywords().toLowerCase().contains(keyword.toLowerCase());
                        break;
                }

                if (matches) {
                    articleList.getItems().add(article.getTitle());
                }
            }
        }
    }

    // Method to perform the search based on the entered keyword and search type
    private void performSearch() {
        String keyword = searchField.getText().trim();
        String searchType = searchTypeComboBox.getValue();
        updateArticleList(keyword, searchType);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("View Articles - Student");
        Scene scene = new Scene(getPage(), 800, 600); // Adjusted size for better layout
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
