package com.bustravel.ui;

import com.bustravel.ui.theme.ThemeManager;
import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {
    
    private JProgressBar progressBar;
    private JLabel loadingLabel;

    public SplashScreen() {
        setSize(500, 300);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeManager.PRIMARY_COLOR);
        mainPanel.setBorder(BorderFactory.createLineBorder(ThemeManager.PRIMARY_COLOR.darker(), 2));
        
        // Logo / Title
        JLabel titleLabel = new JLabel("Bus Travel Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subTitle = new JLabel("Bus Travel Management", SwingConstants.CENTER);
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subTitle.setForeground(new Color(255, 255, 255, 200));
        
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.setOpaque(false);
        centerPanel.add(titleLabel);
        centerPanel.add(subTitle);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Progress Bar
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        loadingLabel = new JLabel("Loading systems...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        loadingLabel.setForeground(Color.WHITE);
        
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBackground(ThemeManager.PRIMARY_COLOR.darker());
        progressBar.setForeground(Color.WHITE);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(460, 6));
        
        bottomPanel.add(loadingLabel, BorderLayout.NORTH);
        bottomPanel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        bottomPanel.add(progressBar, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    public void setProgressText(String text) {
        SwingUtilities.invokeLater(() -> loadingLabel.setText(text));
    }

    public void showSplashAndRun(Runnable nextStep) {
        setVisible(true);
        
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Simulate loading steps for visual effect
                setProgressText("Initializing Theme...");
                Thread.sleep(500);
                setProgressText("Connecting to Database...");
                Thread.sleep(800);
                setProgressText("Loading Modules...");
                Thread.sleep(600);
                setProgressText("Ready.");
                Thread.sleep(300);
                return null;
            }

            @Override
            protected void done() {
                dispose();
                SwingUtilities.invokeLater(nextStep);
            }
        }.execute();
    }
}
