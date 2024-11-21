package cse360;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditGroupPage {

    private maincontroller controller;
    private User currentUser;
    private String groupName;

    public EditGroupPage(maincontroller controller, User currentUser, String groupName) {
        this.controller = controller;
        this.currentUser = currentUser;
        this.groupName = groupName;
    }

    public Parent getPage() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label groupLabel = new Label("Editing Group: " + groupName);

        // List of users in the group
        ListView<String> usersInGroupListView = new ListView<>();
        refreshGroupUsers(usersInGroupListView);

        Button removeUserButton = new Button("Remove Selected Users");
        removeUserButton.setOnAction(e -> handleRemoveUsers(usersInGroupListView));

        // List of users not in the group
        ListView<String> usersNotInGroupListView = new ListView<>();
        refreshUsersNotInGroup(usersNotInGroupListView);

        Button addUserButton = new Button("Add Selected Users as Regular Members");
        addUserButton.setOnAction(e -> handleAddUsers(usersNotInGroupListView, usersInGroupListView, false));

        Button addAdminButton = new Button("Add Selected Users as Admins");
        addAdminButton.setOnAction(e -> handleAddUsers(usersNotInGroupListView, usersInGroupListView, true));

        // Back Button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> controller.showSpecialGroupPage(currentUser));

        layout.getChildren().addAll(
                groupLabel,
                new Label("Users in Group:"),
                usersInGroupListView,
                removeUserButton,
                new Label("Users Not in Group:"),
                usersNotInGroupListView,
                addUserButton,
                addAdminButton,
                backButton
        );

        return layout;
    }

    private void handleAddUsers(ListView<String> usersNotInGroupListView, ListView<String> usersInGroupListView, boolean isAdmin) {
        List<String> selectedUsers = usersNotInGroupListView.getSelectionModel().getSelectedItems();
        if (selectedUsers.isEmpty()) {
            showAlert("Selection Error", "No users selected for addition.");
            return;
        }

        for (String user : selectedUsers) {
            String username = extractUsernameFromUserString(user);
            int userId = Database.getUserId(username);

            if (userId == -1) {
                showAlert("Error", "User not found in the database: " + username);
                continue; // Skip this user and process the next one
            }

            // Add user to the group with the respective role (admin or regular member)
            boolean success = Database.addUserToGroup(userId, groupName, isAdmin);
            if (success) {
                System.out.println("User added to group: " + username + " (Admin: " + isAdmin + ")");
            } else {
                showAlert("Error", "Failed to add user: " + username);
            }
        }

        String role = isAdmin ? "Admins" : "Regular Members";
        showAlert("Success", "Selected users added to the group as " + role + ".");
        refreshUsersNotInGroup(usersNotInGroupListView);
        refreshGroupUsers(usersInGroupListView);
    }

    private void handleRemoveUsers(ListView<String> usersInGroupListView) {
        List<String> selectedUsers = usersInGroupListView.getSelectionModel().getSelectedItems();
        if (selectedUsers.isEmpty()) {
            showAlert("Selection Error", "No users selected for removal.");
            return;
        }

        for (String user : selectedUsers) {
            String username = extractUsernameFromUserString(user);
            try {
                Database.removeUserFromGroup(username, groupName);
                System.out.println("User removed from group: " + username);
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to remove user: " + username);
            }
        }

        showAlert("Success", "Selected users removed from the group.");
        refreshGroupUsers(usersInGroupListView);
    }

    private void refreshGroupUsers(ListView<String> listView) {
        try {
            List<User> usersInGroup = Database.getUsersInGroup(groupName);
            List<String> formattedUsers = formatUserList(usersInGroup);
            listView.setItems(FXCollections.observableArrayList(formattedUsers));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to refresh users in the group.");
        }
    }

    private void refreshUsersNotInGroup(ListView<String> listView) {
        try {
            List<User> usersNotInGroup = Database.getUsersNotInGroup(groupName);
            List<String> formattedUsers = formatUserList(usersNotInGroup);
            listView.setItems(FXCollections.observableArrayList(formattedUsers));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to refresh users not in the group.");
        }
    }

    private List<String> formatUserList(List<User> users) {
        List<String> formattedUsers = new ArrayList<>();
        for (User user : users) {
            formattedUsers.add(formatUserString(user));
        }
        return formattedUsers;
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
