package com.css123group.corr4_be;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TestDbInspect {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("=== registrations ===");
            try (PreparedStatement pst = conn.prepareStatement("SELECT * FROM registrations ORDER BY reg_id DESC LIMIT 50")) {
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("reg_id");
                        String email = rs.getString("email");
                        System.out.println("reg_id=" + id + " email=" + email);
                    }
                }
            } catch (Exception e) {
                System.err.println("Could not query registrations: " + e.getMessage());
            }

            System.out.println("=== user_credentials ===");
            try (PreparedStatement pst = conn.prepareStatement("SELECT * FROM user_credentials ORDER BY auth_id DESC LIMIT 50")) {
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("auth_id");
                        int cid = rs.getInt("c_id");
                        String email = null;
                        try { email = rs.getString("email"); } catch (Exception ex) { email = "<no email column>"; }
                        System.out.println("auth_id=" + id + " c_id=" + cid + " email=" + email);
                    }
                }
            } catch (Exception e) {
                System.err.println("Could not query user_credentials: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
