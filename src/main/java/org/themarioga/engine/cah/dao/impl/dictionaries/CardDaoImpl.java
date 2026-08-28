package org.themarioga.engine.cah.dao.impl.dictionaries;

import org.springframework.stereotype.Repository;
import org.themarioga.commons.engine.dao.AbstractHibernateDao;
import org.themarioga.engine.cah.models.dictionaries.Card;
import org.themarioga.engine.cah.models.dictionaries.Dictionary;
import org.themarioga.engine.cah.dao.intf.dictionaries.CardDao;
import org.themarioga.engine.cah.enums.CardTypeEnum;

import java.util.List;

@Repository
public class CardDaoImpl extends AbstractHibernateDao<Card> implements CardDao {

    public CardDaoImpl() {
        setClazz(Card.class);
    }

    @Override
    public boolean checkCardExistsByDictionaryTypeAndText(Dictionary dictionary, CardTypeEnum cardTypeEnum, String text) {
        return getCurrentSession().createQuery("SELECT count(c) FROM Card c WHERE c.dictionary=:dictionary and c.type=:type and c.text = :text", Long.class).setParameter("dictionary", dictionary).setParameter("type", cardTypeEnum).setParameter("text", text).getSingleResultOrNull() > 0;
    }

    @Override
    public List<Card> findCardsByDictionaryAndType(Dictionary dictionary, CardTypeEnum cardTypeEnum) {
        return getCurrentSession().createQuery("SELECT c FROM Card c where c.dictionary=:dictionary and c.type=:type", Card.class).setParameter("dictionary", dictionary).setParameter("type", cardTypeEnum).getResultList();
    }

    @Override
    public int countCardsByDictionaryAndType(Dictionary dictionary, CardTypeEnum cardTypeEnum) {
        return getCurrentSession().createQuery("SELECT count(c) FROM Card c where c.dictionary=:dictionary and c.type=:type", Long.class).setParameter("dictionary", dictionary).setParameter("type", cardTypeEnum).getSingleResultOrNull().intValue();
    }

}
