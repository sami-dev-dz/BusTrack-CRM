package com.bustravel.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_URL = Constants.DB_URL;
    private static final String SQL_RESOURCE = "/initial_db.sql";
    
    static {
        try {
            Class.forName("org.sqlite.JDBC");
            initDB(); 
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de la base :");
            com.bustravel.utils.AppLogger.error(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private static void initDB() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            boolean tableExists = false;
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='Utilisateur'")) {
                tableExists = rs.next();
            }

            if (!tableExists) {
                com.bustravel.utils.AppLogger.info("Initialisation de la base de données...");
                
                try (java.io.InputStream is = DBConnection.class.getResourceAsStream(SQL_RESOURCE)) {
                    if (is != null) {
                        try (java.util.Scanner s = new java.util.Scanner(is).useDelimiter(";")) {
                            while (s.hasNext()) {
                                String sql = s.next().trim();
                                if (!sql.isEmpty() && !sql.startsWith("--")) {
                                    try {
                                        stmt.execute(sql);
                                    } catch (SQLException e) {
                                        System.err.println("Erreur SQL: " + e.getMessage());
                                    }
                                }
                            }
                        }
                        com.bustravel.utils.AppLogger.info("✅ Base et tables initialisées avec succès !");
                    } else {
                        System.err.println("❌ Fichier SQL introuvable dans les ressources : " + SQL_RESOURCE);
                    }
                }
            } else {
                com.bustravel.utils.AppLogger.info("âœ… Base de donnÃ©es dÃ©jÃ  initialisÃ©e.");
            }

        } catch (Exception e) {
            System.err.println("âŒ Erreur lors de l'initialisation de la base :");
            com.bustravel.utils.AppLogger.error(e);
        }
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            com.bustravel.utils.AppLogger.info("âœ… Connexion rÃ©ussie Ã  la base de donnÃ©es !");
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM Utilisateur");
            if (rs.next()) {
                System.out.println("Nombre d'utilisateurs : " + rs.getInt("total"));
            }
            
        } catch (SQLException e) {
            System.err.println("âŒ Erreur de connexion :");
            com.bustravel.utils.AppLogger.error(e);
        }
    }
}
