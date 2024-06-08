import models.Card;
import models.PlayerStatistics;

import java.util.*;
import java.util.stream.Collectors;

public class TexasHoldemOddsCalculator {

    private static final int SIMULATION_COUNT = 10000;
    private static final String[] RANKS = {
            "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"
    };
    private static final String[] SUITS = {
            "c", "d", "h", "s"
    };
    private static final List<String> DECK = new ArrayList<>();

    private static long MULTIPLIER = (long) Math.pow(10, 13);

    static {
        for (String rank : RANKS) {
            for (String suit : SUITS) {
                DECK.add(rank + suit);
            }
        }
    }

    public static Map<Integer, PlayerStatistics> calculateOdds(String[][] playerHands, String[] board) {
        int playerCount = playerHands.length;
        int[] wins = new int[playerCount];
        int[] ties = new int[playerCount];

        for (int i = 0; i < SIMULATION_COUNT; i++) {
            List<String> deck = new ArrayList<>(DECK);
            for (String[] hand : playerHands) {
                deck.remove(hand[0]);
                deck.remove(hand[1]);
            }
            for (String card : board) {
                if (!card.isEmpty()) {
                    deck.remove(card);
                }
            }
            Collections.shuffle(deck);

            String[] simulatedBoard = new String[5];
            for (int j = 0; j < 5; j++) {
                if (j < board.length && !board[j].isEmpty()) {
                    simulatedBoard[j] = board[j];
                } else {
                    simulatedBoard[j] = deck.remove(deck.size() - 1);
                }
            }


            Map<Integer, Long> playerHandWeights = new HashMap<>();

            for (int p = 0; p < playerHands.length; p++) {
                String[] fullHand = {playerHands[p][0], playerHands[p][1], simulatedBoard[0], simulatedBoard[1], simulatedBoard[2], simulatedBoard[3], simulatedBoard[4]};
                long handValue = evaluateHand(fullHand);
                playerHandWeights.put(p, handValue);
            }

            long max = -1;
            boolean isTie = false;
            for (Map.Entry<Integer, Long> entry : playerHandWeights.entrySet()) {
                if (entry.getValue() > max) {
                    isTie = false;
                    max = entry.getValue();
                } else if (entry.getValue() == max) {
                    isTie = true;
                }
            }

            for (Map.Entry<Integer, Long> entry : playerHandWeights.entrySet()) {
                if (entry.getValue() == max) {
                    if(isTie) {
                        ties[entry.getKey()]++;
                    } else {
                        wins[entry.getKey()]++;
                    }
                }
            }
        }

        Map<Integer, PlayerStatistics> playerStatistics = new HashMap<>();
        for (int i = 0; i < playerCount; i++) {
            playerStatistics.put(i, new PlayerStatistics((double) wins[i] / SIMULATION_COUNT, (double) ties[i] / SIMULATION_COUNT));
        }
        return playerStatistics;
    }


    public static long evaluateHand(String[] hand) {//TODO допилить старшую карту
        List<Card> cards = new ArrayList<>();
        for (String card : hand) {
            cards.add(new Card(card.substring(0, 1), card.substring(1)));
        }

        Collections.sort(cards);

        List<Card> straight = getStraight(cards);


        if (!straight.isEmpty()) {
            if (getOldestFlush(straight) >= 0) {
                return 9 * MULTIPLIER + getOldest(straight).getRank(); // Flush straight (Flush Royal)
            } else {
                return 4 * MULTIPLIER + getOldest(straight).getRank(); // Straight
            }
        } else if (getFourOfAKind(cards) >= 0) {
            return 7 * MULTIPLIER + getFourOfAKind(cards); // Four of a Kind
        } else if (getOldestFullHouse(cards) >= 0) {
            return 6 * MULTIPLIER + getOldestFullHouse(cards); // Full House
        } else if (getOldestFlush(cards) >= 0) {
            return 5 * MULTIPLIER + getOldestFlush(cards); // Flush
        } else if (getThreeOfAKind(cards) >= 0) {
            return 3 * MULTIPLIER + getThreeOfAKind(cards); // Three of a Kind
        } else if (getOldestTwoPair(cards) >= 0) {
            return 2 * MULTIPLIER + getOldestTwoPair(cards); // Two Pair
        } else if (getOnePair(cards) >= 0) {
            return MULTIPLIER + getOnePair(cards); // One Pair
        } else {
            return getOldestCombination(cards); // High Card
        }
    }

    private static Card getOldest(List<Card> cards) {
        return Collections.max(cards);
    }

    private static int  getOldestCombination(List<Card> cards) {
        int result = 0;
        for (Card card : cards) {
            result += (int) Math.pow(10, card.getRank());
        }
        return result;
    }

