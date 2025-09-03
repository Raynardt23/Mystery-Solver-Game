package com.OblivionNexusDetective.game;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaseLoader {
    private static final String CASE_DIRECTORY = "resources/";
    private String caseTitle;
    private String caseDescription;
    private Map<String, List<String>> dialogueMap;
    private List<String> caseEndings;
    
    public CaseLoader() {
        dialogueMap = new HashMap<>();
        caseEndings = new ArrayList<>();
    }
    
    public void loadCase(String caseFileName) {
        try {
            loadCaseDescription(caseFileName);
            loadSuspectDialogues();
            loadCaseEndings();
        } catch (IOException e) {
            System.err.println("Error loading case data: " + e.getMessage());
            // Fallback to default content
            setDefaultContent();
        }
    }
    
    private void loadCaseDescription(String fileName) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(CASE_DIRECTORY + fileName))) {
            String line;
            boolean isTitle = true;
            
            while ((line = reader.readLine()) != null) {
                if (isTitle && !line.trim().isEmpty()) {
                    caseTitle = line.trim();
                    isTitle = false;
                } else {
                    content.append(line).append("\n");
                }
            }
        }
        caseDescription = content.toString();
    }
    
    private void loadSuspectDialogues() throws IOException {
        // Load dialogues for each suspect
        String[] suspectFiles = {
            "dialogues_adrian.txt",
            "dialogues_felix.txt", 
            "dialogues_naomi.txt"
        };
        
        String[] suspectKeys = {
            "ADRIAN_NOVAK",
            "FELIX_JONES",
            "NAOMI_VEXLER"
        };
        
        for (int i = 0; i < suspectFiles.length; i++) {
            List<String> dialogues = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(CASE_DIRECTORY + suspectFiles[i]))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        dialogues.add(line.trim());
                    }
                }
            }
            dialogueMap.put(suspectKeys[i], dialogues);
        }
    }
    
    private void loadCaseEndings() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(CASE_DIRECTORY + "endings.txt"))) {
            String line;
            StringBuilder ending = new StringBuilder();
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("===")) {
                    if (ending.length() > 0) {
                        caseEndings.add(ending.toString());
                        ending = new StringBuilder();
                    }
                } else {
                    ending.append(line).append("\n");
                }
            }
            
            if (ending.length() > 0) {
                caseEndings.add(ending.toString());
            }
        }
    }
    
    private void setDefaultContent() {
        // Default case title and description
        caseTitle = "Oblivion Nexus: A Research Facility Beyond the Known World";
        caseDescription = "Deep beneath an uncharted stretch of the Earth's crust lies Oblivion Nexus, a research " +
                         "facility shrouded in secrecy. Cut off from the outside world, it operates entirely off-grid...";
        
        // Default dialogues for suspects
        List<String> adrianDialogues = new ArrayList<>();
        adrianDialogues.add("I was reviewing security footage during the time of the incident.");
        adrianDialogues.add("The system did show a brief anomaly, but it was automatically corrected.");
        adrianDialogues.add("Nothing gets past my security, I assure you.");
        dialogueMap.put("ADRIAN_NOVAK", adrianDialogues);
        
        List<String> felixDialogues = new ArrayList<>();
        felixDialogues.add("The AI has been acting strange lately... whispering about patterns.");
        felixDialogues.add("I tried to tell them, but no one listens to me.");
        felixDialogues.add("They think I'm unstable, but I know what I heard.");
        dialogueMap.put("FELIX_JONES", felixDialogues);
        
        List<String> naomiDialogues = new ArrayList<>();
        naomiDialogues.add("The victim was working on a breakthrough in quantum encryption.");
        naomiDialogues.add("His work was... fascinating. I offered to collaborate, but he refused.");
        naomiDialogues.add("Pride can be a dangerous thing in a place like this.");
        dialogueMap.put("NAOMI_VEXLER", naomiDialogues);
        
        // Default endings
        caseEndings.add("Congratulations! You correctly identified the culprit and solved the case. " +
                       "The facility can now return to normal operations.");
        caseEndings.add("Unfortunately, you accused the wrong person. The real culprit remains at large, " +
                       "and the mystery continues.");
    }
    
    public String getCaseTitle() {
        return caseTitle;
    }
    
    public String getCaseDescription() {
        return caseDescription;
    }
    
    public List<String> getDialogues(String suspectKey) {
        return dialogueMap.getOrDefault(suspectKey, new ArrayList<>());
    }
    
    public String getEnding(boolean success) {
        if (caseEndings.size() >= 2) {
            return success ? caseEndings.get(0) : caseEndings.get(1);
        }
        return success ? "Case solved successfully!" : "Failed to solve the case.";
    }
    
    public String getRandomDialogue(String suspectKey) {
        List<String> dialogues = getDialogues(suspectKey);
        if (dialogues.isEmpty()) {
            return "I have nothing more to say at this time.";
        }
        return dialogues.get((int) (Math.random() * dialogues.size()));
    }
    
    public static List<String> loadCluesFromFile() {
        List<String> clues = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CASE_DIRECTORY + "clues.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    clues.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading clues: " + e.getMessage());
            // Default clues
            clues.add("Scrambled audio file found on the victim's computer");
            clues.add("Security logs show a brief, unauthorized access to the victim's lab");
            clues.add("The victim's last message was encrypted with a one-time pad that is missing");
            clues.add("A strange, non-human cryptographic signature was found in the system logs");
            clues.add("The victim was working on a project called 'Project Echo'");
            clues.add("A hidden diary entry suggests the victim feared someone was watching him");
        }
        return clues;
    }
}