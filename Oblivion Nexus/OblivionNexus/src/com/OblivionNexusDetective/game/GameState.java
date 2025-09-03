package com.OblivionNexusDetective.game;

import com.OblivionNexusDetective.model.Suspect;
import com.OblivionNexusDetective.data.DatabaseHandler;
import com.OblivionNexusDetective.model.Clue;
import com.OblivionNexusDetective.model.Difficulty;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameState {
    private static GameState instance;
    private int playerId;
    private String playerName;
    private Difficulty difficulty;
    private List<Clue> foundClues;
    private Map<Suspect, Boolean> questionedSuspects;
    private boolean caseSolved;
    private String outcome;
    
    private GameState() {
        foundClues = new ArrayList<>();
        questionedSuspects = new HashMap<>();
        caseSolved = false;
    }
    
    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }
    
    public void initializeGame(String playerName, Difficulty difficulty) {
        this.playerName = playerName;
        this.difficulty = difficulty;
        this.playerId = DatabaseHandler.createPlayer(playerName);
        this.foundClues.clear();
        this.questionedSuspects.clear();
        this.caseSolved = false;
        this.outcome = "";
    }
    
    public void addClue(Clue clue) {
        if (!foundClues.contains(clue)) {
            foundClues.add(clue);
            DatabaseHandler.markClueFound(playerId,clue.getId());
        }
    }
    
    public void markSuspectQuestioned(Suspect suspect) {
        questionedSuspects.put(suspect, true);
        DatabaseHandler.markSuspectQuestioned(playerId,suspect.getId());
    }
    
    public void solveCase(Suspect accused, boolean isCorrect) {
        caseSolved = true;
        if (isCorrect) {
            outcome = "Congratulations! You correctly identified " + accused.getName() + " as the culprit.";
        } else {
            outcome = "Unfortunately, you accused the wrong person. The real culprit remains at large.";
        }
        
        DatabaseHandler.recordChoice(playerId, "Accused " + accused.getName(), outcome);
    }

    public void recordChoice(String choice, String outcome) {
    DatabaseHandler.recordChoice(playerId, choice, outcome);
}
    
    // Getters
    public int getPlayerId() { return playerId; }
    public String getPlayerName() { return playerName; }
    public Difficulty getDifficulty() { return difficulty; }
    public List<Clue> getFoundClues() { return foundClues; }
    public Map<Suspect, Boolean> getQuestionedSuspects() { return questionedSuspects; }
    public boolean isCaseSolved() { return caseSolved; }
    public String getOutcome() { return outcome; }
}
