package bias;

import java.util.*;
import utils.Utils;

public class BiasManager {
    private int roundsPlayed = 0;
    private int winStreak = 0;
    private int lossStreak = 0;
    private int[] diceValues = new int[3];

    private final LinkedList<Boolean> recentOvers = new LinkedList<>();
    private final int memorySize = 6;

    // Track player betting behavior
    private final LinkedList<String> recentChoices = new LinkedList<>();
    private final LinkedList<Double> recentBets = new LinkedList<>();
    private final LinkedList<Boolean> recentWins = new LinkedList<>();

    private static final int MAX_HISTORY = 5;
    private int maxConsecutiveBiasLosses = 0;
    private static final int MAX_BIAS_LOSSES = 4;

    public void updateStreak(boolean playerWon) {
        roundsPlayed++;
        if (playerWon) {
            winStreak++;
            lossStreak = 0;
            maxConsecutiveBiasLosses = 0;
        } else {
            lossStreak++;
            winStreak = 0;
        }
    }

    public int rollBiasedDice(String playerBet, double initBalance, double balance, double betAmount) {
        roundsPlayed++;

        updateBehaviorHistory(playerBet, betAmount);

        boolean doBias = shouldBias(playerBet, initBalance, balance, betAmount);

        if (maxConsecutiveBiasLosses >= MAX_BIAS_LOSSES) {
            doBias = false;
            maxConsecutiveBiasLosses = 0;
        }

        boolean avoidPattern = isOverRepeatedTooMuch();

        if (betAmount <= 0.1 * balance && Utils.randomChance(0.25)) {
            useNaturalWin(playerBet);
            return Utils.sumDice(diceValues);
        }

        if (doBias && !avoidPattern) {
            if (Utils.randomChance(0.8)) {
                forceLoss(playerBet);
            } else {
                int biasedSum = playerBet.equalsIgnoreCase("over") ? Utils.randomBetween(6, 10) : Utils.randomBetween(11, 14);
                diceValues = Utils.generateDiceSum(biasedSum);
                recordOverResult(biasedSum > 10);
                maxConsecutiveBiasLosses++;
            }
        } else {
            diceValues = Utils.rollThreeDice();
            recordOverResult(Utils.sumDice(diceValues) > 10);
        }

        return Utils.sumDice(diceValues);
    }

    private boolean shouldBias(String playerBet, double initBalance, double balance, double betAmount) {
        double profit = balance / initBalance;
        double timeBiasRate = Math.min(0.3, roundsPlayed * 0.01);
        double ratioBet = betAmount / balance;

        boolean isMartingale = isIncreasingBetAfterLoss();
        boolean isRepeatingChoice = isChoiceBiased(playerBet);

        if (profit >= 2) return true;
        if (isMartingale) return true;
        if (isRepeatingChoice && Utils.randomChance(0.7)) return true;
        if (lossStreak > 3 || winStreak > 3 || ratioBet > 0.4) return true;
        return Utils.randomChance(timeBiasRate);
    }

    private boolean isIncreasingBetAfterLoss() {
        if (recentBets.size() < 2 || recentWins.size() < 2) return false;
        double last = recentBets.getLast();
        double prev = recentBets.get(recentBets.size() - 2);
        boolean lastLost = !recentWins.get(recentWins.size() - 2);
        return last > prev && lastLost;
    }

    private boolean isChoiceBiased(String current) {
        long count = recentChoices.stream().filter(choice -> choice.equalsIgnoreCase(current)).count();
        return count >= MAX_HISTORY - 1;
    }

    private void updateBehaviorHistory(String choice, double betAmount) {
        if (recentChoices.size() >= MAX_HISTORY) recentChoices.removeFirst();
        if (recentBets.size() >= MAX_HISTORY) recentBets.removeFirst();
        recentChoices.add(choice);
        recentBets.add(betAmount);
    }

    public void updateLastRoundResult(boolean playerWon) {
        if (recentWins.size() >= MAX_HISTORY) recentWins.removeFirst();
        recentWins.add(playerWon);
    }

    private void forceLoss(String playerBet) {
        int biasedSum = playerBet.equalsIgnoreCase("over") ? Utils.randomBetween(3, 10) : Utils.randomBetween(11, 18);
        diceValues = Utils.generateDiceSum(biasedSum);
        recordOverResult(biasedSum > 10);
        maxConsecutiveBiasLosses++;
    }

    private void useNaturalWin(String playerBet) {
        int biasedSum = playerBet.equalsIgnoreCase("over") ? Utils.randomBetween(11, 18) : Utils.randomBetween(3, 10);
        diceValues = Utils.generateDiceSum(biasedSum);
        recordOverResult(biasedSum > 10);
    }

    private void recordOverResult(boolean isOver) {
        if (recentOvers.size() >= memorySize) recentOvers.removeFirst();
        recentOvers.add(isOver);
    }

    private boolean isOverRepeatedTooMuch() {
        long count = recentOvers.stream().filter(b -> b).count();
        return count >= memorySize - 1 || count <= 1;
    }

    public int[] getDiceValues() {
        return diceValues;
    }

    public void setDiceValues(int[] diceValues) {
        this.diceValues = diceValues;
    }

    public void reset() {
        roundsPlayed = 0;
        winStreak = 0;
        lossStreak = 0;
        maxConsecutiveBiasLosses = 0;
        recentChoices.clear();
        recentBets.clear();
        recentWins.clear();
        recentOvers.clear();
    }

}