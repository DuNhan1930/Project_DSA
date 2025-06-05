package model;

public class House {
    private double balance;

    public House(double initialBalance, boolean biasEnabled) {
        this.balance = initialBalance;
    }

    public double getBalance() {
        return balance;
    }

    public void winMoney(double amount) {
        this.balance += amount;
    }

    public void loseMoney(double amount) {
        this.balance -= amount;
    }
}