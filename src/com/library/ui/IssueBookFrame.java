package com.library.ui;

import com.library.db.DatabaseConnection; // Matches your exact folder paths
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class IssueBookFrame extends JFrame {
    private JTextField txtBookId, txtMemberId, txtIssueDate;

    public IssueBookFrame() {
        setTitle("Library Management System - Issue Book");
        setSize(450, 400); // Expanded slightly to fit the new date row comfortably
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        JLabel lblHeader = new JLabel("ISSUE BOOK TO MEMBER", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setBounds(20, 20, 400, 30);
        add(lblHeader);

        JLabel lblBookId = new JLabel("Book ID:");
        lblBookId.setBounds(50, 90, 100, 25);
        add(lblBookId);

        txtBookId = new JTextField();
        txtBookId.setBounds(150, 90, 220, 30);
        add(txtBookId);

        JLabel lblMemberId = new JLabel("Member ID:");
        lblMemberId.setBounds(50, 140, 100, 25);
        add(lblMemberId);

        txtMemberId = new JTextField();
        txtMemberId.setBounds(150, 140, 220, 30);
        add(txtMemberId);

        // 📅 ADDED: DATE FIELD INPUT (Feature Checklist)
        JLabel lblDate = new JLabel("Issue Date:");
        lblDate.setBounds(50, 190, 100, 25);
        add(lblDate);

        txtIssueDate = new JTextField();
        txtIssueDate.setBounds(150, 190, 220, 30);
        txtIssueDate.setText(LocalDate.now().toString()); // Autofills with today's date (YYYY-MM-DD)
        add(txtIssueDate);

        JButton btnIssue = new JButton("Issue Book");
        btnIssue.setBounds(80, 260, 120, 35);
        btnIssue.setBackground(new Color(225, 225, 225));
        add(btnIssue);

        // ↩️ WORKING BACK BUTTON
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(230, 260, 120, 35);
        btnBack.setBackground(new Color(225, 225, 225));
        add(btnBack);

        btnIssue.addActionListener(e -> issueBookProcess());
        btnBack.addActionListener(e -> this.dispose());
    }

    private void issueBookProcess() {
        String bookId = txtBookId.getText().trim();
        String memberId = txtMemberId.getText().trim();
        String issueDateStr = txtIssueDate.getText().trim();

        if (bookId.isEmpty() || memberId.isEmpty() || issueDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All input tracking fields are mandatory.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Update book status column lock
            String updateBook = "UPDATE books SET status = 'Issued' WHERE book_id = ? AND status = 'Available'";
            try (PreparedStatement pstmt1 = conn.prepareStatement(updateBook)) {
                pstmt1.setString(1, bookId);
                int rows = pstmt1.executeUpdate();
                
                if (rows == 0) {
                    JOptionPane.showMessageDialog(this, "Book is invalid or already issued!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // 🔄 AUTOMATIC DUE DATE CALCULATION (7-Day Return Rule)
            LocalDate issueDate = LocalDate.parse(issueDateStr);
            LocalDate dueDate = issueDate.plusDays(7); 

            // Record transaction row instance with issue_date and due_date
            String insertTx = "INSERT INTO transactions (book_id, member_id, issue_date, due_date) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt2 = conn.prepareStatement(insertTx)) {
                pstmt2.setString(1, bookId);
                pstmt2.setString(2, memberId);
                pstmt2.setString(3, issueDate.toString());
                pstmt2.setString(4, dueDate.toString()); // Stores due date directly for reporting
                pstmt2.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Book successfully issued out!");
            txtBookId.setText("");
            txtMemberId.setText("");
            txtIssueDate.setText(LocalDate.now().toString()); // Reset to real-time current date
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}