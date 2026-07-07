package com.library.ui;

import com.library.db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookManagerFrame extends JFrame {
    private JTextField txtBookId, txtTitle, txtAuthor;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    public BookManagerFrame() {
        setTitle("Library Registry - Core Catalog Manager");
        setSize(780, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 242, 245)); // Polished soft background look

        // Left Data Input Canvas Panel Wrapper
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(null);
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230), 1));
        inputPanel.setBounds(20, 20, 280, 400);
        add(inputPanel);

        JLabel lblFormTitle = new JLabel("Catalog Registry Index");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setBounds(20, 15, 200, 25);
        inputPanel.add(lblFormTitle);

        JLabel lblId = new JLabel("Book Unique ID:");
        lblId.setBounds(20, 60, 120, 25);
        inputPanel.add(lblId);

        txtBookId = new JTextField();
        txtBookId.setBounds(20, 85, 240, 30);
        inputPanel.add(txtBookId);

        JLabel lblTitle = new JLabel("Book Title:");
        lblTitle.setBounds(20, 130, 100, 25);
        inputPanel.add(lblTitle);

        txtTitle = new JTextField();
        txtTitle.setBounds(20, 155, 240, 30);
        inputPanel.add(txtTitle);

        JLabel lblAuthor = new JLabel("Author / Creator:");
        lblAuthor.setBounds(20, 200, 120, 25);
        inputPanel.add(lblAuthor);

        txtAuthor = new JTextField();
        txtAuthor.setBounds(20, 225, 240, 30);
        inputPanel.add(txtAuthor);

        JButton btnAdd = new JButton("Save to Catalog Database");
        btnAdd.setBackground(new Color(46, 204, 113)); // Clean success emerald theme coloring
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBounds(20, 290, 240, 35);
        inputPanel.add(btnAdd);

        // Right Data Table Visual Area
        String[] columns = {"Book ID", "Title", "Author", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        bookTable = new JTable(tableModel);
        bookTable.setRowHeight(25); // Better structural grid rows spacing
        bookTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBounds(320, 20, 420, 400);
        add(scrollPane);

        // Load data tracking rows live from JDBC on execution initialization
        loadCatalogData();

        // Save entry click handler
        btnAdd.addActionListener(e -> saveBookToDatabase());
    }

    private void loadCatalogData() {
        tableModel.setRowCount(0); // Clean up active view list instance lines
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
            System.out.println("Error Loading Books: " + e.getMessage());
        }
    }

    private void saveBookToDatabase() {
        String id = txtBookId.getText().trim();
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();

        if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All input data parameters are required.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "INSERT INTO books (book_id, title, author, status) VALUES (?, ?, ?, 'Available')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, id);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book asset recorded permanently inside MySQL system layers!");
            txtBookId.setText("");
            txtTitle.setText("");
            txtAuthor.setText("");
            
            loadCatalogData(); // Re-trigger tracking lookup mapping update lines
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Execution Exception Error:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}