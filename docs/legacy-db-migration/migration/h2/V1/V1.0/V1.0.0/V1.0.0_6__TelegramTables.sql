-- v1.0.0_6 - Telegram tables

DROP TABLE IF EXISTS T_TELEGRAM_GAME;
CREATE TABLE IF NOT EXISTS T_TELEGRAM_GAME
(
    game_id                 BIGINT NOT NULL,
    group_message_id        BIGINT NOT NULL,
    private_message_id      BIGINT NOT NULL,
    blackcard_message_id    BIGINT,
    PRIMARY KEY (game_id),
    FOREIGN KEY (game_id) REFERENCES t_game (id)
);

DROP TABLE IF EXISTS T_TELEGRAM_PLAYER;
CREATE TABLE IF NOT EXISTS T_TELEGRAM_PLAYER
(
    player_id   BIGINT NOT NULL,
    message_id  BIGINT NOT NULL,
    PRIMARY KEY (player_id),
    FOREIGN KEY (player_id) REFERENCES t_player (id)
);