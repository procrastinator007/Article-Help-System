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
import javafx.scene.control.Button;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;

public class LoginPage extends Application {

    // Attributes
    private Label label_ApplicationTitle = new Label("Login Page");
    private Label label_Username = new Label("Enter your Username:");
    private TextField text_Username = new TextField();
    private Label label_Password = new Label("Enter your Password:");
    private PasswordField text_Password = new PasswordField();
    private Button btn_Login = new Button("Login");
    private Label label_errMessage = new Label("");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login Page");

        // Create a root layout
        VBox layoutCenter = new VBox(15);
        layoutCenter.setAlignment(Pos.TOP_CENTER);
        layoutCenter.setPadding(new Insets(50, 0, 0, 0));
        layoutCenter.setBackground(new Background(new BackgroundFill(Color.web("#FDF5E6"), CornerRadii.EMPTY, null))); // Light Beige background

        // Set up the UI elements
        label_ApplicationTitle.setFont(Font.font("Arial", 30));
        label_ApplicationTitle.setTextFill(Color.web("#2F4F4F")); // Deep Navy text color
        label_Username.setFont(Font.font("Arial", 20));
        label_Username.setTextFill(Color.web("#2F4F4F"));
        text_Username.setFont(Font.font("Arial", 18));
        text_Username.setMaxWidth(400);

        label_Password.setFont(Font.font("Arial", 20));
        label_Password.setTextFill(Color.web("#2F4F4F"));
        text_Password.setFont(Font.font("Arial", 18));
        text_Password.setMaxWidth(400);

        btn_Login.setFont(Font.font("Arial", 18));
        btn_Login.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white;"); // Coral button color
        btn_Login.setPrefWidth(250);
        btn_Login.setPrefHeight(40);

        label_errMessage.setFont(Font.font("Arial", 15));
        label_errMessage.setTextFill(Color.web("#FF6F61")); // Coral for error messages

        // Add all elements to the layout
        layoutCenter.getChildren().addAll(label_ApplicationTitle, label_Username, text_Username, label_Password, text_Password, btn_Login, label_errMessage);

        // Create a BorderPane to organize layout positions
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(layoutCenter);

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

    public static void main(String[] args) {
        launch(args);
    }
}