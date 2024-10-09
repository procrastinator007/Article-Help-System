package cse360;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;  // <-- Import Font class
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;



public class NewUser extends Application {
    
    // Attributes
    private Label label_ApplicationTitle = new Label("New User");
    private Label label_name = new Label("Enter your Name:");
    private TextField text_name = new TextField();
    private Label label_Username = new Label("Enter your Username:");
    private TextField text_Username = new TextField();
    private Label label_Password = new Label("Enter your Password:");
    private PasswordField text_Password = new PasswordField();
    private Label label_ConfirmPassword = new Label("Confirm your Password:");
    private PasswordField text_ConfirmPassword = new PasswordField();
    private Label label_errMessage = new Label("");
    private Label label_erMessage = new Label("");
    private Label label_Role = new Label("Select your role:");
    private Label label_classc = new Label("Enter Class Code:");
    private TextField text_classc = new TextField();
    private ComboBox<String> comboBox_Role = new ComboBox<>(); 
    private Button btn_SignUp = new Button("Sign Up");

    
    // Password requirements
    private Label label_Requirements = new Label("YOUR PASSWORD MUST SATISFY THE FOLLOWING:");
    private Label label_UpperCase = new Label("*At least one upper case letter");
    private Label label_LowerCase = new Label("*At least one lower case letter");
    private Label label_NumericDigit = new Label("*At least one numeric digit");
    private Label label_SpecialChar = new Label("*At least one special character");
    private Label label_LongEnough = new Label("*At least eight characters");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("New User Sign Up");

        // Create a root pane
        Pane theRoot = new Pane();

        // Set up the UI elements and add them to the pane
        setupLabelUI(label_ApplicationTitle, "Arial", 30, 400, Pos.CENTER, 250, 100);
        setupLabelUI(label_name, "Arial", 20, 400, Pos.CENTER, 250, 220);
        setupTextUI(text_name, "Arial", 18, 380, Pos.BASELINE_LEFT, 250, 250, true);
        setupLabelUI(label_Username, "Arial", 20, 400, Pos.CENTER, 250, 300);
        setupTextUI(text_Username, "Arial", 18, 380, Pos.BASELINE_LEFT, 250, 330, true);
        setupLabelUI(label_Password, "Arial", 20, 400, Pos.CENTER, 250, 380);
        setupTextUI(text_Password, "Arial", 18, 380, Pos.BASELINE_LEFT, 250, 410, true);
        text_Password.textProperty().addListener((observable, oldValue, newValue) -> validatePassword());

        setupLabelUI(label_ConfirmPassword, "Arial", 20, 400, Pos.CENTER, 250, 460);
        setupTextUI(text_ConfirmPassword, "Arial", 18, 380, Pos.BASELINE_LEFT, 250, 490, true);
        setupLabelUI(label_classc, "Arial", 20, 400, Pos.CENTER, 250, 540);
        setupTextUI(text_classc, "Arial", 18, 380, Pos.BASELINE_LEFT, 250, 570, true);
        text_ConfirmPassword.textProperty().addListener((observable, oldValue, newValue) -> validatePassword());
        setupLabelUI(label_Role, "Arial", 20, 400, Pos.CENTER, 250, 620);
        comboBox_Role.getItems().addAll("Student", "Instructor");  // Add "Student" and "Instructor" options
        comboBox_Role.setLayoutX(250);  // Set the x position
        comboBox_Role.setLayoutY(650);  // Set the y position
        comboBox_Role.setPrefWidth(380);
        comboBox_Role.setPrefHeight(30);
        
        btn_SignUp.setLayoutX(680);  // Position the button horizontally
        btn_SignUp.setLayoutY(710);  // Position the button vertically
        btn_SignUp.setPrefWidth(100);
        btn_SignUp.setPrefHeight(40);
        btn_SignUp.setOnAction(e -> handleSignUp());// Set the height to 50 pixels (or any value you prefer)


        setupLabelUI(label_errMessage, "Arial", 15, 400, Pos.CENTER, 580, 780);
        label_errMessage.setTextFill(Color.RED);
        setupLabelUI(label_erMessage, "Arial", 15, 400, Pos.CENTER, 550, 780);
        label_erMessage.setTextFill(Color.RED);

        setupLabelUI(label_Requirements, "Arial", 15, 400, Pos.BASELINE_LEFT, 10, 710);
        setupLabelUI(label_UpperCase, "Arial", 12, 400, Pos.BASELINE_LEFT, 10, 730);
        setupLabelUI(label_LowerCase, "Arial", 12, 400, Pos.BASELINE_LEFT, 10, 750);
        setupLabelUI(label_NumericDigit, "Arial", 12, 400, Pos.BASELINE_LEFT, 10, 770);
        setupLabelUI(label_SpecialChar, "Arial", 12, 400, Pos.BASELINE_LEFT, 10, 790);
        setupLabelUI(label_LongEnough, "Arial", 12, 400, Pos.BASELINE_LEFT, 10, 810);
     // Error message label position: below the confirm password field
        


