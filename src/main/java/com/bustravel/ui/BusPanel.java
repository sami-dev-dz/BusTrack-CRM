package com.bustravel.ui;

import com.bustravel.dao.BusDAO;
import com.bustravel.model.Bus;
import com.bustravel.ui.theme.ThemeManager;
import com.bustravel.ui.theme.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class BusPanel extends JPanel {
    private JTextField txtMatricule;
    private JTextField txtMarque;
    private JTextField txtModele;
    private JTextField txtCapacite;

    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnNouveau;

    private JTable tableBus;
    private DefaultTableModel tableModel;

    private ArrayList<Bus> listeBus;
    private int idAdministrateurConnecte = 1;

    public BusPanel() {
        listeBus = new ArrayList<>();
        initComponents();
        layoutComponents();
        chargerBus();
    }

    public BusPanel(int idAdministrateur) {
        this.idAdministrateurConnecte = idAdministrateur;
        listeBus = new ArrayList<>();
        initComponents();
        layoutComponents();
        chargerBus();
    }

    private void initComponents() {
        txtMatricule = new JTextField(15);
        txtMarque = new JTextField(15);
        txtModele = new JTextField(15);
        txtCapacite = new JTextField(10);

        UIUtils.styleTextField(txtMatricule, "Registration (ex: AB-123-CD)");
        UIUtils.styleTextField(txtMarque, "Brand (ex: Mercedes)");
        UIUtils.styleTextField(txtModele, "Model (ex: Sprinter)");
        UIUtils.styleTextField(txtCapacite, "Seats (ex: 50)");

        btnAjouter = new JButton("Add Bus");
        UIUtils.styleButton(btnAjouter, ThemeManager.PRIMARY_COLOR);

        btnModifier = new JButton("Update Bus");
        UIUtils.styleButton(btnModifier, ThemeManager.SECONDARY_COLOR);

        btnSupprimer = new JButton("Delete Bus");
        UIUtils.styleButton(btnSupprimer, ThemeManager.ERROR_COLOR);

        btnNouveau = new JButton("Clear Form");
        UIUtils.styleButton(btnNouveau, Color.GRAY);

        String[] colonnes = {"Registration", "Brand", "Model", "Seats"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBus = new JTable(tableModel);
        tableBus.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIUtils.styleTable(tableBus);

        btnAjouter.addActionListener(e -> ajouterBus());
        btnModifier.addActionListener(e -> modifierBus());
        btnSupprimer.addActionListener(e -> supprimerBus());
        btnNouveau.addActionListener(e -> nouveauBus());

        tableBus.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectionnerBus();
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
        JPanel panelFormulaire = UIUtils.createCardPanel("Bus Details");
        panelFormulaire.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblMatricule = new JLabel("Registration No:");
        lblMatricule.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panelFormulaire.add(lblMatricule, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulaire.add(txtMatricule, gbc);

        JLabel lblMarque = new JLabel("Brand:");
        lblMarque.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        panelFormulaire.add(lblMarque, gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        panelFormulaire.add(txtMarque, gbc);

        JLabel lblModele = new JLabel("Model:");
        lblModele.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelFormulaire.add(lblModele, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulaire.add(txtModele, gbc);

        JLabel lblCapacite = new JLabel("Capacity:");
        lblCapacite.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0;
        panelFormulaire.add(lblCapacite, gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        panelFormulaire.add(txtCapacite, gbc);

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
        JPanel tablePanel = UIUtils.createCardPanel("Bus Inventory");
        tablePanel.setLayout(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(tableBus);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeManager.getCardColor());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void chargerBus() {
        listeBus = BusDAO.getAllBus();
        tableModel.setRowCount(0);
        for (Bus b : listeBus) {
            tableModel.addRow(new Object[]{
                b.getImmatriculation(), 
                b.getMarque(), 
                b.getModele(), 
                b.getNbPlaces()
            });
        }
    }

    private void ajouterBus() {
        if (validerChamps()) {
            String immatriculation = txtMatricule.getText().trim();
            String marque = txtMarque.getText().trim();
            String modele = txtModele.getText().trim();
            int nbPlaces = Integer.parseInt(txtCapacite.getText().trim());

            if (BusDAO.immatriculationExiste(immatriculation)) {
                JOptionPane.showMessageDialog(this,
                        "A bus with this registration already exists!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                txtMatricule.requestFocus();
                return;
            }

            Bus bus = new Bus();
            bus.setImmatriculation(immatriculation);
            bus.setMarque(marque);
            bus.setModele(modele);
            bus.setNbPlaces(nbPlaces);
            bus.setIdAdministrateur(idAdministrateurConnecte);

            if (BusDAO.ajouterBus(bus)) {
                listeBus.add(bus);
                tableModel.addRow(new Object[]{immatriculation, marque, modele, nbPlaces});
                nouveauBus();
            } else {
                JOptionPane.showMessageDialog(this, "Database update failed.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierBus() {
        int selectedRow = tableBus.getSelectedRow();
        if (selectedRow == -1) return;

        if (validerChamps()) {
            String immatriculation = txtMatricule.getText().trim();
            String marque = txtMarque.getText().trim();
            String modele = txtModele.getText().trim();
            int nbPlaces = Integer.parseInt(txtCapacite.getText().trim());

            Bus bus = listeBus.get(selectedRow);
            bus.setImmatriculation(immatriculation);
            bus.setMarque(marque);
            bus.setModele(modele);
            bus.setNbPlaces(nbPlaces);
            bus.setIdAdministrateur(idAdministrateurConnecte);

            if (BusDAO.modifierBus(bus)) {
                tableModel.setValueAt(immatriculation, selectedRow, 0);
                tableModel.setValueAt(marque, selectedRow, 1);
                tableModel.setValueAt(modele, selectedRow, 2);
                tableModel.setValueAt(nbPlaces, selectedRow, 3);
                nouveauBus();
            } else {
                JOptionPane.showMessageDialog(this, "Database update failed.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerBus() {
        int selectedRow = tableBus.getSelectedRow();
        if (selectedRow == -1) return;

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this bus?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            String immatriculation = listeBus.get(selectedRow).getImmatriculation();
            
            if (BusDAO.supprimerBus(immatriculation)) {
                listeBus.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                nouveauBus();
            } else {
                JOptionPane.showMessageDialog(this, "Database delete failed.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectionnerBus() {
        int selectedRow = tableBus.getSelectedRow();
        if (selectedRow != -1) {
            txtMatricule.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtMarque.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtModele.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtCapacite.setText(tableModel.getValueAt(selectedRow, 3).toString());

            btnAjouter.setEnabled(false);
            btnModifier.setEnabled(true);
            btnSupprimer.setEnabled(true);
        }
    }

    private void nouveauBus() {
        txtMatricule.setText("");
        txtMarque.setText("");
        txtModele.setText("");
        txtCapacite.setText("");

        tableBus.clearSelection();
        btnAjouter.setEnabled(true);
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
    }

    private boolean validerChamps() {
        if (txtMatricule.getText().trim().isEmpty() || txtMarque.getText().trim().isEmpty() || txtModele.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All text fields must be filled.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int nbPlaces = Integer.parseInt(txtCapacite.getText().trim());
            if (nbPlaces <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacity must be a positive integer.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            txtCapacite.requestFocus();
            return false;
        }

        return true;
    }
}