
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Updates the delay status of flight LX354 in the FlightDB database.
 *
 * The previous implementation used Statement and concatenated
 * user input directly into an SQL query. This allowed SQL Injection:
 * malicious input could change the query and remove the WHERE condition,
 * causing all 299 arrival records to be updated.
 *
 * This implementation uses PreparedStatement with placeholders (?).
 * User input is bound as data rather than SQL code, preventing it
 * from changing the structure of the SQL statement.
 */
public class SimpleFlightUpdater {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/FlightDB"
                + "?allowLoadLocalInfile=true"
                + "&allowPublicKeyRetrieval=true"
                + "&serverTimezone=Asia/Jerusalem"
                + "&useSSL=false";

        String sql = "UPDATE Arrivals SET delay_status = ? "
                + "WHERE flight_number = ?";

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            System.out.print("Enter new delay status for LX354: ");
            String userInput = scanner.nextLine();

            try (Connection conn =
                         DriverManager.getConnection(url, "root", password);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, userInput);
                pstmt.setString(2, "LX354");

                int affectedRows = pstmt.executeUpdate();

                System.out.println("Rows updated: " + affectedRows);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
