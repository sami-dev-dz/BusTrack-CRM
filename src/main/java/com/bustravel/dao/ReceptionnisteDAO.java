package com.bustravel.dao;

import java.sql.*;
import java.util.ArrayList;
import com.bustravel.model.Receptionniste;
import com.bustravel.utils.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

public class ReceptionnisteDAO {

    public static boolean ajouterReceptionniste(Receptionniste r) {
        Connection conn = null;
        PreparedStatement psUtilisateur = null;
        PreparedStatement psReceptionniste = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlUtilisateur = "INSERT INTO Utilisateur (email, motDePasse, role) VALUES (?, ?, ?)";
            psUtilisateur = conn.prepareStatement(sqlUtilisateur, Statement.RETURN_GENERATED_KEYS);
            psUtilisateur.setString(1, r.getEmail());
            String hashedPwd = BCrypt.hashpw(r.getMotDePasse(), BCrypt.gensalt());
            psUtilisateur.setString(2, hashedPwd);
            psUtilisateur.setString(3, "Réceptionniste");
            psUtilisateur.executeUpdate();

            generatedKeys = psUtilisateur.getGeneratedKeys();
            if (generatedKeys.next()) {
                int idUtilisateur = generatedKeys.getInt(1);

                String sqlReceptionniste = "INSERT INTO Receptionniste (idReceptionniste) VALUES (?)";
                psReceptionniste = conn.prepareStatement(sqlReceptionniste);
                psReceptionniste.setInt(1, idUtilisateur);
                psReceptionniste.executeUpdate();

                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            com.bustravel.utils.AppLogger.error(e);
            return false;
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (psReceptionniste != null) psReceptionniste.close();
                if (psUtilisateur != null) psUtilisateur.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                com.bustravel.utils.AppLogger.error(e);
            }
        }
    }

    public static boolean modifierReceptionniste(Receptionniste r, String ancienEmail) {
        String sql = "UPDATE Utilisateur SET email=?, motDePasse=? WHERE email=? AND role='Réceptionniste'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, r.getEmail());
            
            String hashedPwd = r.getMotDePasse();
            // Hash only if it's not already a bcrypt hash
            if (hashedPwd != null && !hashedPwd.startsWith("$2a$")) {
                hashedPwd = BCrypt.hashpw(hashedPwd, BCrypt.gensalt());
            }
            ps.setString(2, hashedPwd);
            ps.setString(3, ancienEmail);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            com.bustravel.utils.AppLogger.error(e);
            return false;
        }
    }

    public static boolean supprimerReceptionniste(String email) {
        String sql = "DELETE FROM Utilisateur WHERE email=? AND role='Réceptionniste'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            com.bustravel.utils.AppLogger.error(e);
            return false;
        }
    }

    public static ArrayList<Receptionniste> getAllReceptionnistes() {
        ArrayList<Receptionniste> liste = new ArrayList<>();
        String sql = "SELECT * FROM Utilisateur WHERE role='Réceptionniste' ORDER BY email";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Receptionniste r = new Receptionniste();
                r.setIdUtilisateur(rs.getInt("idUtilisateur"));
                r.setEmail(rs.getString("email"));
                r.setMotDePasse(rs.getString("motDePasse"));
                r.setRole(rs.getString("role"));
                liste.add(r);
            }

        } catch (SQLException e) {
            com.bustravel.utils.AppLogger.error(e);
        }

        return liste;
    }

    public static boolean emailExiste(String email) {
        String sql = "SELECT COUNT(*) FROM Utilisateur WHERE email=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }

        } catch (SQLException e) {
            com.bustravel.utils.AppLogger.error(e);
        }
        return false;
    }

    public static Receptionniste getReceptionnisteByEmail(String email) {
        String sql = "SELECT * FROM Utilisateur WHERE email=? AND role='Réceptionniste'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Receptionniste r = new Receptionniste();
                r.setIdUtilisateur(rs.getInt("idUtilisateur"));
                r.setEmail(rs.getString("email"));
                r.setMotDePasse(rs.getString("motDePasse"));
                r.setRole(rs.getString("role"));
                return r;
            }

        } catch (SQLException e) {
            com.bustravel.utils.AppLogger.error(e);
        }
        return null;
    }
}
