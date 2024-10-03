package cse360;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to the CSE360 System");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    register(scanner);
                    break;
                case 2:
                    login(scanner);
                    break;
                case 3:
                    running = false;
                    System.out.println("Thank you for using the CSE360 System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please choose 1, 2, or 3.");
            }
        }
        scanner.close();
    }

    private static void register(Scanner scanner) {
        String username;

        // Loop to keep asking for a valid username
        while (true) {
            System.out.println("Enter a username (must end with @asu.edu):");
            username = scanner.nextLine();

            // Check if the username ends with "@asu.edu"
            if (username.endsWith("@asu.edu")) {
                break;  // Exit the loop when the username is valid
            }

            System.out.println("Invalid username. It must end with @asu.edu. Please try again.");
        }

        // Check if the username is already registered
        if (Database.getUser(username) != null) {
            System.out.println("Username already exists. Please choose a different one.");
            return;  // Exit the method early if the username is taken
        }

        String password;
        // Loop to keep asking for a valid password
        while (true) {
            System.out.println("Enter a password (min 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special character):");
            password = scanner.nextLine();

            // Validate password based on the required rules
            if (isValidPassword(password)) {
                break;  // Exit the loop when the password is valid
            }

            System.out.println("Invalid password. Please try again.");
        }

        System.out.println("Confirm your password:");
        String confirmPassword = scanner.nextLine();

        // Check if the two passwords match
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Registration failed.");
            return;  // Exit the method early if validation fails
        }

        // Hash the password before storing it
        String hashedPassword = PasswordHash.hashPassword(password);

        // Create a new user and add to the database
        User newUser = new User(username, hashedPassword);
        Database.addUser(newUser);

        System.out.println("Registration successful! You can now log in.");
    }

    private static void login(Scanner scanner) {
        System.out.println("Enter your username:");
        String username = scanner.nextLine();

        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        // Fetch the user from the database
        User user = Database.getUser(username);

        if (user != null) {
            // Compare entered password hash with stored password hash
            if (PasswordHash.hashPassword(password).equals(user.getPasswordHash())) {
                System.out.println("Login successful! Welcome, " + username);
            } else {
                System.out.println("Invalid password.");
            }
        } else {
            System.out.println("No such user found.");
        }
    }

    // Function to validate if the password meets the criteria
    private static boolean isValidPassword(String password) {
        // Regular expression for at least 8 characters, 1 uppercase, 1 lowercase, 1 number, 1 special character
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
