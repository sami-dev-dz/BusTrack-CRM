package com.bustravel.ui;

import com.bustravel.dao.DestinationDAO;
import com.bustravel.dao.VoyageDAO;
import com.bustravel.dao.VoyageDestinationDAO;
import com.bustravel.model.Destination;
import com.bustravel.model.Voyage;
import com.bustravel.ui.theme.ThemeManager;
import com.bustravel.ui.theme.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class VoyageDestinationPanel extends JPanel {
    private JComboBox<String> cmbVoyage;
    private JComboBox<String> cmbDestination;

    private JButton btnAssocier;
    private JButton btnDissocier;
    private JButton btnNouveau;
    private JButton btnActualiser;

    private JTable tableAssociations;
    private DefaultTableModel tableModel;

    private ArrayList<Voyage> listeVoyages;
    private ArrayList<Destination> listeDestinations;
    private HashMap<String, Integer> voyageMap;
    private HashMap<String, Integer> destinationMap;

    public VoyageDestinationPanel() {
        listeVoyages = new ArrayList<>();
        listeDestinations = new ArrayList<>();
        voyageMap = new HashMap<>();
        destinationMap = new HashMap<>();
        initComponents();
        layoutComponents();
        chargerDonnees();
    }

    private void initComponents() {
        cmbVoyage = new JComboBox<>();
        cmbDestination = new JComboBox<>();

        btnAssocier = new JButton("Associate");
        UIUtils.styleButton(btnAssocier, ThemeManager.PRIMARY_COLOR);

        btnDissocier = new JButton("Dissociate");
        UIUtils.styleButton(btnDissocier, ThemeManager.ERROR_COLOR);

        btnNouveau = new JButton("Clear Selection");
        UIUtils.styleButton(btnNouveau, Color.GRAY);

        btnActualiser = new JButton("Refresh");
        UIUtils.styleButton(btnActualiser, ThemeManager.SECONDARY_COLOR);

        String[] colonnes = {"Trip Code", "Departure Date", "Step", "Dest. Code", "City"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableAssociations = new JTable(tableModel);
        tableAssociations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIUtils.styleTable(tableAssociations);

        btnAssocier.addActionListener(e -> associer());
        btnDissocier.addActionListener(e -> dissocier());
        btnNouveau.addActionListener(e -> nouveau());
        btnActualiser.addActionListener(e -> chargerDonnees());

        tableAssociations.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectionnerAssociation();
                }
            }
        });

        btnDissocier.setEnabled(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(ThemeManager.getBackgroundColor());
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Form Panel
        JPanel panelFormulaire = UIUtils.createCardPanel("Trip & Destination Association");
        panelFormulaire.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblVoyage = new JLabel("Trip:");
        lblVoyage.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panelFormulaire.add(lblVoyage, gbc);
        
        cmbVoyage.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulaire.add(cmbVoyage, gbc);

        JLabel lblDestination = new JLabel("Destination:");
        lblDestination.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        panelFormulaire.add(lblDestination, gbc);
        
        cmbDestination.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 3; gbc.weightx = 1;
        panelFormulaire.add(cmbDestination, gbc);

        // Buttons Panel
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelBoutons.setBackground(ThemeManager.getCardColor());
        panelBoutons.add(btnActualiser);
        panelBoutons.add(btnNouveau);
        panelBoutons.add(btnAssocier);
        panelBoutons.add(btnDissocier);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(panelFormulaire, BorderLayout.CENTER);
        topContainer.add(panelBoutons, BorderLayout.SOUTH);

        // Table Panel
        JPanel tablePanel = UIUtils.createCardPanel("Associations Directory");
        tablePanel.setLayout(new BorderLayout(0, 10));
        
        JLabel lblInfo = new JLabel("Note: Destinations are listed in trip order (step 1 → step 2 → ...)");
        lblInfo.setFont(ThemeManager.FONT_BODY.deriveFont(Font.ITALIC));
        lblInfo.setForeground(ThemeManager.getSecondaryTextColor());
        lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        tablePanel.add(lblInfo, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(tableAssociations);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeManager.getCardColor());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void chargerDonnees() {
        chargerVoyages();
        chargerDestinations();
        chargerAssociations();
    }

    private void chargerVoyages() {
        cmbVoyage.removeAllItems();
        voyageMap.clear();
        listeVoyages = VoyageDAO.getAllVoyage();

        if (listeVoyages == null || listeVoyages.isEmpty()) {
            cmbVoyage.addItem("No trips available");
            cmbVoyage.setEnabled(false);
            btnAssocier.setEnabled(false);
            return;
        }

        cmbVoyage.setEnabled(true);
        btnAssocier.setEnabled(true);

        for (Voyage v : listeVoyages) {
            String dateStr = v.getDateDepart() != null ? v.getDateDepart().toString() : "Unknown Date";
            String display = v.getCode() + " - " + dateStr;
            cmbVoyage.addItem(display);
            voyageMap.put(display, v.getIdVoyage());
        }
    }

    private void chargerDestinations() {
        cmbDestination.removeAllItems();
        destinationMap.clear();
        listeDestinations = DestinationDAO.getAllDestination();

        if (listeDestinations == null || listeDestinations.isEmpty()) {
            cmbDestination.addItem("No destinations available");
            cmbDestination.setEnabled(false);
            btnAssocier.setEnabled(false);
            return;
        }

        cmbDestination.setEnabled(true);

        for (Destination d : listeDestinations) {
            String display = d.getCodeDestination() + " - " + d.getNomVille();
            cmbDestination.addItem(display);
            destinationMap.put(display, d.getIdDestination());
        }
    }

    private void chargerAssociations() {
        tableModel.setRowCount(0);
        ArrayList<Object[]> associations = VoyageDestinationDAO.getAllAssociations();
        if (associations != null) {
            for (Object[] assoc : associations) {
                tableModel.addRow(assoc);
            }
        }
        btnDissocier.setEnabled(false);
        tableAssociations.clearSelection();
    }

    private void associer() {
        if (!validerChamps()) return;

        String voyageDisplay = (String) cmbVoyage.getSelectedItem();
        String destinationDisplay = (String) cmbDestination.getSelectedItem();

        Integer idVoyage = voyageMap.get(voyageDisplay);
        Integer idDestination = destinationMap.get(destinationDisplay);

        if (idVoyage == null || idDestination == null) {
            JOptionPane.showMessageDialog(this, "Data fetch error", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (VoyageDestinationDAO.associationExiste(idVoyage, idDestination)) {
            JOptionPane.showMessageDialog(this, "Destination is already associated with this trip!", "Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (VoyageDestinationDAO.associer(idVoyage, idDestination)) {
            int ordre = VoyageDestinationDAO.countDestinationsByVoyage(idVoyage);
            chargerAssociations();
            nouveau();
        } else {
            JOptionPane.showMessageDialog(this, "Error completing association", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dissocier() {
        int selectedRow = tableAssociations.getSelectedRow();
        if (selectedRow == -1) return;

        String codeVoyage = tableModel.getValueAt(selectedRow, 0).toString();
        String codeDestination = tableModel.getValueAt(selectedRow, 3).toString();
        String nomVille = tableModel.getValueAt(selectedRow, 4).toString();

        int confirmation = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove this step?\nTrip: " + codeVoyage + "\nDestination: " + nomVille,
            "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            Voyage voyage = VoyageDAO.getVoyageByCode(codeVoyage);
            Destination destination = DestinationDAO.getDestinationByCode(codeDestination);

            if (voyage != null && destination != null) {
                if (VoyageDestinationDAO.dissocier(voyage.getIdVoyage(), destination.getIdDestination())) {
                    chargerAssociations();
                    nouveau();
                } else {
                    JOptionPane.showMessageDialog(this, "Error dissociating trip and destination", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void selectionnerAssociation() {
        int selectedRow = tableAssociations.getSelectedRow();
        if (selectedRow != -1) {
            String codeVoyage = tableModel.getValueAt(selectedRow, 0).toString();
            String codeDestination = tableModel.getValueAt(selectedRow, 3).toString();

            for (int i = 0; i < cmbVoyage.getItemCount(); i++) {
                if (cmbVoyage.getItemAt(i).startsWith(codeVoyage)) {
                    cmbVoyage.setSelectedIndex(i);
                    break;
                }
            }

            for (int i = 0; i < cmbDestination.getItemCount(); i++) {
                if (cmbDestination.getItemAt(i).startsWith(codeDestination)) {
                    cmbDestination.setSelectedIndex(i);
                    break;
                }
            }

            btnAssocier.setEnabled(false);
            btnDissocier.setEnabled(true);
        }
    }

    private void nouveau() {
        if (cmbVoyage.getItemCount() > 0 && cmbVoyage.isEnabled()) cmbVoyage.setSelectedIndex(0);
        if (cmbDestination.getItemCount() > 0 && cmbDestination.isEnabled()) cmbDestination.setSelectedIndex(0);

        tableAssociations.clearSelection();
        btnAssocier.setEnabled(cmbVoyage.isEnabled() && cmbDestination.isEnabled());
        btnDissocier.setEnabled(false);
    }

    private boolean validerChamps() {
        if (cmbVoyage.getSelectedIndex() == -1 || !cmbVoyage.isEnabled() || 
            cmbDestination.getSelectedIndex() == -1 || !cmbDestination.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Please select both a trip and a destination.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if ("No trips available".equals(cmbVoyage.getSelectedItem()) || 
            "No destinations available".equals(cmbDestination.getSelectedItem())) {
            JOptionPane.showMessageDialog(this, "Data is missing. Ensure trips and destinations exist.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}