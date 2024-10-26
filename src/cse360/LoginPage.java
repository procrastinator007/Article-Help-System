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
    // Text field for entering username
    private TextField textUsername = new TextField();
    // Password field for entering password
    private PasswordField textPassword = new PasswordField();
    // Label to display login messages
    private Label labelMessage = new Label("");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login Page");

        // Title label for the login page
        Label labelTitle = new Label("Login");
        labelTitle.setFont(new Font("Arial", 24)); // Set font size and style
        labelTitle.setTextFill(Color.web("#2F4F4F")); // Set color to deep navy

        // Labels for username and password fields
        Label labelUsername = new Label("Username:");
        Label labelPassword = new Label("Password:");
        
        // Login button with style settings
        Button btnLogin = new Button("Login");
        btnLogin.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-family: Arial;");
        btnLogin.setOnAction(e -> handleLogin(primaryStage)); // Set login action

        // Layout setup using VBox for vertical arrangement with spacing
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER); // Center align elements
        // Add all components to the layout
        layout.getChildren().addAll(labelTitle, labelUsername, textUsername, labelPassword, textPassword, btnLogin, labelMessage);
        
        // Create the scene with the layout and set dimensions
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show(); // Display the stage
    }

    // Method to handle the login logic
    private void handleLogin(Stage currentStage) {
        String username = textUsername.getText(); // Retrieve entered username
        String password = textPassword.getText(); // Retrieve entered password

        // Retrieve user from the database using the entered username
        User user = Database.getUser(username);

        // Check if the user exists and if the password matches
        if (user != null && user.getPassword().equals(password)) {
            labelMessage.setTextFill(Color.GREEN); // Set message color to green for success
            labelMessage.setText("Login successful!"); // Set success message
            openDisplayDatabaseFX(currentStage); // Open the next screen (DisplayDatabaseFX)
        } else {
            labelMessage.setTextFill(Color.RED); // Set message color to red for failure
            labelMessage.setText("Invalid username or password."); // Set error message
        }
    }

    // Method to open DisplayDatabaseFX when login is successful
    private void openDisplayDatabaseFX(Stage currentStage) {
        try {
            DisplayDatabaseFX displayDatabaseFX = new DisplayDatabaseFX();
            Stage displayStage = new Stage();
            displayDatabaseFX.start(displayStage); // Start the DisplayDatabaseFX stage
            currentStage.close(); // Close the login page stage
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace in case of an error
        }
    }

    // Main method to launch the application
    public static void main(String[] args) {
        launch(args); // Launch the application
    }
}
