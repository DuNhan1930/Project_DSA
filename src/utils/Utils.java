package utils;

import java.util.Random;

public class Utils {
    private static final Random random = new Random();

    /**
     * Trả về true với xác suất cho trước (0.0 đến 1.0).
     */
    public static boolean randomChance(double probability) {
        return Math.random() < probability;
    }

    /**
     * Trả về số ngẫu nhiên trong khoảng [min, max].
     */
    public static int randomBetween(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * Tung một xúc xắc (công bằng) từ 1–6.
     */
    public static int rollDice() {
        return random.nextInt(6) + 1;
    }

    /**
     * Tung 3 xúc xắc công bằng và trả về mảng 3 giá trị.
     */
    public static int[] rollThreeDice() {
        int[] diceValues = new int[3];
        diceValues[0] = rollDice();
        diceValues[1] = rollDice();
        diceValues[2] = rollDice();
        return diceValues;
    }

    /**
     * Tính tổng của 3 xúc xắc.
     */
    public static int sumDice(int[] diceValues) {
        int sum = 0;
        for (int value : diceValues) {
            sum += value;
        }
        return sum;
    }

    /**
     * Định dạng số thành kiểu tiền tệ (2 số thập phân).
     */
    public static String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }

    /**
     * Sinh ra mảng 3 xúc xắc có tổng là targetSum (tự động điều chỉnh hợp lệ).
     */
    public static int[] generateDiceSum(int targetSum) {
        int[] dice = new int[3];
        do {
            dice[0] = rollDice();
            dice[1] = rollDice();
            dice[2] = targetSum - dice[0] - dice[1];
        } while (dice[2] < 1 || dice[2] > 6);
        return dice;
    }
}
