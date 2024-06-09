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


    public static Map<Integer, PlayerStatistics> calculateOdds(String[][] playerHands, String[] board, int simulationCount) {
        if (simulationCount == -1) {
            simulationCount = SIMULATION_COUNT;
        }
        int playerCount = playerHands.length;
        int[] wins = new int[playerCount];
        int[] ties = new int[playerCount];
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

        for (int i = 0; i < simulationCount; i++) {
            List<String> shuffledDeck = new ArrayList<>(deck);
            Collections.shuffle(shuffledDeck);

            String[] simulatedBoard = new String[5];
            for (int j = 0; j < 5; j++) {
                if (j < board.length && !board[j].isEmpty()) {
                    simulatedBoard[j] = board[j];
                } else {
                    simulatedBoard[j] = shuffledDeck.remove(shuffledDeck.size() - 1);
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
                    if (isTie) {
                        ties[entry.getKey()]++;
                    } else {
                        wins[entry.getKey()]++;
                    }
                }
            }
        }

        Map<Integer, PlayerStatistics> playerStatistics = new HashMap<>();
        for (int i = 0; i < playerCount; i++) {
            playerStatistics.put(i, new PlayerStatistics((double) wins[i] / simulationCount, (double) ties[i] / simulationCount, wins[i], ties[i]));
        }
        return playerStatistics;
    }

    public static PlayerStatistics calculateWinProbability(String[] yourHand, String[] board, int playerCount) {
        int wins = 0;
        int ties = 0;

        String[][] playerHands = new String[playerCount][2];
        playerHands[0] = yourHand;

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

            for (int j = 1; j < playerCount; j++) {
                playerHands[j][0] = shuffledDeck.remove(0);
                playerHands[j][1] = shuffledDeck.remove(0);
            }

            Map<Integer, PlayerStatistics> playerStatistics = calculateOdds(playerHands, board, 1);
            wins += playerStatistics.get(0).getWins();
            ties += playerStatistics.get(0).getTies();
        }

        return new PlayerStatistics(wins / (double) SIMULATION_COUNT, ties / (double) SIMULATION_COUNT, wins, ties);
    }

    public static long evaluateHand(String[] hand) {
        List<Card> cards = new ArrayList<>();
        for (String card : hand) {
            cards.add(new Card(card.substring(0, 1), card.substring(1)));
        }

        Collections.sort(cards);

        List<Card> straight = getStraight(cards);


        if (!straight.isEmpty()) {
            if (getOldestFlush(straight) >= 0) {
                return 8 * 15 * MULTIPLIER + MULTIPLIER * (getOldest(straight).getRank() + 1) + getOldestCombination(cards); // Flush straight (Flush Royal)
            } else {
                return 4 * 15 * MULTIPLIER + MULTIPLIER * (getOldest(straight).getRank() + 1) + getOldestCombination(cards); // Straight
            }
        } else if (getFourOfAKind(cards) >= 0) {
            return 7 * 15 * MULTIPLIER + MULTIPLIER * (getFourOfAKind(cards) + 1) + getOldestCombination(cards); // Four of a Kind
        } else if (getOldestFullHouse(cards) >= 0) {
            return 6 * 15 * MULTIPLIER + MULTIPLIER * (getOldestFullHouse(cards) + 1) + getOldestCombination(cards); // Full House
        } else if (getOldestFlush(cards) >= 0) {
            return 5 * 15 * MULTIPLIER + MULTIPLIER * (getOldestFlush(cards) + 1) + getOldestCombination(cards); // Flush
        } else if (getThreeOfAKind(cards) >= 0) {
            return 3 * 15 * MULTIPLIER + MULTIPLIER * (getThreeOfAKind(cards) + 1) + getOldestCombination(cards); // Three of a Kind
        } else if (getTwoPair(cards) >= 0) {
            return 2 * 15 * MULTIPLIER + MULTIPLIER * (getTwoPair(cards) + 1) + getOldestCombination(cards); // Two Pair
        } else if (getOnePair(cards) >= 0) {
            return 15 * MULTIPLIER + MULTIPLIER * (getOnePair(cards) + 1) + getOldestCombination(cards); // One Pair
        } else {
            return getOldestCombination(cards); // High Card
        }
    }

    private static Card getOldest(List<Card> cards) {
        return Collections.max(cards);
    }

    private static int getOldestCombination(List<Card> cards) {
        int result = 0;
        for (int i = 0; i < 5; i++) {
            result += (int) Math.pow(10, cards.get(i).getRank());
        }
        return result;
    }

    private static List<Card> getStraight(List<Card> cards) {
        Collections.sort(cards);
        int sLen = 0;
        int pointer = 1;
        int finish = -1;
        for (int i = 0; i < cards.size() * 2; i++) {
            int prev = pointer - 1 >= 0 ? pointer - 1 : cards.size() - 1;

            if (cards.get(prev).getRank() == cards.get(pointer).getRank() - 1 || cards.get(prev).getRank() == 12 && cards.get(pointer).getRank() == 0) {
                sLen++;
            } else if (cards.get(prev).getRank() != cards.get(pointer).getRank()) {
                sLen = 0;
            }
            if (sLen >= 4) {
                if (finish == -1 || cards.get(pointer).getRank() < cards.get(finish).getRank()) {
                    finish = pointer;
                }
            }
            pointer = pointer + 1 >= cards.size() ? 0 : pointer + 1;
        }
        if (finish == -1) {
            return Collections.emptyList();
        } else {
            List<Card> straight = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                straight.add(cards.get(finish));
                finish = finish - 1 < 0 ? cards.size() - 1 : finish - 1;
            }
            return straight;
        }
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
            } else if (entry.getValue() >= 2) {
                havePair = true;
            }
        }
        return havePair && haveThree ? oldestCard : -1;
    }

    private static int getTwoPair(List<Card> cards) {
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


    public static double calculateImprovementProbability(String[] hand, String[] board, List<String> combinations) {
        List<String> deck = new ArrayList<>(DECK);
        for (String card : hand) {
            deck.remove(card);
        }
        for (String card : board) {
            deck.remove(card);
        }
        List<String> currentHand = new ArrayList<>(Arrays.asList(hand));
        currentHand.addAll(Arrays.asList(board));
        List<Card> currentCards = new ArrayList<>(currentHand.stream().map(s -> new Card(s.substring(0, 1), s.substring(1))).toList());
        int outs = 0;
        for (int i = 0; i < deck.size(); i++) {
            Card addedCard = new Card(deck.get(i).substring(0, 1), deck.get(i).substring(1));
            currentCards.add(addedCard);
            Collections.sort(currentCards);
            for (String combination : combinations) {
                int lastOuts = outs;
                outs += switch (combination) {
                    case "One Pair" -> getOnePair(currentCards) >= 0 ? 1 : 0;
                    case "Two Pair" -> getTwoPair(currentCards) >= 0 ? 1 : 0;
                    case "Three of a Kind" -> getThreeOfAKind(currentCards) >= 0 ? 1 : 0;
                    case "Straight" -> getStraight(currentCards).isEmpty() ? 0 : 1;
                    case "Flush" -> getOldestFlush(currentCards) >= 0 ? 1 : 0;
                    case "Full House" -> getOldestFullHouse(currentCards) >= 0 ? 1 : 0;
                    case "Four of a Kind" -> getFourOfAKind(currentCards) >= 0 ? 1 : 0;
                    case "Straight Flush" -> {
                        List<Card> straight = getStraight(currentCards);
                        if (straight.isEmpty()) {
                            yield 0;
                        }
                        yield getOldestFlush(straight) >= 0 ? 1 : 0;
                    }
                    case "Royal Flush" -> {
                        List<Card> straight = getStraight(currentCards);
                        if (straight.isEmpty()) {
                            yield 0;
                        }
                        yield getOldestFlush(straight) == 12 ? 1 : 0;
                    }
                    default -> 0;
                };
                if (lastOuts != outs) {
                    break;
                }
            }
            currentCards.remove(addedCard);
        }
        return outs / (double) deck.size();
    }

    public static double calculateME(double bank, double bet, int playerCount, PlayerStatistics playerStatistics) {
        double win = playerStatistics.getWinProbability();
        double tie = playerStatistics.getTieProbability();
        return (win + tie / 2) * (bank + bet * (playerCount - 1)) - (1 - tie - win) * bet;
    }

    public static double calculateMaximumBet(double bank, double improvementProbability, int playerCount) {
        return (bank * improvementProbability) / (1 - (playerCount - 1) * improvementProbability);
    }
}
