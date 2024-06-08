package models;

import java.util.Arrays;

public class Card implements Comparable<Card> {
    private static final String[] RANKS = {
            "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"
    };
    private static final String[] SUITS = {
            "c", "d", "h", "s"
    };

    private final int rank;
    private final int suit;

    public Card(String rank, String suit) {
        this.rank = Arrays.asList(RANKS).indexOf(rank);
        this.suit = Arrays.asList(SUITS).indexOf(suit);
    }

    public int getRank() {
        return rank;
    }

    public int getSuit() {
        return suit;
    }

    @Override
    public int compareTo(Card o) {
        return rank - o.rank;
    }
}