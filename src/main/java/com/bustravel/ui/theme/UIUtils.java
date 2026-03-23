package com.bustravel.ui.theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class UIUtils {

    public static void styleTextField(JTextField txt, String placeholder) {
        txt.putClientProperty("JTextField.placeholderText", placeholder);
        txt.putClientProperty("JComponent.roundRect", true);
        txt.setFont(ThemeManager.FONT_BODY);
        txt.setMargin(new Insets(5, 10, 5, 10));
    }

    public static void styleButton(JButton btn, Color bg) {
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    public static JPanel createCardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(ThemeManager.getCardColor());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        if (title != null && !title.isEmpty()) {
            TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), title, 
                TitledBorder.LEFT, TitledBorder.TOP,
                ThemeManager.FONT_SUBHEADING, ThemeManager.PRIMARY_COLOR
            );
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createCompoundBorder(titledBorder, new EmptyBorder(5, 15, 15, 15))
            ));
        }
        return panel;
    }
    
    public static void styleTable(JTable table) {
        table.getTableHeader().setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(ThemeManager.PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(ThemeManager.FONT_BODY);
        table.setRowHeight(30);
        table.setSelectionBackground(ThemeManager.PRIMARY_COLOR.brighter());
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
    }
}
