package cse360;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class SpecialGroupPage {
    private maincontroller controller;
    private User currentUser;
    private ListView<String> groupList = new ListView<>();
    private TextArea groupDetails = new TextArea();

    public SpecialGroupPage(maincontroller controller, User currentUser) {
        this.controller = controller;
        this.currentUser = currentUser;

        if (controller == null) {
            System.err.println("Error: maincontroller is null in SpecialGroupPage constructor.");
        }

        if (currentUser == null) {
            System.err.println("Error: currentUser is null in SpecialGroupPage constructor.");
        } else {
            System.out.println("SpecialGroupPage initialized with user: " + currentUser.getUsername());
        }
    }

    public Parent getPage() {
        updateGroupList();

        groupList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Selected group: " + newValue);
                loadGroupUsers(newValue); // Fetch and display group users
            }
        });
        groupDetails.setEditable(false);
        groupDetails.setPromptText("Group Details:");

        // Buttons
        Button createGroupButton = new Button("Create Special Group");
        createGroupButton.setOnAction(e -> {
            System.out.println("Navigating to CreateGroupPage.");
            controller.showCreateGroupPage(currentUser);
        });

        Button editGroupButton = new Button("Edit Selected Group");
        editGroupButton.setOnAction(e -> {
            String selectedGroup = groupList.getSelectionModel().getSelectedItem();
            if (selectedGroup == null) {
                System.out.println("No group selected for editing.");
                showAlert("Selection Error", "Please select a group to edit.");
                return;
            }
            System.out.println("Editing group: " + selectedGroup);
            controller.showEditGroupPage(currentUser, selectedGroup);
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            System.out.println("Navigating back to the articles page.");
            controller.showViewArticlesPage(currentUser);
        });

        Button deleteGroupButton = new Button("Delete Selected Group");
        deleteGroupButton.setOnAction(e -> {
            String selectedGroup = groupList.getSelectionModel().getSelectedItem();
            if (selectedGroup == null || selectedGroup.equalsIgnoreCase("No special groups found.")) {
                showAlert("Selection Error", "Please select a group to delete.");
                return;
            }

            // Confirm group deletion
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, 
                "Are you sure you want to delete the group: " + selectedGroup + "?",
                ButtonType.YES, ButtonType.NO);
            confirmation.showAndWait();

            if (confirmation.getResult() == ButtonType.YES) {
                boolean success = Database.deleteGroup(selectedGroup);
                if (success) {
                    showAlert("Success", "Group '" + selectedGroup + "' deleted successfully.");
                    updateGroupList(); // Refresh the group list after deletion
                } else {
                    showAlert("Error", "Failed to delete group: " + selectedGroup);
                }
            }
        });

        // Add the Delete Group button to the layout
        VBox layout = new VBox(10, groupList, groupDetails, createGroupButton, editGroupButton, deleteGroupButton, backButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);


        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(layout);

        return borderPane;
    }

    public void showPage(Stage primaryStage) {
        primaryStage.setTitle("Special Groups");
        if (validateUserRole()) {
            System.out.println("User role validated. Loading group names...");
            updateGroupList();
        } else {
            System.out.println("User role validation failed.");
        }
        Scene scene = new Scene(getPage(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateGroupList() {
        groupList.getItems().clear();

        try {
            System.out.println("Loading group names for user: " + currentUser.getUsername());

            // Retrieve special groups for the current user
            List<String> specialGroups = Database.getUserSpecialGroups(currentUser.getUsername());
            System.out.println("Special groups retrieved: " + specialGroups);

            if (specialGroups.isEmpty()) {
                System.out.println("No special groups found for user: " + currentUser.getUsername());
                groupList.getItems().add("No special groups found.");
            } else {
                groupList.getItems().addAll(specialGroups);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: An exception occurred while loading group names.");
            groupList.getItems().add("Error: Failed to load group names.");
        }

        if (groupList.getItems().isEmpty()) {
            System.err.println("No group names were loaded into the ListView.");
        }
    }

    private void loadGroupUsers(String groupName) {
        try {
            List<User> users = Database.getUsersInGroup(groupName);
            if (users.isEmpty()) {
                groupDetails.setText("No users found in this group.");
            } else {
                String userDetails = users.stream()
                        .map(user -> String.format("%s %s (%s) - %s",
                                user.getFirstName(), user.getLastName(), user.getUsername(), user.getRole()))
                        .collect(Collectors.joining("\n"));
                groupDetails.setText("Users in group '" + groupName + "':\n" + userDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            groupDetails.setText("Error loading users for group: " + groupName);
        }
    }

    private boolean validateUserRole() {
        try {
            System.out.println("Validating role for user: " + currentUser.getUsername());
            User user = Database.getUser(currentUser.getUsername());
            if (user == null) {
                System.err.println("Error: User data not found in the database.");
                return false;
            }

            if (!user.getRole().equalsIgnoreCase("admin")) {
                System.out.println("Access Denied: User is not an admin.");
                return false;
            }

            System.out.println("Access approved for admin user.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: An error occurred while validating user role.");
            return false;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
