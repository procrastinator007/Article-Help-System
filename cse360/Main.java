package cse360;
import java.io.File;
import java.util.Scanner;

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
                    handleExit();
                    running = false;
                default:
                    System.out.println("Invalid choice. Please choose 1, 2, or 3.");
            }
        }
        scanner.close();
    }

     // Handle exiting and cleanup of temporary files
    private static void handleExit() {
        System.out.println("Exiting program...");

        // Remove temporary files
        File[] tempFiles = new File(".").listFiles((dir, name) -> name.endsWith(".tmp") || name.endsWith(".log"));
        if (tempFiles != null) {
            for (File tempFile : tempFiles) {
                if (tempFile.delete()) {
                    System.out.println("Deleted temporary file: " + tempFile.getName());
                }
            }
        }

        System.out.println("All temporary files deleted. Database is permanent.");
        System.exit(0);  // Exit the program
    }


    private static void register(Scanner scanner) {
        String firstName, middleName, lastName, username, email, password;

        // Ask for first name, middle name, last name, and validate lengths
        while (true) {
            System.out.println("Enter First Name (less than 15 chars):");
            firstName = scanner.nextLine();
            if (firstName.length() < 15) break;
            System.out.println("First name is too long.");
        }

        while (true) {
            System.out.println("Enter Middle Name (less than 15 chars):");
            middleName = scanner.nextLine();
            if (middleName.length() < 15) break;
            System.out.println("Middle name is too long.");
        }

        while (true) {
            System.out.println("Enter Last Name (less than 15 chars):");
            lastName = scanner.nextLine();
            if (lastName.length() < 15) break;
            System.out.println("Last name is too long.");
        }

        // Loop to keep asking for a valid email
        while (true) {
            System.out.println("Enter an email (must end with @asu.edu):");
            email = scanner.nextLine();
            if (email.endsWith("@asu.edu")) break;
            System.out.println("Invalid email. Please try again.");
        }

        // Loop for username validation
        while (true) {
            System.out.println("Enter a username (must end with @asu.edu):");
            username = scanner.nextLine();
            if (username.length() <= 15 ) break;
            System.out.println("Invalid username. It must be less than 15 characters and end with @asu.edu.");
        }

        // Check if username is already taken
        if (Database.getUser(username) != null) {
            System.out.println("Username already exists. Please choose a different one.");
            return;
        }

        // Ask for and confirm password (with validation)
        while (true) {
            System.out.println("Enter a password (min 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special character):");
            password = scanner.nextLine();
            System.out.println("Confirm your password:");
            String confirmPassword = scanner.nextLine();

            if (!password.equals(confirmPassword)) {
                System.out.println("Passwords do not match.");
            } else if (!isValidPassword(password)) {
                System.out.println("Invalid password format. Please try again.");
            } else {
                break;
            }
        }

        // Determine role: First user defaults to admin
        User newUser;
        if (Database.isEmpty()) {
    System.out.println("First user detected. Defaulting role to Admin.");
    newUser = new Admin(firstName, middleName, lastName, username, PasswordHash.hashPassword(password), email);
} else {
    System.out.println("Choose a role: 1 for Teacher, 2 for Student (required). Leave blank if not applicable.");
    
    String roleInput = scanner.nextLine().trim();
    
    // If roleInput is empty and it's not the first user, keep prompting
    while (roleInput.isEmpty()) {
        System.out.println("A role is required. Please choose a role: 1 for Teacher, 2 for Student.");
        roleInput = scanner.nextLine().trim();
    }

    int role = Integer.parseInt(roleInput);

    if (role == 1 || role == 2) {
        // Prompt for invite code
        System.out.print("Enter invite code: ");
        String inviteCode = scanner.nextLine().trim();

        while (inviteCode.isEmpty()) {
            System.out.println("Invite code is required. Please enter the invite code: ");
            inviteCode = scanner.nextLine().trim();
        }

        // Assign the role based on input
        if (role == 1) {
            newUser = new Teacher(firstName, middleName, lastName, username, PasswordHash.hashPassword(password), email, inviteCode);
        } else {
            newUser = new Student(firstName, middleName, lastName, username, PasswordHash.hashPassword(password), email, inviteCode);
        }
    } else {
        System.out.println("Invalid role selection. Please restart and choose a valid option.");
        return;
    }
}
        // Add new user to the database
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
                if (user instanceof Admin) {
                    showAdminView(scanner);
                } else if (user instanceof Teacher) {
                    showTeacherView(scanner, (Teacher) user);
                } else {
                    showStudentView(scanner, (Student) user);
                }
            } else {
                System.out.println("Invalid password.");
            }
        } else {
            System.out.println("No such user found.");
        }
    }

    // Show Admin view with privileges to manage users
    private static void showAdminView(Scanner scanner) {
        System.out.println("Admin view: You can view all users, add or delete users.");
        for (User user : Database.getAllUsers()) {
            System.out.println(user.getFullName() + " - " + user.getRole());
        }
    }

    // Show Teacher view
    private static void showTeacherView(Scanner scanner, Teacher teacher) {
        System.out.println("Teacher view: You can see your classes and students.");
    }

    // Show Student view
    private static void showStudentView(Scanner scanner, Student student) {
        System.out.println("Student view: You can see your classes and teacher's contact.");
    }

    // Password validation method
    private static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(passwordRegex);
    }
}
