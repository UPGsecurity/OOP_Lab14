package org.example;

import java.sql.*;
import java.util.Scanner;

public class AuthorsBrowser {
    public static void main(String[] args) {
        String selectAll = "SELECT * FROM Authors";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectAll)) {

            System.out.println("All authors:");
            while (rs.next()) {
                int id = rs.getInt("AuthorID");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                System.out.printf("ID: %d | %s %s%n", id, firstName, lastName);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("\nEnter last-name prefix to search: ");
            String prefix = scanner.nextLine();

            String searchSQL = "SELECT * FROM Authors WHERE LastName LIKE ?";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(searchSQL)) {

                pstmt.setString(1, prefix + "%");
                ResultSet rs = pstmt.executeQuery();

                boolean found = false;
                while (rs.next()) {
                    found = true;
                    int id = rs.getInt("AuthorID");
                    String firstName = rs.getString("FirstName");
                    String lastName = rs.getString("LastName");
                    System.out.printf("ID: %d | %s %s%n", id, firstName, lastName);
                }
                if (!found) {
                    System.out.println("No results found.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }
}
