package com.OblivionNexusDetective.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    
    public static String readFile(String filename) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/" + filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return "File not found: " + filename;
        }
        return content.toString();
    }
    
    public static void saveNotes(String notes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/investigation_notes.txt"))) {
            writer.write(notes);
        } catch (IOException e) {
            System.err.println("Error saving notes: " + e.getMessage());
        }
    }
    
    public static List<String> readClues() {
        List<String> clues = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/clues.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                clues.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading clues file: " + e.getMessage());
            clues.add("Clue file not found");
        }
        return clues;
    }
}

