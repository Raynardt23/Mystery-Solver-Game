package com.OblivionNexusDetective.gui;

import com.OblivionNexusDetective.game.GameState;
import com.OblivionNexusDetective.game.GameLogic;
import com.OblivionNexusDetective.model.Suspect;
import com.OblivionNexusDetective.model.Clue;
import com.OblivionNexusDetective.model.Difficulty;
import com.OblivionNexusDetective.data.DatabaseHandler; // Changed from DatabaseConnection
import com.OblivionNexusDetective.data.FileHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class MainWindow extends JFrame {
    private JTextArea textArea;
    private JButton startCaseButton;
    private JButton viewCluesButton;
    private JButton questionSuspectsButton;
    private JButton makeAccusationButton;
    private JButton exitButton;
    private JButton saveNotesButton;
    private JTextArea notesArea;
    
    public MainWindow() {
        super("Java Detective");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Initialize database - FIXED: Use DatabaseHandler instead of DatabaseConnection
        DatabaseHandler.initializeDatabase();
        
        // Create UI components
        createUI();
        
        // Load initial case description
        loadCaseDescription();
    }
    
    private void createUI() {
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Text area for displaying story and information
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Notes area for player to record observations
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBorder(BorderFactory.createTitledBorder("Investigation Notes"));
        notesArea = new JTextArea();
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesPanel.add(notesScroll, BorderLayout.CENTER);
        
        saveNotesButton = new JButton("Save Notes");
        saveNotesButton.addActionListener(e -> FileHandler.saveNotes(notesArea.getText()));
        notesPanel.add(saveNotesButton, BorderLayout.SOUTH);
        
        mainPanel.add(notesPanel, BorderLayout.EAST);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        
        startCaseButton = new JButton("Start Case");
        viewCluesButton = new JButton("View Clues");
        questionSuspectsButton = new JButton("Question Suspects");
        makeAccusationButton = new JButton("Make Accusation");
        exitButton = new JButton("Exit");
        
        // Disable buttons until case is started
        viewCluesButton.setEnabled(false);
        questionSuspectsButton.setEnabled(false);
        makeAccusationButton.setEnabled(false);
        
        // Add action listeners
        startCaseButton.addActionListener(e -> startCase());
        viewCluesButton.addActionListener(e -> viewClues());
        questionSuspectsButton.addActionListener(e -> questionSuspects());
        makeAccusationButton.addActionListener(e -> makeAccusation());
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(startCaseButton);
        buttonPanel.add(viewCluesButton);
        buttonPanel.add(questionSuspectsButton);
        buttonPanel.add(makeAccusationButton);
        buttonPanel.add(exitButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadCaseDescription() {
        String caseText = FileHandler.readFile("case1.txt");
        textArea.setText("WELCOME TO JAVA DETECTIVE\n\n");
        textArea.append("Case File: Oblivion Nexus Mystery\n\n");
        textArea.append(caseText);
    }
    
    private void startCase() {
        // Get player name
        String playerName = JOptionPane.showInputDialog(this, "Enter your name:", "Detective Identification", JOptionPane.PLAIN_MESSAGE);
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Detective";
        }
        
        // Select difficulty
        Difficulty[] difficulties = Difficulty.values();
        Difficulty selectedDifficulty = (Difficulty) JOptionPane.showInputDialog(
            this,
            "Select case difficulty:",
            "Difficulty Selection",
            JOptionPane.QUESTION_MESSAGE,
            null,
            difficulties,
            difficulties[0]
        );
        
        if (selectedDifficulty == null) {
            selectedDifficulty = Difficulty.MEDIUM;
        }
        
        // Initialize game state
        GameState.getInstance().initializeGame(playerName, selectedDifficulty);
        
        // Enable game buttons
        viewCluesButton.setEnabled(true);
        questionSuspectsButton.setEnabled(true);
        makeAccusationButton.setEnabled(true);
        startCaseButton.setEnabled(false);
        
        textArea.setText("Case started by: " + playerName + "\n");
        textArea.append("Difficulty: " + selectedDifficulty + "\n\n");
        textArea.append("You arrive at the Oblivion Nexus research facility. The air is thick with tension.\n");
        textArea.append("A scientist has disappeared under mysterious circumstances, and it's up to you to find out what happened.\n\n");
        textArea.append("Gather clues, question suspects, and when you're ready, make your accusation.\n");
        textArea.append("But be careful - wrong accusations have consequences in this high-stakes environment.");
    }
    
    private void viewClues() {
        List<Clue> clues = DatabaseHandler.getClues();
        GameState gameState = GameState.getInstance();
        
        JDialog cluesDialog = new JDialog(this, "Discovered Clues", true);
        cluesDialog.setSize(500, 400);
        cluesDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea cluesArea = new JTextArea();
        cluesArea.setEditable(false);
        cluesArea.setLineWrap(true);
        cluesArea.setWrapStyleWord(true);
        
        StringBuilder cluesText = new StringBuilder("CLUES DISCOVERED:\n\n");
        
        if (gameState.getFoundClues().isEmpty()) {
            cluesText.append("You haven't discovered any clues yet.\n");
            cluesText.append("Explore the facility and investigate to find clues.");
        } else {
            for (Clue clue : gameState.getFoundClues()) {
                cluesText.append("• ").append(clue.getDescription()).append("\n");
            }
        }
        
        cluesArea.setText(cluesText.toString());
        panel.add(new JScrollPane(cluesArea), BorderLayout.CENTER);
        
        JButton discoverButton = new JButton("Discover New Clue");
        discoverButton.addActionListener(e -> {
            discoverClueDialog(clues, gameState);
            cluesDialog.dispose();
        });
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> cluesDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(discoverButton);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        cluesDialog.add(panel);
        cluesDialog.setVisible(true);
    }
    
    private void discoverClueDialog(List<Clue> clues, GameState gameState) {
        // Find clues that haven't been discovered yet
        List<Clue> undiscoveredClues = new ArrayList<>();
        for (Clue clue : clues) {
            if (!gameState.getFoundClues().contains(clue)) {
                undiscoveredClues.add(clue);
            }
        }
        
        if (undiscoveredClues.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You've discovered all available clues!", "No More Clues", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Randomly select a clue to discover
        Clue clueToDiscover = undiscoveredClues.get((int) (Math.random() * undiscoveredClues.size()));
        String clueInfo = GameLogic.discoverClue(clueToDiscover);
        
        textArea.append("\n\nNEW CLUE DISCOVERED:\n");
        textArea.append("• " + clueToDiscover.getDescription() + "\n");
        textArea.append("Additional information: " + clueInfo + "\n");
        
        JOptionPane.showMessageDialog(this, 
            "You discovered a new clue:\n\n" + 
            clueToDiscover.getDescription() + "\n\n" +
            "Additional information: " + clueInfo, 
            "Clue Discovered", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void questionSuspects() {
        List<Suspect> suspects = DatabaseHandler.getSuspects();
        GameState gameState = GameState.getInstance();
        
        JDialog suspectsDialog = new JDialog(this, "Question Suspects", true);
        suspectsDialog.setSize(500, 400);
        suspectsDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Suspect suspect : suspects) {
            String questioned = gameState.getQuestionedSuspects().containsKey(suspect) ? " (Questioned)" : "";
            listModel.addElement(suspect.getName() + " - " + suspect.getRole() + questioned);
        }
        
        JList<String> suspectsList = new JList<>(listModel);
        panel.add(new JScrollPane(suspectsList), BorderLayout.CENTER);
        
        JButton questionButton = new JButton("Question Selected Suspect");
        questionButton.addActionListener(e -> {
            int selectedIndex = suspectsList.getSelectedIndex();
            if (selectedIndex >= 0) {
                Suspect selectedSuspect = suspects.get(selectedIndex);
                String response = GameLogic.questionSuspect(selectedSuspect);
                
                textArea.append("\n\nQUESTIONING: " + selectedSuspect.getName() + "\n");
                textArea.append("Response: " + response + "\n");
                
                JOptionPane.showMessageDialog(suspectsDialog, 
                    selectedSuspect.getName() + " says:\n\n" + response, 
                    "Suspect Response", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Update list to show this suspect has been questioned
                String questioned = " (Questioned)";
                if (!listModel.getElementAt(selectedIndex).endsWith(questioned)) {
                    listModel.setElementAt(
                        listModel.getElementAt(selectedIndex) + questioned, 
                        selectedIndex
                    );
                }
            }
        });
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> suspectsDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(questionButton);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        suspectsDialog.add(panel);
        suspectsDialog.setVisible(true);
    }
    
    private void makeAccusation() {
        GameState gameState = GameState.getInstance();
        
        if (!GameLogic.canMakeAccusation()) {
            JOptionPane.showMessageDialog(this, 
                "You need to discover at least 3 clues and question all suspects before making an accusation.", 
                "Not Enough Evidence", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Suspect> suspects = DatabaseHandler.getSuspects();
        
        JDialog accusationDialog = new JDialog(this, "Make Accusation", true);
        accusationDialog.setSize(500, 300);
        accusationDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel label = new JLabel("Select the suspect you believe is guilty:");
        panel.add(label, BorderLayout.NORTH);
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Suspect suspect : suspects) {
            listModel.addElement(suspect.getName() + " - " + suspect.getRole());
        }
        
        JList<String> suspectsList = new JList<>(listModel);
        panel.add(new JScrollPane(suspectsList), BorderLayout.CENTER);
        
        JButton accuseButton = new JButton("Make Accusation");
        accuseButton.addActionListener(e -> {
            int selectedIndex = suspectsList.getSelectedIndex();
            if (selectedIndex >= 0) {
                Suspect accused = suspects.get(selectedIndex);
                int confirm = JOptionPane.showConfirmDialog(accusationDialog, 
                    "Are you sure you want to accuse " + accused.getName() + "?\nThis decision is final.", 
                    "Confirm Accusation", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean isCorrect = GameLogic.makeAccusation(accused);
                    showAccusationResult(isCorrect, accused);
                    accusationDialog.dispose();
                }
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> accusationDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(accuseButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        accusationDialog.add(panel);
        accusationDialog.setVisible(true);
    }
    
    private void showAccusationResult(boolean isCorrect, Suspect accused) {
        String title = isCorrect ? "Case Solved!" : "Wrong Accusation";
        int messageType = isCorrect ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;
        
        String message;
        if (isCorrect) {
            message = "Congratulations! You correctly identified " + accused.getName() + 
                     " as the culprit.\n\nDr. Naomi Vexler was obsessed with the victim's work on quantum encryption. " +
                     "When he refused to collaborate, she tampered with his experiment, causing the accidental " +
                     "quantum entanglement incident that erased him from existence. She then tried to cover her " +
                     "tracks using her cryptographic expertise.";
        } else {
            message = "Unfortunately, you accused the wrong person. The real culprit remains at large.\n\n" +
                     "Without sufficient evidence to make a correct accusation, the case remains unsolved. " +
                     "The Oblivion Nexus facility continues to operate under a cloud of suspicion, and the " +
                     "mystery of the disappeared scientist may never be solved.";
        }
        
        JOptionPane.showMessageDialog(this, message, title, messageType);
        
        textArea.append("\n\nACCUSATION RESULT:\n");
        textArea.append(message + "\n");
        
        // Disable game buttons since the case is now resolved
        viewCluesButton.setEnabled(false);
        questionSuspectsButton.setEnabled(false);
        makeAccusationButton.setEnabled(false);
        startCaseButton.setEnabled(true);
    }
}