        // Add all elements to the root pane
        theRoot.getChildren().addAll(label_ApplicationTitle, label_name, text_name, label_Username, text_Username, 
            label_Password, text_Password, label_ConfirmPassword, text_ConfirmPassword, 
            label_errMessage, label_erMessage, label_Requirements, label_UpperCase, label_LowerCase,
            label_NumericDigit, label_SpecialChar, label_LongEnough, label_Role, comboBox_Role, btn_SignUp, label_classc, text_classc);

        // Set up the scene and stage
        Scene theScene = new Scene(theRoot, 900, 900);
        primaryStage.setScene(theScene);
        primaryStage.show();
    }

    // Utility methods for setting up the UI components
    private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);        
    }

    private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e) {
        t.setFont(Font.font(ff, f));
        t.setMinWidth(w);
        t.setMaxWidth(w);
        t.setAlignment(p);
        t.setLayoutX(x);
        t.setLayoutY(y);        
        t.setEditable(e);
    }

    // Password validation logic
 // Password validation logic
    private void validatePassword() {
        String password = text_Password.getText();
        String confirmPassword = text_ConfirmPassword.getText();

        // Reset error messages
        label_errMessage.setText("");
        label_UpperCase.setTextFill(Color.RED);
        label_LowerCase.setTextFill(Color.RED);
        label_NumericDigit.setTextFill(Color.RED);
        label_SpecialChar.setTextFill(Color.RED);
        label_LongEnough.setTextFill(Color.RED);

        boolean valid = true;

        // Check password requirements
        if (!password.matches(".*[A-Z].*")) {
            label_UpperCase.setTextFill(Color.RED);
            valid = false;
        } else {
            label_UpperCase.setTextFill(Color.GREEN);
        }

        if (!password.matches(".*[a-z].*")) {
            label_LowerCase.setTextFill(Color.RED);
            valid = false;
        } else {
            label_LowerCase.setTextFill(Color.GREEN);
        }

        if (!password.matches(".*\\d.*")) {
            label_NumericDigit.setTextFill(Color.RED);
            valid = false;
        } else {
            label_NumericDigit.setTextFill(Color.GREEN);
        }

        if (!password.matches(".*[!@#$%^&*].*")) {
            label_SpecialChar.setTextFill(Color.RED);
            valid = false;
        } else {
            label_SpecialChar.setTextFill(Color.GREEN);
        }

        if (password.length() < 8) {
            label_LongEnough.setTextFill(Color.RED);
            valid = false;
        } else {
            label_LongEnough.setTextFill(Color.GREEN);
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            label_errMessage.setText("*Passwords do not match!");
            valid = false;  // Set valid to false if passwords don't match
        }

        // Display a message if all conditions are met
        if (valid) {
            label_errMessage.setTextFill(Color.GREEN);
            label_errMessage.setText("Password is valid and matches!");
        } else {
            label_errMessage.setTextFill(Color.RED);
        }
    }
        private void handleSignUp() {
        	String name = text_name.getText();
            String username = text_Username.getText();
            String password = text_Password.getText();
            String confirmPassword = text_ConfirmPassword.getText();
            String selectedRole = comboBox_Role.getValue();

            StringBuilder errorMessage = new StringBuilder();

            // Check if the username is empty
            if (name.isEmpty()) {
                errorMessage.append("*Name is missing.\n");
            }
            if (username.isEmpty()) {
                errorMessage.append("*Username is missing.\n");
            }

            // Check if the password is empty
            if (password.isEmpty()) {
                errorMessage.append("*Password is missing.\n");
            }

            // Check if the confirm password is empty
            if (confirmPassword.isEmpty()) {
                errorMessage.append("*Confirm Password is missing.\n");
            }

            // Check if the role is selected
            if (selectedRole == null) {
                errorMessage.append("*Please select a role.\n");
            }
            if (confirmPassword.isEmpty()) {
                errorMessage.append("*Class Code is missing.\n");
            }

            // If there are any error messages, show them
            if (errorMessage.length() > 0) {
                label_errMessage.setText(errorMessage.toString());
                label_errMessage.setTextFill(Color.RED);
            } else {
            	label_errMessage.setText("");
                // Success: Do something like submit form or proceed to next step
                label_erMessage.setTextFill(Color.GREEN);
                label_erMessage.setText("*Sign Up successful!*");
                
            }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
