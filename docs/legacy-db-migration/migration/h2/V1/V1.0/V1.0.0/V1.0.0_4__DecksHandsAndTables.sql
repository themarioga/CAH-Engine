-- v1.0.0_4 - Decks, Hands and Tables

-- Game Tables
DROP TABLE IF EXISTS t_game_deletionvotes;
CREATE TABLE IF NOT EXISTS t_game_deletionvotes
(
    game_id     BIGINT NOT NULL,
    player_id   BIGINT NOT NULL,
    PRIMARY KEY (game_id, player_id),
    FOREIGN KEY (game_id) REFERENCES t_game (id),
    FOREIGN KEY (player_id) REFERENCES t_player (id)
);

-- Player tables
DROP TABLE IF EXISTS t_player_hand;
CREATE TABLE IF NOT EXISTS t_player_hand
(
    player_id BIGINT NOT NULL,
    card_id   BIGINT NOT NULL,
    PRIMARY KEY (player_id, card_id),
    FOREIGN KEY (player_id) REFERENCES t_player (id),
    FOREIGN KEY (card_id) REFERENCES t_card (id)
);

-- Game tables
DROP TABLE IF EXISTS t_table;
CREATE TABLE IF NOT EXISTS t_table
(
    game_id      BIGINT NOT NULL,
    status       TINYINT NOT NULL DEFAULT 0,
    round_number TINYINT NOT NULL DEFAULT 0,
    blackcard_id BIGINT,
    president_id BIGINT,
    PRIMARY KEY (game_id),
    FOREIGN KEY (game_id) REFERENCES t_game (id),
    FOREIGN KEY (president_id) REFERENCES t_player (id)
);

DROP TABLE IF EXISTS t_table_deck;
CREATE TABLE IF NOT EXISTS t_table_deck
(
    game_id     BIGINT NOT NULL,
    card_id     BIGINT NOT NULL,
    PRIMARY KEY (game_id, card_id),
    FOREIGN KEY (game_id) REFERENCES t_table (game_id),
    FOREIGN KEY (card_id) REFERENCES t_card (id)
);

-- Table tables
DROP TABLE IF EXISTS t_table_playedcards;
CREATE TABLE IF NOT EXISTS t_table_playedcards
(
    game_id     BIGINT NOT NULL,
    player_id   BIGINT NOT NULL,
    card_id     BIGINT NOT NULL,
    PRIMARY KEY (game_id, player_id, card_id),
    FOREIGN KEY (game_id) REFERENCES t_game (id),
    FOREIGN KEY (player_id) REFERENCES t_player (id),
    FOREIGN KEY (card_id) REFERENCES t_card (id)
);

DROP TABLE IF EXISTS t_table_playervotes;
CREATE TABLE IF NOT EXISTS t_table_playervotes
(
    player_id  BIGINT NOT NULL,
    game_id    BIGINT NOT NULL,
    card_id    BIGINT NOT NULL,
    PRIMARY KEY (player_id),
    FOREIGN KEY (player_id) REFERENCES t_player (id),
    FOREIGN KEY (game_id) REFERENCES t_game (id),
    FOREIGN KEY (card_id) REFERENCES t_card (id)
);

-- Add collabs

DROP TABLE IF EXISTS DictionaryCollaborator;
CREATE TABLE IF NOT EXISTS DictionaryCollaborator
(
    user_id       BIGINT NOT NULL,
    dictionary_id BIGINT NOT NULL,
    can_edit      BOOLEAN DEFAULT 0,
    accepted      BOOLEAN DEFAULT 0,
    PRIMARY KEY (user_id, dictionary_id),
    FOREIGN KEY (user_id) REFERENCES t_user (id),
    FOREIGN KEY (dictionary_id) REFERENCES t_dictionary (id)
);
