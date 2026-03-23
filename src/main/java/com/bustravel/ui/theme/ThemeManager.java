package com.bustravel.ui.theme;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class ThemeManager {
    public static final Color PRIMARY_COLOR = new Color(59, 130, 246);   // Blue 500
    public static final Color SECONDARY_COLOR = new Color(16, 185, 129); // Emerald 500
    public static final Color ERROR_COLOR = new Color(239, 68, 68);      // Red 500
    public static final Color DARK_BG = new Color(15, 23, 42);           // Slate 900
    public static final Color DARK_CARD = new Color(30, 41, 59);         // Slate 800
    public static final Color LIGHT_BG = new Color(248, 250, 252);       // Slate 50
    public static final Color LIGHT_CARD = new Color(255, 255, 255);     // White
    
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);

    public static boolean isDarkMode = false;

    public static void setup() {
        if (isDarkMode) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }
        UIManager.put("Button.arc", 15);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("ProgressBar.arc", 10);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
    }

    public static void toggleTheme(JFrame frame) {
        isDarkMode = !isDarkMode;
        setup();
        SwingUtilities.updateComponentTreeUI(frame);
    }
    
    public static Color getBackgroundColor() {
        return isDarkMode ? DARK_BG : LIGHT_BG;
    }
    
    public static Color getCardColor() {
        return isDarkMode ? DARK_CARD : LIGHT_CARD;
    }
    
    public static Color getTextColor() {
        return isDarkMode ? Color.WHITE : new Color(30, 41, 59);
    }
    
    public static Color getSecondaryTextColor() {
        return isDarkMode ? new Color(148, 163, 184) : new Color(100, 116, 139);
    }
}
