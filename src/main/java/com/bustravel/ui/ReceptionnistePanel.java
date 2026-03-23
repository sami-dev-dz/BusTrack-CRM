package com.bustravel.ui;

import com.bustravel.dao.ReceptionnisteDAO;
import com.bustravel.model.Receptionniste;
import com.bustravel.ui.theme.ThemeManager;
import com.bustravel.ui.theme.UIUtils;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ReceptionnistePanel extends JPanel {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$";
    private static final String TITRE_ERREUR = "Validation Error";
    private static final String TITRE_CONFIRMATION = "Confirmation";
    
    private JTextField txtEmail;
    private JPasswordField txtMotDePasse;
    private JToggleButton btnVoirMotDePasse;

    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnNouveau;

    private JTable tableReceptionniste;
    private DefaultTableModel tableModel;

    private ArrayList<Receptionniste> listeReceptionnistes;
    private String emailSelectionne;

    public ReceptionnistePanel() {
        listeReceptionnistes = new ArrayList<>();
        initComponents();
        layoutComponents();
        chargerReceptionnistes();
    }

    private void initComponents() {
        txtEmail = new JTextField(20);
        txtMotDePasse = new JPasswordField(15);

        UIUtils.styleTextField(txtEmail, "Email Address");
        UIUtils.styleTextField(txtMotDePasse, "Password");

        btnVoirMotDePasse = new JToggleButton("👁");
        btnVoirMotDePasse.setPreferredSize(new Dimension(45, 35));
        btnVoirMotDePasse.setFocusable(false);
        btnVoirMotDePasse.setBackground(ThemeManager.getBackgroundColor());
        btnVoirMotDePasse.setForeground(ThemeManager.getTextColor());
        btnVoirMotDePasse.setBorder(BorderFactory.createLineBorder(ThemeManager.getSecondaryTextColor(), 1));

        btnAjouter = new JButton("Add Receptionist");
        UIUtils.styleButton(btnAjouter, ThemeManager.PRIMARY_COLOR);

        btnModifier = new JButton("Update");
        UIUtils.styleButton(btnModifier, ThemeManager.SECONDARY_COLOR);

        btnSupprimer = new JButton("Delete");
        UIUtils.styleButton(btnSupprimer, ThemeManager.ERROR_COLOR);

        btnNouveau = new JButton("Clear Form");
        UIUtils.styleButton(btnNouveau, Color.GRAY);

        String[] colonnes = {"Email"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableReceptionniste = new JTable(tableModel);
        tableReceptionniste.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIUtils.styleTable(tableReceptionniste);

        btnAjouter.addActionListener(e -> ajouterReceptionniste());
        btnModifier.addActionListener(e -> modifierReceptionniste());
        btnSupprimer.addActionListener(e -> supprimerReceptionniste());
        btnNouveau.addActionListener(e -> nouveauReceptionniste());

        btnVoirMotDePasse.addActionListener(e -> {
            if (btnVoirMotDePasse.isSelected()) {
                txtMotDePasse.setEchoChar((char) 0);
            } else {
                txtMotDePasse.setEchoChar('•');
            }
        });

        tableReceptionniste.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectionnerReceptionniste();
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
        JPanel panelFormulaire = UIUtils.createCardPanel("Receptionist Details");
        panelFormulaire.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panelFormulaire.add(lblEmail, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulaire.add(txtEmail, gbc);

        JLabel lblMotDePasse = new JLabel("Password:");
        lblMotDePasse.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelFormulaire.add(lblMotDePasse, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel panelMotDePasse = new JPanel(new BorderLayout(5, 0));
        panelMotDePasse.setOpaque(false);
        panelMotDePasse.add(txtMotDePasse, BorderLayout.CENTER);
        panelMotDePasse.add(btnVoirMotDePasse, BorderLayout.EAST);
        panelFormulaire.add(panelMotDePasse, gbc);

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
        JPanel tablePanel = UIUtils.createCardPanel("Receptionists Directory");
        tablePanel.setLayout(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(tableReceptionniste);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeManager.getCardColor());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void chargerReceptionnistes() {
        listeReceptionnistes = ReceptionnisteDAO.getAllReceptionnistes();
        tableModel.setRowCount(0);
        for (Receptionniste r : listeReceptionnistes) {
            tableModel.addRow(new Object[]{r.getEmail()});
        }
    }

    private void ajouterReceptionniste() {
        String messageErreur = validerChamps();
        if (messageErreur != null) {
            afficherErreur(messageErreur);
            return;
        }

        String email = txtEmail.getText().trim();
        String motDePasse = new String(txtMotDePasse.getPassword());

        if (ReceptionnisteDAO.emailExiste(email)) {
            afficherErreur("Email address already in use.");
            txtEmail.requestFocus();
            txtEmail.selectAll();
            return;
        }

        String hash = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
        Receptionniste receptionniste = new Receptionniste(email, hash);

        if (ReceptionnisteDAO.ajouterReceptionniste(receptionniste)) {
            chargerReceptionnistes();
            nouveauReceptionniste();
        } else {
            afficherErreur("Error adding receptionist.");
        }
    }

    private void modifierReceptionniste() {
        int selectedRow = tableReceptionniste.getSelectedRow();
        if (selectedRow == -1) return;

        String messageErreur = validerChamps();
        if (messageErreur != null) {
            afficherErreur(messageErreur);
            return;
        }

        String email = txtEmail.getText().trim();
        String motDePasse = new String(txtMotDePasse.getPassword());

        if (!emailSelectionne.equalsIgnoreCase(email) && ReceptionnisteDAO.emailExiste(email)) {
            afficherErreur("Email address already in use by another user.");
            txtEmail.requestFocus();
            txtEmail.selectAll();
            return;
        }

        String hash = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
        Receptionniste receptionniste = new Receptionniste(email, hash);

        if (ReceptionnisteDAO.modifierReceptionniste(receptionniste, emailSelectionne)) {
            chargerReceptionnistes();
            nouveauReceptionniste();
        } else {
            afficherErreur("Error updating receptionist.");
        }
    }

    private void supprimerReceptionniste() {
        int selectedRow = tableReceptionniste.getSelectedRow();
        if (selectedRow == -1) return;

        String email = emailSelectionne;
        
        int confirmation = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete " + email + "?",
            TITRE_CONFIRMATION,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            if (ReceptionnisteDAO.supprimerReceptionniste(emailSelectionne)) {
                chargerReceptionnistes();
                nouveauReceptionniste();
            } else {
                afficherErreur("Error deleting receptionist.");
            }
        }
    }

    private void selectionnerReceptionniste() {
        int selectedRow = tableReceptionniste.getSelectedRow();
        if (selectedRow != -1) {
            txtEmail.setText(tableModel.getValueAt(selectedRow, 0).toString());
            emailSelectionne = tableModel.getValueAt(selectedRow, 0).toString();
            txtMotDePasse.setText("");

            btnAjouter.setEnabled(false);
            btnModifier.setEnabled(true);
            btnSupprimer.setEnabled(true);
        }
    }

    private void nouveauReceptionniste() {
        txtEmail.setText("");
        txtMotDePasse.setText("");
        emailSelectionne = null;
        tableReceptionniste.clearSelection();
        btnAjouter.setEnabled(true);
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
    }

    private String validerChamps() {
        String email = txtEmail.getText().trim();
        char[] motDePasse = txtMotDePasse.getPassword();

        if (email.isEmpty() || motDePasse.length == 0) {
            return "All fields are required.";
        }

        if (!email.matches(EMAIL_REGEX)) {
            return "Invalid email format.";
        }

        if (motDePasse.length < 6) {
            return "Password must be at least 6 characters.";
        }

        return null;
    }

    private void afficherErreur(String message) {
        JOptionPane.showMessageDialog(this, message, TITRE_ERREUR, JOptionPane.ERROR_MESSAGE);
    }
}