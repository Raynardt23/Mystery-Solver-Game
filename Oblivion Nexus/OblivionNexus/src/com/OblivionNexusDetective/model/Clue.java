package com.OblivionNexusDetective.model;

public class Clue {
    private int id;
    private String description;
    private boolean found;
    
    public Clue(int id, String description, boolean found) {
        this.id = id;
        this.description = description;
        this.found = found;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public String getDescription() { return description; }
    public boolean isFound() { return found; }
    public void setFound(boolean found) { this.found = found; }
    
    @Override
    public String toString() {
        return description;
    }
}