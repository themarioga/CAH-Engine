-- v1.0.0_1 - First version of database (basic tables)

-- Basic tables

DROP TABLE IF EXISTS t_user;
CREATE TABLE IF NOT EXISTS t_user
(
    id     BIGINT NOT NULL,
    name   NVARCHAR(1000)    NOT NULL,
    active BOOLEAN DEFAULT 1,
    PRIMARY KEY (id)
);

CREATE INDEX user_idx_id ON t_user (id);

DROP TABLE IF EXISTS t_room;
CREATE TABLE IF NOT EXISTS t_room
(
    id       BIGINT NOT NULL,
    name     NVARCHAR(1000)    NOT NULL,
    active   BOOLEAN DEFAULT 1,
    PRIMARY KEY (id)
);

CREATE INDEX room_idx_id ON t_room (id);

DROP TABLE IF EXISTS t_dictionary;
CREATE TABLE IF NOT EXISTS t_dictionary
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    name       NVARCHAR(1000)    NOT NULL UNIQUE,
    creator_id BIGINT NOT NULL,
    published  BOOLEAN DEFAULT 0,
    shared     BOOLEAN DEFAULT 0,
    PRIMARY KEY (id),
    FOREIGN KEY (creator_id) REFERENCES t_user (id)
);

CREATE INDEX dictionary_idx_id ON t_dictionary (id);
CREATE INDEX dictionary_creator_idx_id ON t_dictionary (creator_id);

DROP TABLE IF EXISTS t_card;
CREATE TABLE IF NOT EXISTS t_card
(
    id            BIGINT NOT NULL AUTO_INCREMENT,
    text          NVARCHAR(1000)    NOT NULL,
    type          TINYINT NOT NULL,
    dictionary_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (text, type, dictionary_id),
    FOREIGN KEY (dictionary_id) REFERENCES t_dictionary (id)
);

CREATE INDEX card_idx_id ON t_card (id);
CREATE INDEX card_dictionary_idx_id ON t_card (dictionary_id);

-- Tables for games

DROP TABLE IF EXISTS t_game;
CREATE TABLE IF NOT EXISTS t_game
(
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    room_id             BIGINT NOT NULL,
    creator_id          BIGINT NOT NULL,
    status              TINYINT NOT NULL,
    type                TINYINT NOT NULL,
    punctuation_type    TINYINT NOT NULL,
    dictionary_id       BIGINT NOT NULL,
    n_cards_to_win      TINYINT NOT NULL,
    n_of_rounds         TINYINT NOT NULL,
    n_players           TINYINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (room_id) REFERENCES t_room (id),
    FOREIGN KEY (creator_id) REFERENCES t_user (id),
    FOREIGN KEY (dictionary_id) REFERENCES t_dictionary (id),
    UNIQUE (room_id)
);

CREATE INDEX game_idx_id ON t_game (id);
CREATE INDEX game_room_idx_id ON t_game (room_id);
CREATE INDEX game_creator_idx_id ON t_game (creator_id);

DROP TABLE IF EXISTS t_player;
CREATE TABLE IF NOT EXISTS t_player
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    user_id    BIGINT NOT NULL,
    game_id    BIGINT NOT NULL,
    join_order TINYINT NOT NULL,
    points     TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES t_user (id),
    FOREIGN KEY (game_id) REFERENCES t_game (id),
    UNIQUE (user_id)
);

CREATE INDEX player_idx_id ON t_player (id);
CREATE INDEX player_user_idx_id ON t_player (user_id);
CREATE INDEX player_game_idx_id ON t_player (game_id);

-- Bot config

DROP TABLE IF EXISTS t_configuration;
CREATE TABLE IF NOT EXISTS t_configuration
(
    conf_key   NVARCHAR(1000) NOT NULL,
    conf_value NVARCHAR(1000) NOT NULL,
    PRIMARY KEY (conf_key)
);