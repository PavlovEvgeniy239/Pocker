import models.PlayerStatistics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TexasHoldemOddsCalculatorGUI extends JFrame {
    private JTextField yourPlayersCount;
    private JTextField yourHandField;
    private JTextField yourBoardField;
    private JTextField yourBankField;
    private JTextField yourBetField;
    private JLabel yourResultLabel;
    private JLabel yourMELabel;
    private JLabel yourZeroMELabel;

    private JTextField playerCountField;
    private JPanel playerFieldsPanel;
    private JTextField[] playerHandFields;
    private JTextField allPlayersBoardField;
    private JLabel[] playerWinLabels;
    private JButton calculateAllWinProbabilitiesButton;

    private JTextField improvementHandField;
    private JTextField improvementBoardField;
    private JTextField improvementBankField;
    private JLabel improvementResultLabel;
    private JCheckBox[] comboCheckBoxes;
    private JLabel improvementBetLabel;
    private JTextField improvementPlayerCountField;


    public TexasHoldemOddsCalculatorGUI() {
        setTitle("Texas Holdem Odds Calculator");
        setSize(600, 800);  // Increased the size to accommodate wider fields
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel yourHandPanel = createYourHandPanel();
        JPanel allPlayersPanel = createAllPlayersPanel();
        JPanel improvementPanel = createImprovementPanel();

        tabbedPane.addTab("Your Hand", yourHandPanel);
        tabbedPane.addTab("All Players", allPlayersPanel);
        tabbedPane.addTab("Improvement", improvementPanel);

        add(tabbedPane);
    }

    private JPanel createYourHandPanel() {
        JPanel yourHandPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add player count field
        yourPlayersCount = new JTextField(30);  // Increased width
        gbc.gridx = 0;
        gbc.gridy = 0;
        yourHandPanel.add(new JLabel("Player count"), gbc);
        gbc.gridy = 1;
        yourHandPanel.add(yourPlayersCount, gbc);

        // Add your hand input field
        yourHandField = new JTextField(30);  // Increased width
        gbc.gridx = 0;
        gbc.gridy = 2;
        yourHandPanel.add(new JLabel("Your Hand (comma-separated):"), gbc);
        gbc.gridy = 3;
        yourHandPanel.add(yourHandField, gbc);

        // Add board input field
        yourBoardField = new JTextField(30);  // Increased width
        gbc.gridx = 0;
        gbc.gridy = 4;
        yourHandPanel.add(new JLabel("Board (comma-separated):"), gbc);
        gbc.gridy = 5;
        yourHandPanel.add(yourBoardField, gbc);

        // Add bank input field
        yourBankField = new JTextField(30);  // Increased width
        gbc.gridx = 0;
        gbc.gridy = 6;
        yourHandPanel.add(new JLabel("Bank (excluding bids):"), gbc);
        gbc.gridy = 7;
        yourHandPanel.add(yourBankField, gbc);

        // Add bank input field
        yourBetField = new JTextField(30);  // Increased width
        gbc.gridx = 0;
        gbc.gridy = 8;
        yourHandPanel.add(new JLabel("Bet:"), gbc);
        gbc.gridy = 9;
        yourHandPanel.add(yourBetField, gbc);

        // Add button to calculate win probability for your hand
        JButton calculateYourWinProbabilityButton = new JButton("Calculate Your Win Probability");
        yourResultLabel = new JLabel();
        gbc.gridy = 10;
        yourHandPanel.add(calculateYourWinProbabilityButton, gbc);
        gbc.gridy = 11;
        yourHandPanel.add(yourResultLabel, gbc);

        yourMELabel = new JLabel();
        gbc.gridy = 12;
        yourHandPanel.add(yourMELabel, gbc);

        calculateYourWinProbabilityButton.addActionListener(e -> {
            String[] yourHand = yourHandField.getText().replace(" ", "").split(",");
            String[] board = new String[0];
            if (!yourBoardField.getText().isEmpty()) {
                board = yourBoardField.getText().replace(" ", "").split(",");
            }
            int playerCount = Integer.parseInt(yourPlayersCount.getText());
            PlayerStatistics playerStatistics = TexasHoldemOddsCalculator.calculateWinProbability(yourHand, board, playerCount);
            yourResultLabel.setText("Your Win Probability: " + playerStatistics.getWinProbability() + " Tie Probability: " + playerStatistics.getTieProbability());

            if (!yourBankField.getText().isEmpty() && !yourBetField.getText().isEmpty()) {
                double ME = TexasHoldemOddsCalculator.calculateME(Double.parseDouble(yourBankField.getText()), Double.parseDouble(yourBetField.getText()), playerCount, playerStatistics);
                yourMELabel.setText("Win ME with the specified bet: " + ME);
            } else {
                yourMELabel.setText("In order to calculate win ME, I need to know the value of bank and the value of bet");
            }
        });

        return yourHandPanel;
    }

    private JPanel createAllPlayersPanel() {
        JPanel allPlayersPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add player count input field
        playerCountField = new JTextField(10);  // Increased width
        gbc.gridx = 0;
        gbc.gridy = 0;
        allPlayersPanel.add(new JLabel("Number of Players:"), gbc);
        gbc.gridy = 1;
        allPlayersPanel.add(playerCountField, gbc);

        // Add button to set player count and generate player input fields
        JButton setPlayerCountButton = new JButton("Set Player Count");
        gbc.gridy = 2;
        allPlayersPanel.add(setPlayerCountButton, gbc);

        // Add panel for player input fields
        playerFieldsPanel = new JPanel(new GridBagLayout());
        gbc.gridy = 3;
        allPlayersPanel.add(playerFieldsPanel, gbc);

        // Add board input field
        allPlayersBoardField = new JTextField(30);  // Increased width
        gbc.gridy = 4;
        allPlayersPanel.add(new JLabel("Board (comma-separated):"), gbc);
        gbc.gridy = 5;
        allPlayersPanel.add(allPlayersBoardField, gbc);

        // Add button to calculate win probabilities for all players
        calculateAllWinProbabilitiesButton = new JButton("Calculate All Win Probabilities");
        gbc.gridy = 6;
        allPlayersPanel.add(calculateAllWinProbabilitiesButton, gbc);

        // Add labels to display win probabilities for all players
        playerWinLabels = new JLabel[10]; // Assuming a maximum of 10 players for simplicity
        for (int i = 0; i < playerWinLabels.length; i++) {
            playerWinLabels[i] = new JLabel();
            gbc.gridy = 7 + i;
            allPlayersPanel.add(playerWinLabels[i], gbc);
        }

        setPlayerCountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int playerCount = Integer.parseInt(playerCountField.getText());
                playerHandFields = new JTextField[playerCount];
                playerFieldsPanel.removeAll();
                GridBagConstraints gbcInner = new GridBagConstraints();
                gbcInner.insets = new Insets(5, 5, 5, 5);
                for (int i = 0; i < playerCount; i++) {
                    playerHandFields[i] = new JTextField(30);  // Increased width
                    gbcInner.gridx = 0;
                    gbcInner.gridy = i * 2;
                    playerFieldsPanel.add(new JLabel("Player " + (i + 1) + " Hand (comma-separated):"), gbcInner);
                    gbcInner.gridy = i * 2 + 1;
                    playerFieldsPanel.add(playerHandFields[i], gbcInner);
                }
                allPlayersPanel.revalidate();
                allPlayersPanel.repaint();
            }
        });

        calculateAllWinProbabilitiesButton.addActionListener(e -> {
            int playerCount = Integer.parseInt(playerCountField.getText());
            String[][] playerHands = new String[playerCount][2];
            for (int i = 0; i < playerCount; i++) {
                playerHands[i][0] = playerHandFields[i].getText().replace(" ", "").split(",")[0];
                playerHands[i][1] = playerHandFields[i].getText().replace(" ", "").split(",")[1];
            }
            String[] board = allPlayersBoardField.getText().replace(" ", "").split(",");
            Map<Integer, PlayerStatistics> probabilities = TexasHoldemOddsCalculator.calculateOdds(playerHands, board, -1);
            for (int i = 0; i < playerCount; i++) {
                playerWinLabels[i].setText("Player " + (i + 1) + " Win Probability: " + probabilities.get(i).getWinProbability() + " Tie Probability: " + probabilities.get(i).getTieProbability());
            }
        });

        return allPlayersPanel;
    }

    private JPanel createImprovementPanel() {
        JPanel improvementPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add improvement hand input field
        improvementHandField = new JTextField(30);  // Increased width
        gbc.gridx = 0;
        gbc.gridy = 0;
        improvementPanel.add(new JLabel("Your Hand (comma-separated):"), gbc);
        gbc.gridy = 1;
        improvementPanel.add(improvementHandField, gbc);

        // Add improvement board input field
        improvementBoardField = new JTextField(30);  // Increased width
        gbc.gridx = 0;
        gbc.gridy = 2;
        improvementPanel.add(new JLabel("Board (comma-separated):"), gbc);
        gbc.gridy = 3;
        improvementPanel.add(improvementBoardField, gbc);

        // Add bank input field
        improvementBankField = new JTextField(30);  // Increased width
        gbc.gridx = 0;
        gbc.gridy = 4;
        improvementPanel.add(new JLabel("Bank:"), gbc);
        gbc.gridy = 5;
        improvementPanel.add(improvementBankField, gbc);

        improvementPlayerCountField = new JTextField(30);  // Increased width
        gbc.gridx = 0;
        gbc.gridy = 6;
        improvementPanel.add(new JLabel("Player Count:"), gbc);
        gbc.gridy = 7;
        improvementPanel.add(improvementPlayerCountField, gbc);

        // Add checkboxes for combinations
        String[] combinations = {"Royal Flush", "Straight Flush", "Four of a Kind", "Full House", "Flush", "Straight", "Three of a Kind", "Two Pair", "One Pair"};
        comboCheckBoxes = new JCheckBox[combinations.length];
        gbc.gridx = 0;
        gbc.gridy = 10;
        JPanel comboPanel = new JPanel(new GridLayout(combinations.length, 1));
        for (int i = 0; i < combinations.length; i++) {
            comboCheckBoxes[i] = new JCheckBox(combinations[i]);
            comboPanel.add(comboCheckBoxes[i]);
        }
        improvementPanel.add(comboPanel, gbc);

        // Add button to calculate improvement probabilities
        JButton calculateImprovementProbabilityButton = new JButton("Calculate Improvement Probabilities");
        improvementResultLabel = new JLabel();
        gbc.gridy = 9;
        improvementPanel.add(calculateImprovementProbabilityButton, gbc);
        gbc.gridy = 11;
        improvementPanel.add(improvementResultLabel, gbc);

        improvementBetLabel = new JLabel();
        gbc.gridy = 12;
        improvementPanel.add(improvementBetLabel, gbc);

        calculateImprovementProbabilityButton.addActionListener(e -> {
            String[] hand = improvementHandField.getText().replace(" ", "").split(",");
            String[] board = new String[0];
            if (!yourBoardField.getText().isEmpty()) {
                board = yourBoardField.getText().replace(" ", "").split(",");
            }
            List<String> selectedCombinations = new ArrayList<>();
            for (JCheckBox checkBox : comboCheckBoxes) {
                if (checkBox.isSelected()) {
                    selectedCombinations.add(checkBox.getText());
                }
            }
            double improvementProbability = TexasHoldemOddsCalculator.calculateImprovementProbability(hand, board, selectedCombinations);
            improvementResultLabel.setText("Improvement probability: " + improvementProbability);
            if (!improvementBankField.getText().isEmpty() && !improvementPlayerCountField.getText().isEmpty()) {
                double maximumBet = TexasHoldemOddsCalculator.calculateMaximumBet(Double.parseDouble(improvementBankField.getText()), improvementProbability, Integer.parseInt(improvementPlayerCountField.getText()));
                improvementBetLabel.setText("Maximum bet: " + maximumBet);
            }
        });

        return improvementPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TexasHoldemOddsCalculatorGUI gui = new TexasHoldemOddsCalculatorGUI();
            gui.setVisible(true);
        });
    }
}
