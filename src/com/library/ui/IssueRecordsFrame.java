package com.library.ui;

import com.library.db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class IssueRecordsFrame extends JFrame {
    private JTable recordTable;
    private DefaultTableModel tableModel;

    public IssueRecordsFrame() {
        setTitle("Library Management System - Issue Records");
        setSize(700, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        JLabel lblHeader = new JLabel("TRANSACTIONAL LOAN HISTORY REGISTRY", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setBounds(20, 15, 650, 30);
        add(lblHeader);

        String[] columns = {"Tx ID", "Book ID", "Member ID", "Issue Date", "Return Date"};
        tableModel = new DefaultTableModel(columns, 0);
        recordTable = new JTable(tableModel);
        recordTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(recordTable);
        scrollPane.setBounds(30, 60, 620, 290);
        add(scrollPane);

        // ↩️ WORKING BACK BUTTON AT THE BOTTOM
        JButton btnBack = new JButton("Back to Dashboard");
        btnBack.setBounds(250, 375, 200, 35);
        btnBack.setBackground(new Color(225, 225, 225));
        add(btnBack);

        btnBack.addActionListener(e -> this.dispose());

        loadTransactionLogs();
    }

    private void loadTransactionLogs() {
        tableModel.setRowCount(0);
        String query = "SELECT * FROM transactions";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("transaction_id"),
                    rs.getString("book_id"),
                    rs.getString("member_id"),
                    rs.getString("issue_date"),
                    rs.getString("return_date") == null ? "Active Loan" : rs.getString("return_date")
                });
            }
        } catch (Exception e) {
            System.out.println("Error Loading Transactions: " + e.getMessage());
        }
    }
}