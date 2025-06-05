package panel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class ResultBar extends JPanel {
    private final List<GameResult> lastResults = new ArrayList<>();

    public void addResult(int diceTotal, boolean isOver) {
        if (lastResults.size() >= 15) {
            lastResults.remove(0); // Keep only last 15
        }
        lastResults.add(new GameResult(diceTotal, isOver));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Respect transparency
        if (isOpaque()) {
            super.paintComponent(g); // fills background only if opaque
        }

        Graphics2D g2d = (Graphics2D) g.create();

        int circleDiameter = 30;
        int spacing = 10;
        int totalWidth = (circleDiameter + spacing) * lastResults.size() - spacing;
        int startX = (getWidth() - totalWidth) / 2;
        int y = 15;

        for (int i = 0; i < lastResults.size(); i++) {
            GameResult result = lastResults.get(i);
            int x = startX + i * (circleDiameter + spacing);

            // Fill circle
            g2d.setColor(result.isOver ? Color.WHITE : Color.BLACK);
            g2d.fillOval(x, y, circleDiameter, circleDiameter);

            // Border
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x, y, circleDiameter, circleDiameter);

            // Text
            g2d.setColor(result.isOver ? Color.BLACK : Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String text = String.valueOf(result.diceTotal);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            g2d.drawString(text, x + (circleDiameter - textWidth) / 2 + 1, y + (circleDiameter + textHeight) / 2 - 2);
        }

        g2d.dispose();
    }

    static class GameResult {
        int diceTotal;
        boolean isOver;

        GameResult(int diceTotal, boolean isOver) {
            this.diceTotal = diceTotal;
            this.isOver = isOver;
        }
    }
}
