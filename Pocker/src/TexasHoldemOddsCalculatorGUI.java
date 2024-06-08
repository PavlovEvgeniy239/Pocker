import models.PlayerStatistics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class TexasHoldemOddsCalculatorGUI extends JFrame {
    private JTextField yourHandField;
    private JTextField yourBoardField;
    private JLabel yourResultLabel;

    private JTextField playerCountField;
    private JPanel playerFieldsPanel;
    private JTextField[] playerHandFields;
    private JTextField allPlayersBoardField;
    private JLabel[] playerWinLabels;
    private JButton calculateAllWinProbabilitiesButton;

    public TexasHoldemOddsCalculatorGUI() {
        setTitle("Texas Holdem Odds Calculator");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel yourHandPanel = createYourHandPanel();
        JPanel allPlayersPanel = createAllPlayersPanel();

        tabbedPane.addTab("Your Hand", yourHandPanel);
        tabbedPane.addTab("All Players", allPlayersPanel);

        add(tabbedPane);
    }

    private JPanel createYourHandPanel() {
        JPanel yourHandPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add your hand input field
        yourHandField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        yourHandPanel.add(new JLabel("Your Hand (comma-separated):"), gbc);
        gbc.gridy = 1;
        yourHandPanel.add(yourHandField, gbc);

        // Add board input field
        yourBoardField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 2;
        yourHandPanel.add(new JLabel("Board (comma-separated):"), gbc);
        gbc.gridy = 3;
        yourHandPanel.add(yourBoardField, gbc);

        // Add button to calculate win probability for your hand
        JButton calculateYourWinProbabilityButton = new JButton("Calculate Your Win Probability");
        yourResultLabel = new JLabel();
        gbc.gridy = 4;
        yourHandPanel.add(calculateYourWinProbabilityButton, gbc);
        gbc.gridy = 5;
        yourHandPanel.add(yourResultLabel, gbc);

        calculateYourWinProbabilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] yourHand = yourHandField.getText().replace(" ", "").split(",");
                String[] board = yourBoardField.getText().replace(" ", "").split(",");
                double winProbability = TexasHoldemOddsCalculator.calculateWinProbability(yourHand, board);
                yourResultLabel.setText("Your Win Probability: " + winProbability);
            }
        });

        return yourHandPanel;
    }

    private JPanel createAllPlayersPanel() {
        JPanel allPlayersPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add player count input field
        playerCountField = new JTextField(5);
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
        allPlayersBoardField = new JTextField(20);
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
                    playerHandFields[i] = new JTextField(20);
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

        calculateAllWinProbabilitiesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int playerCount = Integer.parseInt(playerCountField.getText());
                String[][] playerHands = new String[playerCount][2];
                for (int i = 0; i < playerCount; i++) {
                    playerHands[i][0] = playerHandFields[i].getText().replace(" ", "").split(",")[0];
                    playerHands[i][1] = playerHandFields[i].getText().replace(" ", "").split(",")[1];
                }
                String[] board = allPlayersBoardField.getText().replace(" ", "").split(",");
                Map<Integer, PlayerStatistics> probabilities = TexasHoldemOddsCalculator.calculateOdds(playerHands, board);
                for (int i = 0; i < playerCount; i++) {
                    playerWinLabels[i].setText("Player " + (i + 1) + " Win Probability: " + probabilities.get(i).getWinProbability() + " Tie Probability: " + probabilities.get(i).getTieProbability());
                }
            }
        });

        return allPlayersPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TexasHoldemOddsCalculatorGUI gui = new TexasHoldemOddsCalculatorGUI();
                gui.setVisible(true);
            }
        });
    }
}
