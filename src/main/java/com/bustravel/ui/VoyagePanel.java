package com.bustravel.ui;

import com.bustravel.dao.BusDAO;
import com.bustravel.dao.VoyageDAO;
import com.bustravel.model.Bus;
import com.bustravel.model.Voyage;
import com.bustravel.ui.theme.ThemeManager;
import com.bustravel.ui.theme.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class VoyagePanel extends JPanel {
    private JTextField txtCodeVoyage;
    private JTextField txtDateDepart;
    private JTextField txtHeureDepart;
    private JSpinner spinHeures;
    private JSpinner spinMinutes;
    private JComboBox<String> cmbBus;

    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnNouveau;
    private JButton btnActualiserBus;

    private JTable tableVoyage;
    private DefaultTableModel tableModel;

    private ArrayList<Voyage> listeVoyage;
    private HashMap<String, Integer> busMap;
    private int idAdministrateurConnecte = 1;

    public VoyagePanel() {
        listeVoyage = new ArrayList<>();
        busMap = new HashMap<>();
        initComponents();
        layoutComponents();
        chargerBus();
        chargerVoyages();
    }

    public VoyagePanel(int idAdministrateur) {
        this.idAdministrateurConnecte = idAdministrateur;
        listeVoyage = new ArrayList<>();
        busMap = new HashMap<>();
        initComponents();
        layoutComponents();
        chargerBus();
        chargerVoyages();
    }

    private void initComponents() {
        txtCodeVoyage = new JTextField(15);
        txtDateDepart = new JTextField(12);
        txtHeureDepart = new JTextField(8);

        UIUtils.styleTextField(txtCodeVoyage, "Code (e.g. V001)");
        UIUtils.styleTextField(txtDateDepart, "YYYY-MM-DD");
        UIUtils.styleTextField(txtHeureDepart, "HH:mm");

        SpinnerNumberModel heuresModel = new SpinnerNumberModel(1, 0, 99, 1);
        spinHeures = new JSpinner(heuresModel);
        JSpinner.NumberEditor heuresEditor = new JSpinner.NumberEditor(spinHeures, "00");
        spinHeures.setEditor(heuresEditor);

        SpinnerNumberModel minutesModel = new SpinnerNumberModel(0, 0, 59, 15);
        spinMinutes = new JSpinner(minutesModel);
        JSpinner.NumberEditor minutesEditor = new JSpinner.NumberEditor(spinMinutes, "00");
        spinMinutes.setEditor(minutesEditor);

        cmbBus = new JComboBox<>();

        btnAjouter = new JButton("Add Trip");
        UIUtils.styleButton(btnAjouter, ThemeManager.PRIMARY_COLOR);

        btnModifier = new JButton("Update");
        UIUtils.styleButton(btnModifier, ThemeManager.SECONDARY_COLOR);

        btnSupprimer = new JButton("Delete");
        UIUtils.styleButton(btnSupprimer, ThemeManager.ERROR_COLOR);

        btnNouveau = new JButton("Clear Form");
        UIUtils.styleButton(btnNouveau, Color.GRAY);

        btnActualiserBus = new JButton("Refresh Buses");
        UIUtils.styleButton(btnActualiserBus, ThemeManager.SECONDARY_COLOR);

        String[] colonnes = {"Trip Code", "Departure Time", "Duration", "Assigned Bus"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableVoyage = new JTable(tableModel);
        tableVoyage.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIUtils.styleTable(tableVoyage);

        btnAjouter.addActionListener(e -> ajouterVoyage());
        btnModifier.addActionListener(e -> modifierVoyage());
        btnSupprimer.addActionListener(e -> supprimerVoyage());
        btnNouveau.addActionListener(e -> nouveauVoyage());
        btnActualiserBus.addActionListener(e -> chargerBus());

        tableVoyage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectionnerVoyage();
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
        JPanel panelFormulaire = UIUtils.createCardPanel("Trip Details");
        panelFormulaire.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblCode = new JLabel("Trip Code:");
        lblCode.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panelFormulaire.add(lblCode, gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1;
        panelFormulaire.add(txtCodeVoyage, gbc);

        JLabel lblDepart = new JLabel("Departure Date & Time:");
        lblDepart.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0;
        panelFormulaire.add(lblDepart, gbc);
        
        JPanel panelDateTime = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelDateTime.setOpaque(false);
        panelDateTime.add(txtDateDepart);
        JLabel lblAt = new JLabel("at");
        lblAt.setFont(ThemeManager.FONT_BODY);
        panelDateTime.add(lblAt);
        panelDateTime.add(txtHeureDepart);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1;
        panelFormulaire.add(panelDateTime, gbc);

        JLabel lblDuree = new JLabel("Duration:");
        lblDuree.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0;
        panelFormulaire.add(lblDuree, gbc);
        
        JPanel panelDuree = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelDuree.setOpaque(false);
        spinHeures.setPreferredSize(new Dimension(80, 35));
        spinMinutes.setPreferredSize(new Dimension(80, 35));
        panelDuree.add(spinHeures);
        JLabel lblH = new JLabel("h");
        lblH.setFont(ThemeManager.FONT_BODY);
        panelDuree.add(lblH);
        panelDuree.add(spinMinutes);
        JLabel lblM = new JLabel("m");
        lblM.setFont(ThemeManager.FONT_BODY);
        panelDuree.add(lblM);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1;
        panelFormulaire.add(panelDuree, gbc);

        JLabel lblBus = new JLabel("Assigned Bus:");
        lblBus.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.weightx = 0;
        panelFormulaire.add(lblBus, gbc);
        
        JPanel panelBus = new JPanel(new BorderLayout(10, 0));
        panelBus.setOpaque(false);
        cmbBus.setPreferredSize(new Dimension(200, 35));
        panelBus.add(cmbBus, BorderLayout.CENTER);
        panelBus.add(btnActualiserBus, BorderLayout.EAST);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1;
        panelFormulaire.add(panelBus, gbc);

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
        JPanel tablePanel = UIUtils.createCardPanel("Trips Directory");
        tablePanel.setLayout(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(tableVoyage);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeManager.getCardColor());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void chargerVoyages() {
        listeVoyage = VoyageDAO.getAllVoyage();
        tableModel.setRowCount(0);
        for (Voyage v : listeVoyage) {
            String busInfo = v.getBusImmatriculation() != null ? v.getBusImmatriculation() : "Unassigned";
            String dureeAffichage = formatDuree(v.getDuree());
            String dateHeureAffichage = "";
            if (v.getDateDepart() != null) {
                dateHeureAffichage = v.getDateDepart().toString();
                if (v.getHeureDepart() != null && !v.getHeureDepart().isEmpty()) {
                    dateHeureAffichage += " " + v.getHeureDepart();
                }
            }
            tableModel.addRow(new Object[]{v.getCode(), dateHeureAffichage, dureeAffichage, busInfo});
        }
    }

    private String formatDuree(int dureeMinutes) {
        if (dureeMinutes <= 0) return "0h";
        int heures = dureeMinutes / 60;
        int minutes = dureeMinutes % 60;
        if (minutes == 0) return heures + "h";
        return heures + "h " + String.format("%02d", minutes) + "m";
    }

    private void chargerBus() {
        cmbBus.removeAllItems();
        busMap.clear();
        ArrayList<Bus> listeBus = BusDAO.getAllBus();
        for (Bus b : listeBus) {
            String busDisplay = b.getImmatriculation() + " - " + b.getMarque() + " " + b.getModele() + " (" + b.getNbPlaces() + " seats)";
            cmbBus.addItem(busDisplay);
            busMap.put(busDisplay, b.getIdBus());
        }
        if (cmbBus.getItemCount() > 0) cmbBus.setSelectedIndex(0);
    }

    private void ajouterVoyage() {
        if (!validerChamps()) return;

        String codeVoyage = txtCodeVoyage.getText().trim();
        String dateStr = txtDateDepart.getText().trim();
        String heureStr = txtHeureDepart.getText().trim();
        int heures = (Integer) spinHeures.getValue();
        int minutes = (Integer) spinMinutes.getValue();
        int dureeMinutes = heures * 60 + minutes;
        String busDisplay = (String) cmbBus.getSelectedItem();
        Integer idBus = busMap.get(busDisplay);

        if (VoyageDAO.codeVoyageExiste(codeVoyage)) {
            JOptionPane.showMessageDialog(this, "A trip with this code already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate dateDepart = LocalDate.parse(dateStr);
            String[] heureParts = heureStr.split(":");
            if (heureParts.length != 2) throw new DateTimeParseException("Invalid Format", heureStr, 0);
            int heure = Integer.parseInt(heureParts[0]);
            int minute = Integer.parseInt(heureParts[1]);

            if (heure < 0 || heure > 23 || minute < 0 || minute > 59) throw new DateTimeParseException("Invalid Time", heureStr, 0);

            LocalDateTime dateHeureDepart = dateDepart.atTime(heure, minute);
            if (dateHeureDepart.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this, "Departure cannot be in the past", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dureeMinutes <= 0) {
                JOptionPane.showMessageDialog(this, "Duration must be > 0", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!VoyageDAO.busDisponible(idBus, dateDepart, heureStr, dureeMinutes, null)) {
                JOptionPane.showMessageDialog(this, "Bus is not available at this time!", "Conflict Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Voyage voyage = new Voyage(codeVoyage, dateDepart, dureeMinutes, idBus, idAdministrateurConnecte);
            voyage.setHeureDepart(heureStr);

            if (VoyageDAO.ajouterVoyage(voyage)) {
                chargerVoyages();
                nouveauVoyage();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add trip", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format Error. Use YYYY-MM-DD and HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierVoyage() {
        int selectedRow = tableVoyage.getSelectedRow();
        if (selectedRow == -1) return;
        if (!validerChamps()) return;

        Voyage voyageOriginal = listeVoyage.get(selectedRow);

        String codeVoyage = txtCodeVoyage.getText().trim();
        String dateStr = txtDateDepart.getText().trim();
        String heureStr = txtHeureDepart.getText().trim();
        int heures = (Integer) spinHeures.getValue();
        int minutes = (Integer) spinMinutes.getValue();
        int dureeMinutes = heures * 60 + minutes;
        String busDisplay = (String) cmbBus.getSelectedItem();
        Integer idBus = busMap.get(busDisplay);

        try {
            LocalDate dateDepart = LocalDate.parse(dateStr);
            String[] heureParts = heureStr.split(":");
            if (heureParts.length != 2) throw new DateTimeParseException("Invalid format", heureStr, 0);
            int heure = Integer.parseInt(heureParts[0]);
            int minute = Integer.parseInt(heureParts[1]);

            if (heure < 0 || heure > 23 || minute < 0 || minute > 59) throw new DateTimeParseException("Invalid Time", heureStr, 0);

            LocalDateTime dateHeureDepart = dateDepart.atTime(heure, minute);
            if (dateHeureDepart.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this, "Departure cannot be in the past", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dureeMinutes <= 0) {
                JOptionPane.showMessageDialog(this, "Duration must be > 0", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!VoyageDAO.busDisponible(idBus, dateDepart, heureStr, dureeMinutes, voyageOriginal.getIdVoyage())) {
                JOptionPane.showMessageDialog(this, "Bus is not available at this time!", "Conflict Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            voyageOriginal.setCode(codeVoyage);
            voyageOriginal.setDateDepart(dateDepart);
            voyageOriginal.setHeureDepart(heureStr);
            voyageOriginal.setDuree(dureeMinutes);
            voyageOriginal.setIdBus(idBus);
            voyageOriginal.setIdAdministrateur(idAdministrateurConnecte);

            if (VoyageDAO.modifierVoyage(voyageOriginal)) {
                chargerVoyages();
                nouveauVoyage();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update trip", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format Error. Use YYYY-MM-DD and HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerVoyage() {
        int selectedRow = tableVoyage.getSelectedRow();
        if (selectedRow == -1) return;

        int confirmation = JOptionPane.showConfirmDialog(this, "Delete this trip?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            String codeVoyage = listeVoyage.get(selectedRow).getCode();
            if (VoyageDAO.supprimerVoyage(codeVoyage)) {
                chargerVoyages();
                nouveauVoyage();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete trip", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectionnerVoyage() {
        int selectedRow = tableVoyage.getSelectedRow();
        if (selectedRow != -1 && selectedRow < listeVoyage.size()) {
            Voyage v = listeVoyage.get(selectedRow);
            txtCodeVoyage.setText(v.getCode());
            if (v.getDateDepart() != null) txtDateDepart.setText(v.getDateDepart().toString());
            txtHeureDepart.setText(v.getHeureDepart() != null ? v.getHeureDepart() : "");

            int dureeMinutes = v.getDuree();
            spinHeures.setValue(dureeMinutes / 60);
            spinMinutes.setValue(dureeMinutes % 60);

            String busInfo = v.getBusImmatriculation();
            if (busInfo != null) {
                for (int i = 0; i < cmbBus.getItemCount(); i++) {
                    if (cmbBus.getItemAt(i).startsWith(busInfo)) {
                        cmbBus.setSelectedIndex(i);
                        break;
                    }
                }
            }

            btnAjouter.setEnabled(false);
            btnModifier.setEnabled(true);
            btnSupprimer.setEnabled(true);
        }
    }

    private void nouveauVoyage() {
        txtCodeVoyage.setText("");
        txtDateDepart.setText("");
        txtHeureDepart.setText("");
        spinHeures.setValue(1);
        spinMinutes.setValue(0);
        if (cmbBus.getItemCount() > 0) cmbBus.setSelectedIndex(0);

        tableVoyage.clearSelection();
        btnAjouter.setEnabled(true);
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
    }

    private boolean validerChamps() {
        if (txtCodeVoyage.getText().trim().isEmpty() || txtDateDepart.getText().trim().isEmpty() || txtHeureDepart.getText().trim().isEmpty() || cmbBus.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields and select a bus.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}