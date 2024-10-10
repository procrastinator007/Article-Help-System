package cse360;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;

public class NewUser extends Application {
    // Attributes
    private Label label_ApplicationTitle = new Label("New User Registration");
    private Label label_firstName = new Label("Enter your First Name:");
    private TextField text_firstName = new TextField();
    private Label label_middleName = new Label("Enter your Middle Name:");
    private TextField text_middleName = new TextField();
    private Label label_lastName = new Label("Enter your Last Name:");
    private TextField text_lastName = new TextField();
    private Label label_email = new Label("Enter your Email:");
    private TextField text_email = new TextField();
    private Label label_Username = new Label("Enter your Username:");
    private TextField text_Username = new TextField();
    private Label label_Password = new Label("Enter your Password:");
    private PasswordField text_Password = new PasswordField();
    private Label label_ConfirmPassword = new Label("Confirm your Password:");
    private PasswordField text_ConfirmPassword = new PasswordField();
    private Label label_classCode = new Label("Enter Class Code:");
    private TextField text_classCode = new TextField();
    private Label label_Role = new Label("Select your role:");
    private ComboBox<String> comboBox_Role = new ComboBox<>();
    private Button btn_SignUp = new Button("Sign Up");
    private Label label_errMessage = new Label("");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("New User Registration");

        // Create a root layout
        VBox layoutCenter = new VBox(15);
        layoutCenter.setAlignment(Pos.TOP_CENTER);
        layoutCenter.setPadding(new Insets(50, 0, 0, 0));
        layoutCenter.setBackground(new Background(new BackgroundFill(Color.web("#FDF5E6"), CornerRadii.EMPTY, null))); // Light Beige background

        // Set up the UI elements
        label_ApplicationTitle.setFont(Font.font("Arial", 30));
        label_ApplicationTitle.setTextFill(Color.web("#2F4F4F")); // Deep Navy text color
        label_firstName.setFont(Font.font("Arial", 20));
        label_firstName.setTextFill(Color.web("#2F4F4F"));
        text_firstName.setFont(Font.font("Arial", 18));
        text_firstName.setMaxWidth(400);

        label_middleName.setFont(Font.font("Arial", 20));
        label_middleName.setTextFill(Color.web("#2F4F4F"));
        text_middleName.setFont(Font.font("Arial", 18));
        text_middleName.setMaxWidth(400);

        label_lastName.setFont(Font.font("Arial", 20));
        label_lastName.setTextFill(Color.web("#2F4F4F"));
        text_lastName.setFont(Font.font("Arial", 18));
        text_lastName.setMaxWidth(400);

        label_email.setFont(Font.font("Arial", 20));
        label_email.setTextFill(Color.web("#2F4F4F"));
        text_email.setFont(Font.font("Arial", 18));
        text_email.setMaxWidth(400);

        label_Username.setFont(Font.font("Arial", 20));
        label_Username.setTextFill(Color.web("#2F4F4F"));
        text_Username.setFont(Font.font("Arial", 18));
        text_Username.setMaxWidth(400);

        label_Password.setFont(Font.font("Arial", 20));
        label_Password.setTextFill(Color.web("#2F4F4F"));
        text_Password.setFont(Font.font("Arial", 18));
        text_Password.setMaxWidth(400);

        label_ConfirmPassword.setFont(Font.font("Arial", 20));
        label_ConfirmPassword.setTextFill(Color.web("#2F4F4F"));
        text_ConfirmPassword.setFont(Font.font("Arial", 18));
        text_ConfirmPassword.setMaxWidth(400);

        label_classCode.setFont(Font.font("Arial", 20));
        label_classCode.setTextFill(Color.web("#2F4F4F"));
        text_classCode.setFont(Font.font("Arial", 18));
        text_classCode.setMaxWidth(400);

        label_Role.setFont(Font.font("Arial", 20));
        label_Role.setTextFill(Color.web("#2F4F4F"));
        comboBox_Role.getItems().addAll("Student", "Instructor");
        comboBox_Role.setMaxWidth(400);
        comboBox_Role.setPrefHeight(30);

        btn_SignUp.setFont(Font.font("Arial", 18));
        btn_SignUp.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white;"); // Coral button color
        btn_SignUp.setOnAction(e -> handleSignUp());
        btn_SignUp.setPrefWidth(250);
        btn_SignUp.setPrefHeight(40);

        label_errMessage.setFont(Font.font("Arial", 15));
        label_errMessage.setTextFill(Color.web("#FF6F61")); // Coral for error messages

        // Add all elements to the layout
        layoutCenter.getChildren().addAll(label_ApplicationTitle, label_firstName, text_firstName, label_middleName, text_middleName,
                label_lastName, text_lastName, label_email, text_email, label_Username, text_Username,
                label_Password, text_Password, label_ConfirmPassword, text_ConfirmPassword, label_classCode, text_classCode,
                label_Role, comboBox_Role, label_errMessage);

        // Align the sign-up button at the bottom left
        HBox layoutBottomLeft = new HBox(10);
        layoutBottomLeft.setAlignment(Pos.BOTTOM_RIGHT);
        layoutBottomLeft.setPadding(new Insets(20));
        layoutBottomLeft.setBackground(new Background(new BackgroundFill(Color.web("#FDF5E6"), CornerRadii.EMPTY, null))); // Light Beige background
        layoutBottomLeft.getChildren().add(btn_SignUp);

        // Create a BorderPane to organize layout positions
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(layoutCenter);
        borderPane.setBottom(layoutBottomLeft);

        // Set up the scene and make the window full size
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene theScene = new Scene(borderPane, screenBounds.getWidth(), screenBounds.getHeight());
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
        primaryStage.setScene(theScene);
        primaryStage.show();
    }

    // Handle the sign-up process
    private void handleSignUp() {
        String firstName = text_firstName.getText();
        String middleName = text_middleName.getText();
        String lastName = text_lastName.getText();
        String email = text_email.getText();
        String username = text_Username.getText();
        String password = text_Password.getText();
        String confirmPassword = text_ConfirmPassword.getText();
        String selectedRole = comboBox_Role.getValue();
        String classCode = text_classCode.getText();

        StringBuilder errorMessage = new StringBuilder();

        // Validate user input
        if (firstName.isEmpty()) {
            errorMessage.append("*First Name is missing.\n");
        }
        if (lastName.isEmpty()) {
            errorMessage.append("*Last Name is missing.\n");
        }
        if (email.isEmpty()) {
            errorMessage.append("*Email is missing.\n");
        }
        if (username.isEmpty()) {
            errorMessage.append("*Username is missing.\n");
        }
        if (password.isEmpty()) {
            errorMessage.append("*Password is missing.\n");
        }
        if (confirmPassword.isEmpty()) {
            errorMessage.append("*Confirm Password is missing.\n");
        }
        if (selectedRole == null) {
            errorMessage.append("*Please select a role.\n");
        }
        if (selectedRole != null && selectedRole.equals("Student") && classCode.isEmpty()) {
            errorMessage.append("*Class Code is required.\n");
        }
        if (!password.equals(confirmPassword)) {
            errorMessage.append("*Passwords do not match.\n");
        }

        // If there are errors, display them
        if (errorMessage.length() > 0) {
            label_errMessage.setText(errorMessage.toString());
        } else {
            label_errMessage.setTextFill(Color.GREEN);
            label_errMessage.setText("Sign Up successful!");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}