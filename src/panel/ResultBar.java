package panel;
import javax.swing.*;
import java.awt.*;

class ResultBar extends JPanel {
    private final java.util.List<GameResult> lastResults = new java.util.ArrayList<>();

    public void addResult(int diceTotal, boolean isOver) {
        if (lastResults.size() >= 15) {
            lastResults.remove(0); // Keep only last 5
        }
        lastResults.add(new GameResult(diceTotal, isOver));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int circleDiameter = 30;
        int spacing = 10;
        int startX = (getWidth() - (circleDiameter + spacing) * lastResults.size() + spacing) / 2;

        for (int i = 0; i < lastResults.size(); i++) {
            GameResult result = lastResults.get(i);
            int x = startX + i * (circleDiameter + spacing);
            int y = 10;

            g.setColor(result.isOver ? Color.WHITE : Color.BLACK);
            g.fillOval(x, y, circleDiameter, circleDiameter);
            g.setColor(Color.BLACK);
            g.drawOval(x, y, circleDiameter, circleDiameter);

            g.setColor(result.isOver ? Color.BLACK : Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            String text = String.valueOf(result.diceTotal);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            g.drawString(text, x + (circleDiameter - textWidth) / 2, y + (circleDiameter + textHeight) / 2 - 4);
        }
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

