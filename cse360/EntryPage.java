package cse360;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class EntryPage extends Application {

    
    public void start(Stage primaryStage) {
        // Create the main label for the entry page
        Label label_ApplicationTitle = new Label("Sign In");
        label_ApplicationTitle.setStyle("-fx-font-size: 24px; -fx-font-family: Arial;");

        // Create buttons for different sign-in options
        Button btnNewUser = new Button("New User");
        Button btnStudent = new Button("Student");
        Button btnInstructor = new Button("Instructor");

        // Set button sizes
        btnNewUser.setPrefWidth(200);
        btnStudent.setPrefWidth(200);
        btnInstructor.setPrefWidth(200);

        // Set actions for the buttons (these actions can be customized to open the respective forms)
        btnNewUser.setOnAction(e -> handleNewUser());
        btnStudent.setOnAction(e -> handleStudentLogin());
        btnInstructor.setOnAction(e -> handleInstructorLogin());

        // Layout container (VBox) for the label and buttons
        VBox layout = new VBox(20); // Spacing between elements is set to 20
        layout.setAlignment(Pos.CENTER); // Center alignment for all elements
        layout.getChildren().addAll(label_ApplicationTitle, btnNewUser, btnStudent, btnInstructor);

        // Create the scene and set it to the stage
        Scene scene = new Scene(layout, 500, 400); // Window size: 400x300
        primaryStage.setTitle("Entry Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    private void handleNewUser() {
        System.out.println("Redirecting to New User Registration...");
        // Here you can redirect to a new user registration page or another scene
    }

    
    private void handleStudentLogin() {
        System.out.println("Redirecting to Student Login...");
        // Here you can redirect to the student login page or another scene
    }

    
    private void handleInstructorLogin() {
        System.out.println("Redirecting to Instructor Login...");
        // Here you can redirect to the instructor login page or another scene
    }

    public static void main(String[] args) {
        launch(args);
    }
}
