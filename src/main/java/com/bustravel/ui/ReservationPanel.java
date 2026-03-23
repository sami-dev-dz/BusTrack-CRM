package com.bustravel.ui;

import com.bustravel.dao.PassagerDAO;
import com.bustravel.dao.ReservationDAO;
import com.bustravel.dao.VoyageDAO;
import com.bustravel.dao.VoyageDestinationDAO;
import com.bustravel.model.Passager;
import com.bustravel.model.Reservation;
import com.bustravel.model.Voyage;
import com.bustravel.ui.theme.ThemeManager;
import com.bustravel.ui.theme.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class ReservationPanel extends JPanel {
    private JComboBox<String> cmbVoyage;
    private JComboBox<String> cmbPassager;
    private JComboBox<String> cmbStatut;
    private JTextField txtDateReservation;
    private JSpinner spnNbPlaces;
    private JTextField txtRecherche;
    private JTextArea txtDestinations;
    private JLabel lblBusInfo;
    private JLabel lblPlacesInfo;

    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnNouveau;
    private JButton btnActualiser;
    private JButton btnRechercher;

    private JTable tableReservations;
    private DefaultTableModel tableModel;

    private ArrayList<Voyage> listeVoyages;
    private ArrayList<Passager> listePassagers;
    private ArrayList<Reservation> listeReservations;

    private HashMap<String, Integer> voyageMap = new HashMap<>();
    private HashMap<String, Integer> passagerMap = new HashMap<>();

    private int idReceptionnisteConnecte = 1;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReservationPanel() {
        listeReservations = new ArrayList<>();
        initComponents();
        layoutComponents();
        chargerDonnees();
    }

    public ReservationPanel(int idReceptionniste) {
        this.idReceptionnisteConnecte = idReceptionniste;
        listeReservations = new ArrayList<>();
        initComponents();
        layoutComponents();
        chargerDonnees();
    }

    private void initComponents() {
        cmbVoyage = new JComboBox<>();
        cmbPassager = new JComboBox<>();

        String[] statuts = {"En attente", "Confirmée", "Annulée"};
        cmbStatut = new JComboBox<>(statuts);

        txtDateReservation = new JTextField(15);
        txtDateReservation.setText(LocalDate.now().format(dateFormatter));
        UIUtils.styleTextField(txtDateReservation, "YYYY-MM-DD");

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 50, 1);
        spnNbPlaces = new JSpinner(spinnerModel);

        txtRecherche = new JTextField(20);
        UIUtils.styleTextField(txtRecherche, "Search reservations...");

        txtDestinations = new JTextArea(2, 20);
        txtDestinations.setEditable(false);
        txtDestinations.setLineWrap(true);
        txtDestinations.setWrapStyleWord(true);
        txtDestinations.setBackground(ThemeManager.getBackgroundColor());
        txtDestinations.setForeground(ThemeManager.getSecondaryTextColor());
        txtDestinations.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        txtDestinations.setFont(ThemeManager.FONT_BODY);

        lblBusInfo = new JLabel("Bus: -");
        lblBusInfo.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));

        lblPlacesInfo = new JLabel("");
        lblPlacesInfo.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));

        btnAjouter = new JButton("Add Reservation");
        UIUtils.styleButton(btnAjouter, ThemeManager.PRIMARY_COLOR);

        btnModifier = new JButton("Update");
        UIUtils.styleButton(btnModifier, ThemeManager.SECONDARY_COLOR);

        btnSupprimer = new JButton("Delete");
        UIUtils.styleButton(btnSupprimer, ThemeManager.ERROR_COLOR);

        btnNouveau = new JButton("Clear Form");
        UIUtils.styleButton(btnNouveau, Color.GRAY);

        btnActualiser = new JButton("Refresh");
        UIUtils.styleButton(btnActualiser, ThemeManager.SECONDARY_COLOR);

        btnRechercher = new JButton("Search");
        UIUtils.styleButton(btnRechercher, ThemeManager.PRIMARY_COLOR.darker());

        String[] colonnes = {"Date", "Status", "Seats", "Trip", "Bus", "Destinations", "Passenger"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableReservations = new JTable(tableModel);
        tableReservations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIUtils.styleTable(tableReservations);

        btnAjouter.addActionListener(e -> ajouterReservation());
        btnModifier.addActionListener(e -> modifierReservation());
        btnSupprimer.addActionListener(e -> supprimerReservation());
        btnNouveau.addActionListener(e -> nouveauReservation());
        btnActualiser.addActionListener(e -> chargerDonnees());
        btnRechercher.addActionListener(e -> rechercherReservations());

        txtRecherche.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    rechercherReservations();
                }
            }
        });

        cmbVoyage.addActionListener(e -> afficherInfosVoyage());

        tableReservations.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectionnerReservation();
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
        JPanel panelFormulaire = UIUtils.createCardPanel("Reservation Details");
        panelFormulaire.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblDate = new JLabel("Reservation Date:");
        lblDate.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panelFormulaire.add(lblDate, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panelFormulaire.add(txtDateReservation, gbc);

        JLabel lblVoyage = new JLabel("Trip:");
        lblVoyage.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelFormulaire.add(lblVoyage, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cmbVoyage.setPreferredSize(new Dimension(250, 35));
        panelFormulaire.add(cmbVoyage, gbc);

        JLabel lblInfosBus = new JLabel("Bus Info:");
        lblInfosBus.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panelFormulaire.add(lblInfosBus, gbc);
        
        JPanel panelBusPlaces = new JPanel(new GridLayout(2, 1, 5, 2));
        panelBusPlaces.setOpaque(false);
        panelBusPlaces.add(lblBusInfo);
        panelBusPlaces.add(lblPlacesInfo);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panelFormulaire.add(panelBusPlaces, gbc);

        JLabel lblDestinations = new JLabel("Destinations:");
        lblDestinations.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        panelFormulaire.add(lblDestinations, gbc);
        
        JScrollPane scrollDestinations = new JScrollPane(txtDestinations);
        scrollDestinations.setPreferredSize(new Dimension(250, 50));
        scrollDestinations.setBorder(BorderFactory.createLineBorder(ThemeManager.getSecondaryTextColor(), 1));
        gbc.gridx = 3; gbc.gridheight = 2; gbc.weightx = 1.0;
        panelFormulaire.add(scrollDestinations, gbc);

        gbc.gridheight = 1;
        JLabel lblPassager = new JLabel("Passenger:");
        lblPassager.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0;
        panelFormulaire.add(lblPassager, gbc);
        
        cmbPassager.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 3; gbc.weightx = 1.0;
        panelFormulaire.add(cmbPassager, gbc);

        JLabel lblNbPlaces = new JLabel("Seats:");
        lblNbPlaces.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panelFormulaire.add(lblNbPlaces, gbc);
        
        JPanel panelNbPlaces = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelNbPlaces.setOpaque(false);
        spnNbPlaces.setPreferredSize(new Dimension(80, 35));
        panelNbPlaces.add(spnNbPlaces);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panelFormulaire.add(panelNbPlaces, gbc);

        JLabel lblStatut = new JLabel("Status:");
        lblStatut.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 2; gbc.gridy = 3; gbc.weightx = 0;
        panelFormulaire.add(lblStatut, gbc);
        
        cmbStatut.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 3; gbc.weightx = 1.0;
        panelFormulaire.add(cmbStatut, gbc);

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
        JPanel tablePanel = UIUtils.createCardPanel("Reservations Directory");
        tablePanel.setLayout(new BorderLayout(0, 10));

        JPanel panelRecherche = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelRecherche.setOpaque(false);
        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        panelRecherche.add(searchLbl);
        panelRecherche.add(txtRecherche);
        panelRecherche.add(btnRechercher);
        panelRecherche.add(btnActualiser);
        tablePanel.add(panelRecherche, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(tableReservations);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        scrollPane.getViewport().setBackground(ThemeManager.getCardColor());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void chargerDonnees() {
        chargerVoyages();
        chargerPassagers();
        chargerReservations();
        nouveauReservation();
    }

    private void chargerVoyages() {
        cmbVoyage.removeAllItems();
        voyageMap.clear();
        listeVoyages = VoyageDAO.getAllVoyage();

        if (listeVoyages == null || listeVoyages.isEmpty()) {
            cmbVoyage.addItem("No trips available");
            cmbVoyage.setEnabled(false);
            btnAjouter.setEnabled(false);
            txtDestinations.setText("");
            lblBusInfo.setText("Bus: -");
            lblPlacesInfo.setText("");
            return;
        }

        cmbVoyage.setEnabled(true);
        btnAjouter.setEnabled(true);

        for (Voyage v : listeVoyages) {
            String code = v.getCode() != null ? v.getCode() : ("VYG#" + v.getIdVoyage());
            LocalDate d = v.getDateDepart();
            String dateStr = d != null ? d.toString() : "Unknown date";
            String display = code + " - " + dateStr;
            cmbVoyage.addItem(display);
            voyageMap.put(display, v.getIdVoyage());
        }

        afficherInfosVoyage();
    }

    private void chargerPassagers() {
        cmbPassager.removeAllItems();
        passagerMap.clear();
        listePassagers = PassagerDAO.getAllPassagers();

        if (listePassagers == null || listePassagers.isEmpty()) {
            cmbPassager.addItem("No passengers");
            cmbPassager.setEnabled(false);
            btnAjouter.setEnabled(false);
            return;
        }

        cmbPassager.setEnabled(true);

        for (Passager p : listePassagers) {
            String display = p.getNom() + " " + p.getPrenom();
            cmbPassager.addItem(display);
            passagerMap.put(display, p.getIdPassager());
        }
    }

    private void chargerReservations() {
        listeReservations = ReservationDAO.getAllReservations();
        afficherReservationsDansTable();
    }

    private void rechercherReservations() {
        String critere = txtRecherche.getText().trim();
        if (critere.isEmpty()) {
            chargerReservations();
        } else {
            listeReservations = ReservationDAO.rechercher(critere);
            afficherReservationsDansTable();
        }
        nouveauReservation();
    }

    private void afficherReservationsDansTable() {
        tableModel.setRowCount(0);

        if (listeReservations == null) {
            listeReservations = new ArrayList<>();
        }

        for (Reservation r : listeReservations) {
            String voyageInfo = getVoyageCodeById(r.getIdVoyage());
            String busInfo = getBusInfoByVoyageId(r.getIdVoyage());
            String destinationsInfo = getDestinationsByVoyageId(r.getIdVoyage());
            String passagerInfo = getPassagerDisplayById(r.getIdPassager());
            String dateStr = r.getDateReservation() != null ? r.getDateReservation().format(dateFormatter) : "";
            tableModel.addRow(new Object[]{
                dateStr, r.getStatut(), r.getNbPlaces(), voyageInfo, busInfo, destinationsInfo, passagerInfo
            });
        }
    }

    private void afficherInfosVoyage() {
        String voyageDisplay = (String) cmbVoyage.getSelectedItem();
        if (voyageDisplay == null || "No trips available".equals(voyageDisplay) || "Aucun voyage disponible".equals(voyageDisplay)) {
            txtDestinations.setText("");
            lblBusInfo.setText("Bus: -");
            lblPlacesInfo.setText("");
            return;
        }

        Integer idVoyage = voyageMap.get(voyageDisplay);
        if (idVoyage == null) {
            txtDestinations.setText("");
            lblBusInfo.setText("Bus: -");
            lblPlacesInfo.setText("");
            return;
        }

        String destinations = getDestinationsByVoyageId(idVoyage);
        txtDestinations.setText(destinations.isEmpty() ? "No destinations" : destinations);

        String busInfo = getBusInfoByVoyageId(idVoyage);
        lblBusInfo.setText("Bus: " + busInfo);

        int capacite = ReservationDAO.getCapaciteBus(idVoyage);
        int placesDisponibles = ReservationDAO.getPlacesDisponibles(idVoyage);
        int placesEnAttente = ReservationDAO.getPlacesEnAttente(idVoyage);
        int placesConfirmees = ReservationDAO.getPlacesConfirmees(idVoyage);

        String placesText = String.format("Capacity: %d | Available: %d | Pending: %d | Confirmed: %d",
                capacite, placesDisponibles, placesEnAttente, placesConfirmees);
        lblPlacesInfo.setText(placesText);

        if (placesDisponibles <= 0) {
            lblPlacesInfo.setForeground(ThemeManager.ERROR_COLOR);
        } else if (placesDisponibles <= 5) {
            lblPlacesInfo.setForeground(new Color(255, 152, 0));
        } else {
            lblPlacesInfo.setForeground(ThemeManager.PRIMARY_COLOR);
        }

        SpinnerNumberModel model = (SpinnerNumberModel) spnNbPlaces.getModel();
        model.setMaximum(Math.max(placesDisponibles, 1));
        if ((Integer) spnNbPlaces.getValue() > placesDisponibles) {
            spnNbPlaces.setValue(Math.max(placesDisponibles, 1));
        }
    }

    private String getDestinationsByVoyageId(int idVoyage) {
        ArrayList<String> destinations = VoyageDestinationDAO.getDestinationsByVoyage(idVoyage);
        if (destinations == null || destinations.isEmpty()) return "No destination";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < destinations.size(); i++) {
            sb.append(destinations.get(i));
            if (i < destinations.size() - 1) sb.append(" → ");
        }
        return sb.toString();
    }

    private String getBusInfoByVoyageId(int idVoyage) {
        if (listeVoyages != null) {
            for (Voyage v : listeVoyages) {
                if (v.getIdVoyage() == idVoyage) {
                    String busImmat = v.getBusImmatriculation();
                    int nbPlaces = v.getBusNbPlaces();
                    if (busImmat != null && !busImmat.isEmpty()) return busImmat + " (" + nbPlaces + " seats)";
                    return "Unassigned";
                }
            }
        }
        return "Unassigned";
    }

    private String getVoyageCodeById(int idVoyage) {
        if (listeVoyages != null) {
            for (Voyage v : listeVoyages) {
                if (v.getIdVoyage() == idVoyage) {
                    String code = v.getCode() != null ? v.getCode() : ("VYG#" + v.getIdVoyage());
                    LocalDate d = v.getDateDepart();
                    String dateStr = d != null ? d.toString() : "";
                    return code + " - " + dateStr;
                }
            }
        }
        return "Trip #" + idVoyage;
    }

    private String getPassagerDisplayById(int idPassager) {
        if (listePassagers != null) {
            for (Passager p : listePassagers) {
                if (p.getIdPassager() == idPassager) {
                    return p.getNom() + " " + p.getPrenom();
                }
            }
        }
        return "Passenger #" + idPassager;
    }

    private void ajouterReservation() {
        if (!validerChamps()) return;

        String voyageDisplay = (String) cmbVoyage.getSelectedItem();
        String passagerDisplay = (String) cmbPassager.getSelectedItem();
        Integer idVoyage = voyageMap.get(voyageDisplay);
        Integer idPassager = passagerMap.get(passagerDisplay);
        String statut = (String) cmbStatut.getSelectedItem();
        String dateText = txtDateReservation.getText().trim();
        int nbPlaces = (Integer) spnNbPlaces.getValue();

        int placesDisponibles = ReservationDAO.getPlacesDisponibles(idVoyage);
        if (!"Annulée".equals(statut) && !"Cancelled".equals(statut) && placesDisponibles < nbPlaces) {
            JOptionPane.showMessageDialog(this, "Not enough seats available!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (ReservationDAO.existe(idVoyage, idPassager)) {
            JOptionPane.showMessageDialog(this, "An active reservation already exists for this passenger and trip", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate dateReservation;
        try {
            dateReservation = LocalDate.parse(dateText, dateFormatter);
        } catch (DateTimeParseException e) {
            dateReservation = LocalDate.now();
        }

        Reservation reservation = new Reservation();
        reservation.setIdVoyage(idVoyage);
        reservation.setIdPassager(idPassager);
        reservation.setDateReservation(dateReservation);
        reservation.setStatut(statut);
        reservation.setNbPlaces(nbPlaces);
        reservation.setIdReceptionniste(idReceptionnisteConnecte);

        if (ReservationDAO.ajouter(reservation)) {
            chargerReservations();
            nouveauReservation();
            afficherInfosVoyage();
        } else {
            JOptionPane.showMessageDialog(this, "Error adding reservation.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierReservation() {
        int selectedRow = tableReservations.getSelectedRow();
        if (selectedRow == -1) return;
        if (!validerChamps()) return;

        Reservation reservationOriginale = listeReservations.get(selectedRow);

        String voyageDisplay = (String) cmbVoyage.getSelectedItem();
        String passagerDisplay = (String) cmbPassager.getSelectedItem();
        Integer idVoyage = voyageMap.get(voyageDisplay);
        Integer idPassager = passagerMap.get(passagerDisplay);
        String statut = (String) cmbStatut.getSelectedItem();
        String dateText = txtDateReservation.getText().trim();
        int nbPlaces = (Integer) spnNbPlaces.getValue();

        Reservation reservation = new Reservation();
        reservation.setIdReservation(reservationOriginale.getIdReservation());
        reservation.setIdVoyage(idVoyage);
        reservation.setIdPassager(idPassager);
        try {
            reservation.setDateReservation(LocalDate.parse(dateText, dateFormatter));
        } catch (Exception e) {
            reservation.setDateReservation(reservationOriginale.getDateReservation());
        }
        reservation.setStatut(statut);
        reservation.setNbPlaces(nbPlaces);

        if (ReservationDAO.modifier(reservation)) {
            chargerReservations();
            nouveauReservation();
            afficherInfosVoyage();
        } else {
            JOptionPane.showMessageDialog(this, "Error updating reservation.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerReservation() {
        int selectedRow = tableReservations.getSelectedRow();
        if (selectedRow == -1) return;

        Reservation reservation = listeReservations.get(selectedRow);

        int confirmation = JOptionPane.showConfirmDialog(this, "Delete this reservation completely?", "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            if (ReservationDAO.supprimer(reservation.getIdReservation())) {
                chargerReservations();
                nouveauReservation();
                afficherInfosVoyage();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting reservation", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectionnerReservation() {
        int selectedRow = tableReservations.getSelectedRow();
        if (selectedRow != -1 && selectedRow < listeReservations.size()) {
            Reservation reservation = listeReservations.get(selectedRow);

            if (reservation.getDateReservation() != null) {
                txtDateReservation.setText(reservation.getDateReservation().format(dateFormatter));
            }
            cmbStatut.setSelectedItem(reservation.getStatut());
            spnNbPlaces.setValue(reservation.getNbPlaces());

            String voyageDisplay = getVoyageCodeById(reservation.getIdVoyage());
            String passagerDisplay = getPassagerDisplayById(reservation.getIdPassager());

            selectComboByText(cmbVoyage, voyageDisplay);
            selectComboByText(cmbPassager, passagerDisplay);

            afficherInfosVoyage();

            btnAjouter.setEnabled(false);
            btnModifier.setEnabled(true);
            btnSupprimer.setEnabled(true);
        }
    }

    private void selectComboByText(JComboBox<String> combo, String text) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).equals(text)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void nouveauReservation() {
        txtDateReservation.setText(LocalDate.now().format(dateFormatter));
        cmbStatut.setSelectedIndex(0);
        spnNbPlaces.setValue(1);
        if (cmbVoyage.getItemCount() > 0) cmbVoyage.setSelectedIndex(0);
        if (cmbPassager.getItemCount() > 0) cmbPassager.setSelectedIndex(0);

        afficherInfosVoyage();
        tableReservations.clearSelection();
        btnAjouter.setEnabled(cmbVoyage.isEnabled() && cmbPassager.isEnabled());
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
    }

    private boolean validerChamps() {
        if (cmbVoyage.getSelectedIndex() == -1 || cmbPassager.getSelectedIndex() == -1) {
            return false;
        }
        return true;
    }
}