package engine;

import java.util.Random;

import ai.BiasManager;
import model.Player;
import model.House;
import utils.Utils;

public class GameEngine {
    private final House house;
    private final BiasManager biasManager;
    private final int[] diceValues = new int[3];
    private boolean playerWins;

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
        int diceTotal = biasManager.rollBiasedDice(playerBet);

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

        // Update BiasManager streaks (bias manager sẽ cập nhật trạng thái MarkovChain trong đó)
        biasManager.updateStreak(playerWins);

        // Cập nhật trạng thái MarkovChain trong House nếu cần (giữ hay bỏ)
        house.updateState(playerBet, playerWins);

        // Lưu kết quả diceValues nếu cần (hiện chỉ lưu tổng thôi)
        // diceValues = ... (nếu bạn muốn roll từng viên theo cách bias hơn)

        // Thông báo kết quả
        return playerWins ? "You win! Dice total: " + diceTotal : "You lose. Dice total: " + diceTotal;
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
}
