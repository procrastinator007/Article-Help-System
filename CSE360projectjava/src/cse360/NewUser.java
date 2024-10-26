package cse360;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import java.util.ArrayList;

public class NewUser extends Application {
    private maincontroller controller;

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
    private ComboBox<String> comboBox_Role = new ComboBox<>(FXCollections.observableArrayList("Admin", "Teacher", "Student"));
    private Button btn_SignUp = new Button("Sign Up");
    private Label label_errMessage = new Label("");

    // Constructor that accepts the MainController for navigation
    public NewUser(maincontroller controller) {
        this.controller = controller;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("New User Registration");

        // Create the scene using getPage() and set it on the primary stage
        Scene scene = new Scene(getPage(), 1024, 768); // Updated size for larger view
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Parent getPage() {
        VBox layoutCenter = new VBox(10); // Reduced spacing for more compact layout
        layoutCenter.setAlignment(Pos.TOP_CENTER);
        layoutCenter.setPadding(new Insets(30, 0, 0, 0)); // Reduced padding to bring content higher
        layoutCenter.setBackground(new Background(new BackgroundFill(Color.web("#FDF5E6"), CornerRadii.EMPTY, null))); // Light Beige

        setupUIElements();

        // Add all elements to the central layout
        layoutCenter.getChildren().addAll(label_ApplicationTitle, label_firstName, text_firstName, label_middleName, text_middleName,
                label_lastName, text_lastName, label_email, text_email, label_Username, text_Username,
                label_Password, text_Password, label_ConfirmPassword, text_ConfirmPassword,
                label_Role, comboBox_Role, label_InviteCode, text_InviteCode, label_errMessage);

        HBox layoutBottom = new HBox(10);
        layoutBottom.setAlignment(Pos.BOTTOM_RIGHT);
        layoutBottom.setPadding(new Insets(10)); // Reduced padding to save space
        layoutBottom.setBackground(new Background(new BackgroundFill(Color.web("#FDF5E6"), CornerRadii.EMPTY, null))); 
        layoutBottom.getChildren().add(btn_SignUp);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(layoutCenter);
        borderPane.setBottom(layoutBottom);

        btn_SignUp.setOnAction(e -> handleSignUp());

        return borderPane;
    }


    private void setupUIElements() {
        label_ApplicationTitle.setFont(Font.font("Arial", 24)); // Reduced title font size for compactness
        label_ApplicationTitle.setTextFill(Color.web("#2F4F4F"));

        setupLabelAndTextField(label_firstName, text_firstName);
        setupLabelAndTextField(label_middleName, text_middleName);
        setupLabelAndTextField(label_lastName, text_lastName);
        setupLabelAndTextField(label_email, text_email);
        setupLabelAndTextField(label_Username, text_Username);
        setupLabelAndTextField(label_Password, text_Password);
        setupLabelAndTextField(label_ConfirmPassword, text_ConfirmPassword);
        setupLabelAndTextField(label_InviteCode, text_InviteCode);

        label_Role.setFont(Font.font("Arial", 18)); // Reduced label font size
        label_Role.setTextFill(Color.web("#2F4F4F"));
        comboBox_Role.setMaxWidth(350); // Narrowed width to save space
        comboBox_Role.setPrefHeight(30);

        btn_SignUp.setFont(Font.font("Arial", 16)); // Reduced button font size
        btn_SignUp.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white;");
        btn_SignUp.setPrefWidth(200); // Narrowed button width
        btn_SignUp.setPrefHeight(40);

        label_errMessage.setFont(Font.font("Arial", 14)); // Slightly reduced font size for error messages
        label_errMessage.setTextFill(Color.web("#FF6F61"));
    }

    private void setupLabelAndTextField(Label label, TextField textField) {
        label.setFont(Font.font("Arial", 18)); // Reduced font size for labels
        label.setTextFill(Color.web("#2F4F4F"));
        textField.setFont(Font.font("Arial", 16)); // Reduced font size for text fields
        textField.setMaxWidth(350); // Reduced width for compactness
    }

    private void handleSignUp() {
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

        validateUserInput(firstName, lastName, email, username, password, confirmPassword, selectedRole, inviteCode, errorMessage);

        if (errorMessage.length() > 0) {
            label_errMessage.setText(errorMessage.toString());
        } else {
            label_errMessage.setTextFill(Color.GREEN);
            label_errMessage.setText("Sign Up successful!");

            User user = new User(firstName, middleName, lastName, email, username, password, selectedRole);
            Database.addUser(user);
            controller.showEntryPage();
        }
    }

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

        if ("Admin".equals(selectedRole)) {
            ArrayList<User> users = Database.getAllUsers();
            for (User user : users) {
                if ("Admin".equals(user.getRole())) {
                    errorMessage.append("*An admin already exists. Only one admin can be registered.\n");
                    break;
                }
            }
        }

        if ("Teacher".equals(selectedRole) || "Student".equals(selectedRole)) {
            if (inviteCode.isEmpty()) {
                errorMessage.append("*Class/Invite code is required for registering as a Teacher or Student.\n");
            } else if (!Database.validateInviteCode(inviteCode)) {
                errorMessage.append("*Invalid or expired class/invite code.\n");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
