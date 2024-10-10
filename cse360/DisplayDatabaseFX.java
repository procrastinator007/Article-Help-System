package cse360;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class DisplayDatabaseFX extends javafx.application.Application {

    private TableView<DataModel> table;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Database Viewer");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);

        table = new TableView<>();
        setupTableColumns();
        loadDataFromDatabase();

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #FDF5E6;");
        vbox.getChildren().add(table);

        Scene scene = new Scene(vbox);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupTableColumns() {
        TableColumn<DataModel, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameColumn.setMinWidth(150);
        firstNameColumn.setStyle("-fx-background-color: #88C999; -fx-text-fill: #2F4F4F;");

        TableColumn<DataModel, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameColumn.setMinWidth(150);
        lastNameColumn.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #2F4F4F;");

        TableColumn<DataModel, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleColumn.setMinWidth(150);
        roleColumn.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: #2F4F4F;");

        TableColumn<DataModel, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setMinWidth(200);
        emailColumn.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #2F4F4F;");

        table.getColumns().addAll(firstNameColumn, lastNameColumn, roleColumn, emailColumn);
    }

    private void loadDataFromDatabase() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("/mnt/data/database.ser"))) {
            List<DataModel> dataList = (List<DataModel>) ois.readObject();
            table.getItems().addAll(dataList);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class DataModel implements java.io.Serializable {
    private String firstName;
    private String lastName;
    private String role;
    private String email;

    public DataModel(String firstName, String lastName, String role, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }
}