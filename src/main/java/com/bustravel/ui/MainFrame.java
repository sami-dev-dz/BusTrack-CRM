package com.bustravel.ui;

import com.bustravel.model.Receptionniste;
import com.bustravel.ui.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private Receptionniste currentUser;

    private BusPanel busPanel;
    private DestinationPanel destinationPanel;
    private VoyagePanel voyagePanel;
    private VoyageDestinationPanel voyageDestinationPanel;
    private ReceptionnistePanel receptionnistePanel;
    private PassagerPanel passagerPanel;
    private ReservationPanel reservationPanel;

    public MainFrame(Receptionniste user) {
        this.currentUser = user;

        setTitle("BusTrack-CRM - " + user.getEmail() + " (" + user.getRole() + ")");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Navigation Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(ThemeManager.getCardColor());
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        topBar.setPreferredSize(new Dimension(1200, 60));

        JLabel titleLabel = new JLabel("  BusTrack-CRM", SwingConstants.LEFT); // Add icon here later
        titleLabel.setFont(ThemeManager.FONT_HEADING.deriveFont(24f));
        titleLabel.setForeground(ThemeManager.PRIMARY_COLOR);
        topBar.add(titleLabel, BorderLayout.WEST);

        JPanel rightTopPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        rightTopPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("Welcome, " + user.getEmail() + " ");
        userLabel.setFont(ThemeManager.FONT_BODY);
        
        JButton themeToggle = new JButton("Toggle Theme");
        themeToggle.putClientProperty("JButton.buttonType", "roundRect");
        themeToggle.addActionListener(e -> ThemeManager.toggleTheme(this));

        rightTopPanel.add(userLabel);
        rightTopPanel.add(themeToggle);
        topBar.add(rightTopPanel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Sidebar Menu
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ThemeManager.PRIMARY_COLOR);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Content Area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ThemeManager.getBackgroundColor());
        
        // Setup initial dummy welcome panel
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(ThemeManager.getBackgroundColor());
        JLabel welcomeMsg = new JLabel("Welcome to the Management System Dashboard");
        welcomeMsg.setFont(ThemeManager.FONT_HEADING);
        welcomePanel.add(welcomeMsg);
        contentPanel.add(welcomePanel, "WELCOME");

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        initPanelsAndSidebar(sidebar);
        
        cardLayout.show(contentPanel, "WELCOME");
    }

    private void initPanelsAndSidebar(JPanel sidebar) {
        JLabel menuLabel = new JLabel(" MENU");
        menuLabel.setFont(ThemeManager.FONT_SUBHEADING.deriveFont(14f));
        menuLabel.setForeground(new Color(255, 255, 255, 180));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        sidebar.add(menuLabel);

        if (currentUser.isAdmin()) {
            busPanel = new BusPanel();
            destinationPanel = new DestinationPanel();
            voyagePanel = new VoyagePanel();
            voyageDestinationPanel = new VoyageDestinationPanel();
            receptionnistePanel = new ReceptionnistePanel();

            contentPanel.add(busPanel, "BUS");
            contentPanel.add(destinationPanel, "DESTINATION");
            contentPanel.add(voyagePanel, "VOYAGE");
            contentPanel.add(voyageDestinationPanel, "VOYAGE_DESTINATION");
            contentPanel.add(receptionnistePanel, "RECEPTIONNISTE");

            sidebar.add(createMenuButton("Bus Management", e -> showCard("BUS")));
            sidebar.add(createMenuButton("Destinations", e -> showCard("DESTINATION")));
            sidebar.add(createMenuButton("Voyages", e -> showCard("VOYAGE")));
            sidebar.add(createMenuButton("Voyage-Dest", e -> showCard("VOYAGE_DESTINATION")));
            sidebar.add(createMenuButton("Staff Mgmt", e -> showCard("RECEPTIONNISTE")));

        } else if (currentUser.isReceptionniste()) {
            passagerPanel = new PassagerPanel();
            reservationPanel = new ReservationPanel();

            contentPanel.add(passagerPanel, "PASSAGER");
            contentPanel.add(reservationPanel, "RESERVATION");

            sidebar.add(createMenuButton("Passengers", e -> showCard("PASSAGER")));
            sidebar.add(createMenuButton("Reservations", e -> showCard("RESERVATION")));
        }

        sidebar.add(Box.createVerticalGlue()); // Push logout to bottom
        
        JButton logoutBtn = createMenuButton("Sign Out", e -> deconnecter());
        logoutBtn.setBackground(ThemeManager.ERROR_COLOR);
        sidebar.add(logoutBtn);
    }
    
    private void showCard(String name) {
        cardLayout.show(contentPanel, name);
    }

    private JButton createMenuButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD, 15f));
        button.setForeground(Color.WHITE);
        button.setBackground(ThemeManager.PRIMARY_COLOR);
        button.setBorder(new EmptyBorder(12, 15, 12, 15));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!button.getBackground().equals(ThemeManager.ERROR_COLOR)) {
                    button.setBackground(ThemeManager.PRIMARY_COLOR.darker());
                } else {
                    button.setBackground(ThemeManager.ERROR_COLOR.darker());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!button.getBackground().equals(ThemeManager.ERROR_COLOR.darker())) {
                    button.setBackground(ThemeManager.PRIMARY_COLOR);
                } else {
                    button.setBackground(ThemeManager.ERROR_COLOR);
                }
            }
        });

        button.addActionListener(action);
        return button;
    }

    private void deconnecter() {
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to sign out?",
                "Sign Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirmation == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginPanel().setVisible(true));
        }
    }

    public Receptionniste getCurrentUser() {
        return currentUser;
    }
}