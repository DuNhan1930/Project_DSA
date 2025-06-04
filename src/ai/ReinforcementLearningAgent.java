package ai;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ReinforcementLearningAgent {
    private final Map<String, Double> qTable = new HashMap<>();

    private final Random random = new Random();
    private String lastState = "start";

    /**
     * Choose action (under/over) based on learned Q-values or explore randomly.
     */
    public String chooseAction() {
        double explorationRate = 0.2;
        if (random.nextDouble() < explorationRate) {
            return random.nextBoolean() ? "under" : "over";
        }

        double qUnder = getQValue(lastState, "under");
        double qOver = getQValue(lastState, "over");
        return (qOver > qUnder) ? "over" : "under";
    }

    /**
     * Update Q-values after observing the result.
     *
     * @param currentState e.g., dice total or round result
     * @param action       "under" or "over"
     * @param reward       +1 for win, -1 for loss
     */
    public void update(String currentState, String action, int reward) {
        String key = stateActionKey(lastState, action);
        double currentQ = qTable.getOrDefault(key, 0.0);

        // Estimate the best future value
        double futureQ = Math.max(getQValue(currentState, "under"), getQValue(currentState, "over"));

        // Q-learning formula
        double learningRate = 0.1;
        double discountFactor = 0.9;
        double updatedQ = currentQ + learningRate * (reward + discountFactor * futureQ - currentQ);
        qTable.put(key, updatedQ);

        // Move to next state
        lastState = currentState;
    }

    private double getQValue(String state, String action) {
        return qTable.getOrDefault(stateActionKey(state, action), 0.0);
    }

    private String stateActionKey(String state, String action) {
        return state + "::" + action;
    }

    public void printPolicy() {
        System.out.println("\n=== Agent's Learned Q-values ===");
        qTable.forEach((key, value) -> System.out.printf("%-20s : %.2f\n", key, value));
        System.out.println("================================\n");
    }

    public void reset() {
        qTable.clear();
        lastState = "start";
    }
}
