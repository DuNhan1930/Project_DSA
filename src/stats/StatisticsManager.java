package stats;

import java.util.HashMap;
import java.util.Map;

public class StatisticsManager {
    private int totalRounds = 0;
    private int playerWins = 0;
    private int playerLosses = 0;
    private final Map<Integer, Integer> diceOutcomeFrequency = new HashMap<>();

    public void recordOutcome(int diceTotal, boolean playerWon) {
        totalRounds++;
        if (playerWon) {
            playerWins++;
        } else {
            playerLosses++;
        }

        // Track dice total frequency
        diceOutcomeFrequency.put(diceTotal, diceOutcomeFrequency.getOrDefault(diceTotal, 0) + 1);
    }

    public void printStatistics() {
        System.out.println("\n=== Game Statistics ===");
        System.out.println("Total rounds played: " + totalRounds);
        System.out.println("Player wins: " + playerWins);
        System.out.println("Player losses: " + playerLosses);

        double winRate = totalRounds == 0 ? 0.0 : (double) playerWins / totalRounds * 100;
        System.out.printf("Win rate: %.2f%%\n", winRate);

        System.out.println("\nDice Outcome Frequency:");
        for (int total = 3; total <= 18; total++) {
            int count = diceOutcomeFrequency.getOrDefault(total, 0);
            System.out.printf("Sum %2d: %d\n", total, count);
        }
        System.out.println("========================\n");
    }

    public void reset() {
        totalRounds = 0;
        playerWins = 0;
        playerLosses = 0;
        diceOutcomeFrequency.clear();
    }
}
