package models;

public class PlayerStatistics {
    private double winProbability;

    private double tieProbability;

    public PlayerStatistics(double winProbability, double tieProbability) {
        this.winProbability = winProbability;
        this.tieProbability = tieProbability;
    }

    public double getWinProbability() {
        return winProbability;
    }

    public double getTieProbability() {
        return tieProbability;
    }
}
