package cse360;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main1 extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login Page");

        // Create a GridPane layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Username field
        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 1);
        TextField usernameField = new TextField();
        grid.add(usernameField, 1, 1);

        // Password field
        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 2);
        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);

        // Role selection combo box
        Label roleLabel = new Label("Role:");
        grid.add(roleLabel, 0, 3);
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Admin", "Teacher", "Student");
        grid.add(roleComboBox, 1, 3);

        // Login button
        Button loginButton = new Button("Login");
        grid.add(loginButton, 1, 4);

        // Add action to the login button
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = roleComboBox.getValue();

            // Validate login (this would involve using your backend `Database` class)
            User user = Database.getUser(username);
            if (user != null && PasswordHash.hashPassword(password).equals(user.getPasswordHash()) && user.getRole().equals(role)) {
                // Navigate to the appropriate dashboard based on role
                switch (role) {
                    case "Admin":
                        showAdminDashboard(primaryStage);
                        break;
                    case "Teacher":
                        showTeacherDashboard(primaryStage);
                        break;
                    case "Student":
                        showStudentDashboard(primaryStage);
                        break;
                }
            } else {
                // Show error message for invalid credentials
                System.out.println("Invalid credentials or role mismatch.");
            }
        });

        // Set up the scene
        Scene loginScene = new Scene(grid, 400, 300);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    // Method to show the Admin dashboard
    private void showAdminDashboard(Stage stage) {
        VBox adminLayout = new VBox(10);
        adminLayout.setPadding(new Insets(20));
        adminLayout.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome, Admin!");
        Button viewUsersButton = new Button("View All Users");
        Button addUserButton = new Button("Add User");
        Button deleteUserButton = new Button("Delete User");

        adminLayout.getChildren().addAll(welcomeLabel, viewUsersButton, addUserButton, deleteUserButton);

        // Add actions for each button (like viewing, adding, and deleting users)
        viewUsersButton.setOnAction(e -> {
            // Show a list of all users (using `Database.getAllUsers()`)
        });
        addUserButton.setOnAction(e -> {
            // Transition to the add user screen
        });
        deleteUserButton.setOnAction(e -> {
            // Transition to the delete user screen
        });

        Scene adminScene = new Scene(adminLayout, 400, 300);
        stage.setScene(adminScene);
        stage.setTitle("Admin Dashboard");
    }

    // Method to show the Teacher dashboard
    private void showTeacherDashboard(Stage stage) {
        VBox teacherLayout = new VBox(10);
        teacherLayout.setPadding(new Insets(20));
        teacherLayout.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome, Teacher!");
        Button viewClassesButton = new Button("View Classes");

        teacherLayout.getChildren().addAll(welcomeLabel, viewClassesButton);

        // Set up teacher-specific actions
        viewClassesButton.setOnAction(e -> {
            // Show teacher's assigned classes
        });

        Scene teacherScene = new Scene(teacherLayout, 400, 300);
        stage.setScene(teacherScene);
        stage.setTitle("Teacher Dashboard");
    }

    // Method to show the Student dashboard
    private void showStudentDashboard(Stage stage) {
        VBox studentLayout = new VBox(10);
        studentLayout.setPadding(new Insets(20));
        studentLayout.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome, Student!");
        Button viewCoursesButton = new Button("View Courses");

        studentLayout.getChildren().addAll(welcomeLabel, viewCoursesButton);

        // Set up student-specific actions
        viewCoursesButton.setOnAction(e -> {
            // Show student's assigned courses
        });

        Scene studentScene = new Scene(studentLayout, 400, 300);
        stage.setScene(studentScene);
        stage.setTitle("Student Dashboard");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
