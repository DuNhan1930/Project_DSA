package model;

public class Player {
    private final String name;
    private double balance;
    private double betAmount;
    private String betChoice; // "under" or "over"

    public Player(String name, double initialBalance) {
        this.name = name;
        this.balance = initialBalance;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public double getBetAmount() {
        return betAmount;
    }

    public String getBetChoice() {
        return betChoice;
    }

    // Setters
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setBetAmount(double betAmount) {
        this.betAmount = betAmount;
    }

    public void setBetChoice(String betChoice) {
        this.betChoice = betChoice;
    }

    public boolean hasEnoughBalance() {
        return balance >= betAmount;
    }

    public void winBet() {
        balance += betAmount;
    }

    public void loseBet() {
        balance -= betAmount;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
