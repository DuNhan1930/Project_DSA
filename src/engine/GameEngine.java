package engine;

import bias.BiasManager;
import model.Player;
import model.House;

public class GameEngine {
    private final House house;
    private final BiasManager biasManager;
    private int[] diceValues = new int[3];
    private boolean playerWins;
    private String resultMessage;

    public GameEngine(House house) {
        this.house = house;
        this.biasManager = new BiasManager();
    }

    /**
     * Play one round with bias logic.
     */
    public String playRound(Player player) {
        if (!player.hasEnoughBalance()) {
            return "Insufficient balance to place the bet.";
        }

        String playerBet = player.getBetChoice();

        // Use BiasManager to roll biased dice total
        int diceTotal = biasManager.rollBiasedDice(playerBet, player.getInitialBalance(), player.getBalance(), player.getBetAmount());
        diceValues = biasManager.getDiceValues();

        // Check win or lose based on dice total and bet
        playerWins = checkWin(diceTotal, playerBet);

        // Update player/housing balances
        if (playerWins) {
            player.winBet();
            house.loseMoney(player.getBetAmount());
        } else {
            player.loseBet();
            house.winMoney(player.getBetAmount());
        }

        // Update BiasManager streaks
        biasManager.updateStreak(playerWins);
        biasManager.updateLastRoundResult(playerWins);

        // Result message
        return resultMessage = playerWins ? "You win! Dice total: " + diceTotal : "You lose. Dice total: " + diceTotal;
    }

    private boolean checkWin(int sum, String choice) {
        boolean isOverChoice = choice.equalsIgnoreCase("over");
        boolean isActualOver = sum > 10; // 11-18
        return (isOverChoice && isActualOver) || (!isOverChoice && !isActualOver);
    }

    public int[] getDiceValues() {
        return diceValues;
    }

    public boolean isPlayerWon() {
        return playerWins;
    }

    public String getResultMessage() {
        return resultMessage;
    }
}
