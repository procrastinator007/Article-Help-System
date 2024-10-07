CSE 360  - Help System for students and teachers
Main.java: 
The Main.java file appears to be the entry point for a simple console-based system that handles user registration and login.
The program offers three main options:
Register a new user.
Login to the system.
Exit the system.

Registration involves gathering user details like first name, last name, username, and email, while ensuring input constraints (e.g., character limits). It seems users can be of different types, including Admin, Teacher, and Student.

For login, the system checks the user's credentials against a database, where passwords are hashed.

Based on the user's role (Admin, Teacher, or Student), the system presents a relevant view with options such as managing users for Admins or viewing classes for Teachers and Students.

There are references to the following classes:

Database for user storage and retrieval.
PasswordHash for hashing passwords.
User and its subclasses (Admin, Teacher, and Student).

Database.Java: 
The Database.java file manages user data for the system. It holds lists for Admin, Teacher, and Student users and provides methods to interact with them. Here's a summary of the key features:

getAllUsers: Returns all users in the system, useful for admins to view everyone.
addUser: Adds a user to the appropriate list based on their role (Admin, Teacher, or Student).
deleteUser: Deletes a user by username.
getUser: Retrieves a user by their username, used during login.
isEmpty: Checks if the database is empty, which might be relevant during the first registration.
Next, I'll check User.java, which likely defines the structure of a user, and its subclasses (Admin.java, Teacher.java, and Student.java). I'll start with User.java. ​​

User.java: 
Fields:

firstName, middleName, lastName: Names of the user.
username: Username for login purposes.
passwordHash: Stores the hashed password.
email: User's email.
Constructor: Initializes these fields upon user creation.

Methods:

getUsername(): Returns the username.
getPasswordHash(): Returns the password hash.
getFullName(): Combines first, middle, and last names into a full name.
getRole(): Abstract method that must be implemented by subclasses to return the role (e.g., Admin, Teacher, Student).
This abstract class will be extended by the Admin, Teacher, and Student classes. Let's now check the contents of Admin.java. ​​

Admin.java: 
The Admin.java file defines the Admin class, which extends the User abstract class. It overrides the getRole() method to return "Admin" as the role. Other functionalities, such as user details and password management, are inherited from the User class.

Teacher.Java:

The Teacher.java file defines the Teacher class, which extends the User class. Here's what it contains:

Fields:

classes[]: A placeholder for classes the teacher is handling (though it hasn't been initialized or used within the class yet).
Constructor: Inherits from User, allowing teacher-specific details to be passed in.

getRole(): Returns "Teacher" as the role.

Student.java:
The Student.java file defines the Student class, which extends the User class. Here are its details:

Fields:

className: Represents the class assigned to the student (though not yet utilized within the code).
Constructor: Inherits from User, allowing student-specific details to be passed during instantiation.

getRole(): Returns "Student" as the role.

Password.java:
The PasswordHash.java file provides a simple utility to hash passwords using the SHA-256 algorithm. Here’s a summary of its functionality:

hashPassword(String password):
Uses the MessageDigest class to hash the input password.
The resulting hash is converted into a hexadecimal string and returned.
This method will be used during both registration and login to ensure password security by comparing hashes rather than storing plain-text passwords.