    private static List<Card> getStraight(List<Card> cards) {
        int start = 0;
        for (int i = 1; i < cards.size(); i++) {
            if (cards.get(i - 1).getRank() != cards.get(i).getRank() - 1) {
                start = i;
            }
            if (i - start == 5) {
                return cards.subList(start, i);
            }
        }
        return Collections.emptyList();
    }


    private static int getOldestFlush(List<Card> cards) {
        Map<Integer, List<Card>> suitCounts = cards.stream().collect(Collectors.groupingBy(Card::getSuit));
        for (Map.Entry<Integer, List<Card>> entry : suitCounts.entrySet()) {
            if (entry.getValue().size() >= 5) {
                return Collections.max(entry.getValue()).getRank();
            }
        }
        return -1;
    }

    private static int getFourOfAKind(List<Card> cards) {
        return getOldestOfNOfAKind(cards, 4);
    }

    private static int getOldestFullHouse(List<Card> cards) {
        Map<Integer, Long> map = cards.stream()
                .collect(Collectors.groupingBy(Card::getRank, Collectors.counting()));
        int oldestCard = -1;
        boolean haveThree = false;
        boolean havePair = false;
        for (Map.Entry<Integer, Long> entry : map.entrySet()) {
            if (entry.getValue() >= 3) {
                if (!haveThree) {
                    haveThree = true;
                    oldestCard = entry.getKey();
                } else {
                    havePair = true;
                    oldestCard = Math.max(oldestCard, entry.getKey());
                }
            } else if (entry.getValue() >= 2){
                havePair = true;
            }
        }
        return havePair && haveThree ? oldestCard : -1;
    }

    private static int getOldestTwoPair(List<Card> cards) {
        Map<Integer, Long> map = cards.stream()
                .collect(Collectors.groupingBy(Card::getRank, Collectors.counting()));
        int oldestCard = -1;
        int pairCount = 0;
        for (Map.Entry<Integer, Long> entry : map.entrySet()) {
            if (entry.getValue() >= 2) {
                pairCount++;
                oldestCard = Math.max(oldestCard, entry.getKey());
            }
        }
        return pairCount >= 2 ? oldestCard : -1;
    }

    private static int getThreeOfAKind(List<Card> cards) {
        return getOldestOfNOfAKind(cards, 3);
    }

    private static int getOldestOfNOfAKind(List<Card> cards, int n) {
        Map<Integer, Long> suitCounts = cards.stream()
                .collect(Collectors.groupingBy(Card::getRank, Collectors.counting()));
        for (Map.Entry<Integer, Long> e : suitCounts.entrySet()) {
            if (e.getValue() >= n) {
                return e.getKey();
            }
        }
        return -1;
    }


    private static int getOnePair(List<Card> cards) {
        return getOldestOfNOfAKind(cards, 2);
    }

    private static int handValue(List<String> ranks) {
        int value = 0;
        for (int i = 0; i < ranks.size(); i++) {
            value += (int) (Arrays.asList(RANKS).indexOf(ranks.get(i)) * Math.pow(15, i));
        }
        return value;
    }

    public static double calculateWinProbability(String[] yourHand, String[] board) {
        int wins = 0;
        int losses = 0;
        int ties = 0;

        List<String> deck = new ArrayList<>(DECK);
        for (String card : yourHand) {
            deck.remove(card);
        }
        for (String card : board) {
            if (!card.isEmpty()) {
                deck.remove(card);
            }
        }

        for (int i = 0; i < SIMULATION_COUNT; i++) {
            List<String> shuffledDeck = new ArrayList<>(deck);
            Collections.shuffle(shuffledDeck);

            String[] opponentHand = {shuffledDeck.remove(0), shuffledDeck.remove(0)};
            String[] opponentBoard = new String[5];
            for (int j = 0; j < 5; j++) {
                if (j < board.length && !board[j].isEmpty()) {
                    opponentBoard[j] = board[j];
                } else {
                    opponentBoard[j] = shuffledDeck.remove(0);
                }
            }

            long yourHandValue = evaluateHand(concatenateArrays(yourHand, board));
            long opponentHandValue = evaluateHand(concatenateArrays(opponentHand, opponentBoard));

            if (yourHandValue > opponentHandValue) {
                wins++;
            } else if (yourHandValue < opponentHandValue) {
                losses++;
            } else {
                ties++;
            }
        }

        return (double) wins / SIMULATION_COUNT;
    }

    private static String[] concatenateArrays(String[] arr1, String[] arr2) {
        String[] result = new String[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, result, 0, arr1.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
        return result;
    }

}
