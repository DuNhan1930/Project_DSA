package ai;

import java.util.Random;

public class BiasManager {
    private final MarkovChain markovChain;
    private final Random random = new Random();

    private int roundsPlayed = 0;   // Number of rounds played
    private int winStreak = 0;      // Consecutive player wins
    private int lossStreak = 0;     // Consecutive player losses
    private boolean biasEnabled = true;  // Whether bias mechanism is enabled

    public BiasManager() {
        markovChain = new MarkovChain();
    }

    /**
     * Reset the bias manager to initial state.
     */
    public void reset() {
        roundsPlayed = 0;
        winStreak = 0;
        lossStreak = 0;
        biasEnabled = true;
        markovChain.setCurrentState("NEUTRAL");
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

        // Optionally update Markov state here based on player bet and result.
        // This method currently does not handle player bet input.
    }

    /**
     * Decide whether to roll dice with bias or fair, and execute roll accordingly.
     *
     * @param playerBet The player's bet, either "under" or "over".
     * @return The sum of the three dice after applying bias or fair roll.
     */
    public int rollBiasedDice(String playerBet) {
        roundsPlayed++;

        // Bias probability increases over time (up to 30%)
        double timeBiasRate = Math.min(0.3, roundsPlayed * 0.01);
        boolean doBias = biasEnabled && (random.nextDouble() < timeBiasRate);

        // If player has a long winning streak (4 or more), temporarily disable bias for fairness
        if (winStreak >= 4) {
            doBias = false;
            markovChain.setCurrentState("NEUTRAL");
        }

        // Additional bias logic based on recent streaks
        if (biasEnabled && !doBias) {
            if (winStreak >= 2) {
                // If player won 2+ times in a row, bias to cause a slight loss
                doBias = random.nextDouble() < 0.6;
            } else if (lossStreak >= 2) {
                // If player lost 2+ times in a row, bias to help player win again
                doBias = random.nextDouble() < 0.6;
            }
        }

        // Apply bias if decided
        if (doBias) {
            // Bias result toward boundary numbers:
            // If player bets "over" favor rolling around 10,
            // If player bets "under" favor rolling around 11
            int target = playerBet.equalsIgnoreCase("over") ? 10 : 11;

            // Generate biased roll near target ±1
            int biasedRoll = target + random.nextInt(3) - 1;

            // Clamp result between valid minimum and maximum dice sums
            if (biasedRoll < 3) biasedRoll = 3;
            if (biasedRoll > 18) biasedRoll = 18;

            return biasedRoll;
        } else {
            // Fair roll (no bias)
            int[] diceValues = utils.Utils.rollThreeDice();
            return utils.Utils.sumDice(diceValues);
        }
    }

    public MarkovChain getMarkovChain() {
        return markovChain;
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public void setBiasEnabled(boolean enabled) {
        this.biasEnabled = enabled;
    }

    public boolean isBiasEnabled() {
        return biasEnabled;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public int getLossStreak() {
        return lossStreak;
    }
}
