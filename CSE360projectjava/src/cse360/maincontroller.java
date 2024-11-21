package cse360;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class maincontroller extends Application {
    private Stage primaryStage;
    private Scene entryPageScene, loginPageScene, newUserScene, displayDatabaseScene,
            viewArticlesPageScene, viewArticlesTeacherPageScene, displayUserDatabaseScene,
            createArticlePageScene, viewarticlestudentScene, listOfGroupsPageScene, specialGroupScene, getHelpScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Initialize the first scene
        showEntryPage();
        primaryStage.setTitle("CSE360 App");
        primaryStage.setOnCloseRequest(event -> Database.close()); // Close DB connection on app close
        primaryStage.show();
    }

    public void showEntryPage() {
        EntryPage entryPage = new EntryPage(this);
        if (entryPage.getPage() == null) {
            System.err.println("Error: EntryPage root is null");
            return;
        }
        entryPageScene = new Scene(entryPage.getPage(), 1024, 768);
        primaryStage.setScene(entryPageScene);
    }

    public void showLoginPage() {
        LoginPage loginPage = new LoginPage(this);
        if (loginPage.getPage() == null) {
            System.err.println("Error: LoginPage root is null");
            return;
        }
        loginPageScene = new Scene(loginPage.getPage(), 1024, 768);
        primaryStage.setScene(loginPageScene);
    }

    public void showBackupRestorationPage(User user) {
        BackupRestorationPage backupRestorationPage = new BackupRestorationPage(this, user);
        Scene scene = new Scene(backupRestorationPage.getPage(), 1024, 768);
        primaryStage.setScene(scene);
    }

    public void showViewArticlesPage(User loggedInUser) {
        ViewArticlesPage viewArticlesPage = new ViewArticlesPage(this, loggedInUser);
        viewArticlesPageScene = new Scene(viewArticlesPage.getPage(), 1024, 768);
        primaryStage.setScene(viewArticlesPageScene);
    }

    public void showListOfGroupsPage(User loggedInUser) {
        ListOfGroupsPage listOfGroupsPage = new ListOfGroupsPage(this, loggedInUser);
        listOfGroupsPageScene = new Scene(listOfGroupsPage.getPage(), 1024, 768);
        primaryStage.setScene(listOfGroupsPageScene);
    }

    public void showViewArticlesTeacherPage(User loggedInUser, String groupName) {
        ViewArticlesTeacherPage viewArticlesTeacherPage = new ViewArticlesTeacherPage(this, loggedInUser, groupName);
        viewArticlesTeacherPageScene = new Scene(viewArticlesTeacherPage.getPage(), 1024, 768);
        primaryStage.setScene(viewArticlesTeacherPageScene);
    }
    
    public void showHelpTeacherPage(User user, String groupName) {
        HelpTeacherPage helpTeacherPage = new HelpTeacherPage(this, user, groupName);
        Scene helpTeacherScene = new Scene(helpTeacherPage.getPage(), 1024, 768);
        primaryStage.setScene(helpTeacherScene);
    }

    public void showCreateArticleForGroupPage(User currentUser, String groupName, Article articleToEdit) {
        CreateArticleForGroupPage createArticleForGroupPage = new CreateArticleForGroupPage(this, currentUser, groupName, articleToEdit);
        Scene createArticleForGroupScene = new Scene(createArticleForGroupPage.getPage(), 1024, 768);
        primaryStage.setScene(createArticleForGroupScene);
    }

    public void showGroupUserManagementPage(User user, String groupName) {
        GroupUserManagementPage groupUserManagementPage = new GroupUserManagementPage(this, user, groupName);
        Scene groupUserManagementScene = new Scene(groupUserManagementPage.getPage(), 1024, 768);
        primaryStage.setScene(groupUserManagementScene);
    }

    public void viewArticleStudent(User loggedInUser, String groupName) {
        viewarticlestudent viewarticlestudentPage = new viewarticlestudent(this, loggedInUser, groupName);
        viewarticlestudentScene = new Scene(viewarticlestudentPage.getPage(), 1024, 768);
        primaryStage.setScene(viewarticlestudentScene);
    }

    public void showCreateArticlePage(User currentUser) {
        showCreateArticlePage(currentUser, null); // Default to creating a new article if no article is provided
    }
    
    public void showGetHelpPage(User currentUser, String groupName) {
        GetHelpPage getHelpPage = new GetHelpPage(this, currentUser, groupName);
        Scene getHelpScene = new Scene(getHelpPage.getPage(), 1024, 768);
        primaryStage.setScene(getHelpScene);
    }


    public void showCreateArticlePage(User currentUser, Article articleToEdit) {
        CreateArticlePage createArticlePage = new CreateArticlePage(this, currentUser, articleToEdit);
        Scene createArticleScene = new Scene(createArticlePage.getPage(), 1024, 768);
        primaryStage.setScene(createArticleScene);
    }

    public void showDisplayUserDatabasePage(User currentUser) {
        DisplayDatabaseFX displayUserDatabase = new DisplayDatabaseFX(this, currentUser);
        displayUserDatabaseScene = new Scene(displayUserDatabase.getPage(), 1024, 768);
        primaryStage.setScene(displayUserDatabaseScene);
    }

    public void showEditArticlePage(Article article, User currentUser) {
        showCreateArticlePage(currentUser, article); // Call showCreateArticlePage with article to edit
    }

    public void showSpecialGroupPage(User currentUser) {
        SpecialGroupPage specialGroupPage = new SpecialGroupPage(this, currentUser);
        specialGroupScene = new Scene(specialGroupPage.getPage(), 1024, 768);
        primaryStage.setScene(specialGroupScene);
    }

    public void showCreateGroupPage(User currentUser) {
        CreateGroupPage createGroupPage = new CreateGroupPage(this, currentUser);
        Scene createGroupScene = new Scene(createGroupPage.getPage(), 1024, 768);
        primaryStage.setScene(createGroupScene);
    }

    public void showEditGroupPage(User currentUser, String groupName) {
        EditGroupPage editGroupPage = new EditGroupPage(this, currentUser, groupName);
        Scene editGroupScene = new Scene(editGroupPage.getPage(), 1024, 768);
        primaryStage.setScene(editGroupScene);
    }
    
    public void showHelpRequestPage(User loggedInUser) {
        if (primaryStage == null) {
            System.err.println("Error: primaryStage is not initialized.");
            return;
        }

        allHelpRequestPage allhelpRequestPage = new allHelpRequestPage(this, loggedInUser);
        Scene helpRequestScene = new Scene(allhelpRequestPage.getPage(), 1024, 768);
        primaryStage.setScene(helpRequestScene);
    }



    public void showNewUserPage() {
        NewUser newUserPage = new NewUser(this);
        if (newUserPage.getPage() == null) {
            System.err.println("Error: NewUserPage root is null");
            return;
        }
        newUserScene = new Scene(newUserPage.getPage(), 1024, 768);
        primaryStage.setScene(newUserScene);
    }

    public void logout() {
        showEntryPage(); // Go back to the entry page
    }

    public static void main(String[] args) {
        launch(args);
    }
}
