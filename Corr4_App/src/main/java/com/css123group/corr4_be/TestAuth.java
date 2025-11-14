package com.css123group.corr4_be;

public class TestAuth {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java com.css123group.corr4_be.TestAuth <email> <password>");
            System.exit(1);
        }
        String email = args[0].trim().toLowerCase();
        String password = args[1];

        Auth auth = new Auth();
        try {
            Customer c = auth.authenticate(email, password);
            if (c != null) {
                System.out.println("AUTH OK: customer id=" + c.getId() + " email=" + c.getEmail());
            } else {
                System.out.println("AUTH FAIL: credentials invalid or not found for " + email);
            }
        } catch (Exception e) {
            System.err.println("AUTH ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
