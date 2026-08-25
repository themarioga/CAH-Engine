package org.themarioga.engine.cah.dao.impl.dictionaries;

import org.springframework.stereotype.Repository;
import org.themarioga.engine.commons.dao.AbstractHibernateDao;
import org.themarioga.engine.commons.models.User;
import org.themarioga.engine.cah.dao.intf.dictionaries.DictionaryDao;
import org.themarioga.engine.cah.models.dictionaries.Dictionary;

import java.util.List;

@Repository
public class DictionaryDaoImpl extends AbstractHibernateDao<Dictionary> implements DictionaryDao {

    public DictionaryDaoImpl() {
        setClazz(Dictionary.class);
    }

    @Override
    public List<Dictionary> getDictionariesByCreator(User user) {
        return getCurrentSession().createQuery("SELECT d FROM Dictionary d WHERE d.creator=:user", Dictionary.class).setParameter("user", user).getResultList();
    }

    @Override
    public List<Dictionary> getDictionariesByCollaborator(User collaborator) {
        return getCurrentSession().createQuery("SELECT d FROM Dictionary d WHERE d IN (SELECT c.dictionary FROM DictionaryCollaborator c WHERE c.user=:collaborator and c.accepted=true)", Dictionary.class).setParameter("collaborator", collaborator).getResultList();
    }

    @Override
    public List<Dictionary> getDictionariesPaginatedForTable(User collaborator, int firstResult, int maxResults) {
        return getCurrentSession().createQuery("SELECT d FROM Dictionary d WHERE d.lang=:lang and d.published=true and (d.shared=true or (d.shared=false and d IN (SELECT c.dictionary FROM DictionaryCollaborator c WHERE c.user=:collaborator and c.accepted=true)))", Dictionary.class).setParameter("lang", collaborator.getLang()).setParameter("collaborator", collaborator).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    @Override
    public Long getDictionaryCountForTable(User collaborator) {
        return getCurrentSession().createQuery("SELECT count(*) FROM Dictionary d WHERE d.lang=:lang and d.published=true and (d.shared=true or (d.shared=false and d IN (SELECT c.dictionary FROM DictionaryCollaborator c WHERE c.user=:collaborator and c.accepted=true)))", Long.class).setParameter("lang", collaborator.getLang()).setParameter("collaborator", collaborator).getSingleResult();
    }

    @Override
    public Long countDictionariesByName(String name) {
        return getCurrentSession().createQuery("SELECT count(d) FROM Dictionary d WHERE d.name = :name", Long.class).setParameter("name", name).getSingleResult();
    }

    @Override
    public Long countUnpublishedDictionariesByCreator(User user) {
        return getCurrentSession().createQuery("SELECT count(d) FROM Dictionary d WHERE d.creator=:user and d.published=false", Long.class).setParameter("user", user).getSingleResult();
    }

    @Override
    public boolean isDictionaryCollaborator(Dictionary dictionary, User user) {
        return getCurrentSession().createQuery("SELECT count(c) FROM DictionaryCollaborator c WHERE c.dictionary=:dictionary and c.user=:user", Long.class).setParameter("dictionary", dictionary).setParameter("user", user).getSingleResultOrNull() > 0;
    }

    @Override
    public boolean isDictionaryEditor(Dictionary dictionary, User user) {
        return getCurrentSession().createQuery("SELECT count(c) FROM DictionaryCollaborator c WHERE c.dictionary=:dictionary and c.user=:user and c.accepted=true and c.canEdit=true", Long.class).setParameter("dictionary", dictionary).setParameter("user", user).getSingleResultOrNull() > 0;
    }

}

