package com.OblivionNexusDetective.test;

import java.sql.Connection;

import com.OblivionNexusDetective.data.DatabaseConnection;

public class testing {

     public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println(" Connected to DB: " + conn.getCatalog());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 
}