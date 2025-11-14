package com.css123group.corr4_be;

import java.time.LocalDate;

public class TestSeedUser {
    public static void main(String[] args) {
        String email = args.length > 0 ? args[0] : "dev@example.com";
        String password = args.length > 1 ? args[1] : "Test1234!";
        try {
            Auth auth = new Auth();
            Customer c = new Customer();
            c.setFirstName("Dev");
            c.setLastName("User");
            c.setEmail(email);
            c.setPhone("1234567890");
            c.setAddress("123 Dev St");
            c.setDateOfBirth(LocalDate.of(1990,1,1));

            Customer created = auth.registerCustomer(c, password);
            if (created != null) {
                System.out.println("SEED OK: created customer id=" + created.getId() + " email=" + created.getEmail());
            } else {
                System.out.println("SEED FAIL: could not create customer (maybe already exists)");
            }
        } catch (Exception e) {
            System.err.println("SEED ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
