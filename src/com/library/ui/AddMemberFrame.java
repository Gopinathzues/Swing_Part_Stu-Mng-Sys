package com.library.ui;

import com.library.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddMemberFrame extends JFrame {
    private JTextField txtMemberId, txtName, txtEmail;

    public AddMemberFrame() {
        setTitle("Library Management System - Add Member");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        JLabel lblHeader = new JLabel("REGISTER NEW PATRON", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setBounds(20, 20, 400, 30);
        add(lblHeader);

        JLabel lblId = new JLabel("Member ID:");
        lblId.setBounds(50, 80, 100, 25);
        add(lblId);

        txtMemberId = new JTextField();
        txtMemberId.setBounds(150, 80, 220, 30);
        add(txtMemberId);

        JLabel lblName = new JLabel("Full Name:");
        lblName.setBounds(50, 140, 100, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(150, 140, 220, 30);
        add(txtName);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(50, 200, 100, 25);
        add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(150, 200, 220, 30);
        add(txtEmail);

        JButton btnSave = new JButton("Register");
        btnSave.setBounds(80, 270, 120, 35);
        btnSave.setBackground(new Color(225, 225, 225));
        add(btnSave);

        // ↩️ THE BACK BUTTON
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(230, 270, 120, 35);
        btnBack.setBackground(new Color(225, 225, 225));
        add(btnBack);

        btnSave.addActionListener(e -> saveMember());
        btnBack.addActionListener(e -> this.dispose());
    }

    private void saveMember() {
        String id = txtMemberId.getText().trim();
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();

        if (id.isEmpty() || name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "INSERT INTO members (member_id, name, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Member profile saved!");
            txtMemberId.setText("");
            txtName.setText("");
            txtEmail.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}