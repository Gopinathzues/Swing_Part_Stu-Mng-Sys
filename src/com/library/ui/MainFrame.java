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

        // Action routing listeners to sub-panels
       // Inside MainFrame.java constructor, update these exact lines:
        btnAddBook.addActionListener(e -> new AddBookFram().setVisible(true));
        btnManageBooks.addActionListener(e -> new ManageBooksFrame().setVisible(true));
        btnAddMember.addActionListener(e -> new AddMemberFrame().setVisible(true));
        btnManageMembers.addActionListener(e -> new ManageMembersFrame().setVisible(true));
        btnIssueBook.addActionListener(e -> new IssueBookFrame().setVisible(true));
        btnReturnBook.addActionListener(e -> new ReturnBookFrame().setVisible(true));
        btnIssueRecords.addActionListener(e -> new IssueRecordsFrame().setVisible(true));
        btnLogout.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });
    }
}