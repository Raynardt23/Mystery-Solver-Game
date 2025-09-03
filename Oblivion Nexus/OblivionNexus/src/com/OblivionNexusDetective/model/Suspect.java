package com.OblivionNexusDetective.model;


/*
 *  This represents the suspects in my detective game.
 *  Each suspect has a full name and a job role.
 */

public enum Suspect {
    ADRIAN_NOVAK(1, "Lt. Adrian Novak", "Head of Security", 
                 "Former military officer haunted by battlefield losses, obsessively loyal to Nexus, but rumored to hide classified failures",
                 false),
    
    FELIX_JONES(2, "Felix Jones", "AI Systems Engineer",
                "Brilliant coder with unorthodox methods, once dismissed for unstable experiments, insists the AI whispers things only he hears",
                false),
    
    NAOMI_VEXLER(3, "Dr. Naomi Vexler", "Cipher Specialist",
                 "Prodigy cryptographer driven by obsession with unsolvable codes, rumored to hide encrypted diaries no one has ever cracked",
                 true);
    
    private final int id;
    private final String name;
    private final String jobRole;
    private final String description;
    private final boolean guilty;
    private boolean questioned;
    
    Suspect(int id, String name, String jobRole, String description, boolean guilty) {
        this.id = id;
        this.name = name;
        this.jobRole = jobRole;
        this.description = description;
        this.guilty = guilty;
        this.questioned = false;
    }
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return jobRole; }
    public String getDescription() { return description; }
    public boolean isGuilty() { return guilty; }
    public boolean isQuestioned() { return questioned; }
    
    // Setters
    public void setQuestioned(boolean questioned) { this.questioned = questioned; }
    
    // Helper method to get suspect by ID
    public static Suspect getById(int id) {
        for (Suspect suspect : values()) {
            if (suspect.id == id) {
                return suspect;
            }
        }
        throw new IllegalArgumentException("No suspect with ID: " + id);
    }
    
    @Override
    public String toString() {
        return name + " - " + jobRole;
    }
}




