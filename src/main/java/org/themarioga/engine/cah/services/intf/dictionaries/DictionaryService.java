package org.themarioga.engine.cah.services.intf.dictionaries;

import org.themarioga.commons.engine.models.Lang;
import org.themarioga.commons.engine.models.User;
import org.themarioga.engine.cah.models.dictionaries.Dictionary;
import org.themarioga.engine.cah.models.dictionaries.DictionaryCollaborator;

import java.util.List;
import java.util.UUID;

public interface DictionaryService {

    Dictionary create(String name, User creator);

    Dictionary setName(Dictionary dictionary, String newName);

    Dictionary setLanguage(Dictionary dictionary, Lang lang);

    Dictionary togglePublished(Dictionary dictionary);

    Dictionary toggleShared(Dictionary dictionary);

    void delete(Dictionary dictionary);

    DictionaryCollaborator addCollaborator(Dictionary dictionary, User user);

    DictionaryCollaborator toggleAcceptedCollaborator(Dictionary dictionary, User user);

    DictionaryCollaborator toggleCanEditCollaborator(Dictionary dictionary, User user);

    void removeCollaborator(Dictionary dictionary, User user);

    Dictionary getDictionaryById(UUID id);

    List<Dictionary> getDictionariesByCreator(User creator);

    List<Dictionary> getDictionariesByCollaborator(User creator);

    List<Dictionary> getDictionariesPaginatedForTable(User creator, int firstResult, int maxResults);

    Long getDictionaryCountForTable(User creator);

    boolean isDictionaryCollaborator(Dictionary dictionary, User user);

    boolean isDictionaryEditor(Dictionary dictionary, User user);
}
