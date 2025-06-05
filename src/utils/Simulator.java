package utils;

import java.util.Random;

import engine.GameEngine;
import model.House;
import model.Player;
import stats.StatisticsManager;

public class Simulator {
    public Simulator() {
    }

    public static void main(String[] args) {
        Random random = new Random();
        int SIMULATION_ROUNDS = 100000;
        Player player = new Player("SimPlayer", 10000);
        House house = new House(1000.0);
        GameEngine engine = new GameEngine(house);
        StatisticsManager stats = new StatisticsManager();

        double baseBet = 1.0;
        double currentBet = baseBet;

        for (int i = 0; i < SIMULATION_ROUNDS; ++i) {
            if (player.getBalance() < currentBet) {
                System.out.println("SimPlayer cannot afford bet of $" + currentBet + " at round " + (i + 1));
                break;
            }

            player.setBetAmount(currentBet);
            player.setBetChoice("over");  // always betting "under"

            engine.playRound(player);

            boolean playerWon = engine.isPlayerWon();
            int[] finalRolls = engine.getDiceValues();
            int diceTotal = Utils.sumDice(finalRolls);

            stats.recordOutcome(diceTotal, playerWon);

            if (playerWon) {
                currentBet = baseBet;  // reset to base after win
            } else {
                currentBet *= 2;       // double after loss
            }
        }

        System.out.println("✅ Simulation completed.");
        stats.printStatistics();
        System.out.println("Final Player Balance: " + Utils.formatCurrency(player.getBalance()));
        System.out.println("Final House Balance: " + Utils.formatCurrency(house.getBalance()));
    }
}

