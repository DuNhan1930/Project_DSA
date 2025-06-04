package model;

import ai.MarkovChain;
import utils.Utils;

public class House {
    private double balance;
    private boolean biasEnabled;
    private int[] diceValues = new int[3];
    private final MarkovChain markovChain;

    public House(double initialBalance, boolean biasEnabled) {
        this.balance = initialBalance;
        this.biasEnabled = biasEnabled;
        this.markovChain = new MarkovChain();
    }

    public double getBalance() {
        return balance;
    }

    public boolean isBiasEnabled() {
        return biasEnabled;
    }

    public void setBiasEnabled(boolean biasEnabled) {
        this.biasEnabled = biasEnabled;
    }

    public void winMoney(double amount) {
        this.balance += amount;
    }

    public void loseMoney(double amount) {
        this.balance -= amount;
    }

    /**
     * Roll dice biased by current Markov state.
     */
    public int rollBiasedDice() {
        String state = markovChain.getCurrentState();

        // Example: define bias per state
        // Here you can define different biased dice sets per state
        switch (state) {
            case "UNDER_WIN":
                return biasedRollWithShift(-1); // Favor under sums (lower)
            case "UNDER_LOSS":
                return biasedRollWithShift(0);  // Neutral
            case "OVER_WIN":
                return biasedRollWithShift(1);  // Favor over sums (higher)
            case "OVER_LOSS":
                return biasedRollWithShift(0);  // Neutral
            case "NEUTRAL":
            default:
                int[] diceValues = Utils.rollThreeDice();
                return Utils.sumDice(diceValues);   // Fair roll
        }
    }

    /**
     * Roll 3 dice with a shift to bias sum higher (+1) or lower (-1).
     * Shift can be -1, 0, or 1 to nudge sums.
     */
    private int biasedRollWithShift(int shift) {
        int[] diceValues = Utils.rollThreeDice();
        int roll = Utils.sumDice(diceValues) + shift;
        if (roll < 3) roll = 3;
        if (roll > 18) roll = 18;
        return roll;
    }

    /**
     * Update Markov state based on last round outcome.
     * Call this after each round with player's bet and whether player won.
     */
    public void updateState(String playerBet, boolean playerWon) {
        String newState;

        if (playerBet.equalsIgnoreCase("under")) {
            newState = playerWon ? "UNDER_WIN" : "UNDER_LOSS";
        } else {
            newState = playerWon ? "OVER_WIN" : "OVER_LOSS";
        }

        markovChain.setCurrentState(newState);
        markovChain.nextState();
    }

    public int[] getDiceValues() {
        return diceValues;
    }
}