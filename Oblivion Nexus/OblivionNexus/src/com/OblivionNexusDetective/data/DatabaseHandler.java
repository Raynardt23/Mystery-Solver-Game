package com.OblivionNexusDetective.data;

import com.OblivionNexusDetective.model.Suspect;
import com.OblivionNexusDetective.model.Clue;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    
    public static void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Connected to database successfully");
            
            // Create database if it doesn't exist
            stmt.execute("CREATE DATABASE IF NOT EXISTS oblivionnexus_db");
            stmt.execute("USE oblivionnexus_db");
            
            // Create players table
            String createPlayersTable = "CREATE TABLE IF NOT EXISTS players (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "progress INTEGER DEFAULT 0, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            stmt.execute(createPlayersTable);
            
            // Create suspects table
            String createSuspectsTable = "CREATE TABLE IF NOT EXISTS suspects (" +
                    "id INTEGER PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "role VARCHAR(100) NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "is_guilty BOOLEAN DEFAULT FALSE)";
            stmt.execute(createSuspectsTable);
            
            // Create clues table
            String createCluesTable = "CREATE TABLE IF NOT EXISTS clues (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "description TEXT NOT NULL, " +
                    "found BOOLEAN DEFAULT FALSE)";
            stmt.execute(createCluesTable);
            
            // Create player choices table
            String createChoicesTable = "CREATE TABLE IF NOT EXISTS player_choices (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "player_id INTEGER, " +
                    "choice_made TEXT, " +
                    "outcome TEXT, " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (player_id) REFERENCES players (id))";
            stmt.execute(createChoicesTable);
            
            // Create player progress table
            String createProgressTable = "CREATE TABLE IF NOT EXISTS player_progress (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "player_id INTEGER, " +
                    "clue_id INTEGER DEFAULT NULL, " +
                    "suspect_id INTEGER DEFAULT NULL, " +
                    "discovered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (player_id) REFERENCES players (id), " +
                    "FOREIGN KEY (clue_id) REFERENCES clues (id), " +
                    "FOREIGN KEY (suspect_id) REFERENCES suspects (id))";
            stmt.execute(createProgressTable);
            
            // Insert initial data if tables are empty
            if (isTableEmpty(conn, "suspects")) {
                insertInitialSuspects(conn);
            }
            
            if (isTableEmpty(conn, "clues")) {
                insertInitialClues(conn);
            }
            
            System.out.println("Database initialized successfully");
            
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static boolean isTableEmpty(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }
    
    private static void insertInitialSuspects(Connection conn) throws SQLException {
        String sql = "INSERT INTO suspects (id, name, role, description, is_guilty) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Lt. Adrian Novak
            pstmt.setInt(1, 1);
            pstmt.setString(2, "Lt. Adrian Novak");
            pstmt.setString(3, "Head of Security");
            pstmt.setString(4, "Former military officer haunted by battlefield losses, obsessively loyal to Nexus, but rumored to hide classified failures");
            pstmt.setBoolean(5, false);
            pstmt.executeUpdate();
            
            // Felix Jones
            pstmt.setInt(1, 2);
            pstmt.setString(2, "Felix Jones");
            pstmt.setString(3, "AI Systems Engineer");
            pstmt.setString(4, "Brilliant coder with unorthodox methods, once dismissed for unstable experiments, insists the AI whispers things only he hears");
            pstmt.setBoolean(5, false);
            pstmt.executeUpdate();
            
            // Dr. Naomi Vexler
            pstmt.setInt(1, 3);
            pstmt.setString(2, "Dr. Naomi Vexler");
            pstmt.setString(3, "Cipher Specialist");
            pstmt.setString(4, "Prodigy cryptographer driven by obsession with unsolvable codes, rumored to hide encrypted diaries no one has ever cracked");
            pstmt.setBoolean(5, true);
            pstmt.executeUpdate();
        }
    }
    
    private static void insertInitialClues(Connection conn) throws SQLException {
        String sql = "INSERT INTO clues (description, found) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String[] clues = {
                "Scrambled audio file found on the victim's computer",
                "Security logs show a brief, unauthorized access to the victim's lab",
                "The victim's last message was encrypted with a one-time pad that is missing",
                "A strange, non-human cryptographic signature was found in the system logs",
                "The victim was working on a project called 'Project Echo' which involved quantum entanglement",
                "A hidden diary entry suggests the victim feared someone was watching him"
            };
            
            for (String clue : clues) {
                pstmt.setString(1, clue);
                pstmt.setBoolean(2, false);
                pstmt.executeUpdate();
            }
        }
    }
    
    // Get all suspects from the database
    public static List<Suspect> getSuspects() {
        List<Suspect> suspects = new ArrayList<>();
        String sql = "SELECT name FROM suspects";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                // Map name to enum
                for (Suspect s : Suspect.values()) {
                    if (s.getName().equals(name)) {
                        suspects.add(s);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting suspects: " + e.getMessage());
            e.printStackTrace();
        }

        return suspects;
    }
    
    // Get all clues from the database
    public static List<Clue> getClues() {
        List<Clue> clues = new ArrayList<>();
        String sql = "SELECT * FROM clues";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Clue clue = new Clue(
                    rs.getInt("id"),
                    rs.getString("description"),
                    rs.getBoolean("found")
                );
                clues.add(clue);
            }
        } catch (SQLException e) {
            System.err.println("Error getting clues: " + e.getMessage());
            e.printStackTrace();
        }
        
        return clues;
    }
    
    // Create a new player and return their ID
    public static int createPlayer(String name) {
        String sql = "INSERT INTO players (name) VALUES (?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating player: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    // Record a player's choice and its outcome
    public static void recordChoice(int playerId, String choice, String outcome) {
        String sql = "INSERT INTO player_choices (player_id, choice_made, outcome) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            pstmt.setString(2, choice);
            pstmt.setString(3, outcome);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error recording choice: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Mark a clue as found and record progress
    public static void markClueFound(int playerId, int clueId) {
        String updateClueSql = "UPDATE clues SET found = TRUE WHERE id = ?";
        String insertProgressSql = "INSERT INTO player_progress (player_id, clue_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateClueSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertProgressSql)) {
            
            // Update the clue status
            updateStmt.setInt(1, clueId);
            updateStmt.executeUpdate();
            
            // Record the progress
            insertStmt.setInt(1, playerId);
            insertStmt.setInt(2, clueId);
            insertStmt.executeUpdate();
            
            // Update player progress percentage
            updatePlayerProgress(playerId, conn);
            
        } catch (SQLException e) {
            System.err.println("Error marking clue as found: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Record that a suspect has been questioned
    public static void markSuspectQuestioned(int playerId, int suspectId) {
        String sql = "INSERT INTO player_progress (player_id, suspect_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, suspectId);
            pstmt.executeUpdate();
            
            // Update player progress percentage
            updatePlayerProgress(playerId, conn);
            
        } catch (SQLException e) {
            System.err.println("Error recording questioned suspect: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Update player's progress percentage
    private static void updatePlayerProgress(int playerId, Connection conn) throws SQLException {
        // Calculate progress percentage
        int totalItems = getTotalDiscoverableItems(conn);
        int discoveredItems = getDiscoveredItemsCount(playerId, conn);
        
        int progress = (int) (((double) discoveredItems / totalItems) * 100);
        
        String sql = "UPDATE players SET progress = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, progress);
            pstmt.setInt(2, playerId);
            pstmt.executeUpdate();
        }
    }
    
    // Get total number of discoverable items (clues + suspects)
    private static int getTotalDiscoverableItems(Connection conn) throws SQLException {
        String cluesSql = "SELECT COUNT(*) FROM clues";
        String suspectsSql = "SELECT COUNT(*) FROM suspects";
        
        int totalClues, totalSuspects;
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(cluesSql)) {
            totalClues = rs.getInt(1);
        }
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(suspectsSql)) {
            totalSuspects = rs.getInt(1);
        }
        
        return totalClues + totalSuspects;
    }
    
    // Get count of items discovered by a player
    private static int getDiscoveredItemsCount(int playerId, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM player_progress WHERE player_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
    
    // Get all choices made by a player
    public static List<String> getPlayerChoices(int playerId) {
        List<String> choices = new ArrayList<>();
        String sql = "SELECT choice_made, outcome, timestamp FROM player_choices WHERE player_id = ? ORDER BY timestamp";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String choice = String.format("[%s] %s -> %s", 
                        rs.getTimestamp("timestamp").toString(),
                        rs.getString("choice_made"),
                        rs.getString("outcome"));
                    choices.add(choice);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting player choices: " + e.getMessage());
            e.printStackTrace();
        }
        
        return choices;
    }
    
    // Get player's current progress percentage
    public static int getPlayerProgress(int playerId) {
        String sql = "SELECT progress FROM players WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("progress");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting player progress: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // Check if a player has already discovered a specific clue
    public static boolean hasPlayerDiscoveredClue(int playerId, int clueId) {
        String sql = "SELECT COUNT(*) FROM player_progress WHERE player_id = ? AND clue_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, clueId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking clue discovery: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Check if a player has already questioned a specific suspect
    public static boolean hasPlayerQuestionedSuspect(int playerId, int suspectId) {
        String sql = "SELECT COUNT(*) FROM player_progress WHERE player_id = ? AND suspect_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, suspectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking suspect questioning: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Get player's case completion status
    public static boolean isCaseCompleted(int playerId) {
        String sql = "SELECT COUNT(pc.id) FROM player_choices pc " +
                    "WHERE pc.player_id = ? AND pc.choice_made LIKE 'Accused %'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking case completion: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Get player's case outcome
    public static String getCaseOutcome(int playerId) {
        String sql = "SELECT outcome FROM player_choices " +
                    "WHERE player_id = ? AND choice_made LIKE 'Accused %' " +
                    "ORDER BY timestamp DESC LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("outcome");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting case outcome: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "Case not completed yet";
    }
    
    // Reset player progress (for starting a new case)
    public static void resetPlayerProgress(int playerId) {
        String deleteProgressSql = "DELETE FROM player_progress WHERE player_id = ?";
        String deleteChoicesSql = "DELETE FROM player_choices WHERE player_id = ?";
        String resetCluesSql = "UPDATE clues SET found = FALSE";
        String resetPlayerSql = "UPDATE players SET progress = 0 WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement progressStmt = conn.prepareStatement(deleteProgressSql);
             PreparedStatement choicesStmt = conn.prepareStatement(deleteChoicesSql);
             PreparedStatement cluesStmt = conn.prepareStatement(resetCluesSql);
             PreparedStatement playerStmt = conn.prepareStatement(resetPlayerSql)) {
            
            // Delete progress records
            progressStmt.setInt(1, playerId);
            progressStmt.executeUpdate();
            
            // Delete choice records
            choicesStmt.setInt(1, playerId);
            choicesStmt.executeUpdate();
            
            // Reset all clues to not found
            cluesStmt.executeUpdate();
            
            // Reset player progress to 0
            playerStmt.setInt(1, playerId);
            playerStmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error resetting player progress: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Get all players with their progress
    public static List<String> getAllPlayers() {
        List<String> players = new ArrayList<>();
        String sql = "SELECT id, name, progress, created_at FROM players ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String playerInfo = String.format("ID: %d, Name: %s, Progress: %d%%, Created: %s",
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("progress"),
                    rs.getTimestamp("created_at").toString());
                players.add(playerInfo);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all players: " + e.getMessage());
            e.printStackTrace();
        }
        
        return players;
    }
}