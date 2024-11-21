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
import java.util.List;

public class GroupUserManagementPage extends Application {
    private maincontroller controller;
    private TableView<User> tableView;
    private ObservableList<User> userObservableList;
    
    private User currentUser; // Store the current user (the admin teacher)
    private String groupName; // The specific group name

    // Constructor that accepts MainController, current User, and group name for navigation
    public GroupUserManagementPage(maincontroller controller, User currentUser, String groupName) {
        this.controller = controller;
        this.currentUser = currentUser;
        this.groupName = groupName; // Set the specific group name
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Group User Management - " + groupName);

        // Create the scene using getPage() and set it on the primary stage
        Scene scene = new Scene(getPage(), 800, 400); // Default size for demonstration
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to construct and return the main layout for GroupUserManagementPage
    public Parent getPage() {
        // Set up the TableView and its columns
        userObservableList = FXCollections.observableArrayList();
        tableView = new TableView<>();
        setupTableColumns();

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> controller.showViewArticlesTeacherPage(currentUser, groupName));
        btnBack.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px;");

        // Button to refresh user data in the group
        Button btnRefresh = new Button("Refresh");
        btnRefresh.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px;");
        btnRefresh.setOnAction(e -> refreshUserData());

        // Button to add a new user to the group from existing users
        Button btnAddUser = new Button("Add User to Group");
        btnAddUser.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px;");
        btnAddUser.setOnAction(e -> addUserToGroup());

        // Button to remove the selected user from the group
        Button btnRemoveUser = new Button("Remove Selected User");
        btnRemoveUser.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px;");
        btnRemoveUser.setOnAction(e -> removeUserFromGroup());

        // Layout configuration using VBox with spacing and alignment
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(tableView, btnRefresh, btnAddUser, btnRemoveUser, btnBack);

        // Initial load of user data into the TableView
        refreshUserData();

        return layout; // Return the main layout
    }

    // Set up columns for the TableView to display user information
    private void setupTableColumns() {
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

        tableView.getColumns().addAll(firstNameCol, middleNameCol, lastNameCol, emailCol);
    }

    // Method to refresh and reload user data into the TableView for the specific group
    private void refreshUserData() {
        try {
            List<User> groupUsers = Database.getUsersInGroup(groupName); // Fetch users in the specified group
            userObservableList.clear(); // Clear current data in the observable list
            userObservableList.addAll(groupUsers); // Reload fresh data from the database
            tableView.setItems(userObservableList); // Set updated list in the table view
        } catch (SQLException e) {
            showError("Error loading group user data", e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to add a user to the group from the existing users
    private void addUserToGroup() {
        try {
            // Fetch all users who are not currently in the group
            List<User> nonGroupUsers = Database.getUsersNotInGroup(groupName);

            ChoiceDialog<User> dialog = new ChoiceDialog<>(null, nonGroupUsers);
            dialog.setTitle("Add User to Group");
            dialog.setHeaderText("Select a user to add to the group:");
            dialog.setContentText("User:");

            dialog.showAndWait().ifPresent(selectedUser -> {
                int userId = Database.getUserId(selectedUser.getUsername());
                if (userId != -1) {
                    // Call addUserToGroup with userId, groupName, and false for non-admin status
					Database.addUserToGroup(userId, groupName, false);
					showAlert("Success", "User added to the group successfully.");
					refreshUserData();
                } else {
                    showAlert("Error", "User ID not found for the selected user.");
                }
            });
        } catch (SQLException e) {
            showError("Error fetching users not in group", e.getMessage());
        }
    }





    // Method to remove the selected user from the group
    private void removeUserFromGroup() {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                Database.removeUserFromGroup(selectedUser.getUsername(), groupName);
                showAlert("Success", "User removed from the group successfully.");
                refreshUserData();
            } catch (SQLException e) {
                showError("Error removing user from group", e.getMessage());
            }
        } else {
            showAlert("No Selection", "Please select a user to remove from the group.");
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
