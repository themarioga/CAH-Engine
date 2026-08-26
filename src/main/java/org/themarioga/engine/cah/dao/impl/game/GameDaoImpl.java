package org.themarioga.engine.cah.dao.impl.game;

import org.springframework.stereotype.Repository;
import org.themarioga.engine.cah.models.game.Game;
import org.themarioga.commons.engine.dao.AbstractHibernateDao;
import org.themarioga.engine.cah.dao.intf.game.GameDao;
import org.themarioga.commons.engine.models.Room;
import org.themarioga.commons.engine.models.User;

@Repository
public class GameDaoImpl extends AbstractHibernateDao<Game> implements GameDao {

    public GameDaoImpl() {
        setClazz(Game.class);
    }

    @Override
    public Game getByRoom(Room room) {
        return getCurrentSession().createQuery("SELECT t FROM Game t where t.room=:room", Game.class).setParameter("room", room).getSingleResultOrNull();
    }

    @Override
    public Game getByCreator(User creator) {
        return getCurrentSession().createQuery("SELECT t FROM Game t where t.creator=:creator", Game.class).setParameter("creator", creator).getSingleResultOrNull();
    }

    @Override
    public Long countByRoom(Room room) {
        return getCurrentSession().createQuery("SELECT count(t) FROM Game t where t.room=:room", Long.class).setParameter("room", room).getSingleResultOrNull();
    }

    @Override
    public Long countByCreator(User creator) {
        return getCurrentSession().createQuery("SELECT count(t) FROM Game t where t.creator=:creator", Long.class).setParameter("creator", creator).getSingleResultOrNull();
    }

    @Override
    public void transferCardsFromDictionaryToDeck(Game game) {
        getCurrentSession().createNativeMutationQuery("INSERT INTO game_black_cards_deck(game_id, card_id) SELECT :game AS game_id, id FROM card WHERE dictionary_id=:dictionary and type=0").setParameter("game", game.getId()).setParameter("dictionary", game.getDictionary().getId()).executeUpdate();
        getCurrentSession().createNativeMutationQuery("INSERT INTO game_white_cards_deck(game_id, card_id) SELECT :game AS game_id, id FROM card WHERE dictionary_id=:dictionary and type=1").setParameter("game", game.getId()).setParameter("dictionary", game.getDictionary().getId()).executeUpdate();
        getCurrentSession().refresh(game);
    }

}
