package cse360;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class GetHelpPage {
    private maincontroller controller;
    private User currentUser;
    private String groupName;
    private VBox layout; // Class-level variable for the main layout

    public GetHelpPage(maincontroller controller, User currentUser, String groupName) {
        this.controller = controller;
        this.currentUser = currentUser;
        this.groupName = groupName;
    }

    public Parent getPage() {
        // Initialize the main layout
        layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Header label
        Label titleLabel = new Label("Get Help");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Scrollable message container
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(getMessagesContainer());
        scrollPane.setFitToWidth(true);

        // Button to compose a new message
        Button composeButton = new Button("Write New Message");
        composeButton.setOnAction(e -> showComposeMessageDialog());

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> controller.viewArticleStudent(currentUser, groupName));

        layout.getChildren().addAll(titleLabel, scrollPane, composeButton, backButton);

        return layout;
    }

    // Method to display a dialog box for composing a new message
    private void showComposeMessageDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Compose New Message");

        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.setAlignment(Pos.CENTER);

        Label instructionLabel = new Label("Write your message below:");

        TextArea messageInput = new TextArea();
        messageInput.setPromptText("Enter your message here...");
        messageInput.setWrapText(true);

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String messageContent = messageInput.getText().trim();
            if (!messageContent.isEmpty()) {
                boolean success = MessagesDatabaseHandler.addMessage(
                        currentUser.getUsername(),
                        groupName + ":admin", // Admin of the group
                        groupName,
                        messageContent
                );
                if (success) {
                    showAlert("Success", "Message sent successfully.");
                    dialog.close();
                    // Refresh the message list
                    refreshMessages();
                } else {
                    showAlert("Error", "Failed to send message.");
                }
            } else {
                showAlert("Error", "Message cannot be empty.");
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> dialog.close());

        dialogLayout.getChildren().addAll(instructionLabel, messageInput, sendButton, cancelButton);

        Scene dialogScene = new Scene(dialogLayout, 400, 300);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    // Method to refresh messages in the layout
    private void refreshMessages() {
        layout.getChildren().set(1, getMessagesContainer()); // Replace the message container with a refreshed one
    }

    // Method to get a VBox container with updated messages
    private VBox getMessagesContainer() {
        VBox messageContainer = new VBox(10);

        List<String> userMessages = MessagesDatabaseHandler.getMessagesFromUser(currentUser.getUsername());
        if (userMessages != null && !userMessages.isEmpty()) {
            for (String message : userMessages) {
                // Extract message ID and details for delete operation
                String[] messageLines = message.split("\n");
                String fromUser = messageLines[0].split(": ")[1].trim();
                String toUser = messageLines[1].split(": ")[1].trim();
                String content = messageLines[2].split(": ")[1].trim();
                int messageId = MessagesDatabaseHandler.getMessageId(fromUser, toUser, content);

                if (messageId == -1) {
                    System.err.println("Error: Unable to retrieve message ID for: " + message);
                    continue;
                }

                // Create a label for the message
                Label messageLabel = new Label(message);

                // Delete button for the message
                Button deleteButton = new Button("Delete");
                deleteButton.setOnAction(e -> {
                    boolean deleted = MessagesDatabaseHandler.deleteMessage(messageId);
                    if (deleted) {
                        showAlert("Success", "Message deleted successfully.");
                        // Refresh the message list
                        refreshMessages();
                    } else {
                        showAlert("Error", "Failed to delete message.");
                    }
                });

                // Add the message label and delete button to an HBox
                HBox messageBox = new HBox(10, messageLabel, deleteButton);
                messageBox.setAlignment(Pos.CENTER_LEFT);
                messageContainer.getChildren().add(messageBox);
            }
        } else {
            Label noMessagesLabel = new Label("No messages sent yet.");
            messageContainer.getChildren().add(noMessagesLabel);
        }

        return messageContainer;
    }

    // Method to display an alert dialog
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
