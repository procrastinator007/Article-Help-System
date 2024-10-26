package cse360;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;

public class DisplayDatabaseFX extends Application {

    // TableView to display users
    private TableView<User> tableView;
    // ObservableList to hold users for display in the TableView
    private ObservableList<User> userObservableList;
    // Label to display the invite code
    private Label labelInviteCode = new Label();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("User Database");

        // Set up the TableView and its columns
        tableView = new TableView<>();
        setupTableColumns();

        // Refresh button to reload the user data
        Button btnRefresh = new Button("Refresh");
        btnRefresh.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px;");
        btnRefresh.setOnAction(e -> refreshUserData());

        // Button to generate a new invite code
        Button btnGenerateInvite = new Button("Generate Invite Code");
        btnGenerateInvite.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px;");
        btnGenerateInvite.setOnAction(e -> generateInviteCode());

        // Button to delete the selected user
        Button btnDelete = new Button("Delete Selected User");
        btnDelete.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px;");
        btnDelete.setOnAction(e -> deleteUser());

        // Exit button to go back to EntryPage
        Button btnExit = new Button("Exit");
        btnExit.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px;");
        btnExit.setOnAction(e -> exitToEntryPage(primaryStage));

        // Layout configuration using VBox with spacing and alignment
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        // Add all elements to the layout
        layout.getChildren().addAll(tableView, btnRefresh, btnGenerateInvite, btnDelete, labelInviteCode, btnExit);

        // Initial load of user data into the TableView
        refreshUserData();

        // Set the scene with the layout and dimensions, and show the stage
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Set up columns for the TableView to display user information
    private void setupTableColumns() {
        // Column for first name
        TableColumn<User, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        // Column for middle name
        TableColumn<User, String> middleNameCol = new TableColumn<>("Middle Name");
        middleNameCol.setMinWidth(100);
        middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));

        // Column for last name
        TableColumn<User, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        // Column for email
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Column for username
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setMinWidth(150);
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        // Column for user role
        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setMinWidth(100);
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Add all columns to the TableView
        tableView.getColumns().addAll(firstNameCol, middleNameCol, lastNameCol, emailCol, usernameCol, roleCol);
    }

    // Method to refresh and reload user data into the TableView
    private void refreshUserData() {
        // Fetch all users from the database
        ArrayList<User> users = Database.getAllUsers();
        // Convert the user list to an ObservableList for the TableView
        userObservableList = FXCollections.observableArrayList(users);
        tableView.setItems(userObservableList);
    }

    // Method to generate an invite code and display it in the label
    private void generateInviteCode() {
        // Get a new invite code from the database
        String inviteCode = Database.generateInviteCode();
        // Set the label to show the generated code
        labelInviteCode.setText("Invite Code: " + inviteCode);
    }

    // Method to delete the selected user from the TableView and database
    private void deleteUser() {
        // Get the selected user from the TableView
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            // Delete the user from the database using their username
            Database.deleteUser(selectedUser.getUsername());
            // Refresh the TableView to reflect the deletion
            refreshUserData();
        } else {
            // Show a warning if no user is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user to delete.");
            alert.showAndWait();
        }
    }

    // Method to exit to the EntryPage without altering the database
    private void exitToEntryPage(Stage currentStage) {
        try {
            // Create and show the EntryPage stage
            EntryPage entryPage = new EntryPage();
            Stage entryStage = new Stage();
            entryPage.start(entryStage);
            // Close the current stage
            currentStage.close();
        } catch (Exception e) {
            // Print the stack trace in case of an error
            e.printStackTrace();
        }
    }

    // Main method to launch the application
    public static void main(String[] args) {
        launch(args);
    }
}
