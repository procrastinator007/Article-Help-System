package cse360;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;

public class EntryPage extends Application {

    public void start(Stage primaryStage) {
        // Create the main label for the entry page
        Label label_ApplicationTitle = new Label("CSE360 Help System");
        label_ApplicationTitle.setFont(new Font("Arial", 28));
        label_ApplicationTitle.setTextFill(Color.web("#2F4F4F")); // Deep Navy

        // Create buttons for different sign-in options
        Button btnNewUser = new Button("Register");
        Button btnLogin = new Button("Login");
        Button btnExit = new Button("Exit");

        // Set button styles to make them friendly and inviting
        String buttonStyle = "-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-family: Arial;"; // Coral for buttons
        btnNewUser.setStyle(buttonStyle);
        btnLogin.setStyle(buttonStyle);
        btnExit.setStyle(buttonStyle);

        // Set button sizes
        btnNewUser.setPrefWidth(300);
        btnNewUser.setPrefHeight(60);
        btnLogin.setPrefWidth(300);
        btnLogin.setPrefHeight(60);
        btnExit.setPrefWidth(300);
        btnExit.setPrefHeight(60);

        // Set actions for the buttons (these actions can be customized to open the respective forms)
        btnNewUser.setOnAction(e -> {
            NewUser newUserPage = new NewUser();
            Stage newUserStage = new Stage();
            newUserPage.start(newUserStage);
        });
        btnLogin.setOnAction(e -> handleLogin());
        btnExit.setOnAction(e -> primaryStage.close());

        // Layout container (VBox) for the label and buttons
        VBox layoutCenter = new VBox(20); // Spacing between elements is set to 20
        layoutCenter.setAlignment(Pos.CENTER); // Align all elements to the center
        layoutCenter.setPadding(new Insets(50, 0, 0, 0)); // Add padding to move label higher
        layoutCenter.getChildren().addAll(label_ApplicationTitle, btnNewUser, btnLogin, btnExit);

        // Create a BorderPane to organize layout positions
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(layoutCenter);
        borderPane.setBackground(new Background(new BackgroundFill(Color.web("#FDF5E6"), CornerRadii.EMPTY, null))); // Light Beige background

        // Get the screen bounds and set the scene to fill the screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(borderPane, screenBounds.getWidth(), screenBounds.getHeight());
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());

        // Set the title and scene to the stage
        primaryStage.setTitle("CSE360 Help System - Entry Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleLogin() {
        System.out.println("Redirecting to Login...");
        // Here you can implement the login process similar to Main.java's login() method
    }

    public static void main(String[] args) {
        launch(args);
    }
}