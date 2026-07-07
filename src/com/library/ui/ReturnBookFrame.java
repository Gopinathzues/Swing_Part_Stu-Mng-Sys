package com.library.ui;

import com.library.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class ReturnBookFrame extends JFrame {
    private JTextField txtBookId;

    public ReturnBookFrame() {
        setTitle("Library Management System - Return Book");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        JLabel lblHeader = new JLabel("PROCESS ASSET RETURN", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setBounds(20, 20, 400, 30);
        add(lblHeader);

        JLabel lblBookId = new JLabel("Book ID:");
        lblBookId.setBounds(50, 90, 100, 25);
        add(lblBookId);

        txtBookId = new JTextField();
        txtBookId.setBounds(150, 90, 220, 30);
        add(txtBookId);

        JButton btnReturn = new JButton("Return Book");
        btnReturn.setBounds(80, 170, 120, 35);
        btnReturn.setBackground(new Color(225, 225, 225));
        add(btnReturn);

        // ↩️ WORKING BACK BUTTON
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(230, 170, 120, 35);
        btnBack.setBackground(new Color(225, 225, 225));
        add(btnBack);

        btnReturn.addActionListener(e -> returnBookProcess());
        btnBack.addActionListener(e -> this.dispose());
    }

    private void returnBookProcess() {
        String bookId = txtBookId.getText().trim();
        String today = LocalDate.now().toString();

        if (bookId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a valid Book ID.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Update transaction file logs
            String updateTx = "UPDATE transactions SET return_date = ? WHERE book_id = ? AND return_date IS NULL";
            try (PreparedStatement pstmt1 = conn.prepareStatement(updateTx)) {
                pstmt1.setString(1, today);
                pstmt1.setString(2, bookId);
                int rows = pstmt1.executeUpdate();
                
                if (rows == 0) {
                    JOptionPane.showMessageDialog(this, "No active transaction found for this Book ID.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Restore book availability status
            String updateBook = "UPDATE books SET status = 'Available' WHERE book_id = ?";
            try (PreparedStatement pstmt2 = conn.prepareStatement(updateBook)) {
                pstmt2.setString(1, bookId);
                pstmt2.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Book returned successfully to inventory storage!");
            txtBookId.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}