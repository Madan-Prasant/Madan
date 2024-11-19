import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import calculator.PercentageCalculator;

public class Demo {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/studentdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Madan28..";

    public static void main(String[] args) {
        PercentageCalculator calculator = new PercentageCalculator();

        try (Scanner ip = new Scanner(System.in)) {
            System.out.println("Enter student name:");
            String name = ip.nextLine();

            System.out.println("Enter roll number (format 12ABC123):");
            String rollNo = ip.nextLine();

            double[] subjectMarks = new double[5];
            double totalObtainedMarks = 0;
            for (int i = 0; i < 5; i++) {
                while (true) {
                    System.out.println("Enter marks for Subject " + (i + 1) + " (out of 50):");
                    double mark = ip.nextDouble();
                    if (mark > 50) {
                        System.out.println("Max mark per subject is 50. Please enter a valid mark.");
                    } else {
                        subjectMarks[i] = mark;
                        totalObtainedMarks += mark;
                        break;
                    }
                }
            }

            
            double adjustedObtainedMarks = totalObtainedMarks * 2;
            double totalMarks = totalObtainedMarks*2; 
            double percentage = calculator.calculatePercentage(adjustedObtainedMarks, 500); 
            System.out.println("The percentage is: " + percentage + "%");

           
            saveToDatabase(name, rollNo, subjectMarks, totalMarks, percentage);
        }
    }

    private static void saveToDatabase(String name, String rollNo, double[] subjectMarks, double totalMarks, double percentage) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "INSERT INTO students (name, roll_no, subject1, subject2, subject3, subject4, subject5, total_marks, percentage) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, rollNo);
            for (int i = 0; i < 5; i++) {
                preparedStatement.setDouble(3 + i, subjectMarks[i]); 
            }
            preparedStatement.setDouble(8, totalMarks);
            preparedStatement.setDouble(9, percentage);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Student data saved successfully!");
            }

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Please add it to your project's classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database. Please check the URL, username, or password.");
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
