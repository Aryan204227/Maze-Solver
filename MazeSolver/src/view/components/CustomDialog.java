package view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import view.Theme;

/**
 * A premium, modern modal dialog replacement for JOptionPane.
 * Incorporates custom layouts, rounded borders, and themed buttons.
 */
public class CustomDialog extends JDialog {
    private boolean confirmed = false;
    private String inputValue = null;

    private CustomDialog(Frame parent, String title, String message, int type, String defaultInputVal) {
        super(parent, title, true);
        setUndecorated(true);
        setResizable(false);

        // Define colors based on type
        // Type: 0 = Info/Success, 1 = Error, 2 = Confirm, 3 = Input
        Color accentColor;
        if (type == 1) {
            accentColor = Theme.getEndCellColor(); // Error red
        } else {
            accentColor = Theme.getAccentColor(); // Purple accent
        }

        // Custom main panel with rounded corners and card theme
        RoundedPanel mainPanel = new RoundedPanel(20);
        mainPanel.setBackground(Theme.getCardBgColor());
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Dialog Header / Icon representation
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(Theme.getTitleFont(18));
        titleLabel.setForeground(accentColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(titleLabel, gbc);

        // Dialog Message
        JLabel messageLabel = new JLabel("<html><div style='text-align: center; width: 280px;'>" + message + "</div></html>", SwingConstants.CENTER);
        messageLabel.setFont(Theme.getBodyFont(14));
        messageLabel.setForeground(Theme.getPrimaryTextColor());
        gbc.gridy = 1;
        contentPanel.add(messageLabel, gbc);

        // Optional input text field
        JTextField inputField = null;
        if (type == 3) {
            inputField = new JTextField(defaultInputVal);
            inputField.setFont(Theme.getBodyFont(14));
            inputField.setBackground(Theme.getBgColor());
            inputField.setForeground(Theme.getPrimaryTextColor());
            inputField.setCaretColor(Theme.getPrimaryTextColor());
            inputField.setBorder(new EmptyBorder(8, 8, 8, 8));
            gbc.gridy = 2;
            contentPanel.add(inputField, gbc);
        }

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        buttonsPanel.setOpaque(false);

        RoundedButton btnOk = new RoundedButton("OK");
        btnOk.setColors(accentColor, accentColor.brighter(), accentColor.darker(), Color.WHITE, Color.WHITE);
        
        final JTextField finalInputField = inputField;
        btnOk.addActionListener(e -> {
            confirmed = true;
            if (type == 3 && finalInputField != null) {
                inputValue = finalInputField.getText();
            }
            dispose();
        });

        if (type == 2 || type == 3) {
            // Include Cancel button
            RoundedButton btnCancel = new RoundedButton("Cancel");
            btnCancel.setColors(Theme.getCardBgColor(), Theme.getBgColor(), Theme.getBgColor().darker(), Theme.getSecondaryTextColor(), Theme.getPrimaryTextColor());
            btnCancel.addActionListener(e -> {
                confirmed = false;
                dispose();
            });
            buttonsPanel.add(btnCancel);
        }
        
        buttonsPanel.add(btnOk);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Window size and alignment
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        pack();
        
        // Ensure minimum size
        setSize(Math.max(getWidth(), 360), Math.max(getHeight(), 200));
        setLocationRelativeTo(parent);
    }

    private static Frame getParentFrame(Component c) {
        if (c == null) return null;
        if (c instanceof Frame) return (Frame) c;
        return (Frame) SwingUtilities.getAncestorOfClass(Frame.class, c);
    }

    /**
     * Shows a success/info dialog.
     */
    public static void showSuccess(Component parent, String title, String message) {
        CustomDialog dialog = new CustomDialog(getParentFrame(parent), title, message, 0, null);
        dialog.setVisible(true);
    }

    /**
     * Shows an error dialog.
     */
    public static void showError(Component parent, String title, String message) {
        CustomDialog dialog = new CustomDialog(getParentFrame(parent), title, message, 1, null);
        dialog.setVisible(true);
    }

    /**
     * Shows a confirmation dialog (Yes/Cancel).
     *
     * @return true if the user clicks OK, false otherwise.
     */
    public static boolean showConfirm(Component parent, String title, String message) {
        CustomDialog dialog = new CustomDialog(getParentFrame(parent), title, message, 2, null);
        dialog.setVisible(true);
        return dialog.confirmed;
    }

    /**
     * Shows an input text prompt dialog.
     *
     * @return the text entered, or null if cancelled.
     */
    public static String showInput(Component parent, String title, String message, String defaultValue) {
        CustomDialog dialog = new CustomDialog(getParentFrame(parent), title, message, 3, defaultValue);
        dialog.setVisible(true);
        return dialog.confirmed ? dialog.inputValue : null;
    }
}
