package panel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private String playerName;
    private double playerBalance;

    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerBalance(double balance) {
        this.playerBalance = balance;
    }

    public double getPlayerBalance() {
        return playerBalance;
    }

    public MainFrame() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        setTitle("Under or Over Dice Game - DSA Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 480);

        HomePanel homePanel = new HomePanel(this);
        cardPanel.add(homePanel, "homePanel");

        add(cardPanel);
        setLocationRelativeTo(null);
    }

    public void showPanel(String name) {
        cardLayout.show(cardPanel, name);
    }

    public void setGamePanel(GamePanel gamePanel) {
        cardPanel.add(gamePanel, "gamePanel");
        cardLayout.show(cardPanel, "gamePanel");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}

