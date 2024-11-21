package cse360;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListOfGroupsPage {
    private maincontroller controller;
    private User loggedInUser;
    private ListView<String> groupsListView = new ListView<>();

    public ListOfGroupsPage(maincontroller controller, User loggedInUser) {
        this.controller = controller;
        this.loggedInUser = loggedInUser;
    }

    public Parent getPage() {
        // Load the list of unique groups the user is a member of
        updateGroupsList();

        Button btnSelectGroup = new Button("Select Group");
        btnSelectGroup.setOnAction(e -> handleGroupSelection());

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> controller.showLoginPage());

        VBox layout = new VBox(10, new Label("Select a Group:"), groupsListView, btnSelectGroup, btnBack);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    private void updateGroupsList() {
        // Retrieve the list of unique groups the user is a member of
        List<String> userGroups = getUniqueGroupNames();
        groupsListView.getItems().clear();
        groupsListView.getItems().addAll(userGroups);
    }

    private List<String> getUniqueGroupNames() {
        List<String> uniqueGroups = new ArrayList<>();
        Set<String> seenGroups = new HashSet<>();

        for (String groupName : loggedInUser.getGroupNamesList()) {
            if (seenGroups.add(groupName)) { // Add only if it's not already in the set
                uniqueGroups.add(groupName);
            }
        }

        return uniqueGroups;
    }

    private void handleGroupSelection() {
        String selectedGroup = groupsListView.getSelectionModel().getSelectedItem();
        if (selectedGroup != null) {
            boolean isAdminInGroup = false;

            // Check if the user is an admin in the selected group
            for (User.SpecialGroup group : loggedInUser.getSpecialGroups()) {
                if (group.getGroupName().equals(selectedGroup) && group.isAdmin()) {
                    isAdminInGroup = true;
                    break;
                }
            }

            // Redirect to the appropriate view based on admin status in the selected group
            if (isAdminInGroup) {
                controller.showViewArticlesTeacherPage(loggedInUser, selectedGroup);
            } else {
                controller.viewArticleStudent(loggedInUser, selectedGroup);
            }
        } else {
            showAlert("No Selection", "Please select a group to continue.");
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
