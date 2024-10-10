package cse360;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import java.util.ArrayList;

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
    private Label label_InviteCode = new Label("Enter Class/Invite Code:");
    private TextField text_InviteCode = new TextField();
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
        setupUIElements();

        // Add all elements to the layout
        layoutCenter.getChildren().addAll(label_ApplicationTitle, label_firstName, text_firstName, label_middleName, text_middleName,
                label_lastName, text_lastName, label_email, text_email, label_Username, text_Username,
                label_Password, text_Password, label_ConfirmPassword, text_ConfirmPassword,
                label_Role, comboBox_Role, label_InviteCode, text_InviteCode, label_errMessage);

        // Align the sign-up button at the bottom right
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

        // Set button action
        btn_SignUp.setOnAction(e -> handleSignUp(primaryStage));
    }

    // Setup UI elements
    private void setupUIElements() {
        label_ApplicationTitle.setFont(Font.font("Arial", 30));
        label_ApplicationTitle.setTextFill(Color.web("#2F4F4F")); // Deep Navy text color

        setupLabelAndTextField(label_firstName, text_firstName);
        setupLabelAndTextField(label_middleName, text_middleName);
        setupLabelAndTextField(label_lastName, text_lastName);
        setupLabelAndTextField(label_email, text_email);
        setupLabelAndTextField(label_Username, text_Username);
        setupLabelAndTextField(label_Password, text_Password);
        setupLabelAndTextField(label_ConfirmPassword, text_ConfirmPassword);
        setupLabelAndTextField(label_InviteCode, text_InviteCode);

        label_Role.setFont(Font.font("Arial", 20));
        label_Role.setTextFill(Color.web("#2F4F4F"));
        comboBox_Role.getItems().addAll("Admin", "Teacher", "Student");
        comboBox_Role.setMaxWidth(400);
        comboBox_Role.setPrefHeight(30);

        btn_SignUp.setFont(Font.font("Arial", 18));
        btn_SignUp.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white;"); // Coral button color
        btn_SignUp.setPrefWidth(250);
        btn_SignUp.setPrefHeight(40);

        label_errMessage.setFont(Font.font("Arial", 15));
        label_errMessage.setTextFill(Color.web("#FF6F61")); // Coral for error messages
    }

    // Setup individual label and text fields
    private void setupLabelAndTextField(Label label, TextField textField) {
        label.setFont(Font.font("Arial", 20));
        label.setTextFill(Color.web("#2F4F4F"));
        textField.setFont(Font.font("Arial", 18));
        textField.setMaxWidth(400);
    }

    // Handle the sign-up process
    private void handleSignUp(Stage currentStage) {
        String firstName = text_firstName.getText();
        String middleName = text_middleName.getText();
        String lastName = text_lastName.getText();
        String email = text_email.getText();
        String username = text_Username.getText();
        String password = text_Password.getText();
        String confirmPassword = text_ConfirmPassword.getText();
        String selectedRole = comboBox_Role.getValue();
        String inviteCode = text_InviteCode.getText();

        StringBuilder errorMessage = new StringBuilder();

        // Validate user input
        validateUserInput(firstName, lastName, email, username, password, confirmPassword, selectedRole, inviteCode, errorMessage);

        // If there are errors, display them
        if (errorMessage.length() > 0) {
            label_errMessage.setText(errorMessage.toString());
        } else {
            label_errMessage.setTextFill(Color.GREEN);
            label_errMessage.setText("Sign Up successful!");

            // Create user and add to database
            User user = new User(firstName, middleName, lastName, email, username, password, selectedRole);
            Database.addUser(user);  // Add the user to the database
            openEntryPage(currentStage); // Open EntryPage when the user is registered
        }
    }

    // Validate user input fields
    private void validateUserInput(String firstName, String lastName, String email, String username, String password, String confirmPassword,
                                   String selectedRole, String inviteCode, StringBuilder errorMessage) {
        if (firstName.isEmpty() || firstName.length() >= 15) errorMessage.append("*First Name must be less than 15 characters.\n");
        if (!text_middleName.getText().isEmpty() && text_middleName.getText().length() >= 15) {
            errorMessage.append("*Middle Name must be less than 15 characters if provided.\n");
        }
        if (lastName.isEmpty() || lastName.length() >= 15) errorMessage.append("*Last Name must be less than 15 characters.\n");
        if (email.isEmpty() || !email.endsWith("@asu.edu")) errorMessage.append("*Email must end with @asu.edu.\n");
        if (username.isEmpty()) errorMessage.append("*Username is missing.\n");
        if (password.isEmpty()) errorMessage.append("*Password is missing.\n");
        if (confirmPassword.isEmpty()) errorMessage.append("*Confirm Password is missing.\n");
        if (!password.equals(confirmPassword)) errorMessage.append("*Passwords do not match.\n");
        if (selectedRole == null) errorMessage.append("*Please select a role.\n");

        // Check if the admin role already exists
        if ("Admin".equals(selectedRole)) {
            ArrayList<User> users = Database.getAllUsers();
            for (User user : users) {
                if ("Admin".equals(user.getRole())) {
                    errorMessage.append("*An admin already exists. Only one admin can be registered.\n");
                    break;
                }
            }
        }

        // Check invite code for Teacher or Student
        if ("Teacher".equals(selectedRole) || "Student".equals(selectedRole)) {
            if (inviteCode.isEmpty()) {
                errorMessage.append("*Class/Invite code is required for registering as a Teacher or Student.\n");
            } else if (!Database.validateInviteCode(inviteCode)) {
                errorMessage.append("*Invalid or expired class/invite code.\n");
            }
        }
    }

    // Open EntryPage and close current stage
    private void openEntryPage(Stage currentStage) {
        try {
            EntryPage entryPage = new EntryPage();
            Stage entryStage = new Stage();
            entryPage.start(entryStage);
            currentStage.close(); // Close the current stage
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
