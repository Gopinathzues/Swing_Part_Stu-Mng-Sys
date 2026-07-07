package com.library.ui;

import com.library.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddBookFram extends JFrame {
    private JTextField txtBookId, txtTitle, txtAuthor;

    public AddBookFram() {
        setTitle("Library Management System - Add Book");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        JLabel lblHeader = new JLabel("ADD NEW BOOK ASSET", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setBounds(20, 20, 400, 30);
        add(lblHeader);

        JLabel lblId = new JLabel("Book ID:");
        lblId.setBounds(50, 80, 100, 25);
        add(lblId);

        txtBookId = new JTextField();
        txtBookId.setBounds(150, 80, 220, 30);
        add(txtBookId);

        JLabel lblTitle = new JLabel("Book Title:");
        lblTitle.setBounds(50, 140, 100, 25);
        add(lblTitle);

        txtTitle = new JTextField();
        txtTitle.setBounds(150, 140, 220, 30);
        add(txtTitle);

        JLabel lblAuthor = new JLabel("Author:");
        lblAuthor.setBounds(50, 200, 100, 25);
        add(lblAuthor);

        txtAuthor = new JTextField();
        txtAuthor.setBounds(150, 200, 220, 30);
        add(txtAuthor);

        JButton btnSave = new JButton("Save Book");
        btnSave.setBounds(80, 270, 120, 35);
        btnSave.setBackground(new Color(225, 225, 225));
        add(btnSave);

        // ↩️ THE BACK BUTTON
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(230, 270, 120, 35);
        btnBack.setBackground(new Color(225, 225, 225));
        add(btnBack);

        // Actions
        btnSave.addActionListener(e -> saveBook());
        btnBack.addActionListener(e -> this.dispose());
    }

    private void saveBook() {
        String id = txtBookId.getText().trim();
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();

        if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All input data tracks are mandatory.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "INSERT INTO books (book_id, title, author, status) VALUES (?, ?, ?, 'Available')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book entry recorded permanently!");
            txtBookId.setText("");
            txtTitle.setText("");
            txtAuthor.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}