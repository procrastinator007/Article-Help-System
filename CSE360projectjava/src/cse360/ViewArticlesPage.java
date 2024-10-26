package cse360;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import cse360.maincontroller;

public class ViewArticlesPage {
    private maincontroller controller;
    private TextArea articleDetails = new TextArea();

    public ViewArticlesPage(maincontroller controller) {
        this.controller = controller;
    }

    public Parent getPage() {
        ListView<String> articleList = new ListView<>();
        articleList.getItems().addAll(new ArticleDatabaseHandler().listArticleTitles());

        articleList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Article article = new ArticleDatabaseHandler().getArticleByTitle(newValue);
                articleDetails.setText(article.toString());
            }
        });

        articleDetails.setEditable(false);

        Button btnDisplayUsers = new Button("User Database");
        btnDisplayUsers.setOnAction(e -> controller.showDisplayUserDatabasePage());

        Button btnCreateArticle = new Button("Create Article");
        btnCreateArticle.setOnAction(e -> controller.showCreateArticlePage());

        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> controller.logout());

        VBox layout = new VBox(10, articleList, articleDetails, btnDisplayUsers, btnCreateArticle, btnLogout);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return layout;
    }
}
