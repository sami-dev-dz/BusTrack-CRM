package com.bustravel.utils;

public class Constants {
    // Database
    public static final String DB_URL = "jdbc:sqlite:bus_travel.db";

    // Application Details
    public static final String APP_NAME = "Bus Travel Management System";
    public static final String APP_VERSION = "1.0.0";

    // Error Messages
    public static final String MSG_ERR_DATABASE = "Un problème est survenu lors de l'accès à la base de données.";
    public static final String MSG_ERR_VALIDATION = "Veuillez remplir correctement tous les champs obligatoires.";
    public static final String MSG_ERR_NOT_FOUND = "L'enregistrement demandé est introuvable.";
    
    // Status Messages
    public static final String STATUS_EN_ATTENTE = "En attente";
    public static final String STATUS_CONFIRMEE = "Confirmée";
    public static final String STATUS_ANNULEE = "Annulée";

    // Roles
    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_RECEPTIONNISTE = "Réceptionniste";
}
