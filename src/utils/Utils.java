package utils;

import java.util.Random;

public class Utils {
    private static final Random random = new Random();

    /**
     * Rolls a single six-sided die (fair roll).
     *
     * @return A number between 1 and 6.
     */
    public static int rollDice() {
        return random.nextInt(6) + 1; // Returns 1–6
    }

    /**
     * Rolls 3 fair dice and returns their individual values.
     *
     * @return An array of 3 integers, where each element is the result of one fair die roll (1-6).
     */
    public static int[] rollThreeDice() {
        int[] diceValues = new int[3];
        diceValues[0] = rollDice();
        diceValues[1] = rollDice();
        diceValues[2] = rollDice();
        return diceValues;
    }

    /**
     * Calculates the sum of an array of integer dice values.
     *
     * @param diceValues An array of integers representing dice rolls.
     * @return The sum of the integers in the array.
     */
    public static int sumDice(int[] diceValues) {
        if (diceValues == null) {
            return 0;
        }
        int sum = 0;
        for (int value : diceValues) {
            sum += value;
        }

        return sum;
    }

    /**
     * Format currency-style output for balance.
     *
     * @param amount Balance or bet.
     * @return Formatted string.
     */
    public static String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }

    /**
     * Rolls a single six-sided die using a specific bias.
     * The bias makes results like 5 more likely and 1 impossible.
     * Possible outcomes: {2, 3, 4, 5, 5, 6}.
     *
     * @return A number between 2 and 6 based on the bias.
     */
    public static int rollSingleBiasedOverDice() {
        int[] biasedOutcomes = {2, 3, 4, 5, 5, 6};
        return biasedOutcomes[random.nextInt(biasedOutcomes.length)];
    }

    /**
     * Rolls 3 dice, each using a biased mechanism, and returns their individual values.
     * Each die independently uses the bias defined in rollSingleBiasedDie().
     * (This method was previously named biasedRollDie and returned a single int).
     *
     * @return An array of 3 integers, where each element is the result of one biased die roll.
     */
    public static int[] biasedRollDice() {
        int[] diceValues = new int[3];
        diceValues[0] = rollSingleBiasedOverDice();
        diceValues[1] = rollSingleBiasedOverDice();
        diceValues[2] = rollSingleBiasedOverDice();
        return diceValues;
    }
}