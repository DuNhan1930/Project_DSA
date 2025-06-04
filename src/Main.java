import engine.GameEngine;
import model.House;
import model.Player;
import stats.StatisticsManager;
import utils.Utils;
import ai.ReinforcementLearningAgent;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Initialize components
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        Player player = new Player(name, 100.0); // Player starts with $100
        House house = new House(1000.0, false);   // House with bias enabled
        GameEngine engine = new GameEngine(house);
        StatisticsManager stats = new StatisticsManager();
        ReinforcementLearningAgent agent = new ReinforcementLearningAgent();

        System.out.println("\n🎲 Welcome to Under-Over Dice Game, " + name + "!");
        System.out.println("Rules: 3–10 = Under, 11–18 = Over. Win if your guess is correct.");
        System.out.println("Type 'exit' to quit, 'stats' to view statistics, or 'policy' to view AI knowledge.");

        while (true) {
            System.out.println("\nYour balance: " + Utils.formatCurrency(player.getBalance()));
            System.out.print("Enter your bet amount: ");
            String betInput = scanner.nextLine();

            if (betInput.equalsIgnoreCase("exit")) break;
            if (betInput.equalsIgnoreCase("stats")) {
                stats.printStatistics();
                continue;
            }
            if (betInput.equalsIgnoreCase("policy")) {
                agent.printPolicy();
                continue;
            }

            double betAmount;
            try {
                betAmount = Double.parseDouble(betInput);
                if (betAmount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid bet amount.");
                continue;
            }

            player.setBetAmount(betAmount);

            if (!player.hasEnoughBalance()) {
                System.out.println("❌ You don't have enough money for that bet.");
                continue;
            }

            // AI suggestion
            String aiSuggestion = agent.chooseAction();
            System.out.println("💡 AI Suggests: " + aiSuggestion.toUpperCase());

            System.out.print("Place your bet on 'under' or 'over': ");
            String choice = scanner.nextLine().toLowerCase();

            if (!choice.equals("under") && !choice.equals("over")) {
                System.out.println("❌ Invalid choice.");
                continue;
            }

            player.setBetChoice(choice);

            // Determine dice total
            int[] diceValues = Utils.rollThreeDice();
            int diceTotal = house.isBiasEnabled() ? house.rollBiasedDice() : Utils.sumDice(diceValues);
            boolean isOver = diceTotal > 10;
            boolean playerWon = (isOver && choice.equals("over")) || (!isOver && choice.equals("under"));

            // Play the round
            String result = engine.playRound(player);
            System.out.println(result);

            // Update stats and AI
            stats.recordOutcome(diceTotal, playerWon);
            int reward = playerWon ? 1 : -1;
            agent.update(String.valueOf(diceTotal), choice, reward);

            // End if player is broke
            if (player.getBalance() <= 0) {
                System.out.println("💥 You're out of money! Game over.");
                break;
            }
        }

        // End of game summary
        System.out.println("\n🏁 Game Over! Thanks for playing, " + player.getName() + "!");
        stats.printStatistics();
        System.out.println("Your Final Balance: " + Utils.formatCurrency(player.getBalance()));
        System.out.println("House Balance: " + Utils.formatCurrency(house.getBalance()));
        System.out.println("House Balance: " + Utils.formatCurrency(house.getBalance()));
    }
}
