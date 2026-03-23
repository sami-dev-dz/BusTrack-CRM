package com.bustravel.ui;

import com.bustravel.dao.PassagerDAO;
import com.bustravel.model.Passager;
import com.bustravel.ui.theme.ThemeManager;
import com.bustravel.ui.theme.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class PassagerPanel extends JPanel {
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JTextField txtAdresse;
    private JTextField txtTelephone;
    private JTextField txtRecherche;

    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnNouveau;
    private JButton btnRechercher;
    private JButton btnActualiser;

    private JTable tablePassagers;
    private DefaultTableModel tableModel;
    private ArrayList<Passager> listePassagers;

    public PassagerPanel() {
        listePassagers = new ArrayList<>();
        initComponents();
        layoutComponents();
        chargerPassagers();
    }

    private void initComponents() {
        txtNom = new JTextField(15);
        txtPrenom = new JTextField(15);
        txtAdresse = new JTextField(15);
        txtTelephone = new JTextField(15);
        txtRecherche = new JTextField(20);

        UIUtils.styleTextField(txtNom, "Last Name (ex: Doe)");
        UIUtils.styleTextField(txtPrenom, "First Name (ex: John)");
        UIUtils.styleTextField(txtAdresse, "Address");
        UIUtils.styleTextField(txtTelephone, "Phone (ex: +33...)");
        UIUtils.styleTextField(txtRecherche, "Search Passengers...");

        btnAjouter = new JButton("Add Passenger");
        UIUtils.styleButton(btnAjouter, ThemeManager.PRIMARY_COLOR);

        btnModifier = new JButton("Update");
        UIUtils.styleButton(btnModifier, ThemeManager.SECONDARY_COLOR);

        btnSupprimer = new JButton("Delete");
        UIUtils.styleButton(btnSupprimer, ThemeManager.ERROR_COLOR);

        btnNouveau = new JButton("Clear Form");
        UIUtils.styleButton(btnNouveau, Color.GRAY);

        btnRechercher = new JButton("Search");
        UIUtils.styleButton(btnRechercher, ThemeManager.PRIMARY_COLOR.darker());

        btnActualiser = new JButton("Refresh");
        UIUtils.styleButton(btnActualiser, ThemeManager.SECONDARY_COLOR);

        String[] colonnes = {"Last Name", "First Name", "Address", "Phone"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablePassagers = new JTable(tableModel);
        tablePassagers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIUtils.styleTable(tablePassagers);

        btnAjouter.addActionListener(e -> ajouterPassager());
        btnModifier.addActionListener(e -> modifierPassager());
        btnSupprimer.addActionListener(e -> supprimerPassager());
        btnNouveau.addActionListener(e -> nouveauPassager());
        btnRechercher.addActionListener(e -> rechercherPassagers());
        btnActualiser.addActionListener(e -> chargerPassagers());

        txtRecherche.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    rechercherPassagers();
                }
            }
        });

        tablePassagers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectionnerPassager();
                }
            }
        });

        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(ThemeManager.getBackgroundColor());
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Form Panel
        JPanel panelFormulaire = UIUtils.createCardPanel("Passenger Details");
        panelFormulaire.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblNom = new JLabel("Last Name:");
        lblNom.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panelFormulaire.add(lblNom, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulaire.add(txtNom, gbc);

        JLabel lblPrenom = new JLabel("First Name:");
        lblPrenom.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        panelFormulaire.add(lblPrenom, gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        panelFormulaire.add(txtPrenom, gbc);

        JLabel lblAdresse = new JLabel("Address:");
        lblAdresse.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelFormulaire.add(lblAdresse, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulaire.add(txtAdresse, gbc);

        JLabel lblTelephone = new JLabel("Phone:");
        lblTelephone.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0;
        panelFormulaire.add(lblTelephone, gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        panelFormulaire.add(txtTelephone, gbc);

        // Buttons Panel
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelBoutons.setBackground(ThemeManager.getCardColor());
        panelBoutons.add(btnNouveau);
        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnModifier);
        panelBoutons.add(btnSupprimer);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(panelFormulaire, BorderLayout.CENTER);
        topContainer.add(panelBoutons, BorderLayout.SOUTH);

        // Search Panel
        JPanel panelRecherche = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelRecherche.setOpaque(false);
        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        panelRecherche.add(searchLbl);
        panelRecherche.add(txtRecherche);
        panelRecherche.add(btnRechercher);
        panelRecherche.add(btnActualiser);

        // Table Panel
        JPanel tablePanel = UIUtils.createCardPanel("Passengers Directory");
        tablePanel.setLayout(new BorderLayout());
        
        tablePanel.add(panelRecherche, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(tablePassagers);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        scrollPane.getViewport().setBackground(ThemeManager.getCardColor());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void chargerPassagers() {
        listePassagers = PassagerDAO.getAllPassagers();
        afficherPassagersDansTable();
        nouveauPassager();
    }

    private void rechercherPassagers() {
        String critere = txtRecherche.getText().trim();
        if (critere.isEmpty()) {
            chargerPassagers();
        } else {
            listePassagers = PassagerDAO.rechercher(critere);
            afficherPassagersDansTable();
        }
        nouveauPassager();
    }

    private void afficherPassagersDansTable() {
        tableModel.setRowCount(0);
        if (listePassagers == null) {
            listePassagers = new ArrayList<>();
        }
        for (Passager p : listePassagers) {
            tableModel.addRow(new Object[]{
                p.getNom(), p.getPrenom(), p.getAdresse(), p.getTelephone()
            });
        }
    }

    private void ajouterPassager() {
        if (validerChamps()) {
            String nom = txtNom.getText().trim();
            String prenom = txtPrenom.getText().trim();
            String adresse = txtAdresse.getText().trim();
            String telephone = txtTelephone.getText().trim();

            Passager passager = new Passager(nom, prenom, adresse, telephone);

            if (PassagerDAO.ajouter(passager)) {
                chargerPassagers();
                nouveauPassager();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add passenger",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierPassager() {
        int selectedRow = tablePassagers.getSelectedRow();
        if (selectedRow == -1) return;

        if (validerChamps()) {
            Passager passagerOriginal = listePassagers.get(selectedRow);
            int id = passagerOriginal.getIdPassager();

            String nom = txtNom.getText().trim();
            String prenom = txtPrenom.getText().trim();
            String adresse = txtAdresse.getText().trim();
            String telephone = txtTelephone.getText().trim();

            Passager passager = new Passager(nom, prenom, adresse, telephone);
            passager.setIdPassager(id);

            if (PassagerDAO.modifier(passager)) {
                chargerPassagers();
                nouveauPassager();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerPassager() {
        int selectedRow = tablePassagers.getSelectedRow();
        if (selectedRow == -1) return;

        Passager passager = listePassagers.get(selectedRow);

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Delete " + passager.getNom() + " " + passager.getPrenom() + "?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            if (PassagerDAO.supprimer(passager.getIdPassager())) {
                chargerPassagers();
                nouveauPassager();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting. They might have active reservations.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectionnerPassager() {
        int selectedRow = tablePassagers.getSelectedRow();
        if (selectedRow != -1 && selectedRow < listePassagers.size()) {
            Passager passager = listePassagers.get(selectedRow);
            txtNom.setText(passager.getNom());
            txtPrenom.setText(passager.getPrenom());
            txtAdresse.setText(passager.getAdresse());
            txtTelephone.setText(passager.getTelephone());

            btnAjouter.setEnabled(false);
            btnModifier.setEnabled(true);
            btnSupprimer.setEnabled(true);
        }
    }

    private void nouveauPassager() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtAdresse.setText("");
        txtTelephone.setText("");

        tablePassagers.clearSelection();
        btnAjouter.setEnabled(true);
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
    }

    private boolean validerChamps() {
        if (txtNom.getText().trim().isEmpty() || txtPrenom.getText().trim().isEmpty() || 
            txtAdresse.getText().trim().isEmpty() || txtTelephone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}