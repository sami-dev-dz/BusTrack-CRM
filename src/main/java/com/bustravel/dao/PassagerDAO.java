package com.bustravel.dao;

import com.bustravel.model. Passager;
import com.bustravel.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class PassagerDAO {

    public static ArrayList<Passager> getAllPassagers() {
        ArrayList<Passager> list = new ArrayList<>();
        String sql = "SELECT idPassager, nom, prenom, adresse, telephone FROM Passager ORDER BY nom, prenom";
        try (Connection cnx = DBConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Passager p = new Passager();
                p.setIdPassager(rs.getInt("idPassager"));
                p.setNom(rs.getString("nom"));
                p.setPrenom(rs.getString("prenom"));
                p.setAdresse(rs.getString("adresse"));
                p.setTelephone(rs.getString("telephone"));
                list.add(p);
            }
        } catch (SQLException e) {
            com.bustravel.utils.AppLogger.error(e);
        }
        return list;
    }

    public static ArrayList<Passager> rechercher(String critere) {
        ArrayList<Passager> list = new ArrayList<>();
        String sql = "SELECT idPassager, nom, prenom, adresse, telephone FROM Passager " +
                     "WHERE nom LIKE ? OR prenom LIKE ? OR adresse LIKE ? OR telephone LIKE ?  " +
                     "ORDER BY nom, prenom";
        try (Connection cnx = DBConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            String recherche = "%" + critere + "%";
            ps.setString(1, recherche);
            ps.setString(2, recherche);
            ps.setString(3, recherche);
            ps.setString(4, recherche);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Passager p = new Passager();
                    p.setIdPassager(rs.getInt("idPassager"));
                    p.setNom(rs.getString("nom"));
                    p. setPrenom(rs.getString("prenom"));
                    p. setAdresse(rs.getString("adresse"));
                    p. setTelephone(rs.getString("telephone"));
                    list. add(p);
                }
            }
        } catch (SQLException e) {
            com.bustravel.utils.AppLogger.error(e);
        }
        return list;
    }

    public static boolean ajouter(Passager p) {
        String sql = "INSERT INTO Passager (nom, prenom, adresse, telephone) VALUES (?, ?, ?, ?)";
        try (Connection cnx = DBConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setString(3, p.getAdresse());
            ps.setString(4, p. getTelephone());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            com.bustravel.utils.AppLogger.error(e);
        }
        return false;
    }

    public static boolean modifier(Passager p) {
        String sql = "UPDATE Passager SET nom = ?, prenom = ?, adresse = ?, telephone = ? WHERE idPassager = ? ";
        try (Connection cnx = DBConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setString(3, p. getAdresse());
            ps.setString(4, p.getTelephone());
            ps.setInt(5, p.getIdPassager());
            return ps. executeUpdate() == 1;
        } catch (SQLException e) {
            com.bustravel.utils.AppLogger.error(e);
        }
        return false;
    }

    public static boolean supprimer(int idPassager) {
        String sql = "DELETE FROM Passager WHERE idPassager = ?";
        try (Connection cnx = DBConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, idPassager);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            com.bustravel.utils.AppLogger.error(e);
        }
        return false;
    }

    public static Passager getById(int idPassager) {
        String sql = "SELECT idPassager, nom, prenom, adresse, telephone FROM Passager WHERE idPassager = ?";
        try (Connection cnx = DBConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, idPassager);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Passager p = new Passager();
                    p.setIdPassager(rs.getInt("idPassager"));
                    p.setNom(rs.getString("nom"));
                    p. setPrenom(rs.getString("prenom"));
                    p. setAdresse(rs.getString("adresse"));
                    p. setTelephone(rs.getString("telephone"));
                    return p;
                }
            }
        } catch (SQLException e) {
            com.bustravel.utils.AppLogger.error(e);
        }
        return null;
    }

    public static boolean existePassager(String nom, String prenom, String telephone) {
        String sql = "SELECT 1 FROM Passager WHERE nom = ? AND prenom = ? AND telephone = ?  LIMIT 1";
        try (Connection cnx = DBConnection. getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, telephone);
            try (ResultSet rs = ps. executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            com.bustravel.utils.AppLogger.error(e);
        }
        return false;
    }
}
