package cse360;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class viewarticlestudent {
    private maincontroller controller;
    private TextArea articleDetails = new TextArea();
    private ListView<String> articleList = new ListView<>();
    private TextField searchField = new TextField();

    public viewarticlestudent(maincontroller controller) {
        this.controller = controller;
    }

    public Parent getPage() {
        updateArticleList("");

        searchField.setPromptText("Enter keyword to search");
        Button btnSearch = new Button("Search");
        btnSearch.setOnAction(e -> performSearch());

        HBox searchLayout = new HBox(10, searchField, btnSearch);
        searchLayout.setAlignment(Pos.CENTER_RIGHT);
        searchLayout.setPadding(new Insets(10));

        articleList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Article article = new ArticleDatabaseHandler().getArticleByTitle(newValue.replace(" (Hidden)", ""));
                articleDetails.setText(article != null ? article.toString() : "Article details not found.");
            }
        });
        articleDetails.setEditable(false);

        Button btnExit = new Button("Exit");
        btnExit.setOnAction(e -> controller.logout());

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> controller.showEntryPage());

        VBox layout = new VBox(10, articleList, articleDetails, btnBack, btnExit);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(searchLayout);
        borderPane.setCenter(layout);

        return borderPane;
    }

    private void updateArticleList(String keyword) {
        ArticleDatabaseHandler handler = new ArticleDatabaseHandler();
        articleList.getItems().clear();
        
        // Fetch the list of article titles with the given keyword and only show non-hidden articles
        List<String> titles = handler.listArticleTitles(keyword, false);
        
        // Debugging print statement to check fetched titles
        System.out.println("Fetched Articles: " + titles);
        
        articleList.getItems().addAll(titles);
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        updateArticleList(keyword);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("View Articles");
        Scene scene = new Scene(getPage(), 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
