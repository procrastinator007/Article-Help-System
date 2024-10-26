package cse360;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class CreateArticlePage extends Application {
    private ArticleDatabaseHandler databaseHandler;
    private maincontroller controller;

    // Constructor to initialize the controller and database handler
    public CreateArticlePage(maincontroller controller) {
        this.controller = controller;
        this.databaseHandler = new ArticleDatabaseHandler(); // Initialize database handler
    }

    // Method to create the main layout for the CreateArticlePage
    public Parent getPage() {
        // Article input fields
        TextField titleField = new TextField();
        titleField.setPromptText("Article Title");

        TextField authorField = new TextField();
        authorField.setPromptText("Author");

        TextArea abstractField = new TextArea();
        abstractField.setPromptText("Abstract");

        TextField keywordsField = new TextField();
        keywordsField.setPromptText("Keywords (comma-separated)");

        TextArea bodyField = new TextArea();
        bodyField.setPromptText("Body");

        TextField referencesField = new TextField();
        referencesField.setPromptText("References (comma-separated)");

        // Save button to save the article data
        Button btnSave = new Button("Save Article");
        btnSave.setOnAction(e -> {
            // Retrieve data from fields
            String title = titleField.getText();
            String author = authorField.getText();
            String articleAbstract = abstractField.getText();
            String keywords = keywordsField.getText();
            String body = bodyField.getText();
            String references = referencesField.getText();

            // Create an article instance
            Article article = new Article(title, author, articleAbstract, keywords, body, references);

            // Save the article to the database
            databaseHandler.saveArticle(article);

            // Show confirmation alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Article saved successfully!");
            alert.showAndWait();
        });

        // Back button to navigate to the ViewArticlesPage
        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> controller.showViewArticlesPage());

        // Layout configuration
        VBox layout = new VBox(10, titleField, authorField, abstractField, keywordsField, bodyField, referencesField, btnSave, btnBack);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Create Article");
        Scene scene = new Scene(getPage(), 500, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
