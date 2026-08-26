package org.themarioga.engine.cah.dao.intf.game;

import org.themarioga.engine.cah.models.dictionaries.Card;
import org.themarioga.engine.cah.models.game.PlayedCard;
import org.themarioga.engine.cah.models.game.Round;
import org.themarioga.commons.engine.dao.InterfaceHibernateDao;

import java.util.List;

public interface RoundDao extends InterfaceHibernateDao<Round> {

    /**
     * Returns every card tied for the highest vote count in the round (usually a single
     * card, but more than one when the vote is tied), so callers can apply their own
     * tie-break rule instead of relying on arbitrary database row order.
     */
    List<Card> getMostVotedCards(Round round);

    PlayedCard getPlayedCardByCard(Round round, Card card);

    long countPlayedCards(Round round);

    long countVotedCards(Round round);

}
