/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.library.db.DatabaseConnection;
/**
 *
 * @author ADMIN
 */
public class LoginFrame extends JFrame { // "extends JFrame" makes this class a window
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnReset;

    // This is the Constructor method that configures and builds the layout UI
    public LoginFrame() {
        setTitle("Library Management System - Login");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(245, 245, 245));

        JLabel lblTitle = new JLabel("SYSTEM LOGIN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBounds(50, 30, 400, 30);
        add(lblTitle);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setBounds(80, 110, 100, 25);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(180, 110, 220, 25);
        add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setBounds(80, 160, 100, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(180, 160, 220, 25);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(130, 230, 100, 35);
        add(btnLogin);

        btnReset = new JButton("Reset");
        btnReset.setBounds(250, 230, 100, 35);
        add(btnReset);

        // Action Handler for Login validation logic
        btnLogin.addActionListener(e -> {
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword());

            if (user.equals("admin") && pass.equals("admin123")) {
                JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); // Closes the Login Window safely
                new MainFrame().setVisible(true); // Opens the Main Dashboard
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials!", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Action Handler for clearing data fields
        btnReset.addActionListener(e -> {
            txtUsername.setText("");
            txtPassword.setText("");
            txtUsername.requestFocus();
        });
    }

    // This is the Main running method that executes the application window
    public static void main(String[] args) {
        try { 
            // Sets native system look and feel for a clean UI
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception e) {
            System.out.println("Look and feel setting failed: " + e.getMessage());
        }
        
       
        com.library.db.DatabaseConnection.initializeDatabase(); 

        // Launch the login interface frame safely on the event dispatch thread
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}