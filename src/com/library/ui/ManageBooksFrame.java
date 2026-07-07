package com.library.ui;

import com.library.db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageBooksFrame extends JFrame {
    private JTable bookTable;
    private DefaultTableModel tableModel;

    public ManageBooksFrame() {
        setTitle("Manage Books");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        String[] columns = {"Book ID", "Title", "Author", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        bookTable = new JTable(tableModel);
        bookTable.setRowHeight(25);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBounds(30, 30, 725, 330);
        add(scrollPane);

        Font btnFont = new Font("Segoe UI", Font.PLAIN, 12);
        Color btnGray = new Color(225, 225, 225);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(150, 390, 130, 35);
        btnRefresh.setFont(btnFont);
        btnRefresh.setBackground(btnGray);
        btnRefresh.setFocusPainted(false);
        add(btnRefresh);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(335, 390, 130, 35);
        btnDelete.setFont(btnFont);
        btnDelete.setBackground(btnGray);
        btnDelete.setFocusPainted(false);
        add(btnDelete);

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(520, 390, 130, 35);
        btnBack.setFont(btnFont);
        btnBack.setBackground(btnGray);
        btnBack.setFocusPainted(false);
        add(btnBack);

        btnRefresh.addActionListener(e -> loadCatalogData());
        btnDelete.addActionListener(e -> deleteBookProcess());
        btnBack.addActionListener(e -> this.dispose());

        loadCatalogData();
    }

    private void loadCatalogData() {
        tableModel.setRowCount(0);
        String query = "SELECT * FROM books";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            System.out.println("Data Load Error: " + e.getMessage());
        }
    }

    private void deleteBookProcess() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book row from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookId = tableModel.getValueAt(selectedRow, 0).toString();
        String bookTitle = tableModel.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete this book?\n(" + bookTitle + ")", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM books WHERE book_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, bookId);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Book asset removed successfully.");
                loadCatalogData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Cannot delete book: It has active issue history links.", "Database Restriction", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}