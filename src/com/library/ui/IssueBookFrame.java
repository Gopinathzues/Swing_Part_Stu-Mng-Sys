package com.library.ui;

import com.library.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class IssueBookFrame extends JFrame {
    private JTextField txtBookId, txtMemberId;

    public IssueBookFrame() {
        setTitle("Library Management System - Issue Book");
        setSize(450, 350);
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
        lblMemberId.setBounds(50, 150, 100, 25);
        add(lblMemberId);

        txtMemberId = new JTextField();
        txtMemberId.setBounds(150, 150, 220, 30);
        add(txtMemberId);

        JButton btnIssue = new JButton("Issue Book");
        btnIssue.setBounds(80, 230, 120, 35);
        btnIssue.setBackground(new Color(225, 225, 225));
        add(btnIssue);

        // ↩️ WORKING BACK BUTTON
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(230, 230, 120, 35);
        btnBack.setBackground(new Color(225, 225, 225));
        add(btnBack);

        btnIssue.addActionListener(e -> issueBookProcess());
        btnBack.addActionListener(e -> this.dispose());
    }

    private void issueBookProcess() {
        String bookId = txtBookId.getText().trim();
        String memberId = txtMemberId.getText().trim();
        String today = LocalDate.now().toString(); // Auto tracks modern system date YYYY-MM-DD

        if (bookId.isEmpty() || memberId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both fields are mandatory.", "Warning", JOptionPane.WARNING_MESSAGE);
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

            // Record transaction row instance
            String insertTx = "INSERT INTO transactions (book_id, member_id, issue_date) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt2 = conn.prepareStatement(insertTx)) {
                pstmt2.setString(1, bookId);
                pstmt2.setString(2, memberId);
                pstmt2.setString(3, today);
                pstmt2.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Book successfully issued out!");
            txtBookId.setText("");
            txtMemberId.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}