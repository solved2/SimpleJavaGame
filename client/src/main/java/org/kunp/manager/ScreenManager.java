package org.kunp.manager;

import javax.swing.*;
import java.awt.*;

public class ScreenManager {
    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    public ScreenManager() {
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void addScreen(String name, JPanel screen) {
        mainPanel.add(screen, name);
    }

    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);
    }
}

