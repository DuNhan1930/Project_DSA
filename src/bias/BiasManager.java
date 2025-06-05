package bias;

import java.util.Random;
import utils.Utils;

public class BiasManager {
    private final Random random = new Random();

    private int roundsPlayed = 0;   // Number of rounds played
    private int winStreak = 0;      // Consecutive player wins
    private int lossStreak = 0;     // Consecutive player losses
    private int[] diceValues = new int[3]; // Add this field

    public BiasManager() {
    }

    /**
     * Update the winning/losing streak counters after each round.
     * @param playerWon true if player won this round, false otherwise.
     */
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

    /**
     * Decide whether to roll dice with bias or fair, and execute roll accordingly.
     *
     * @param playerBet The player's bet, either "under" or "over".
     * @return The sum of the three dice after applying bias or fair roll.
     */
    public int rollBiasedDice(String playerBet, double initBalance, double balance, double betAmount) {
        roundsPlayed++;
        double profit = balance/initBalance;
        double ratioBet = betAmount/balance;

        // Bias probability increases over time (up to 30%)
        double timeBiasRate = Math.min(0.3, roundsPlayed * 0.01);
        boolean doBias;

        // Additional bias logic based
        if (profit >= 2) {
            doBias = random.nextDouble() < 0.85;
        } else if (lossStreak > 3) {
            if (ratioBet > 0.6) {
                doBias = random.nextDouble() < 0.75;
            }
            else {
                doBias = false;
            }
        } else if (winStreak > 3) {
            if (ratioBet > 0.6) {
                doBias = random.nextDouble() < 0.75;
            }
            else {
                doBias = false;
            }
        } else {
            doBias = random.nextDouble() < timeBiasRate;
        }

        // Apply bias if decided
        if (doBias) {
            if (random.nextDouble() < 0.8) {
                if (playerBet.equalsIgnoreCase("over")) {
                    diceValues = Utils.biasedUnderRollDice(); // Make they get UNDER result
                } else {
                    diceValues = Utils.biasedOverRollDice(); // Make they get OVER result
                }
                return Utils.sumDice(diceValues);
            } else {
                int biasedRoll = playerBet.equalsIgnoreCase("over") ? 10 : 11;
                diceValues = Utils.generateDiceSum(biasedRoll);
                return biasedRoll;
            }
        } else {
            // Fair roll (no bias)
            diceValues = Utils.rollThreeDice();
            return Utils.sumDice(diceValues);
        }
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
