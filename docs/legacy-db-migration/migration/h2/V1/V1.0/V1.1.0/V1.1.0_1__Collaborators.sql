-- v1.1.0_1 - Collaborators

INSERT INTO DictionaryCollaborator(DICTIONARY_ID, USER_ID, ACCEPTED, CAN_EDIT) SELECT ID, CREATOR_ID, true, true FROM Dictionary;