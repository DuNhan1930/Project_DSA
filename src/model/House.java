package model;

public class House {
    private double balance;
    private boolean biasEnabled;

    public House(double initialBalance, boolean biasEnabled) {
        this.balance = initialBalance;
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
}