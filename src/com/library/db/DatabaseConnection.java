package com.library.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseConnection {
    // Update "library_db", "root", and "your_password" with your local MySQL configurations
    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Gopinath"; 

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Database Connection Failed: " + e.getMessage());
        }
        return conn;
    }

    // Helper method to quickly create tables if they don't exist yet
    public static void initializeDatabase() {
        String createBooksTable = "CREATE TABLE IF NOT EXISTS books (" +
        "book_id VARCHAR(50) PRIMARY KEY, " +
        "title VARCHAR(150) NOT NULL, " +
        "author VARCHAR(100) NOT NULL, " +
        "status VARCHAR(20) DEFAULT 'Available')";

String createMembersTable = "CREATE TABLE IF NOT EXISTS members (" +
        "member_id VARCHAR(50) PRIMARY KEY, " +
        "name VARCHAR(100) NOT NULL, " +
        "email VARCHAR(100))";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createBooksTable);
            stmt.execute(createMembersTable);
            System.out.println("Database Tables Verified/Created Successfully.");
        } catch (Exception e) {
            System.out.println("Table Initialization Failed: " + e.getMessage());
        }
    }
}