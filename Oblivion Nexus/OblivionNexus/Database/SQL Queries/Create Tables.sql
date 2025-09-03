-- Players Table

CREATE TABLE Players (

player_id INT PRIMARY KEY AUTO_INCREMENT,
name TEXT NOT NULL,
progress INT DEFAULT 0,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

-- Suspects Table

CREATE TABLE Suspects (

suspect_id INT PRIMARY KEY,
name TEXT NOT NULL,
job_role TEXT NOT NULL,
description TEXT NOT NULL,
is_guilty BOOLEAN DEFAULT FALSE

);

-- Clues table
CREATE TABLE clues (
    clue_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    description TEXT NOT NULL,
    found BOOLEAN DEFAULT FALSE
);

-- Player choices table
CREATE TABLE player_choices (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    player_id INTEGER,
    choice_made TEXT,
    outcome TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES Players (player_id)
);

-- Player progress table 

CREATE TABLE player_progress (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    player_id INTEGER,
    clue_id INTEGER,
    suspect_id INTEGER,
    discovered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players (player_id),
    FOREIGN KEY (clue_id) REFERENCES clues (clue_id),
    FOREIGN KEY (suspect_id) REFERENCES suspects (suspect_id)
);