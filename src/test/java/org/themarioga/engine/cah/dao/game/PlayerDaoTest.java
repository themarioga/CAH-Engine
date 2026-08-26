package org.themarioga.engine.cah.dao.game;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.themarioga.engine.cah.dao.impl.game.PlayerDaoImpl;
import org.themarioga.engine.cah.models.game.Game;
import org.themarioga.engine.cah.models.game.Player;
import org.themarioga.commons.engine.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerDaoTest {

    private PlayerDaoImpl playerDao;

    @Mock
    private EntityManager entityManager;

    private Player player;
    private Game game;
    private User user;

    @BeforeEach
    void setUp() {
        playerDao = new PlayerDaoImpl();
        playerDao.setEntityManager(entityManager);

        game = new Game();
        game.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        user = new User();
        user.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        player = new Player();
        player.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        player.setGame(game);
        player.setUser(user);
        player.setJoinOrder(0);
        player.setCreationDate(new Date());
    }

    @Test
    void createPlayer() {
        when(entityManager.merge(any(Player.class))).thenReturn(player);

        Player newPlayer = new Player();
        newPlayer.setGame(game);
        newPlayer.setUser(user);
        newPlayer.setJoinOrder(1);
        newPlayer.setCreationDate(new Date());

        Player createdPlayer = playerDao.createOrUpdate(newPlayer);

        Assertions.assertNotNull(createdPlayer.getId());
        verify(entityManager).merge(newPlayer);
    }

    @Test
    void updatePlayer() {
        when(entityManager.merge(any(Player.class))).thenReturn(player);

        player.setJoinOrder(1);

        Player updatedPlayer = playerDao.createOrUpdate(player);

        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), updatedPlayer.getId());
        verify(entityManager).merge(player);
    }

    @Test
    void deletePlayer() {
        doNothing().when(entityManager).remove(player);

        playerDao.delete(player);

        verify(entityManager).remove(player);
    }

    @Test
    void findPlayer() {
        when(entityManager.find(Player.class, player.getId())).thenReturn(player);

        Player foundPlayer = playerDao.findOne(player.getId());

        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), foundPlayer.getId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAllPlayers() {
        List<Player> list = new ArrayList<>();
        list.add(player);

        TypedQuery<Player> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Player.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Player> players = playerDao.findAll();

        Assertions.assertEquals(1, players.size());
        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), players.get(0).getId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void countAllPlayers() {
        TypedQuery<Player> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Player.class))).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.of(player));

        long total = playerDao.countAll();

        Assertions.assertEquals(1, total);
    }

}
