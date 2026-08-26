package org.themarioga.engine.cah.dao.intf.dictionaries;

import org.themarioga.engine.cah.models.dictionaries.Card;
import org.themarioga.engine.cah.models.dictionaries.Dictionary;
import org.themarioga.commons.engine.dao.InterfaceHibernateDao;
import org.themarioga.engine.cah.enums.CardTypeEnum;

import java.util.List;

public interface CardDao extends InterfaceHibernateDao<Card> {

    boolean checkCardExistsByDictionaryTypeAndText(Dictionary dictionary, CardTypeEnum cardTypeEnum, String text);

    List<Card> findCardsByDictionaryAndType(Dictionary dictionary, CardTypeEnum cardTypeEnum);

    int countCardsByDictionaryAndType(Dictionary dictionary, CardTypeEnum cardTypeEnum);

}
