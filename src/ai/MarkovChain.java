package ai;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MarkovChain {
    private final Map<String, Map<String, Double>> transitionMatrix;
    private String currentState;
    private final Random random = new Random();

    public MarkovChain() {
        currentState = "NEUTRAL";
        transitionMatrix = new HashMap<>();

        Map<String, Double> neutral = new HashMap<>();
        neutral.put("UNDER_WIN", 0.25);
        neutral.put("UNDER_LOSS", 0.25);
        neutral.put("OVER_WIN", 0.25);
        neutral.put("OVER_LOSS", 0.20);
        neutral.put("NEUTRAL", 0.05);
        transitionMatrix.put("NEUTRAL", neutral);

        Map<String, Double> underWin = new HashMap<>();
        underWin.put("UNDER_WIN", 0.4);
        underWin.put("UNDER_LOSS", 0.3);
        underWin.put("OVER_WIN", 0.1);
        underWin.put("OVER_LOSS", 0.15);
        underWin.put("NEUTRAL", 0.05);
        transitionMatrix.put("UNDER_WIN", underWin);

        Map<String, Double> underLoss = new HashMap<>();
        underLoss.put("UNDER_WIN", 0.3);
        underLoss.put("UNDER_LOSS", 0.4);
        underLoss.put("OVER_WIN", 0.15);
        underLoss.put("OVER_LOSS", 0.1);
        underLoss.put("NEUTRAL", 0.05);
        transitionMatrix.put("UNDER_LOSS", underLoss);

        Map<String, Double> overWin = new HashMap<>();
        overWin.put("UNDER_WIN", 0.1);
        overWin.put("UNDER_LOSS", 0.15);
        overWin.put("OVER_WIN", 0.4);
        overWin.put("OVER_LOSS", 0.3);
        overWin.put("NEUTRAL", 0.05);
        transitionMatrix.put("OVER_WIN", overWin);

        Map<String, Double> overLoss = new HashMap<>();
        overLoss.put("UNDER_WIN", 0.15);
        overLoss.put("UNDER_LOSS", 0.1);
        overLoss.put("OVER_WIN", 0.3);
        overLoss.put("OVER_LOSS", 0.4);
        overLoss.put("NEUTRAL", 0.05);
        transitionMatrix.put("OVER_LOSS", overLoss);
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String state) {
        if (transitionMatrix.containsKey(state)) {
            this.currentState = state;
        }
    }

    /**
     * Samples the next state based on transition probabilities.
     */
    public String nextState() {
        Map<String, Double> transitions = transitionMatrix.get(currentState);
        double p = random.nextDouble();
        double cumulative = 0.0;

        for (Map.Entry<String, Double> entry : transitions.entrySet()) {
            cumulative += entry.getValue();
            if (p <= cumulative) {
                currentState = entry.getKey();
                return currentState;
            }
        }

        // fallback, should not happen if probabilities sum to 1
        return currentState;
    }
}
