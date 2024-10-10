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

    private TableView<User> tableView;
    private ObservableList<User> userObservableList;
    private Label labelInviteCode = new Label();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("User Database");

        // Set up the TableView
        tableView = new TableView<>();
        setupTableColumns();

        // Refresh button
        Button btnRefresh = new Button("Refresh");
        btnRefresh.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px;");
        btnRefresh.setOnAction(e -> refreshUserData());

        // Generate invite code button
        Button btnGenerateInvite = new Button("Generate Invite Code");
        btnGenerateInvite.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px;");
        btnGenerateInvite.setOnAction(e -> generateInviteCode());

        // Delete button
        Button btnDelete = new Button("Delete Selected User");
        btnDelete.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px;");
        btnDelete.setOnAction(e -> deleteUser());

        // Exit button to go back to EntryPage
        Button btnExit = new Button("Exit");
        btnExit.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white; -fx-font-size: 18px;");
        btnExit.setOnAction(e -> exitToEntryPage(primaryStage));

        // Layout
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(tableView, btnRefresh, btnGenerateInvite, btnDelete, labelInviteCode, btnExit);

        // Initial load of user data
        refreshUserData();

        // Set the scene and show the stage
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Set up columns for the TableView
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

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setMinWidth(150);
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setMinWidth(100);
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        tableView.getColumns().addAll(firstNameCol, middleNameCol, lastNameCol, emailCol, usernameCol, roleCol);
    }

    // Method to refresh the user data in the TableView
    private void refreshUserData() {
        ArrayList<User> users = Database.getAllUsers();
        userObservableList = FXCollections.observableArrayList(users);
        tableView.setItems(userObservableList);
    }

    // Method to generate the invite code and display it
    private void generateInviteCode() {
        String inviteCode = Database.generateInviteCode();
        labelInviteCode.setText("Invite Code: " + inviteCode);
    }

    // Method to delete the selected user
    private void deleteUser() {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Database.deleteUser(selectedUser.getUsername());
            refreshUserData(); // Refresh the table to show the updated user list
        } else {
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
