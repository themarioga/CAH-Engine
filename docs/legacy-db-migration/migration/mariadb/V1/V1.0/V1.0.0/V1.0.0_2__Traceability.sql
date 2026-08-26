-- v1.0.0_2 - Traceability

-- Basic tables
ALTER TABLE t_user ADD creation_date DATETIME;
ALTER TABLE t_room ADD creation_date DATETIME;
ALTER TABLE t_card ADD creation_date DATETIME;
ALTER TABLE t_dictionary ADD creation_date DATETIME;

-- Advanced tables
ALTER TABLE t_game ADD creation_date DATETIME;
ALTER TABLE t_player ADD creation_date DATETIME;