package view;

import controller.AppController;
import view.ui.RoundedPanel;
import view.ui.Theme;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SettingsPanel extends JPanel {

    public interface SettingsListener {
        void onThemeChanged();
    }

    private final AppController controller;
    private final SettingsListener settingsListener;

    private JButton themeToggleBtn;
    private JLabel speedValueLabel;

    public SettingsPanel(AppController ctrl, SettingsListener listener) {
        this.controller = ctrl;
        this.settingsListener = listener;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        add(buildAppearanceCard());
        add(Box.createVerticalStrut(20));
        add(buildSpeedCard());
        add(Box.createVerticalStrut(20));
        add(buildAboutCard());
        add(Box.createVerticalGlue());
    }

    private JPanel buildAppearanceCard() {
        RoundedPanel card = new RoundedPanel(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel title = new JLabel("Appearance");
        title.setFont(Theme.FONT_TITLE.deriveFont(Font.BOLD, 16f));
        title.setForeground(Theme.getText());
        title.setAlignmentX(LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(12));

        String btnText = Theme.isDark() ? "Switch to Light Mode" : "Switch to Dark Mode";
        themeToggleBtn = new JButton(btnText);
        themeToggleBtn.setFont(Theme.FONT_BODY.deriveFont(Font.PLAIN, 13f));
        themeToggleBtn.setBackground(Theme.BLUE);
        themeToggleBtn.setForeground(Color.WHITE);
        themeToggleBtn.setFocusPainted(false);
        themeToggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeToggleBtn.setOpaque(true);
        themeToggleBtn.setBorderPainted(false);
        themeToggleBtn.setAlignmentX(LEFT_ALIGNMENT);
        themeToggleBtn.setMaximumSize(new Dimension(220, 38));

        themeToggleBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Theme.toggleMode();
                themeToggleBtn.setText(Theme.isDark() ? "Switch to Light Mode" : "Switch to Dark Mode");
                if (settingsListener != null) settingsListener.onThemeChanged();
            }
        });

        card.add(themeToggleBtn);
        return card;
    }

    private JPanel buildSpeedCard() {
        RoundedPanel card = new RoundedPanel(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JLabel title = new JLabel("Animation Speed");
        title.setFont(Theme.FONT_TITLE.deriveFont(Font.BOLD, 16f));
        title.setForeground(Theme.getText());
        title.setAlignmentX(LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(8));

        JLabel desc = new JLabel("Default animation delay (ms):");
        desc.setFont(Theme.FONT_BODY.deriveFont(13f));
        desc.setForeground(Theme.getSecondaryText());
        desc.setAlignmentX(LEFT_ALIGNMENT);
        card.add(desc);
        card.add(Box.createVerticalStrut(8));

        final JSlider slider = new JSlider(10, 500, 40);
        slider.setOpaque(false);
        slider.setAlignmentX(LEFT_ALIGNMENT);
        slider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        speedValueLabel = new JLabel("40 ms");
        speedValueLabel.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 13f));
        speedValueLabel.setForeground(Theme.BLUE);
        speedValueLabel.setAlignmentX(LEFT_ALIGNMENT);

        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int val = slider.getValue();
                speedValueLabel.setText(val + " ms");
                controller.setAnimationDelay(val);
            }
        });

        card.add(slider);
        card.add(Box.createVerticalStrut(4));
        card.add(speedValueLabel);
        return card;
    }

    private JPanel buildAboutCard() {
        RoundedPanel card = new RoundedPanel(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JLabel title = new JLabel("About");
        title.setFont(Theme.FONT_TITLE.deriveFont(Font.BOLD, 16f));
        title.setForeground(Theme.getText());
        title.setAlignmentX(LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(10));

        card.add(makeAboutLine("Maze Solver v1.0", Theme.BLUE, Font.BOLD, 14f));
        card.add(Box.createVerticalStrut(4));
        card.add(makeAboutLine("Built with Java Swing & AWT", Theme.getSecondaryText(), Font.PLAIN, 13f));
        card.add(Box.createVerticalStrut(2));
        card.add(makeAboutLine("Algorithm: Recursive DFS + Backtracking", Theme.getSecondaryText(), Font.PLAIN, 13f));
        card.add(Box.createVerticalStrut(2));
        card.add(makeAboutLine("University Project", Theme.getSecondaryText(), Font.PLAIN, 13f));
        return card;
    }

    private JLabel makeAboutLine(String text, Color color, int style, float size) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BODY.deriveFont(style, size));
        label.setForeground(color);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    public void refreshTheme() {
        themeToggleBtn.setText(Theme.isDark() ? "Switch to Light Mode" : "Switch to Dark Mode");
        repaint();
    }
}
