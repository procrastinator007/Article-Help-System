package cse360;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoginPage extends Application {
    private TextField textUsername = new TextField();
    private PasswordField textPassword = new PasswordField();
    private Label labelMessage = new Label("");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login Page");

        Label labelTitle = new Label("Login");
        labelTitle.setFont(new Font("Arial", 24));
        labelTitle.setTextFill(Color.web("#2F4F4F")); // Deep Navy

        Label labelUsername = new Label("Username:");
        Label labelPassword = new Label("Password:");
        
        Button btnLogin = new Button("Login");
        btnLogin.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-family: Arial;");
        btnLogin.setOnAction(e -> handleLogin(primaryStage));

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(labelTitle, labelUsername, textUsername, labelPassword, textPassword, btnLogin, labelMessage);
        
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleLogin(Stage currentStage) {
        String username = textUsername.getText();
        String password = textPassword.getText();

        // Retrieve user from the database
        User user = Database.getUser(username);

        // Check if the user exists and the password matches
        if (user != null && user.getPassword().equals(password)) {
            labelMessage.setTextFill(Color.GREEN);
            labelMessage.setText("Login successful!");
            openDisplayDatabaseFX(currentStage); // Launch DisplayDatabaseFX
        } else {
            labelMessage.setTextFill(Color.RED);
            labelMessage.setText("Invalid username or password.");
        }
    }

    // Open DisplayDatabaseFX upon successful login
    private void openDisplayDatabaseFX(Stage currentStage) {
        try {
            DisplayDatabaseFX displayDatabaseFX = new DisplayDatabaseFX();
            Stage displayStage = new Stage();
            displayDatabaseFX.start(displayStage);
            currentStage.close(); // Close the login page
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
