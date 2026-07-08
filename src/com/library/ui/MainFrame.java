package com.library.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Library Management System");
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null); // Absolute positioning to match layout perfectly
        getContentPane().setBackground(new Color(240, 240, 240));

        // Main Title Header
        JLabel lblTitle = new JLabel("LIBRARY MANAGEMENT SYSTEM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setBounds(50, 30, 650, 35);
        add(lblTitle);

        // Welcome Subtitle
        JLabel lblSubtitle = new JLabel("Welcome, Administrator", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setBounds(50, 75, 650, 25);
        add(lblSubtitle);

        // Horizontal Line Divider
        JSeparator separator = new JSeparator();
        separator.setBounds(40, 115, 670, 10);
        add(separator);

        // Button Dimensions & Common Styling
        Font btnFont = new Font("Segoe UI", Font.PLAIN, 13);
        Dimension btnSize = new Dimension(220, 40);

        // Column 1 Buttons (Left Side)
        JButton btnAddBook = new JButton("Add Book");
        btnAddBook.setBounds(100, 150, 220, 40);

        JButton btnAddMember = new JButton("Add Member");
        btnAddMember.setBounds(100, 230, 220, 40);

        JButton btnIssueBook = new JButton("Issue Book");
        btnIssueBook.setBounds(100, 310, 220, 40);

        JButton btnIssueRecords = new JButton("Issue Records");
        btnIssueRecords.setBounds(100, 390, 220, 40);
        new AdminReportsFrame().setVisible(true);

        // Column 2 Buttons (Right Side)
        JButton btnManageBooks = new JButton("Manage Books");
        btnManageBooks.setBounds(430, 150, 220, 40);

        JButton btnManageMembers = new JButton("Manage Members");
        btnManageMembers.setBounds(430, 230, 220, 40);

        JButton btnReturnBook = new JButton("Return Book");
        btnReturnBook.setBounds(430, 310, 220, 40);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(430, 390, 220, 40);

        // Apply style loop to match your image buttons precisely
        JButton[] components = {
            btnAddBook, btnManageBooks, btnAddMember, btnManageMembers,
            btnIssueBook, btnReturnBook, btnIssueRecords, btnLogout
        };

        for (JButton btn : components) {
            btn.setFont(btnFont);
            btn.setBackground(new Color(225, 225, 225)); // Perfect matching gray fill
            btn.setForeground(Color.BLACK);
            btn.setFocusPainted(false);
            add(btn);
        }

        btnAddBook.addActionListener(e -> {
            // 1. Create a structured container panel
            JPanel pnlInput = new JPanel();
            pnlInput.setLayout(null); // Absolute layout for total control over sizing
            pnlInput.setPreferredSize(new Dimension(420, 180)); // Sets custom fixed dimensions (Width: 420px, Height: 180px)

            // 2. Define and style your text input components
            JLabel lblId = new JLabel("Book ID:");
            lblId.setBounds(20, 20, 100, 25);
            JTextField txtId = new JTextField();
            txtId.setBounds(130, 20, 250, 25);

            JLabel lblBookTitle = new JLabel("Book Title:");
            lblBookTitle.setBounds(20, 70, 100, 25);
            JTextField txtTitle = new JTextField();
            txtTitle.setBounds(130, 70, 250, 25);

            JLabel lblAuthor = new JLabel("Author:");
            lblAuthor.setBounds(20, 120, 100, 25);
            JTextField txtAuthor = new JTextField();
            txtAuthor.setBounds(130, 120, 250, 25);

            // 3. Mount all layout elements onto the container panel
            pnlInput.add(lblId);
            pnlInput.add(txtId);
            pnlInput.add(lblBookTitle);
            pnlInput.add(txtTitle);
            pnlInput.add(lblAuthor);
            pnlInput.add(txtAuthor);

            // 4. Fire the dialog window passing our custom panel container
            int option = JOptionPane.showConfirmDialog(
                    this,
                    pnlInput,
                    "Add New Book Asset",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (option == JOptionPane.OK_OPTION) {
                String bookId = txtId.getText().trim();
                String title = txtTitle.getText().trim();
                String author = txtAuthor.getText().trim();

                if (bookId.isEmpty() || title.isEmpty() || author.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All input fields are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Execute SQL Database Insert Operation
                String query = "INSERT INTO books (book_id, title, author, status) VALUES (?, ?, ?, 'Available')";
                try (java.sql.Connection conn = com.library.db.DatabaseConnection.getConnection(); java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {

                    pstmt.setString(1, bookId);
                    pstmt.setString(2, title);
                    pstmt.setString(3, author);
                    pstmt.executeUpdate();

                    // SUCCESS POPUP SHOWS UP FIRST
                    JOptionPane.showMessageDialog(this, "Book Saved Successfully!");

                    // AUTOMATICALLY SHOWS THE CATALOG LIST IMMEDIATELY AFTER CLOSING POPUP
                    ManageBooksFrame bookList = new ManageBooksFrame("Admin");
                    bookList.setVisible(true);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error saving book: " + ex.getMessage(), "Database Fault", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnManageBooks.addActionListener(e -> new ManageBooksFrame("Admin").setVisible(true));
        // 👥 ADD MEMBER PROCESS (Replaces the placeholder message popup)
        btnAddMember.addActionListener(e -> {
            // 1. Create a structured custom panel container with absolute positioning
            JPanel pnlMemberInput = new JPanel();
            pnlMemberInput.setLayout(null);
            pnlMemberInput.setPreferredSize(new Dimension(420, 130)); // Perfect fixed layout dimensions

            // 2. Define and position UI components cleanly
            JLabel lblMemberId = new JLabel("Member ID:");
            lblMemberId.setBounds(20, 20, 100, 25);
            JTextField txtMemberId = new JTextField();
            txtMemberId.setBounds(130, 20, 250, 25);

            JLabel lblMemberName = new JLabel("Student Name:");
            lblMemberName.setBounds(20, 70, 100, 25);
            JTextField txtMemberName = new JTextField();
            txtMemberName.setBounds(130, 70, 250, 25);

            // 3. Mount all layout elements onto the container panel
            pnlMemberInput.add(lblMemberId);
            pnlMemberInput.add(txtMemberId);
            pnlMemberInput.add(lblMemberName);
            pnlMemberInput.add(txtMemberName);

            // 4. Launch the dialog box cleanly without generic icon clutter
            int option = JOptionPane.showConfirmDialog(
                    this,
                    pnlMemberInput,
                    "Add New Student Member",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (option == JOptionPane.OK_OPTION) {
                String memberId = txtMemberId.getText().trim();
                String name = txtMemberName.getText().trim();

                // Validation check to make sure text strings aren't blank
                if (memberId.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All input fields are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 5. Execute SQL Insertion to your real MySQL database registry
                String query = "INSERT INTO members (member_id, name) VALUES (?, ?)";
                try (java.sql.Connection conn = com.library.db.DatabaseConnection.getConnection(); java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {

                    pstmt.setString(1, memberId);
                    pstmt.setString(2, name);
                    pstmt.executeUpdate();

                    // Show success confirmation popup message
                    JOptionPane.showMessageDialog(this, "Student Member Registered Successfully!");

                    // 6. AUTOMATICALLY OPEN THE UPDATED MANAGEMENT LIST FRAME RIGHT AFTER
                    new ManageMembersFrame().setVisible(true);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error saving member: " + ex.getMessage(), "Database Fault", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnManageMembers.addActionListener(e -> new ManageMembersFrame().setVisible(true));
        btnIssueBook.addActionListener(e -> new IssueBookFrame().setVisible(true));
        btnReturnBook.addActionListener(e -> {
            JPanel pnlReturn = new JPanel();
            pnlReturn.setLayout(null);
            pnlReturn.setPreferredSize(new Dimension(420, 130));

            JLabel lblBookId = new JLabel("Book ID:");
            lblBookId.setBounds(20, 20, 100, 25);
            JTextField txtBookId = new JTextField();
            txtBookId.setBounds(130, 20, 250, 25);

            JLabel lblMemberId = new JLabel("Member ID:");
            lblMemberId.setBounds(20, 70, 100, 25);
            JTextField txtMemberId = new JTextField();
            txtMemberId.setBounds(130, 70, 250, 25);

            pnlReturn.add(lblBookId);
            pnlReturn.add(txtBookId);
            pnlReturn.add(lblMemberId);
            pnlReturn.add(txtMemberId);

            int option = JOptionPane.showConfirmDialog(this, pnlReturn, "Process Asset Return", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String bId = txtBookId.getText().trim();
                String mId = txtMemberId.getText().trim();

                if (bId.isEmpty() || mId.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Fields cannot be left blank!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Database Transaction: 1. Set return timestamp, 2. Toggle book status back to Available
                String updateTx = "UPDATE transactions SET return_date = CURDATE() WHERE book_id = ? AND member_id = ? AND return_date IS NULL";
                String updateBook = "UPDATE books SET status = 'Available' WHERE book_id = ?";

                try (java.sql.Connection conn = com.library.db.DatabaseConnection.getConnection()) {
                    conn.setAutoCommit(false); // Transaction wrapper block

                    try (java.sql.PreparedStatement p1 = conn.prepareStatement(updateTx); java.sql.PreparedStatement p2 = conn.prepareStatement(updateBook)) {

                        p1.setString(1, bId);
                        p1.setString(2, mId);
                        int rowsUpdated = p1.executeUpdate();

                        if (rowsUpdated > 0) {
                            p2.setString(1, bId);
                            p2.executeUpdate();
                            conn.commit(); // Save changes together safely

                            JOptionPane.showMessageDialog(this, "Asset processed successfully! Rental record closed.");

                            // Instantly refresh and show the Admin Reports panel to prove the overdue item cleared!
                            new ManageBooksFrame("Admin").setVisible(true);
                        } else {
                            conn.rollback();
                            JOptionPane.showMessageDialog(this, "No matching active transaction found for this pair.", "Record Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Transaction Error: " + ex.getMessage(), "Database Fault", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // Inside your MainFrame button click action listener:
        btnIssueRecords.addActionListener(e -> {

            JFrame reportWindow = new JFrame("Admin Compliance & Data Analytics");
            reportWindow.setSize(900, 600);
            reportWindow.setLocationRelativeTo(this); // Centers it relative to your dashboard
            reportWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            AdminReportsPanel reportsPanel = new AdminReportsPanel();
            reportWindow.add(reportsPanel);

            reportWindow.setVisible(true);
        });
        btnLogout.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });
    }
}
