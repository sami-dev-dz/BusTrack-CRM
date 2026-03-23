package com.bustravel.service;

import com.bustravel.model.Receptionniste;
import com.bustravel.utils.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    /**
     * Authenticates a user based on email and password.
     * @param email The user's email
     * @param password The user's password
     * @return Receptionniste object if successful, null otherwise
     * @throws SQLException on database errors
     */
    public static Receptionniste authenticate(String email, String password) throws SQLException {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }

        String query = "SELECT idUtilisateur, email, motDePasse, role FROM Utilisateur WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String motDePasseHash = rs.getString("motDePasse");
                    
                    // Allow plain text bypass if needed, but primarily use BCrypt
                    boolean isPasswordValid = false;
                    try {
                        isPasswordValid = BCrypt.checkpw(password, motDePasseHash);
                    } catch (IllegalArgumentException e) {
                        // In case the DB has non-hashed passwords (from before BCrypt)
                        isPasswordValid = password.equals(motDePasseHash);
                    }

                    if (isPasswordValid) {
                        int idUtilisateur = rs.getInt("idUtilisateur");
                        String emailBD = rs.getString("email");
                        String roleBD = rs.getString("role");
                        
                        return new Receptionniste(idUtilisateur, emailBD, motDePasseHash, roleBD);
                    }
                }
            }
        }
        return null;
    }
}
