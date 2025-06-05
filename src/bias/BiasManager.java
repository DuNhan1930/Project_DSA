package bias;

import java.util.LinkedList;
import java.util.Random;
import utils.Utils;

public class BiasManager {
    private final Random random = new Random();

    private int roundsPlayed = 0;
    private int winStreak = 0;
    private int lossStreak = 0;
    private int[] diceValues = new int[3];

    // Track result history (for smoothing / avoiding patterns)
    private final LinkedList<Boolean> recentOvers = new LinkedList<>();
    private final int memorySize = 6;

    public BiasManager() {}

    public void updateStreak(boolean playerWon) {
        roundsPlayed++;
        if (playerWon) {
            winStreak++;
            lossStreak = 0;
        } else {
            lossStreak++;
            winStreak = 0;
        }
    }

    public int rollBiasedDice(String playerBet, double initBalance, double balance, double betAmount) {
        roundsPlayed++;
        double profit = balance / initBalance;
        double ratioBet = betAmount / balance;

        boolean doBias;
        double timeBiasRate = Math.min(0.3, roundsPlayed * 0.01);

        // Default bias decision logic
        if (profit >= 2) {
            doBias = random.nextDouble() < 0.85;
        } else if (lossStreak > 3 || winStreak > 3) {
            doBias = ratioBet > 0.6 && random.nextDouble() < 0.75;
        } else {
            doBias = random.nextDouble() < timeBiasRate;
        }

        boolean avoidPattern = isOverRepeatedTooMuch();

        // Occasionally allow fair win (small bet)
        if (betAmount <= 0.1 * balance && random.nextDouble() < 0.25) {
            if (playerBet.equalsIgnoreCase("over")) {
                diceValues = Utils.biasedOverRollDice();
                recordOverResult(true);
            } else {
                diceValues = Utils.biasedUnderRollDice();
                recordOverResult(false);
            }
            return Utils.sumDice(diceValues);
        }

        if (doBias && !avoidPattern) {
            // House forces win (player loses)
            if (random.nextDouble() < 0.8) {
                if (playerBet.equalsIgnoreCase("over")) {
                    diceValues = Utils.biasedUnderRollDice();
                    recordOverResult(false);
                } else {
                    diceValues = Utils.biasedOverRollDice();
                    recordOverResult(true);
                }
            } else {
                // More direct control (sum close to crossover point)
                int biasedSum = playerBet.equalsIgnoreCase("over") ? 10 : 11;
                diceValues = Utils.generateDiceSum(biasedSum);
                recordOverResult(biasedSum > 10);
            }
        } else {
            // Fair roll
            diceValues = Utils.rollThreeDice();
            recordOverResult(Utils.sumDice(diceValues) > 10);
        }

        return Utils.sumDice(diceValues);
    }

    private void recordOverResult(boolean isOver) {
        if (recentOvers.size() >= memorySize) {
            recentOvers.removeFirst();
        }
        recentOvers.add(isOver);
    }

    private boolean isOverRepeatedTooMuch() {
        long overCount = recentOvers.stream().filter(b -> b).count();
        return overCount >= memorySize - 1 || overCount <= 1;
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public int getLossStreak() {
        return lossStreak;
    }

    public void setDiceValues(int[] diceValues) {
        this.diceValues = diceValues;
    }

    public int[] getDiceValues() {
        return diceValues;
    }
}