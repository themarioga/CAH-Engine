package org.themarioga.engine.cah.dao.intf.dictionaries;

import org.themarioga.commons.engine.dao.InterfaceHibernateDao;
import org.themarioga.commons.engine.models.User;
import org.themarioga.engine.cah.models.dictionaries.Dictionary;

import java.util.List;

public interface DictionaryDao extends InterfaceHibernateDao<Dictionary> {

    List<Dictionary> getDictionariesByCreator(User user);

    List<Dictionary> getDictionariesByCollaborator(User creator);

    List<Dictionary> getDictionariesPaginatedForTable(User creator, int firstResult, int maxResults);

    Long getDictionaryCountForTable(User creator);

    Long countDictionariesByName(String name);

    Long countUnpublishedDictionariesByCreator(User user);

    boolean isDictionaryCollaborator(Dictionary dictionary, User user);

    boolean isDictionaryEditor(Dictionary dictionary, User user);

}
