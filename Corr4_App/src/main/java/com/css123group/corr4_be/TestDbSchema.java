package com.css123group.corr4_be;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TestDbSchema {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            printColumns(conn, "registrations");
            printColumns(conn, "user_credentials");
        } catch (Exception e) {
            System.err.println("Schema inspection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printColumns(Connection conn, String table) throws Exception {
        System.out.println("Columns for table: " + table);
        String sql = "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = ? ORDER BY ordinal_position";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, table);
            try (ResultSet rs = pst.executeQuery()) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.println(" - " + rs.getString("column_name") + " (" + rs.getString("data_type") + ")");
                }
                if (!any) System.out.println("  <no columns found or table not present>");
            }
        }
    }
}
