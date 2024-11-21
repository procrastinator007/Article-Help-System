package cse360;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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

    private maincontroller controller;

    // Constructor that takes MainController as a parameter for navigation
    public EntryPage(maincontroller controller) {
        this.controller = controller;
    }

    @Override
    public void start(Stage primaryStage) {
        // Create and set up the primary stage with the EntryPage scene
        primaryStage.setTitle("CSE360 Help System - Entry Page");

        // Create the scene and set it on the stage
        Scene scene = new Scene(getPage(), 800, 600);  // Default size for demonstration
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to return the main layout for the EntryPage
    public Parent getPage() {
        // Create the main label for the entry page
        Label label_ApplicationTitle = new Label("CSE360 Help System");
        label_ApplicationTitle.setFont(new Font("Arial", 28));
        label_ApplicationTitle.setTextFill(Color.web("#2F4F4F")); // Deep Navy

        // Create buttons for different sign-in options
        Button btnNewUser = new Button("Register");
        Button btnLogin = new Button("Login");
        Button btnExit = new Button("Exit");

        // Set button styles to make them friendly and inviting
        String buttonStyle = "-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-family: Arial;";
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

        // Set actions for the buttons to navigate within the MainController
        btnNewUser.setOnAction(e -> controller.showNewUserPage());
        btnLogin.setOnAction(e -> controller.showLoginPage());
        btnExit.setOnAction(e -> {
            controller.logout();  // Optional: perform any logout actions if needed
            System.exit(0);       // Terminate the application
        });
        // Layout container (VBox) for the label and buttons
        VBox layoutCenter = new VBox(20);
        layoutCenter.setAlignment(Pos.CENTER);
        layoutCenter.setPadding(new Insets(50, 0, 0, 0)); // Padding to move label higher
        layoutCenter.getChildren().addAll(label_ApplicationTitle, btnNewUser, btnLogin, btnExit);

        // Create a BorderPane to organize layout positions
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(layoutCenter);
        borderPane.setBackground(new Background(new BackgroundFill(Color.web("#FDF5E6"), CornerRadii.EMPTY, null))); // Light Beige background

        return borderPane; // Return the main layout
    }

    public static void main(String[] args) {
        launch(args);
    }
}
