package cse360;
import java.util.ArrayList;

public class Database {
    private static ArrayList<User> users = new ArrayList<>();

    public static void addUser(User user) {
        users.add(user);
    }

    public static User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null; // Return null if the user is not found
    }
}
