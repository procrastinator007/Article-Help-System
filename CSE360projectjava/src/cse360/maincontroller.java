package cse360;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class maincontroller extends Application {
    private Stage primaryStage;
    private Scene entryPageScene, loginPageScene, newUserScene, displayDatabaseScene, viewArticlesPageScene, displayUserDatabaseScene, createArticlePageScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Initialize the first scene
        showEntryPage();
        primaryStage.setTitle("CSE360 App");
        primaryStage.show();
    }

    public void showEntryPage() {
        EntryPage entryPage = new EntryPage(this);
        if (entryPage.getPage() == null) {
            System.err.println("Error: EntryPage root is null");
            return;
        }
        entryPageScene = new Scene(entryPage.getPage(), 800, 600);
        primaryStage.setScene(entryPageScene);
    }

    public void showLoginPage() {
        LoginPage loginPage = new LoginPage(this);
        if (loginPage.getPage() == null) {
            System.err.println("Error: LoginPage root is null");
            return;
        }
        loginPageScene = new Scene(loginPage.getPage(), 800, 600);
        primaryStage.setScene(loginPageScene);
    }
    
    public void showViewArticlesPage() {
        ViewArticlesPage viewArticlesPage = new ViewArticlesPage(this);
        viewArticlesPageScene = new Scene(viewArticlesPage.getPage(), 1024, 768);
        primaryStage.setScene(viewArticlesPageScene);
    }

    public void showCreateArticlePage() {
        CreateArticlePage createArticlePage = new CreateArticlePage(this);
        createArticlePageScene = new Scene(createArticlePage.getPage(), 1024, 768);
        primaryStage.setScene(createArticlePageScene);
    }

    public void showDisplayUserDatabasePage() {
        DisplayDatabaseFX displayUserDatabase = new DisplayDatabaseFX(this);
        displayUserDatabaseScene = new Scene(displayUserDatabase.getPage(), 1024, 768);
        primaryStage.setScene(displayUserDatabaseScene);
    }



    public void showNewUserPage() {
        NewUser newUserPage = new NewUser(this);
        if (newUserPage.getPage() == null) {
            System.err.println("Error: NewUserPage root is null");
            return;
        }
        newUserScene = new Scene(newUserPage.getPage(), 800, 600);
        primaryStage.setScene(newUserScene);
    }
    

    public void showDisplayDatabasePage() {
        DisplayDatabaseFX displayDatabase = new DisplayDatabaseFX(this);
        if (displayDatabase.getPage() == null) {
            System.err.println("Error: DisplayDatabaseFX root is null");
            return;
        }
        displayDatabaseScene = new Scene(displayDatabase.getPage(), 800, 600);
        primaryStage.setScene(displayDatabaseScene);
    }
    
    public void logout() {
        showEntryPage(); // Go back to the entry page
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
