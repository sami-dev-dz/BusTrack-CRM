package com.bustravel.ui;

import com.bustravel.model.Receptionniste;
import com.bustravel.service.AuthService;
import com.bustravel.ui.theme.ThemeManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPanel extends JFrame {
    
    private static final Logger logger = LogManager.getLogger(LoginPanel.class);
    
    private JTextField txtEmail;
    private JPasswordField txtMotDePasse;
    private JButton btnConnexion;
    private JLabel lblMessageErreur;
    
    private int tentativesEchouees = 0;
    private static final int MAX_TENTATIVES = 5;
    
    public LoginPanel() {
        setTitle("Bus Travel Management System - Authentication");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main container
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(ThemeManager.getBackgroundColor());
        
        // Left Panel (Branding)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(ThemeManager.PRIMARY_COLOR);
        
        JLabel lblLogo = new JLabel("🌍", SwingConstants.CENTER); // Placeholder for SVG/Icon
        lblLogo.setFont(new Font("Segoe UI", Font.PLAIN, 100));
        
        JLabel lblBrand = new JLabel("Enterprise Bus Travel", SwingConstants.CENTER);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblBrand.setForeground(Color.WHITE);
        
        JLabel lblTagline = new JLabel("Premium Bus Travel Management", SwingConstants.CENTER);
        lblTagline.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblTagline.setForeground(new Color(255, 255, 255, 200));
        
        JPanel brandInnerPanel = new JPanel(new GridLayout(3, 1));
        brandInnerPanel.setOpaque(false);
        brandInnerPanel.add(lblLogo);
        brandInnerPanel.add(lblBrand);
        brandInnerPanel.add(lblTagline);
        brandInnerPanel.setBorder(new EmptyBorder(120, 20, 20, 20));
        
        leftPanel.add(brandInnerPanel, BorderLayout.CENTER);
        
        // Right Panel (Form)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(ThemeManager.getCardColor());
        rightPanel.setBorder(new EmptyBorder(60, 60, 60, 60));
        
        JLabel lblWelcome = new JLabel("Welcome back");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(ThemeManager.getTextColor());
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitle = new JLabel("Please enter your details to sign in");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.GRAY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        txtEmail = new JTextField();
        txtEmail.putClientProperty("JTextField.placeholderText", "Email");
        txtEmail.putClientProperty("JComponent.roundRect", true);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        txtMotDePasse = new JPasswordField();
        txtMotDePasse.putClientProperty("JTextField.placeholderText", "Password");
        txtMotDePasse.putClientProperty("JComponent.roundRect", true);
        txtMotDePasse.putClientProperty("JTextField.showRevealButton", true);
        txtMotDePasse.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtMotDePasse.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        btnConnexion = new JButton("Sign In");
        btnConnexion.putClientProperty("JButton.buttonType", "roundRect");
        btnConnexion.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnConnexion.setBackground(ThemeManager.PRIMARY_COLOR);
        btnConnexion.setForeground(Color.WHITE);
        btnConnexion.setFocusPainted(false);
        btnConnexion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConnexion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnConnexion.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblMessageErreur = new JLabel(" ");
        lblMessageErreur.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMessageErreur.setForeground(ThemeManager.ERROR_COLOR);
        lblMessageErreur.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Actions
        btnConnexion.addActionListener(e -> connecter());
        txtMotDePasse.addActionListener(e -> connecter());
        txtEmail.addActionListener(e -> txtMotDePasse.requestFocus());
        
        // Assembly right panel
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(lblWelcome);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(lblSubtitle);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(emailLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(txtEmail);
        
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JLabel pwdLabel = new JLabel("Password");
        pwdLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pwdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(pwdLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(txtMotDePasse);
        
        rightPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        rightPanel.add(btnConnexion);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        rightPanel.add(lblMessageErreur);
        rightPanel.add(Box.createVerticalGlue());
        
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel);
    }
    
    private void afficherErreur(String message) {
        lblMessageErreur.setText(message);
    }
    
    private void connecter() {
        lblMessageErreur.setText(" ");
        
        if (tentativesEchouees >= MAX_TENTATIVES) {
            JOptionPane.showMessageDialog(this, 
                "Maximum attempts reached. Application will exit for security reasons.", 
                "Access Blocked", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }
        
        String email = txtEmail.getText().trim();
        String motDePasse = new String(txtMotDePasse.getPassword()).trim();
        
        if (email.isEmpty() || motDePasse.isEmpty()) {
            afficherErreur("Both fields are required.");
            return;
        }
        
        // Show loading state
        btnConnexion.setText("Authenticating...");
        btnConnexion.setEnabled(false);
        
        // Run DB Auth on background thread
        new SwingWorker<Receptionniste, Void>() {
            @Override
            protected Receptionniste doInBackground() {
                try {
                	return AuthService.authenticate(email, motDePasse);
                } catch (Exception ex) {
                    logger.error("Database Error during authentication", ex);
                    return null;
                }
            }

            @Override
            protected void done() {
                btnConnexion.setText("Sign In");
                btnConnexion.setEnabled(true);
                
                try {
                    Receptionniste user = get(); // retrieve result
                    if (user != null) {
                        tentativesEchouees = 0;
                        dispose(); // Close Login window
                        
                        // Open Main Application correctly in EDT
                        SwingUtilities.invokeLater(() -> {
                            MainFrame app = new MainFrame(user);
                            app.setVisible(true);
                        });
                        
                    } else {
                        tentativesEchouees++;
                        afficherErreur("Invalid credentials (Attempt " + tentativesEchouees + "/" + MAX_TENTATIVES + ")");
                        txtMotDePasse.setText("");
                        txtMotDePasse.requestFocus();
                        logger.warn("Failed login attempt for: " + email);
                    }
                } catch (Exception ex) {
                    afficherErreur("System Error: Unable to connect.");
                    logger.error("SwingWorker execution issue", ex);
                }
            }
        }.execute();
    }
    
    public static void main(String[] args) {
        ThemeManager.isDarkMode = false; // Default preference
        ThemeManager.setup();
        
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.showSplashAndRun(() -> {
                new LoginPanel().setVisible(true);
            });
        });
    }
}