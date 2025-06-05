package utils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import engine.GameEngine;
import model.House;
import model.Player;
import stats.StatisticsManager;

public class Simulator {
    static final int SIMULATION_ROUNDS = 100000;
    static final double BASE_BET = 5.0;

    public static void main(String[] args) {
        Random random = new Random();
        Player player = new Player("SmartBot", 1000);
        House house = new House(1000.0);
        GameEngine engine = new GameEngine(house);
        StatisticsManager stats = new StatisticsManager();

        Deque<Boolean> lastWins = new ArrayDeque<>();
        Deque<Integer> lastSums = new ArrayDeque<>();

        for (int i = 0; i < SIMULATION_ROUNDS; i++) {
            if (player.getBalance() < BASE_BET) break;

            // Backtracking: analyze last 5 rounds
            int over = 0, under = 0;
            for (int sum : lastSums) {
                if (sum > 10) over++;
                else under++;
            }

            String guess;
            if (lastWins.size() >= 5) {
                // Predict based on trend
                guess = (under > over) ? "over" : "under";
            } else {
                guess = random.nextBoolean() ? "under" : "over";
            }

            double betAmount = BASE_BET;
            if (!lastWins.isEmpty() && !lastWins.peekLast()) {
                betAmount *= 2; // simulate martingale
            }

            player.setBetAmount(betAmount);
            player.setBetChoice(guess);
            engine.playRound(player);
            boolean playerWon = engine.isPlayerWon();
            int[] finalRolls = engine.getDiceValues();
            int total = Utils.sumDice(finalRolls);

            stats.recordOutcome(total, playerWon);

            // Track history
            if (lastWins.size() == 5) lastWins.removeFirst();
            if (lastSums.size() == 5) lastSums.removeFirst();
            lastWins.add(playerWon);
            lastSums.add(total);
        }

        System.out.println("\n===== SIMULATION STATS =====");
        stats.printStatistics();
        System.out.println("Final Player Balance: " + Utils.formatCurrency(player.getBalance()));
        System.out.println("Final House Balance:  " + Utils.formatCurrency(house.getBalance()));
    }
}
