package com.library.ui;

import com.library.db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AdminReportsPanel extends JPanel {
    private JTable overdueTable, popularTable;
    private DefaultTableModel overdueModel, popularModel;

    public AdminReportsPanel() {
        setLayout(new GridLayout(2, 1, 10, 20)); // Splits the panel area perfectly in two halves
        setBackground(new Color(245, 245, 245));

        // --- SECTION 1: OVERDUE CHECKS & FINE CALCULATIONS ---
        JPanel pnlOverdue = new JPanel(new BorderLayout(5, 5));
        pnlOverdue.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "ADMIN OVERDUE REGISTER (Fine Rate: ₹10/Day)"
        ));
        
        String[] overdueColumns = {"Book ID", "Member ID", "Issue Date", "Due Date", "Days Late", "Pending Fine"};
        overdueModel = new DefaultTableModel(overdueColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        overdueTable = new JTable(overdueModel);
        overdueTable.setRowHeight(22);
        pnlOverdue.add(new JScrollPane(overdueTable), BorderLayout.CENTER);
        add(pnlOverdue);

        // --- SECTION 2: METRICS (Most Borrowed Books Tally) ---
        JPanel pnlPopular = new JPanel(new BorderLayout(5, 5));
        pnlPopular.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "POPULARITY ANALYTICS: MOST BORROWED BOOKS TALLY"
        ));

        String[] popularColumns = {"Book ID", "Book Title", "Author", "Total Times Borrowed"};
        popularModel = new DefaultTableModel(popularColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        popularTable = new JTable(popularModel);
        popularTable.setRowHeight(22);
        pnlPopular.add(new JScrollPane(popularTable), BorderLayout.CENTER);
        add(pnlPopular);

        // Load data initially
        compileSystemReports();
    }

    public void compileSystemReports() {
        overdueModel.setRowCount(0);
        popularModel.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            
            // 1. Calculate Overdue entries and live fines dynamically
            String checkOverdueQuery = "SELECT book_id, member_id, issue_date, due_date FROM transactions WHERE return_date IS NULL";
            try (ResultSet rs = stmt.executeQuery(checkOverdueQuery)) {
                while (rs.next()) {
                    String bId = rs.getString("book_id");
                    String mId = rs.getString("member_id");
                    String issueStr = rs.getString("issue_date");
                    String dueStr = rs.getString("due_date");

                    if (dueStr != null && !dueStr.isEmpty()) {
                        LocalDate dueDate = LocalDate.parse(dueStr);
                        LocalDate today = LocalDate.now();

                        if (today.isAfter(dueDate)) {
                            long lateDays = ChronoUnit.DAYS.between(dueDate, today);
                            long fineCalculated = lateDays * 10;
                            
                            overdueModel.addRow(new Object[]{
                                bId, mId, issueStr, dueStr, lateDays + " Days", "₹ " + fineCalculated
                            });
                        }
                    }
                }
            }

            // 2. Fetch count for most borrowed items
            String checkPopularityQuery = "SELECT b.book_id, b.title, b.author, COUNT(t.transaction_id) AS borrow_count " +
                                          "FROM books b JOIN transactions t ON b.book_id = t.book_id " +
                                          "GROUP BY b.book_id, b.title, b.author " +
                                          "ORDER BY borrow_count DESC";
            try (ResultSet rsPop = stmt.executeQuery(checkPopularityQuery)) {
                while (rsPop.next()) {
                    popularModel.addRow(new Object[]{
                        rsPop.getString("book_id"),
                        rsPop.getString("title"),
                        rsPop.getString("author"),
                        rsPop.getInt("borrow_count") + " Times"
                    });
                }
            }

        } catch (Exception e) {
            System.out.println("Analytics Compile Failure: " + e.getMessage());
        }
    }
}