package cse360;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class CreateGroupPage {

    private final maincontroller controller;
    private final User currentUser;

    public CreateGroupPage(maincontroller controller, User currentUser) {
        this.controller = controller;
        this.currentUser = currentUser;
    }

    public Parent getPage() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Group Name Input
        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Group Name");

        // ListView for selecting secondary admins
        Label adminLabel = new Label("Select Secondary Admins:");
        ListView<String> adminListView = new ListView<>();
        adminListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // ListView for selecting regular users
        Label userLabel = new Label("Select Users (Non-Admins):");
        ListView<String> userListView = new ListView<>();
        userListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Populate both ListViews
        try {
            List<User> allUsers = Database.getAllUsers(); // Fetch all users
            List<String> formattedUsers = allUsers.stream()
                    .map(this::formatUserString) // Use a function to format the user details
                    .toList();
            adminListView.setItems(FXCollections.observableArrayList(formattedUsers));
            userListView.setItems(FXCollections.observableArrayList(formattedUsers));
        } catch (Exception e) {
            showAlert("Database Error", "Failed to load users from the database.");
        }

        // Create Group Button
        Button createButton = new Button("Create Group");
        createButton.setOnAction(e -> {
            String groupName = groupNameField.getText().trim();
            if (groupName.isEmpty()) {
                showAlert("Input Error", "Group name cannot be empty.");
                return;
            }

            List<String> selectedAdmins = adminListView.getSelectionModel().getSelectedItems();
            if (selectedAdmins.isEmpty()) {
                showAlert("Input Error", "You must select at least one user as an admin.");
                return;
            }

            try {
                // Add the current user as the primary admin
                int currentUserId = Database.getUserId(currentUser.getUsername());
                if (currentUserId != -1) {
                    Database.addUserToGroup(currentUserId, groupName, true);
                } else {
                    System.err.println("Error: Current user not found in the database.");
                }

                // Add selected secondary admins
                for (String admin : selectedAdmins) {
                    String username = extractUsernameFromUserString(admin);
                    int adminId = Database.getUserId(username);
                    if (adminId != -1) {
                        Database.addUserToGroup(adminId, groupName, true);
                    } else {
                        System.err.println("Error: Admin user not found in the database. Username: " + username);
                    }
                }

                // Add selected non-admin users
                List<String> selectedUsers = userListView.getSelectionModel().getSelectedItems();
                for (String user : selectedUsers) {
                    String username = extractUsernameFromUserString(user);
                    int userId = Database.getUserId(username);
                    if (userId != -1) {
                        Database.addUserToGroup(userId, groupName, false);
                    } else {
                        System.err.println("Error: Regular user not found in the database. Username: " + username);
                    }
                }

                showAlert("Success", "Group created successfully with the selected admins and users.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Database Error", "Failed to create the group. Please try again.");
            }
        });

        // Back Button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> controller.showSpecialGroupPage(currentUser));

        // Layout
        layout.getChildren().addAll(
                new Label("Enter the name of the new group:"),
                groupNameField,
                adminLabel,
                adminListView,
                userLabel,
                userListView,
                createButton,
                backButton
        );

        return layout;
    }

    private String formatUserString(User user) {
        return user.getFirstName() + " " + user.getLastName() + " (" + user.getUsername() + ")";
    }

    private String extractUsernameFromUserString(String userString) {
        return userString.split("\\(")[1].replace(")", "").trim();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
