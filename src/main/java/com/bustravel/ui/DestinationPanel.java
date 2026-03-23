package com.bustravel.ui;

import com.bustravel.dao.DestinationDAO;
import com.bustravel.model.Destination;
import com.bustravel.ui.theme.ThemeManager;
import com.bustravel.ui.theme.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class DestinationPanel extends JPanel {
    private JTextField txtCodeDestination;
    private JTextField txtNomVille;
    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnNouveau;

    private JTable tableDestination;
    private DefaultTableModel tableModel;
    private ArrayList<Destination> listeDestinations;
    private int idAdministrateurConnecte = 1;

    public DestinationPanel() {
        listeDestinations = new ArrayList<>();
        initComponents();
        layoutComponents();
        chargerDestinations();
    }

    public DestinationPanel(int idAdministrateur) {
        this.idAdministrateurConnecte = idAdministrateur;
        listeDestinations = new ArrayList<>();
        initComponents();
        layoutComponents();
        chargerDestinations();
    }

    private void initComponents() {
        txtCodeDestination = new JTextField(15);
        txtNomVille = new JTextField(15);

        UIUtils.styleTextField(txtCodeDestination, "Dest. Code (ex: PAR)");
        UIUtils.styleTextField(txtNomVille, "City Name (ex: Paris)");

        btnAjouter = new JButton("Add Destination");
        UIUtils.styleButton(btnAjouter, ThemeManager.PRIMARY_COLOR);

        btnModifier = new JButton("Update");
        UIUtils.styleButton(btnModifier, ThemeManager.SECONDARY_COLOR);

        btnSupprimer = new JButton("Delete");
        UIUtils.styleButton(btnSupprimer, ThemeManager.ERROR_COLOR);

        btnNouveau = new JButton("Clear Form");
        UIUtils.styleButton(btnNouveau, Color.GRAY);

        String[] colonnes = {"Destination Code", "City Name"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableDestination = new JTable(tableModel);
        tableDestination.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIUtils.styleTable(tableDestination);

        btnAjouter.addActionListener(e -> ajouterDestination());
        btnModifier.addActionListener(e -> modifierDestination());
        btnSupprimer.addActionListener(e -> supprimerDestination());
        btnNouveau.addActionListener(e -> nouvelleDestination());

        tableDestination.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectionnerDestination();
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
        JPanel panelFormulaire = UIUtils.createCardPanel("Destination Details");
        panelFormulaire.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblCode = new JLabel("Destination Code:");
        lblCode.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panelFormulaire.add(lblCode, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulaire.add(txtCodeDestination, gbc);

        JLabel lblVille = new JLabel("City Name:");
        lblVille.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        panelFormulaire.add(lblVille, gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        panelFormulaire.add(txtNomVille, gbc);

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

        // Table Panel
        JPanel tablePanel = UIUtils.createCardPanel("Available Destinations");
        tablePanel.setLayout(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(tableDestination);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeManager.getCardColor());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void chargerDestinations() {
        listeDestinations = DestinationDAO.getAllDestination();
        tableModel.setRowCount(0);
        for (Destination d : listeDestinations) {
            tableModel.addRow(new Object[]{d.getCodeDestination(), d.getNomVille()});
        }
    }

    private void ajouterDestination() {
        if (validerChamps()) {
            String codeDestination = txtCodeDestination.getText().trim();
            String nomVille = txtNomVille.getText().trim();

            if (DestinationDAO.codeDestinationExiste(codeDestination)) {
                JOptionPane.showMessageDialog(this,
                        "A destination with this code already exists!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                txtCodeDestination.requestFocus();
                return;
            }

            Destination destination = new Destination();
            destination.setCodeDestination(codeDestination);
            destination.setNomVille(nomVille);
            destination.setIdAdministrateur(idAdministrateurConnecte);

            if (DestinationDAO.ajouterDestination(destination)) {
                listeDestinations.add(destination);
                tableModel.addRow(new Object[]{codeDestination, nomVille});
                nouvelleDestination();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add destination",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierDestination() {
        int selectedRow = tableDestination.getSelectedRow();
        if (selectedRow == -1) return;

        if (validerChamps()) {
            String codeDestination = txtCodeDestination.getText().trim();
            String nomVille = txtNomVille.getText().trim();

            Destination destination = listeDestinations.get(selectedRow);
            destination.setCodeDestination(codeDestination);
            destination.setNomVille(nomVille);
            destination.setIdAdministrateur(idAdministrateurConnecte);

            if (DestinationDAO.modifierDestination(destination)) {
                tableModel.setValueAt(codeDestination, selectedRow, 0);
                tableModel.setValueAt(nomVille, selectedRow, 1);
                nouvelleDestination();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerDestination() {
        int selectedRow = tableDestination.getSelectedRow();
        if (selectedRow == -1) return;

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this destination?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            String codeDestination = listeDestinations.get(selectedRow).getCodeDestination();
            
            if (DestinationDAO.supprimerDestination(codeDestination)) {
                listeDestinations.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                nouvelleDestination();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectionnerDestination() {
        int selectedRow = tableDestination.getSelectedRow();
        if (selectedRow != -1) {
            txtCodeDestination.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtNomVille.setText(tableModel.getValueAt(selectedRow, 1).toString());

            btnAjouter.setEnabled(false);
            btnModifier.setEnabled(true);
            btnSupprimer.setEnabled(true);
        }
    }

    private void nouvelleDestination() {
        txtCodeDestination.setText("");
        txtNomVille.setText("");

        tableDestination.clearSelection();
        btnAjouter.setEnabled(true);
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
    }

    private boolean validerChamps() {
        if (txtCodeDestination.getText().trim().isEmpty() || txtNomVille.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}