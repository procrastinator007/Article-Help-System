package cse360;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class allHelpRequestPage {
    private maincontroller controller;
    private User loggedInUser;

    public allHelpRequestPage(maincontroller controller, User loggedInUser) {
        this.controller = controller;
        this.loggedInUser = loggedInUser;
    }

    public Parent getPage() {
        // Main layout
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);

        // Header label
        Label titleLabel = new Label("All Help Requests");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Scrollable container for help requests
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(getHelpRequestContainer());
        scrollPane.setFitToWidth(true);

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            System.out.println("Navigating back to View Articles Page.");
            controller.showViewArticlesPage(loggedInUser);
        });

        // Adding components to the main layout
        mainLayout.getChildren().addAll(titleLabel, scrollPane, backButton);

        return mainLayout;
    }

    // Method to get a container for help requests from all admin groups
    private VBox getHelpRequestContainer() {
        VBox helpRequestContainer = new VBox(10);
        helpRequestContainer.setPadding(new Insets(20));
        helpRequestContainer.setAlignment(Pos.CENTER);

        // Fetch all unique groups where the user is an admin
        List<User.SpecialGroup> adminGroups = getUniqueAdminGroups();
        if (!adminGroups.isEmpty()) {
            for (User.SpecialGroup group : adminGroups) {
                String groupName = group.getGroupName();
                Label groupLabel = new Label("Group: " + groupName);
                groupLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                helpRequestContainer.getChildren().add(groupLabel);

                List<String> helpRequests = MessagesDatabaseHandler.getAllMessagesFormatted(groupName);
                if (helpRequests != null && !helpRequests.isEmpty()) {
                    for (String helpRequest : helpRequests) {
                        try {
                            HBox messageBox = createHelpRequestBox(helpRequest);
                            helpRequestContainer.getChildren().add(messageBox);
                        } catch (Exception ex) {
                            System.err.println("Error processing help request for group " + groupName + ": " + ex.getMessage());
                        }
                    }
                } else {
                    Label noMessagesLabel = new Label("No help requests available in this group.");
                    noMessagesLabel.setStyle("-fx-font-style: italic;");
                    helpRequestContainer.getChildren().add(noMessagesLabel);
                }
            }
        } else {
            helpRequestContainer.getChildren().add(new Label("You are not an admin in any group."));
        }

        return helpRequestContainer;
    }

    // Method to fetch all unique admin groups for the logged-in user
    private List<User.SpecialGroup> getUniqueAdminGroups() {
        List<User.SpecialGroup> adminGroups = new ArrayList<>();
        Set<String> uniqueGroupNames = new HashSet<>();

        for (User.SpecialGroup group : loggedInUser.getSpecialGroups()) {
            if (group.isAdmin() && uniqueGroupNames.add(group.getGroupName())) {
                adminGroups.add(group);
            }
        }

        return adminGroups;
    }

    // Method to create a help request box with message details and status controls
    private HBox createHelpRequestBox(String helpRequest) throws Exception {
        String[] lines = helpRequest.split("\n");
        String fromUser = lines[0].split(": ")[1].trim();
        String toUser = lines[1].split(": ")[1].trim();
        String content = lines[2].split(": ")[1].trim();
        int messageId = MessagesDatabaseHandler.getMessageId(fromUser, toUser, content);

        if (messageId == -1) {
            throw new Exception("Unable to retrieve message ID.");
        }

        // Message label
        Label messageLabel = new Label(helpRequest);
        messageLabel.setMaxWidth(300);
        messageLabel.setWrapText(true);

        // Status combo box
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Received", "Work in Progress", "Resolved");
        statusComboBox.setValue(lines[3].split(": ")[1].trim());

        // Update status button
        Button updateStatusButton = new Button("Update Status");
        updateStatusButton.setOnAction(e -> {
            String selectedStatus = statusComboBox.getValue();
            if (MessagesDatabaseHandler.updateMessageStatus(messageId, selectedStatus)) {
                showAlert("Success", "Message status updated to: " + selectedStatus);
            } else {
                showAlert("Error", "Failed to update message status.");
            }
        });

        // Message box layout
        HBox messageBox = new HBox(10, messageLabel, statusComboBox, updateStatusButton);
        messageBox.setAlignment(Pos.CENTER_LEFT);
        return messageBox;
    }

    // Method to display an alert
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
