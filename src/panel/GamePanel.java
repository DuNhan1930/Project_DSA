package panel;

import engine.GameEngine;
import model.House;
import model.Player;
import stats.StatisticsManager;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class GamePanel extends JPanel {
    private final JTextField betField;
    private final JButton five, ten, twenty, fifty, hundred, allIn;
    private final JButton underButton, overButton;
    private final JLabel balanceLabel;
    private final JLabel winStreakLabel = new JLabel("Win Streak: 0");
    private final ResultBar resultBar = new ResultBar();
    private final Random random = new Random();

    private final JLabel diceLabel1;
    private final JLabel diceLabel2;
    private final JLabel diceLabel3;

    private final Player player;
    private final House house;
    private final GameEngine engine;
    private final StatisticsManager stats;

    private int currentStreak = 0;

    public GamePanel(String playerName, double startingBalance, MainFrame mainFrame) {
        String name = mainFrame.getPlayerName();
        double balance = mainFrame.getPlayerBalance();

        this.player = new Player(name, balance);
        this.house = new House(1000.0);
        this.engine = new GameEngine(house);
        this.engine.getBiasManager().reset();
        this.stats = new StatisticsManager();

        setOpaque(false);
        setBackground(new Color(27, 94, 149));
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JButton exitButton = getExitButton(mainFrame);
        topPanel.add(exitButton, BorderLayout.WEST);
        JLabel welcomeLabel = new JLabel("Place a Bet, " + playerName + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 50));
        welcomeLabel.setForeground(new Color(255, 204, 102));
        topPanel.add(welcomeLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        resultBar.setOpaque(false);
        resultBar.setPreferredSize(new Dimension(400, 70));
        resultBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(resultBar);

        diceLabel1 = createDiceLabel();
        diceLabel2 = createDiceLabel();
        diceLabel3 = createDiceLabel();

        JPanel dicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        dicePanel.setOpaque(false);
        dicePanel.add(diceLabel1);
        dicePanel.add(diceLabel2);
        dicePanel.add(diceLabel3);
        dicePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(dicePanel);

        // Balance + Win Streak Panel (2 columns)
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setMaximumSize(new Dimension(400, 40));
        statsPanel.setOpaque(false);

        balanceLabel = new JLabel("Balance: " + Utils.formatCurrency(player.getBalance()));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        balanceLabel.setForeground(new Color(255, 204, 102));
        balanceLabel.setHorizontalAlignment(JLabel.CENTER);

        winStreakLabel.setFont(new Font("Arial", Font.BOLD, 18));
        winStreakLabel.setForeground(new Color(255, 204, 102));
        winStreakLabel.setHorizontalAlignment(JLabel.CENTER);

        statsPanel.add(balanceLabel);
        statsPanel.add(winStreakLabel);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(statsPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);

        betField = new JTextField(10);
        betField.setMaximumSize(new Dimension(200, 35));
        betField.setFont(new Font("Arial", Font.PLAIN, 18));
        betField.setHorizontalAlignment(JTextField.CENTER);
        betField.setBorder(BorderFactory.createLineBorder(new Color(44, 44, 44), 2));

        JLabel betLabel = new JLabel("Enter Bet:");
        betLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        betLabel.setForeground(Color.WHITE);
        inputPanel.add(betLabel);
        inputPanel.add(betField);

        five = new JButton("5$");
        ten = new JButton("10$");
        twenty = new JButton("20$");
        fifty = new JButton("50$");
        hundred = new JButton("100$");
        allIn = new JButton("All In");

        JButton[] betButtons = {five, ten, twenty, fifty, hundred, allIn};
        for (JButton btn : betButtons) styleButton(btn);

        five.addActionListener(e -> betField.setText("5"));
        ten.addActionListener(e -> betField.setText("10"));
        twenty.addActionListener(e -> betField.setText("20"));
        fifty.addActionListener(e -> betField.setText("50"));
        hundred.addActionListener(e -> betField.setText("100"));
        allIn.addActionListener(e -> betField.setText(String.valueOf(player.getBalance())));

        inputPanel.add(five);
        inputPanel.add(ten);
        inputPanel.add(twenty);
        inputPanel.add(fifty);
        inputPanel.add(hundred);
        inputPanel.add(allIn);
        bottomPanel.add(inputPanel, BorderLayout.NORTH);

        JPanel choicePanel = new JPanel();
        choicePanel.setOpaque(false);

        underButton = new JButton("Under (3–10)");
        overButton = new JButton("Over (11–18)");
        styleButton(underButton);
        styleButton(overButton);

        underButton.addActionListener(e -> playRound("under"));
        overButton.addActionListener(e -> playRound("over"));

        choicePanel.add(underButton);
        choicePanel.add(overButton);
        bottomPanel.add(choicePanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
        updateDiceIcons(1, 1, 1);
    }

    private JButton getExitButton(MainFrame mainFrame) {
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton.setBackground(new Color(180, 50, 50));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setMargin(new Insets(1, 10, 1, 10));
        exitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Exit to main menu?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainFrame.showPanel("homePanel");
            }
        });
        return exitButton;
    }

    private void playRound(String choice) {
        String betText = betField.getText().trim();
        double bet;
        try {
            bet = Double.parseDouble(betText);
            if (bet <= 0 || bet > player.getBalance()) {
                JOptionPane.showMessageDialog(this, "Invalid bet amount!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid number!");
            return;
        }

        player.setBetAmount(bet);
        if (!player.hasEnoughBalance()) {
            JOptionPane.showMessageDialog(this, "Not enough balance.");
            return;
        }

        underButton.setEnabled(false);
        overButton.setEnabled(false);

        Timer animationTimer = new Timer(100, null);
        final int[] count = {0};

        animationTimer.addActionListener(e -> {
            updateDiceIcons(random.nextInt(6) + 1, random.nextInt(6) + 1, random.nextInt(6) + 1);
            count[0]++;

            if (count[0] >= 10) {
                animationTimer.stop();

                player.setBetChoice(choice);
                String result = engine.playRound(player);
                boolean playerWon = engine.isPlayerWon();

                int[] finalRolls = engine.getDiceValues();
                int diceTotal = Utils.sumDice(finalRolls);
                updateDiceIcons(finalRolls[0], finalRolls[1], finalRolls[2]);

                JOptionPane.showMessageDialog(this, result);
                resultBar.addResult(diceTotal, diceTotal > 10);

                stats.recordOutcome(diceTotal, playerWon);
                updateBalance();
                updateWinStreak(playerWon);

                if (player.getBalance() <= 0) {
                    JOptionPane.showMessageDialog(this, "You're out of money! Game over.");
                }

                underButton.setEnabled(player.getBalance() > 0);
                overButton.setEnabled(player.getBalance() > 0);
            }
        });
        animationTimer.start();
    }

    private void updateBalance() {
        balanceLabel.setText("Balance: " + Utils.formatCurrency(player.getBalance()));
    }

    private void updateWinStreak(boolean playerWon) {
        if (playerWon) {
            currentStreak++;
        } else {
            currentStreak = 0;
        }
        winStreakLabel.setText("Win Streak: " + currentStreak);
    }

    private void updateDiceIcons(int val1, int val2, int val3) {
        diceLabel1.setIcon(resizeIcon(new ImageIcon("src/resources/dice" + val1 + ".png")));
        diceLabel2.setIcon(resizeIcon(new ImageIcon("src/resources/dice" + val2 + ".png")));
        diceLabel3.setIcon(resizeIcon(new ImageIcon("src/resources/dice" + val3 + ".png")));
    }

    private Icon resizeIcon(ImageIcon icon) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    private JLabel createDiceLabel() {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(80, 80));
        label.setOpaque(true);
        label.setBackground(new Color(220, 220, 220));
        label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 0));
        return label;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(50, 150, 50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(44, 44, 44), 2),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(70, 170, 70));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(50, 150, 50));
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        Color color1 = new Color(27, 94, 149);
        Color color2 = new Color(10, 50, 100);
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }
}
