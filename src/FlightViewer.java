
import java.sql.*;
import java.util.Scanner;

public class FlightViewer {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/FlightDB"
                + "?allowLoadLocalInfile=true"
                + "&allowPublicKeyRetrieval=true"
                + "&serverTimezone=Asia/Jerusalem"
                + "&useSSL=false";

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            System.out.print("Enter terminal number (2, 3, 4, 5): ");
            String terminal = scanner.nextLine().trim();

            if (!terminal.matches("[2345]")) {
                System.out.println("Invalid terminal number.");
                return;
            }

            String sql = """
                    SELECT A.id, A.flight_number, F.from_city,
                           A.scheduled, A.delay_status, A.terminal
                    FROM Arrivals A
                    INNER JOIN FlightRoutes F
                    ON A.flight_number = F.flight_number
                    WHERE A.terminal = ?
                    ORDER BY A.scheduled ASC
                    """;

            try (Connection conn =
                         DriverManager.getConnection(url, "root", password);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, terminal);

                try (ResultSet rs = pstmt.executeQuery()) {

                    int count = 0;

                    while (rs.next()) {
                        System.out.printf(
                                "ID: %d | Flight: %s | From: %s | Scheduled: %s | Status: %s | Terminal: %s%n",
                                rs.getInt("id"),
                                rs.getString("flight_number"),
                                rs.getString("from_city"),
                                rs.getString("scheduled"),
                                rs.getString("delay_status"),
                                rs.getString("terminal")
                        );
                        count++;
                    }

                    System.out.println("Total flights found: " + count);
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
