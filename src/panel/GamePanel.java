package panel;

import ai.ReinforcementLearningAgent;
import engine.GameEngine;
import model.House;
import model.Player;
import stats.StatisticsManager;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GamePanel extends JPanel {
    private final JTextField betField;
    private final JButton five, ten, twenty, fifty, hundred, allIn;
    private final JButton underButton, overButton;
    private final JLabel balanceLabel;
    private final ResultBar resultBar = new ResultBar();
    private final Random random = new Random();

    private final JLabel diceLabel1;
    private final JLabel diceLabel2;
    private final JLabel diceLabel3;

    private final Player player;
    private final House house;
    private final GameEngine engine;
    private final StatisticsManager stats;
    private final ReinforcementLearningAgent agent;

    public GamePanel(String playerName, double startingBalance, MainFrame mainFrame) {
        String name;
        double balance;

        name = mainFrame.getPlayerName();
        balance = mainFrame.getPlayerBalance();

        this.player = new Player(name, balance);
        this.house = new House(1000.0, true);
        this.engine = new GameEngine(house);
        this.stats = new StatisticsManager();
        this.agent = new ReinforcementLearningAgent();

        setOpaque(false);
        setBackground(new Color(27, 94, 149));
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create top panel with BorderLayout to hold exit button and welcome label
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false); // match background

        // Exit button on top-left
        JButton exitButton = getExitButton(mainFrame);
        topPanel.add(exitButton, BorderLayout.WEST);

        // Welcome label in the center
        JLabel welcomeLabel = new JLabel("Place a Bet, " + playerName + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 40));
        welcomeLabel.setForeground(new Color(255, 204, 102));
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

// Add the topPanel to GamePanel
        add(topPanel, BorderLayout.NORTH);


        // Center panel for dice and balance
        JPanel centerPanel = new JPanel(new BorderLayout(10, 40));
        centerPanel.setBackground(new Color(27, 94, 149));

        JPanel dicePanel = new JPanel(new FlowLayout());
        dicePanel.setBackground(new Color(27, 94, 149));
        diceLabel1 = createDiceLabel();
        diceLabel2 = createDiceLabel();
        diceLabel3 = createDiceLabel();
        dicePanel.add(diceLabel1);
        dicePanel.add(diceLabel2);
        dicePanel.add(diceLabel3);

        balanceLabel = new JLabel("Balance: " + Utils.formatCurrency(player.getBalance()));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        balanceLabel.setForeground(new Color(255, 204, 102));
        balanceLabel.setHorizontalAlignment(JLabel.CENTER);

        centerPanel.add(dicePanel, BorderLayout.CENTER);
        centerPanel.add(balanceLabel, BorderLayout.SOUTH);
        centerPanel.add(resultBar, BorderLayout.NORTH);
        resultBar.setPreferredSize(new Dimension(400, 70));
        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for betting and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel();
        betField = new JTextField(10);
        inputPanel.add(new JLabel("Enter Bet:"));
        inputPanel.add(betField);

        five = new JButton("5$");
        ten = new JButton("10$");
        twenty = new JButton("20$");
        fifty = new JButton("50$");
        hundred = new JButton("100$");
        allIn = new JButton("All In");

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
        underButton = new JButton("Under (3–10)");
        overButton = new JButton("Over (11–18)");

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
        exitButton.setMargin(new Insets(1, 10, 1, 10)); // Padding
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

        // Dice animation: 10 frames with 100ms delay
        Timer animationTimer = new Timer(100, null);
        final int[] count = {0};

        animationTimer.addActionListener(e -> {
            // Animate with random dice
            updateDiceIcons(random.nextInt(6) + 1, random.nextInt(6) + 1, random.nextInt(6) + 1);
            count[0]++;

            if (count[0] >= 10) {
                animationTimer.stop();

                player.setBetChoice(choice);

                int diceTotal = house.rollBiasedDice();  // This rolls and stores final dice values inside House

                String result = engine.playRound(player);
                boolean playerWon = engine.isPlayerWon();

                int[] finalRolls = engine.getDiceValues(); // Make sure this returns a length-3 int array
                updateDiceIcons(finalRolls[0], finalRolls[1], finalRolls[2]); // Show final roll here

                JOptionPane.showMessageDialog(this, result);

                stats.recordOutcome(diceTotal, playerWon);
                agent.update(String.valueOf(diceTotal), choice, playerWon ? 1 : -1);

                updateBalance();

                if (player.getBalance() <= 0) {
                    JOptionPane.showMessageDialog(this, "You're out of money! Game over.");
                }

                resultBar.addResult(diceTotal, diceTotal > 10);

                underButton.setEnabled(player.getBalance() > 0);
                overButton.setEnabled(player.getBalance() > 0);
            }
        });
        animationTimer.start();
    }


    private void updateBalance() {
        balanceLabel.setText("Balance: " + Utils.formatCurrency(player.getBalance()));
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

    // Custom paint for gradient background
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
