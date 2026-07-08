package com.library.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import com.library.db.DatabaseConnection;

public class AdminReportsFrame extends JFrame {
    private JTable overdueTable, popularTable;
    private DefaultTableModel overdueModel, popularModel;

    public AdminReportsFrame() {
        setTitle("Admin Compliance & Analytics Reports");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2, 1, 10, 20)); // Splits screen into two clean reports

        // --- REPORT 1: OVERDUE ITEMS & FINE CALCULATOR ---
        JPanel pnlOverdue = new JPanel(new BorderLayout());
        pnlOverdue.setBorder(BorderFactory.createTitledBorder("Overdue Book Loans & Fine Calculations (> 7 Days)"));
        overdueModel = new DefaultTableModel(new String[]{"Book ID", "Member ID", "Issued Date", "Days Held", "Calculated Fine"}, 0);
        overdueTable = new JTable(overdueModel);
        pnlOverdue.add(new JScrollPane(overdueTable), BorderLayout.CENTER);
        add(pnlOverdue);

        // --- REPORT 2: MOST BORROWED BOOKS ---
        JPanel pnlPopular = new JPanel(new BorderLayout());
        pnlPopular.setBorder(BorderFactory.createTitledBorder("Metrics: Most Borrowed Books Tally"));
        popularModel = new DefaultTableModel(new String[]{"Book ID", "Book Title", "Total Times Borrowed"}, 0);
        popularTable = new JTable(popularModel);
        pnlPopular.add(new JScrollPane(popularTable), BorderLayout.CENTER);
        add(pnlPopular);

        generateReports();
    }

    private void generateReports() {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            
            // 1. Calculate Overdue entries and live fines dynamically
            ResultSet rsTrack = stmt.executeQuery("SELECT book_id, member_id, issue_date FROM transactions WHERE return_date IS NULL");
            while (rsTrack.next()) {
                String bId = rsTrack.getString("book_id");
                String mId = rsTrack.getString("member_id");
                LocalDate issueDate = LocalDate.parse(rsTrack.getString("issue_date"));
                long daysHeld = ChronoUnit.DAYS.between(issueDate, LocalDate.now());
                
                if (daysHeld > 7) {
                    long fineAmount = (daysHeld - 7) * 10; // ₹10 fine applied for each overdue day
                    overdueModel.addRow(new Object[]{bId, mId, issueDate.toString(), daysHeld + " days", "₹ " + fineAmount});
                }
            }

            // 2. Fetch aggregate count for most borrowed items
            String popularQuery = "SELECT b.book_id, b.title, COUNT(t.transaction_id) as borrow_count " +
                                  "FROM books b JOIN transactions t ON b.book_id = t.book_id " +
                                  "GROUP BY b.book_id, b.title ORDER BY borrow_count DESC";
            ResultSet rsPop = stmt.executeQuery(popularQuery);
            while (rsPop.next()) {
                popularModel.addRow(new Object[]{rsPop.getString("book_id"), rsPop.getString("title"), rsPop.getInt("borrow_count")});
            }

        } catch (Exception ex) {
            System.out.println("Report Generation Error: " + ex.getMessage());
        }
    }
}