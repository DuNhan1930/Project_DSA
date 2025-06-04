package panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HomePanel extends JPanel {
    private final JTextField nameField;
    private final JTextField balanceField;

    public HomePanel(MainFrame mainFrame) {
        setLayout(new BorderLayout(10, 60));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Custom panel with gradient background
        setOpaque(false);
        setBackground(new Color(27, 94, 149)); // Fallback color

        // Title panel with title text only
        JLabel titleLabel = new JLabel("Under or Over Dice Game", JLabel.CENTER);
        titleLabel.setFont(new Font("Impact", Font.BOLD, 54));
        titleLabel.setForeground(new Color(255, 204, 102)); // Vibrant orange

        // Use BoxLayout for vertical stacking of title, image, and input
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(Box.createVerticalStrut(10));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);

        // Add resized dice image between title and input
        ImageIcon originalIcon = new ImageIcon("src/resources/dice5.png"); // Replace with your path if different
        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel iconLabel = new JLabel(scaledIcon);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(Box.createVerticalStrut(20));
        titlePanel.add(iconLabel);

        add(titlePanel, BorderLayout.NORTH);

        // Center panel for input
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Wrapper panel to hold name and balance side-by-side
        JPanel inputRowPanel = new JPanel(new GridLayout(1, 2, 20, 0)); // 2 columns, 20px gap
        inputRowPanel.setOpaque(false);
        inputRowPanel.setMaximumSize(new Dimension(500, 80)); // Width to keep things centered

        // === Name column ===
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel("Enter Your Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField = new JTextField(15);
        nameField.setMaximumSize(new Dimension(200, 35));
        nameField.setFont(new Font("Arial", Font.PLAIN, 18));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField.setBorder(BorderFactory.createLineBorder(new Color(44, 44, 44), 2));

        namePanel.add(nameLabel);
        namePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        namePanel.add(nameField);

        // === Balance column ===
        JPanel balancePanel = new JPanel();
        balancePanel.setLayout(new BoxLayout(balancePanel, BoxLayout.Y_AXIS));
        balancePanel.setOpaque(false);

        JLabel balanceLabel = new JLabel("Enter Starting Balance:");
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        balanceField = new JTextField(15);
        balanceField.setMaximumSize(new Dimension(200, 35));
        balanceField.setFont(new Font("Arial", Font.PLAIN, 18));
        balanceField.setHorizontalAlignment(JTextField.CENTER);
        balanceField.setAlignmentX(Component.CENTER_ALIGNMENT);
        balanceField.setBorder(BorderFactory.createLineBorder(new Color(44, 44, 44), 2));

        balancePanel.add(balanceLabel);
        balancePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        balancePanel.add(balanceField);

        // Add both panels to the input row
        inputRowPanel.add(namePanel);
        inputRowPanel.add(balancePanel);

        // Add to centerPanel
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing above
        centerPanel.add(inputRowPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing below


        // Start button with hover effect
        JButton startButton = getJButton(mainFrame);

        centerPanel.add(startButton);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JButton getJButton(MainFrame mainFrame) {
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setBackground(new Color(50, 150, 50));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(44, 44, 44), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        startButton.setPreferredSize(new Dimension(180, 50));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hover effect
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startButton.setBackground(new Color(70, 170, 70)); // Lighter green
            }

            @Override
            public void mouseExited(MouseEvent e) {
                startButton.setBackground(new Color(50, 150, 50)); // Original green
            }
        });

        // Start button action with input validation
        startButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String balanceText = balanceField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your name!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (balanceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a starting balance!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    double balance = Double.parseDouble(balanceText);
                    if (balance <= 0) {
                        JOptionPane.showMessageDialog(this, "Balance must be greater than 0!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    mainFrame.setPlayerName(name);
                    mainFrame.setPlayerBalance(balance);
                    mainFrame.setGamePanel(new GamePanel(name, balance, mainFrame));  // Create GamePanel here
                    mainFrame.showPanel("gamePanel");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for balance!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return startButton;
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