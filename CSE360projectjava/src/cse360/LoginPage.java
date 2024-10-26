package cse360;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoginPage extends Application {
    private maincontroller controller;
    private TextField textUsername = new TextField(); // Text field for entering username
    private PasswordField textPassword = new PasswordField(); // Password field for entering password
    private ComboBox<String> comboBoxUserType = new ComboBox<>(); // ComboBox for selecting user type
    private Label labelMessage = new Label(""); // Label to display login messages

    // Constructor that accepts MainController for navigation
    public LoginPage(maincontroller controller) {
        this.controller = controller;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login Page");

        // Create the scene using getPage() and set it on the primary stage
        Scene scene = new Scene(getPage(), 1024, 768); // Updated size for a larger view
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to construct and return the main layout for LoginPage
    public Parent getPage() {
        // Clear existing items to prevent duplication
        comboBoxUserType.getItems().clear();
        comboBoxUserType.getItems().addAll("Admin", "Teacher", "Student");

        // UI elements setup
        Label labelTitle = new Label("Login");
        labelTitle.setFont(new Font("Arial", 32));
        labelTitle.setTextFill(Color.web("#2F4F4F"));

        Label labelUsername = new Label("Username:");
        labelUsername.setFont(new Font("Arial", 20));
        Label labelPassword = new Label("Password:");
        labelPassword.setFont(new Font("Arial", 20));

        Label labelUserType = new Label("Select User Type:");
        labelUserType.setFont(new Font("Arial", 20));

        textUsername.setPrefWidth(400);
        textUsername.setFont(new Font("Arial", 18));
        textPassword.setPrefWidth(400);
        textPassword.setFont(new Font("Arial", 18));

        Button btnLogin = new Button("Login");
        btnLogin.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-family: Arial;");
        btnLogin.setPrefWidth(200);
        btnLogin.setPrefHeight(50);
        btnLogin.setOnAction(e -> handleLogin());

        labelMessage.setFont(new Font("Arial", 16));

        // Back button to return to the entry page
        Button btnBack = new Button("Back");
        btnBack.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-family: Arial;");
        btnBack.setPrefWidth(200);
        btnBack.setPrefHeight(50);
        btnBack.setOnAction(e -> controller.showEntryPage()); // Go back to the entry page when clicked

        // Layout configuration
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPrefWidth(500);
        layout.setPrefHeight(600);
        layout.setStyle("-fx-padding: 50; -fx-background-color: #FDF5E6;");

        // Add all components to the layout
        layout.getChildren().addAll(labelTitle, labelUserType, comboBoxUserType, labelUsername, textUsername, labelPassword, textPassword, btnLogin, labelMessage, btnBack);

        return layout;
    }

    // Method to handle the login logic
    private void handleLogin() {
        String username = textUsername.getText(); // Retrieve entered username
        String password = textPassword.getText(); // Retrieve entered password
        String selectedUserType = comboBoxUserType.getValue(); // Get selected user type

        if (selectedUserType == null) {
            labelMessage.setTextFill(Color.RED);
            labelMessage.setText("Please select a user type."); // Error if no user type is selected
            return;
        }

        // Retrieve user from the database using the entered username
        User user = Database.getUser(username);

        // Check if the user exists, if the password matches, and if the user type matches
        if (user != null && user.getPassword().equals(password) && user.getRole().equalsIgnoreCase(selectedUserType)) {
            labelMessage.setTextFill(Color.GREEN); // Set message color to green for success
            labelMessage.setText("Login successful!"); // Set success message
         
            if (selectedUserType.equalsIgnoreCase("Admin")) {
                controller.showViewArticlesPage(); // Show full page for Admin
            } else if (selectedUserType.equalsIgnoreCase("Teacher")) {
                controller.showViewArticlesTeacherPage(); // Show restricted page for Teacher
            } else {
                controller.viewarticlestudent(); // Show limited view for Student
            }
        } else {	
            labelMessage.setTextFill(Color.RED); // Set message color to red for failure
            labelMessage.setText("Invalid credentials or user type."); // Set error message
        }
    }

    public static void main(String[] args) {
        launch(args); // Launch the application
    }
}
