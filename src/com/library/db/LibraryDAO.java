package com.library.db;

import java.sql.*;
import java.util.Vector;

public class LibraryDAO {

    // 1. Core Feature: Add a new book using PreparedStatement
    public boolean addBook(String title, String author) {
        String sql = "INSERT INTO books (title, author, status) VALUES (?, ?, 'Available')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, title);
            ps.setString(2, author);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Database Error while inserting book: " + e.getMessage());
            return false;
        }
    }
    public boolean issueBook(int bookId, int memberId) {
    String insertTransactionSql = "INSERT INTO transactions (book_id, member_id) VALUES (?, ?)";
    String updateBookSql = "UPDATE books SET status = 'Borrowed' WHERE book_id = ?";
    
    Connection conn = null;
    try {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false); // Start transaction to ensure both steps succeed together

        // 1. Insert into transactions
        try (PreparedStatement psInsert = conn.prepareStatement(insertTransactionSql)) {
            psInsert.setInt(1, bookId);
            psInsert.setInt(2, memberId);
            psInsert.executeUpdate();
        }

        // 2. Update Book Status
        try (PreparedStatement psUpdate = conn.prepareStatement(updateBookSql)) {
            psUpdate.setInt(1, bookId);
            psUpdate.executeUpdate();
        }

        conn.commit(); // Commit changes if both statements pass
        return true;
        
    } catch (SQLException e) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ex) { System.err.println(ex.getMessage()); }
        }
        System.err.println("Database Error during book issuance: " + e.getMessage());
        return false;
    } finally {
        if (conn != null) {
            try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { System.err.println(ex.getMessage()); }
        }
    }
}
    

    // 2. Core Feature: Register a new member
    public boolean registerMember(String name, String email) {
        String sql = "INSERT INTO members (name, email) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, name);
            ps.setString(2, email);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Database Error while registering member: " + e.getMessage());
            return false;
        }
    }

    // 3. Helper Feature: Fetch live books to populate our Swing UI JTable
    public Vector<Vector<Object>> getAllBooksForUI() {
        Vector<Vector<Object>> data = new Vector<>();
        String sql = "SELECT book_id, title, author, status FROM books ORDER BY book_id ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("book_id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("author"));
                row.add(rs.getString("status"));
                data.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Database Error while fetching catalog: " + e.getMessage());
        }
        return data;
    }
    public java.util.Vector<java.util.Vector<Object>> searchBooksForUI(String keyword) {
    java.util.Vector<java.util.Vector<Object>> data = new java.util.Vector<>();
    // SQL query to search across either title OR author columns matching the keyword
    String sql = "SELECT book_id, title, author, status FROM books " +
                 "WHERE title LIKE ? OR author LIKE ? ORDER BY book_id ASC";
    
    try (java.sql.Connection conn = DatabaseConnection.getConnection();
         java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
        
        // The % wildcards allow matching partial words anywhere in the text
        String searchPattern = "%" + keyword + "%";
        ps.setString(1, searchPattern);
        ps.setString(2, searchPattern);
        
        try (java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                java.util.Vector<Object> row = new java.util.Vector<>();
                row.add(rs.getInt("book_id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("author"));
                row.add(rs.getString("status"));
                data.add(row);
            }
        }
    } catch (java.sql.SQLException e) {
        System.err.println("Database Error while searching catalog: " + e.getMessage());
    }
    return data;
}
    
    
    public java.util.Vector<java.util.Vector<Object>> getTransactionHistoryForUI() {
    java.util.Vector<java.util.Vector<Object>> data = new java.util.Vector<>();
    String sql = "SELECT t.transaction_id, b.title, m.name, t.issue_date, t.return_date " +
                 "FROM transactions t " +
                 "JOIN books b ON t.book_id = b.book_id " +
                 "JOIN members m ON t.member_id = m.member_id " +
                 "ORDER BY t.transaction_id DESC";
    
    try (java.sql.Connection conn = DatabaseConnection.getConnection();
         java.sql.PreparedStatement ps = conn.prepareStatement(sql);
         java.sql.ResultSet rs = ps.executeQuery()) {
        
        while (rs.next()) {
            java.util.Vector<Object> row = new java.util.Vector<>();
            row.add(rs.getInt("transaction_id"));
            row.add(rs.getString("title"));
            row.add(rs.getString("name"));
            row.add(rs.getTimestamp("issue_date"));
            
            java.sql.Timestamp retDate = rs.getTimestamp("return_date");
            row.add(retDate == null ? "Still Borrowed" : retDate);
            
            data.add(row);
        }
    } catch (java.sql.SQLException e) {
        System.err.println("Database Error while fetching transaction history: " + e.getMessage());
    }
    return data;
}
}
