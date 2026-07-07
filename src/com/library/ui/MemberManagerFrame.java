package com.library.ui;

import com.library.db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MemberManagerFrame extends JFrame {
    private JTextField txtMemberId, txtName, txtEmail;
    private JTable memberTable;
    private DefaultTableModel tableModel;

    public MemberManagerFrame() {
        setTitle("Library Registry - Manage Members");
        setSize(750, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 242, 245));

        // Layout UI Styling - Form inputs panel container
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(null);
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230), 1));
        inputPanel.setBounds(20, 20, 280, 400);
        add(inputPanel);

        JLabel lblTitle = new JLabel("Add New Member");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setBounds(20, 15, 200, 25);
        inputPanel.add(lblTitle);

        JLabel lblId = new JLabel("Member ID:");
        lblId.setBounds(20, 60, 100, 25);
        inputPanel.add(lblId);

        txtMemberId = new JTextField();
        txtMemberId.setBounds(20, 85, 240, 30);
        inputPanel.add(txtMemberId);

        JLabel lblName = new JLabel("Full Name:");
        lblName.setBounds(20, 130, 100, 25);
        inputPanel.add(lblName);

        txtName = new JTextField();
        txtName.setBounds(20, 155, 240, 30);
        inputPanel.add(txtName);

        JLabel lblEmail = new JLabel("Email Address:");
        lblEmail.setBounds(20, 200, 100, 25);
        inputPanel.add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(20, 225, 240, 30);
        inputPanel.add(txtEmail);

        JButton btnAdd = new JButton("Register Member");
        btnAdd.setBackground(new Color(9, 132, 227));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBounds(20, 290, 240, 35);
        inputPanel.add(btnAdd);

        // Member Data Grid View (Right Side Components)
        String[] columns = {"Member ID", "Name", "Email Address"};
        tableModel = new DefaultTableModel(columns, 0);
        memberTable = new JTable(tableModel);
        memberTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBounds(320, 20, 390, 400);
        add(scrollPane);

        // Actions
        loadMemberData();
        btnAdd.addActionListener(e -> saveMemberToDatabase());
    }

    private void loadMemberData() {
        tableModel.setRowCount(0);
        String query = "SELECT * FROM members";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("member_id"),
                    rs.getString("name"),
                    rs.getString("email")
                });
            }
        } catch (Exception e) {
            System.out.println("Error Loading Members: " + e.getMessage());
        }
    }

    private void saveMemberToDatabase() {
        String id = txtMemberId.getText().trim();
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();

        if(id.isEmpty() || name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All input data tracks are mandatory.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "INSERT INTO members (member_id, name, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Member tracking profile registered permanently!");
            txtMemberId.setText("");
            txtName.setText("");
            txtEmail.setText("");
            loadMemberData(); // Refresh grid layout context view
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Write Failure: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}