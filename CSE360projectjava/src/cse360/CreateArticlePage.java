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
    private ArticleDatabaseHandler databaseHandler = new ArticleDatabaseHandler();
    private maincontroller controller;

    public CreateArticlePage(maincontroller controller) {
        this.controller = controller;
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
            
         // Check for duplicate title before saving
            if (databaseHandler.getArticleByTitle(title) != null) {
                showAlert("An article with this title already exists.");
                return;
            }


            Article article = new Article(title, author, articleAbstract, keywords, body, references, level);
            databaseHandler.saveArticle(article);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Article saved successfully!");
            alert.showAndWait();
        });

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> controller.showViewArticlesPage());

        VBox layout = new VBox(10, titleField, authorField, abstractField, keywordsField, bodyField, referencesField, levelComboBox, btnSave, btnBack);
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Create Article");

        Scene scene = new Scene(getPage(), 500, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
