package models;

public class PlayerStatistics {
    private double winProbability;

    private double tieProbability;

    private int wins;

    private int ties;

    public PlayerStatistics(double winProbability, double tieProbability, int wins, int ties) {
        this.winProbability = winProbability;
        this.tieProbability = tieProbability;
        this.wins = wins;
        this.ties = ties;
    }

    public int getWins() {
        return wins;
    }

    public int getTies() {
        return ties;
    }

    public double getWinProbability() {
        return winProbability;
    }

    public double getTieProbability() {
        return tieProbability;
    }
}
