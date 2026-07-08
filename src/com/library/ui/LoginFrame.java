package com.library.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.library.db.DatabaseConnection; // Secure package binding

/**
 *
 * @author ADMIN
 */
public class LoginFrame extends JFrame { // "extends JFrame" makes this class a window
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole; 
    private JButton btnLogin, btnReset;
    private JLabel lblPass; // ✅ Fixed: Moved to a class variable so the listener can modify it dynamically

    // This is the Constructor method that configures and builds the layout UI
    public LoginFrame() {
        setTitle("Library Management System - Login");
        setSize(500, 380); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(245, 245, 245));

        JLabel lblTitle = new JLabel("SYSTEM LOGIN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBounds(50, 20, 400, 30);
        add(lblTitle);

        // 🎓 ROLE SELECTION ROW
        JLabel lblRole = new JLabel("Login As:");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblRole.setBounds(80, 80, 100, 25);
        add(lblRole);

        String[] roles = {"Admin", "Student"};
        cmbRole = new JComboBox<>(roles);
        cmbRole.setBounds(180, 80, 220, 25);
        add(cmbRole);

        JLabel lblUser = new JLabel("Username / ID:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setBounds(80, 130, 100, 25);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(180, 130, 220, 25);
        add(txtUsername);

        lblPass = new JLabel("Password:"); // ✅ Fixed: Initialized directly without re-declaring 'JLabel'
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setBounds(80, 180, 100, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(180, 180, 220, 25);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(130, 250, 100, 35);
        add(btnLogin);

        btnReset = new JButton("Reset");
        btnReset.setBounds(250, 250, 100, 35);
        add(btnReset);

        // 🔄 DYNAMIC FIELD TOGGLE ACTION
        // Automatically disables and greys out the password field if 'Student' is selected
        cmbRole.addActionListener(e -> {
            String selectedRole = cmbRole.getSelectedItem().toString();
            if (selectedRole.equals("Student")) {
                txtPassword.setText("");
                txtPassword.setEnabled(false); // Completely disables input
                txtPassword.setBackground(new Color(210, 210, 210)); // Greys out box visually
                lblPass.setForeground(Color.GRAY); // Greys out label text
            } else {
                txtPassword.setEnabled(true); // Re-enables for Admin
                txtPassword.setBackground(Color.WHITE); // Reverts back to white canvas
                lblPass.setForeground(Color.BLACK);
            }
        });

        // Action Handler for Login validation logic
        btnLogin.addActionListener(e -> {
            String selectedRole = cmbRole.getSelectedItem().toString();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your identification credentials.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (selectedRole.equals("Admin")) {
                // Admin validation path
                if (username.equals("admin") && password.equals("admin123")) {
                    new MainFrame().setVisible(true); // Opens full admin dashboard
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Admin Credentials", "Authentication Fault", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // 🎓 STUDENT LOG IN LOGIC
                if (verifyStudentInDatabase(username)) {
                    ManageBooksFrame studentView = new ManageBooksFrame("Student"); 
                    studentView.setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Student Member ID not found in database registry!", "Authentication Fault", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Action Handler for clearing data fields
        btnReset.addActionListener(e -> {
            txtUsername.setText("");
            txtPassword.setText("");
            cmbRole.setSelectedIndex(0);
            txtPassword.setEnabled(true);
            txtPassword.setBackground(Color.WHITE);
            lblPass.setForeground(Color.BLACK);
            txtUsername.requestFocus();
        });
    }

    // 🔍 DATABASE METHOD: Verifies if the student exists in the 'members' table
    private boolean verifyStudentInDatabase(String studentId) {
        String query = "SELECT * FROM members WHERE member_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if a matching student row exists
            }
        } catch (Exception e) {
            System.out.println("Student Login Database Error: " + e.getMessage());
            return false;
        }
    }

    // This is the Main running method that executes the application window
    public static void main(String[] args) {
        try { 
            // Sets native system look and feel for a clean UI
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception e) {
            System.out.println("Look and feel setting failed: " + e.getMessage());
        }
        
        DatabaseConnection.initializeDatabase(); 

        // Launch the login interface frame safely on the event dispatch thread
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}