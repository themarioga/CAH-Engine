package org.themarioga.engine.cah.dao.impl.game;

import org.springframework.stereotype.Repository;
import org.themarioga.engine.cah.dao.intf.game.PlayerDao;
import org.themarioga.engine.cah.models.game.Player;
import org.themarioga.commons.engine.dao.AbstractHibernateDao;
import org.themarioga.commons.engine.models.Game;
import org.themarioga.commons.engine.models.User;

@Repository
public class PlayerDaoImpl extends AbstractHibernateDao<Player> implements PlayerDao {

    public PlayerDaoImpl() {
        setClazz(Player.class);
    }

    @Override
    public Player findPlayerByUser(User user) {
        return getCurrentSession().createQuery("SELECT p FROM Player p where p.user=:user", Player.class).setParameter("user", user).getSingleResultOrNull();
    }

    @Override
    public Player findPlayerByUserAndGame(User user, Game game) {
        return getCurrentSession().createQuery("SELECT p FROM Player p where p.user=:user and p.game=:game", Player.class).setParameter("user", user).setParameter("game", game).getSingleResultOrNull();
    }

}
