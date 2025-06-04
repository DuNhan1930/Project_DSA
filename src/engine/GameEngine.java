package engine;

import java.util.Random;

import ai.MarkovChain;
import ai.ReinforcementLearningAgent;
import model.Player;
import model.House;
import utils.Utils;

public class GameEngine {
    private final House house;
    private final MarkovChain markovChain = new MarkovChain();
    private final ReinforcementLearningAgent rlAgent = new ReinforcementLearningAgent();
    private final Random random = new Random();

    private boolean playerWon;
    private boolean forcedWinActive = false;
    private boolean forcedLossActive;
    private int currentLossStreak = 0, currentWinStreak = 0;
    private int[] diceValues = new int[3];

    public GameEngine(House house) {
        this.house = house;
        forcedLossActive = false;
    }

    /**
     * Plays one round of the game for the given player.
     * @param player The player placing a bet
     * @return The outcome message
     */
    public String playRound(Player player) {
        if (!player.hasEnoughBalance()) {
            return "Insufficient balance to place the bet.";
        }
        String agentAction = rlAgent.chooseAction();
        String mood = markovChain.nextState();

        if (!forcedLossActive && player.getBalance() > 0.7 * house.getBalance()) forcedLossActive = true;
        if (forcedLossActive && player.getBalance() < 0.3 * house.getBalance()) forcedLossActive = false;

        boolean forceLoss = forcedLossActive && random.nextDouble() < 0.7; // 70% chance to force loss
        boolean forceWin = forcedWinActive && random.nextDouble() < 0.5;   // 50% chance to force win

        // Roll the dice
        int diceTotal;
        if (forceLoss) {
            // House needs to win
            do {
                diceValues = Utils.rollThreeDice();
                diceTotal = Utils.sumDice(diceValues);
            } while (checkWin(diceTotal, player.getBetChoice()));
            currentLossStreak++;
            currentWinStreak = 0;
        } else if (forceWin) {
            // Player needs to win
            do {
                diceValues = Utils.rollThreeDice();
                diceTotal = Utils.sumDice(diceValues);
            } while (!checkWin(diceTotal, player.getBetChoice()));
            currentWinStreak++;
            currentLossStreak = 0;
        } else {
            // Use MarkovChain mood and RL agent to bias outcome
            boolean shouldWin;
            if (mood.contains("WIN") && agentAction.equalsIgnoreCase(player.getBetChoice().toLowerCase())) {
                shouldWin = true;
            } else if (mood.contains("LOSS")) {
                shouldWin = false;
            } else {
                shouldWin = random.nextBoolean();
            }

            do {
                diceValues = Utils.rollThreeDice();
                diceTotal = Utils.sumDice(diceValues);
            } while (checkWin(diceTotal, player.getBetChoice()) != shouldWin);

            if (shouldWin) {
                currentWinStreak++;
                currentLossStreak = 0;
            } else {
                currentLossStreak++;
                currentWinStreak = 0;
            }
        }

        // Determine win/loss
        boolean playerWins = checkWin(diceTotal, player.getBetChoice());

        // Update balances
        String resultMessage;
        int reward = playerWins ? 1 : -1;
        if (playerWins) {
            player.winBet();
            house.loseMoney(player.getBetAmount());
            playerWon = true;
            resultMessage = "You win! Dice total: " + diceTotal;
        } else {
            player.loseBet();
            house.winMoney(player.getBetAmount());
            playerWon = false;
            resultMessage = "You lose. Dice total: " + diceTotal;
        }

        house.updateState(player.getBetChoice(), playerWins);

        rlAgent.update(String.valueOf(diceTotal), player.getBetChoice().toLowerCase(), reward);

        return resultMessage;
    }

    private boolean checkWin(int sum, String choice) {
        boolean isOverChoice = choice.equalsIgnoreCase("over");
        boolean isActualOver = sum > 10; // 11-18
        // If player chose "over" and sum is > 10, player wins.
        // If player chose "under" and sum is <= 10, player wins.
        return (isOverChoice && isActualOver) || (!isOverChoice && !isActualOver);
    }

    public int getCurrentLossStreak() {
        return currentLossStreak;
    }

    public void setCurrentLossStreak(int currentLossStreak) {
        this.currentLossStreak = currentLossStreak;
    }

    public int getCurrentWinStreak() {
        return currentWinStreak;
    }

    public void setCurrentWinStreak(int currentWinStreak) {
        this.currentWinStreak = currentWinStreak;
    }

    public int[] getDiceValues() {
        return diceValues;
    }

    public boolean isPlayerWon() {
        return playerWon;
    }
}
