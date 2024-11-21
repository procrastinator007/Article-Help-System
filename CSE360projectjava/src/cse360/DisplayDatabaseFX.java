package cse360;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

public class DisplayDatabaseFX extends Application {
    private maincontroller controller;
    private TableView<User> tableView;
    private ObservableList<User> userObservableList;
    
    private TextField textInviteCode = new TextField(); // Initialize the text field
    private User currentUser; // Store the current user

    // Constructor that accepts MainController and the current User for navigation
    public DisplayDatabaseFX(maincontroller controller, User currentUser) {
        this.controller = controller;
        this.currentUser = currentUser; // Store the current user for navigation purposes
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("User Database");

        // Create the scene using getPage() and set it on the primary stage
        Scene scene = new Scene(getPage(), 1000, 500); // Default size for demonstration
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to construct and return the main layout for DisplayDatabaseFX
    public Parent getPage() {
        // Set up the TableView and its columns
        userObservableList = FXCollections.observableArrayList();
        tableView = new TableView<>();
        setupTableColumns();

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> controller.showViewArticlesPage(currentUser)); // Pass currentUser to navigate back
        btnBack.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px;");

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
        btnExit.setOnAction(e -> controller.showEntryPage());

        // Configure the invite code text field to be non-editable and styled
        setupInviteCodeField();

        // Layout configuration using VBox with spacing and alignment
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(tableView, btnRefresh, btnGenerateInvite, btnDelete, textInviteCode, btnBack, btnExit);

        // Initial load of user data into the TableView
        refreshUserData();

        return layout; // Return the main layout
    }

    // Set up columns for the TableView to display user information
    private void setupTableColumns() {
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setMinWidth(100);
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> passwordCol = new TableColumn<>("Password");
        passwordCol.setMinWidth(100);
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setMinWidth(100);
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<User, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<User, String> middleNameCol = new TableColumn<>("Middle Name");
        middleNameCol.setMinWidth(100);
        middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));

        TableColumn<User, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> groupsCol = new TableColumn<>("Groups");
        groupsCol.setMinWidth(200);
        groupsCol.setCellValueFactory(new PropertyValueFactory<>("groupNames")); // Custom property to display group names

        TableColumn<User, String> adminStatusCol = new TableColumn<>("Admin Status in Groups");
        adminStatusCol.setMinWidth(200);
        adminStatusCol.setCellValueFactory(new PropertyValueFactory<>("adminStatus")); // Custom property for admin status

        tableView.getColumns().addAll(usernameCol, passwordCol, roleCol, firstNameCol, middleNameCol, lastNameCol, emailCol, groupsCol, adminStatusCol);
    }

    // Method to refresh and reload user data into the TableView from SQL database
    private void refreshUserData() {
        try {
            ArrayList<User> users = Database.getAllUsers(); // Fetch all users from the SQL database
            userObservableList.clear(); // Clear current data in the observable list
            userObservableList.addAll(users); // Reload fresh data from the database
            tableView.setItems(userObservableList); // Set updated list in the table view
        } catch (SQLException e) {
            showError("Error loading user data", e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to initialize invite code display
    private void setupInviteCodeField() {
        textInviteCode.setEditable(false);
        textInviteCode.setPromptText("Invite Code will appear here");
        textInviteCode.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: black;");
    }

    // Method to generate a new invite code
    private void generateInviteCode() {
        try {
            String inviteCode = Database.generateInviteCode(); // Get invite code from SQL database
            if (inviteCode != null && !inviteCode.isEmpty()) {
                textInviteCode.setText(inviteCode); // Display the invite code
            } else {
                textInviteCode.setText("Failed to generate invite code. Please try again.");
            }
        } catch (SQLException e) {
            showError("Error generating invite code", e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to delete the selected user from the TableView and SQL database
    private void deleteUser() {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Database.deleteUser(selectedUser.getUsername()); // Deletes from SQL database by username
            tableView.getItems().remove(selectedUser); // Remove from TableView immediately
            System.out.println("Deleted user from TableView: " + selectedUser.getUsername());
        } else {
            showAlert("No Selection", "Please select a user to delete.");
        }
    }

    // Utility method to show an alert dialog
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Utility method to show an error dialog
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args); // Launch the application
    }
}
