-- v1.1.0_1 - Collaborators

INSERT INTO DictionaryCollaborator(dictionary_id, user_id, accepted, can_edit) SELECT id, creator_id, true, true FROM t_dictionary;