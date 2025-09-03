package com.OblivionNexusDetective.game;

import com.OblivionNexusDetective.model.Suspect;
import com.OblivionNexusDetective.model.Clue;
import com.OblivionNexusDetective.game.CaseLoader;

public class GameLogic {
    private static CaseLoader caseLoader = new CaseLoader();
    
    static {
        caseLoader.loadCase("case1.txt");
    }
    
    public static boolean makeAccusation(Suspect accused) {
        GameState gameState = GameState.getInstance();
        boolean isCorrect = accused.isGuilty();
        gameState.solveCase(accused, isCorrect);
        return isCorrect;
    }
    
    public static boolean canMakeAccusation() {
        GameState gameState = GameState.getInstance();
        return gameState.getFoundClues().size() >= 3 && 
               gameState.getQuestionedSuspects().size() >= 3;
    }
    
    public static String questionSuspect(Suspect suspect) {
        GameState gameState = GameState.getInstance();
        suspect.setQuestioned(true);
        gameState.markSuspectQuestioned(suspect);
        
        String response;
        switch (suspect) {
            case ADRIAN_NOVAK:
                response = caseLoader.getRandomDialogue("ADRIAN_NOVAK");
                break;
            case FELIX_JONES:
                response = caseLoader.getRandomDialogue("FELIX_JONES");
                break;
            case NAOMI_VEXLER:
                response = caseLoader.getRandomDialogue("NAOMI_VEXLER");
                break;
            default:
                response = "I have nothing more to say at this time.";
        }
        
        gameState.recordChoice("Questioned " + suspect.getName(), "Received response: " + response);
        return response;
    }


     public static String discoverClue(Clue clue) {
        GameState gameState = GameState.getInstance();
        gameState.addClue(clue);
        
        // You can return extra info here; for now, just return a simple message
        return "This clue seems important for the case. Analyze it carefully.";
    }
    
    public static String getCaseTitle() {
        return caseLoader.getCaseTitle();
    }
    
    public static String getCaseDescription() {
        return caseLoader.getCaseDescription();
    }
    
    public static String getEnding(boolean success) {
        return caseLoader.getEnding(success);
    }
}

