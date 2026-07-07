package com.library.ui;

import com.library.db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageMembersFrame extends JFrame {
    private JTable memberTable;
    private DefaultTableModel tableModel;

    public ManageMembersFrame() {
        setTitle("Manage Members");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        String[] columns = {"Member ID", "Name", "Email Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        memberTable = new JTable(tableModel);
        memberTable.setRowHeight(25);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBounds(30, 30, 725, 330);
        add(scrollPane);

        Font btnFont = new Font("Segoe UI", Font.PLAIN, 12);
        Color btnGray = new Color(225, 225, 225);

        // 🔄 Refresh Button
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(150, 390, 130, 35);
        btnRefresh.setFont(btnFont);
        btnRefresh.setBackground(btnGray);
        btnRefresh.setFocusPainted(false);
        add(btnRefresh);

        // 🗑️ Delete Button
        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(335, 390, 130, 35);
        btnDelete.setFont(btnFont);
        btnDelete.setBackground(btnGray);
        btnDelete.setFocusPainted(false);
        add(btnDelete);

        // ↩️ Back Button
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(520, 390, 130, 35);
        btnBack.setFont(btnFont);
        btnBack.setBackground(btnGray);
        btnBack.setFocusPainted(false);
        add(btnBack);

        btnRefresh.addActionListener(e -> loadMemberData());
        btnDelete.addActionListener(e -> deleteMemberProcess());
        btnBack.addActionListener(e -> this.dispose());

        loadMemberData();
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
            System.out.println("Data Load Error: " + e.getMessage());
        }
    }

    private void deleteMemberProcess() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member row first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String memberId = tableModel.getValueAt(selectedRow, 0).toString();
        String memberName = tableModel.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete this member account?\n(" + memberName + ")", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM members WHERE member_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, memberId);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Member profile deleted successfully.");
                loadMemberData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Cannot delete member: Active transaction loan history exists.", "Database Restriction", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}