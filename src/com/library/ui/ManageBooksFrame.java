package com.library.ui;

import com.library.db.DatabaseConnection; // Matches your folder hierarchy cleanly
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;

public class ManageBooksFrame extends JFrame {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton btnDelete; // Made a class variable so we can hide it

    // 🔄 Modified constructor to accept the user's role ("Admin" or "Student")
    public ManageBooksFrame(String userRole) {
        setTitle(userRole.equals("Admin") ? "Manage Books (Admin Portal)" : "Browse Book Catalog (Student Portal)");
        setSize(800, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        // Search Interface Components
        JLabel lblSearch = new JLabel("Search Catalog:");
        lblSearch.setBounds(30, 20, 100, 25);
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(lblSearch);

        txtSearch = new JTextField();
        txtSearch.setBounds(140, 20, 615, 25);
        add(txtSearch);

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
        scrollPane.setBounds(30, 60, 725, 310);
        add(scrollPane);

        Font btnFont = new Font("Segoe UI", Font.PLAIN, 12);
        Color btnGray = new Color(225, 225, 225);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(150, 400, 130, 35);
        btnRefresh.setFont(btnFont);
        btnRefresh.setBackground(btnGray);
        btnRefresh.setFocusPainted(false);
        add(btnRefresh);

        // Delete Button
        btnDelete = new JButton("Delete");
        btnDelete.setBounds(335, 400, 130, 35);
        btnDelete.setFont(btnFont);
        btnDelete.setBackground(btnGray);
        btnDelete.setFocusPainted(false);
        add(btnDelete);

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(520, 400, 130, 35);
        btnBack.setFont(btnFont);
        btnBack.setBackground(btnGray);
        btnBack.setFocusPainted(false);
        add(btnBack);

        // 🛡️ CRITICAL SECURITY CHECK: If student, completely hide the Delete button!
        if (userRole.equals("Student")) {
            btnDelete.setVisible(false); // It disappears completely from the screen
            // Reposition the remaining buttons nicely for the student view
            btnRefresh.setBounds(230, 400, 130, 35);
            btnBack.setBounds(440, 400, 130, 35);
        }

        // Live Search Filter Logic
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
                bookTable.setRowSorter(sorter);
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txtSearch.getText().trim()));
            }
        });

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            if (bookTable.getRowSorter() != null) {
                bookTable.setRowSorter(null);
            }
            loadCatalogData();
        });
        
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

        int modelRow = bookTable.convertRowIndexToModel(selectedRow);
        String bookId = tableModel.getValueAt(modelRow, 0).toString();
        String bookTitle = tableModel.getValueAt(modelRow, 1).toString();

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
                txtSearch.setText("");
                if (bookTable.getRowSorter() != null) {
                    bookTable.setRowSorter(null);
                }
                loadCatalogData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Cannot delete book: It has active issue history links.", "Database Restriction", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}