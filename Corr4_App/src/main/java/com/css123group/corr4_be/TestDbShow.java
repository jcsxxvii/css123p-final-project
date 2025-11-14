package com.css123group.corr4_be;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TestDbShow {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Registrations:");
            String q1 = "SELECT * FROM registrations ORDER BY reg_id DESC LIMIT 10";
            try (PreparedStatement p = conn.prepareStatement(q1)) {
                try (ResultSet rs = p.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("reg_id=" + rs.getInt("reg_id") + ", email=" + rs.getString("email") + ", fname=" + rs.getString("fname") + ", password_hash=" + rs.getString("password_hash"));
                    }
                }
            } catch (Exception e) {
                System.err.println("Registrations query failed (reg_id columns may differ): " + e.getMessage());
                // try alternative column names
                String q1a = "SELECT * FROM registrations ORDER BY id DESC LIMIT 10";
                try (PreparedStatement p2 = conn.prepareStatement(q1a)) {
                    try (ResultSet rs2 = p2.executeQuery()) {
                        while (rs2.next()) {
                            System.out.println("id=" + rs2.getInt("id") + ", email=" + rs2.getString("email") + ", first_name=" + rs2.getString("first_name") + ", password_hash=" + rs2.getString("password_hash"));
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Alternative registrations query failed: " + ex.getMessage());
                }
            }

            System.out.println("\nUser Credentials:");
            String q2 = "SELECT * FROM user_credentials ORDER BY auth_id DESC LIMIT 10";
            try (PreparedStatement p = conn.prepareStatement(q2)) {
                try (ResultSet rs = p.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("auth_id=" + rs.getInt("auth_id") + ", c_id=" + rs.getInt("c_id") + ", registration_id?=" + safeGet(rs, "registration_id") + ", email=" + rs.getString("email") + ", password_hash=" + rs.getString("password_hash") + ", salt=" + rs.getString("salt"));
                    }
                }
            } catch (Exception e) {
                System.err.println("user_credentials query failed (auth_id/c_id may differ): " + e.getMessage());
                // try alternative names
                String q2a = "SELECT * FROM user_credentials ORDER BY id DESC LIMIT 10";
                try (PreparedStatement p2 = conn.prepareStatement(q2a)) {
                    try (ResultSet rs2 = p2.executeQuery()) {
                        while (rs2.next()) {
                            System.out.println("id=" + rs2.getInt("id") + ", registration_id=" + safeGet(rs2, "registration_id") + ", email=" + rs2.getString("email") + ", password_hash=" + rs2.getString("password_hash") + ", salt=" + rs2.getString("salt"));
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Alternative user_credentials query failed: " + ex.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("DB show failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String safeGet(ResultSet rs, String col) {
        try { return rs.getString(col); } catch (Exception e) { return "<missing>"; }
    }
}
