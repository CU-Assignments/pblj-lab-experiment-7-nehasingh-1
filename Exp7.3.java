import java.sql.*;
import java.util.Scanner;
class Student {
    private int studentID;
    private String name;
    private String department;
    private double marks;
    public Student(int studentID, String name, String department, double marks) {
        this.studentID = studentID;
        this.name = name;
        this.department = department;
        this.marks = marks;
    }
    public int getStudentID() {
        return studentID;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public double getMarks() {
        return marks;
    }
}

// View class: Displays data to the user
class StudentView {
    public void displayStudentDetails(Student student) {
        System.out.println("Student ID: " + student.getStudentID());
        System.out.println("Name: " + student.getName());
        System.out.println("Department: " + student.getDepartment());
        System.out.println("Marks: " + student.getMarks());
    }

    public void displayAllStudents(ResultSet rs) throws SQLException {
        while (rs.next()) {
            System.out.println("Student ID: " + rs.getInt("StudentID"));
            System.out.println("Name: " + rs.getString("Name"));
            System.out.println("Department: " + rs.getString("Department"));
            System.out.println("Marks: " + rs.getDouble("Marks"));
            System.out.println("------------------------");
        }
    }

    public void promptForDetails() {
        System.out.println("Enter Student Details:");
    }
}

// Controller class: Handles user inputs and manages the interaction between Model and View
class StudentController {
    private Connection conn;
    private StudentView view;

    public StudentController(Connection conn, StudentView view) {
        this.conn = conn;
        this.view = view;
    }

    public void addStudent(Student student) throws SQLException {
        String query = "INSERT INTO Student (StudentID, Name, Department, Marks) VALUES (?, ?, ?, ?)");
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, student.getStudentID());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getDepartment());
            stmt.setDouble(4, student.getMarks());
            stmt.executeUpdate();
            System.out.println("Student added successfully!");
        }
    }

    public void viewStudents() throws SQLException {
        String query = "SELECT * FROM Student";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            view.displayAllStudents(rs);
        }
    }

    public void updateStudent(int studentID, String name, String department, double marks) throws SQLException {
        String query = "UPDATE Student SET Name = ?, Department = ?, Marks = ? WHERE StudentID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, department);
            stmt.setDouble(3, marks);
            stmt.setInt(4, studentID);
            stmt.executeUpdate();
            System.out.println("Student updated successfully!");
        }
    }

    public void deleteStudent(int studentID) throws SQLException {
        String query = "DELETE FROM Student WHERE StudentID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentID);
            stmt.executeUpdate();
            System.out.println("Student deleted successfully!");
        }
    }
}

// Main class: Entry point to the application
public class StudentManagementApp {
    static final String URL = "jdbc:mysql://localhost:3306/StudentDB";
    static final String USER = "your_username";
    static final String PASSWORD = "your_password";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            StudentView view = new StudentView();
            StudentController controller = new StudentController(conn, view);

            while (true) {
                System.out.println("\n1. Add Student");
                System.out.println("2. View Students");
                System.out.println("3. Update Student");
                System.out.println("4. Delete Student");
                System.out.println("5. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        view.promptForDetails();
                        System.out.print("Enter Student ID: ");
                        int studentID = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Enter Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter Department: ");
                        String department = scanner.nextLine();
                        System.out.print("Enter Marks: ");
                        double marks = scanner.nextDouble();
                        controller.addStudent(new Student(studentID, name, department, marks));
                        break;
                    case 2:
                        controller.viewStudents();
                        break;
                    case 3:
                        System.out.print("Enter Student ID to update: ");
                        int updateID = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Enter new Name: ");
                        String newName = scanner.nextLine();
                        System.out.print("Enter new Department: ");
                        String newDepartment = scanner.nextLine();
                        System.out.print("Enter new Marks: ");
                        double newMarks = scanner.nextDouble();
                        controller.updateStudent(updateID, newName, newDepartment, newMarks);
                        break;
                    case 4:
                        System.out.print("Enter Student ID to delete: ");
                        int deleteID = scanner.nextInt();
                        controller.deleteStudent(deleteID);
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
