package org.themarioga.engine.cah.dao.game;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.themarioga.engine.cah.dao.impl.game.GameDaoImpl;
import org.themarioga.engine.cah.enums.PunctuationModeEnum;
import org.themarioga.engine.cah.enums.VotationModeEnum;
import org.themarioga.engine.cah.models.dictionaries.Dictionary;
import org.themarioga.engine.cah.models.game.Game;
import org.themarioga.engine.cah.models.game.Player;
import org.themarioga.commons.engine.enums.GameStatusEnum;
import org.themarioga.commons.engine.models.Room;
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
class GameDaoTest {

    private GameDaoImpl gameDao;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Session session;

    private Game game;
    private Room room;
    private User user;
    private Dictionary dictionary;

    @BeforeEach
    void setUp() {
        gameDao = new GameDaoImpl();
        gameDao.setEntityManager(entityManager);

        room = new Room();
        room.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        user = new User();
        user.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        dictionary = new Dictionary();
        dictionary.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        game = new Game();
        game.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        game.setStatus(GameStatusEnum.CREATED);
        game.setRoom(room);
        game.setCreator(user);
        game.setDictionary(dictionary);
        game.setMaxNumberOfPlayers(1);
        game.setNumberOfPointsToWin(1);
        game.setNumberOfRoundsToEnd(1);
        game.setPunctuationMode(PunctuationModeEnum.POINTS);
        game.setVotationMode(VotationModeEnum.DEMOCRACY);
        game.setCreationDate(new Date());

        Player player = new Player();
        player.setGame(game);
        player.setUser(user);
        game.getPlayers().add(player);
        game.getDeletionVotes().add(user);
    }

    @Test
    void createGame() {
        when(entityManager.merge(any(Game.class))).thenReturn(game);

        Game newGame = new Game();
        newGame.setStatus(GameStatusEnum.CREATED);
        newGame.setRoom(room);
        newGame.setCreator(user);
        newGame.setDictionary(dictionary);
        newGame.setMaxNumberOfPlayers(1);
        newGame.setNumberOfPointsToWin(1);
        newGame.setNumberOfRoundsToEnd(1);
        newGame.setPunctuationMode(PunctuationModeEnum.POINTS);
        newGame.setVotationMode(VotationModeEnum.DEMOCRACY);
        newGame.setCreationDate(new Date());

        Game createdGame = gameDao.createOrUpdate(newGame);

        Assertions.assertNotNull(createdGame.getId());
        verify(entityManager).merge(newGame);
    }

    @Test
    void updateGame() {
        when(entityManager.merge(any(Game.class))).thenReturn(game);

        game.setStatus(GameStatusEnum.STARTED);

        Game updatedGame = gameDao.createOrUpdate(game);

        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), updatedGame.getId());
        verify(entityManager).merge(game);
    }

    @Test
    void deleteGame() {
        doNothing().when(entityManager).remove(game);

        gameDao.delete(game);

        verify(entityManager).remove(game);
    }

    @Test
    void findGame() {
        when(entityManager.find(Game.class, game.getId())).thenReturn(game);

        Game foundGame = gameDao.findOne(game.getId());

        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), foundGame.getId());
        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), foundGame.getRoom().getId());
        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), foundGame.getCreator().getId());
        Assertions.assertEquals(GameStatusEnum.CREATED, foundGame.getStatus());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getByRoomId() {
        Query<Game> query = mock(Query.class);
        when(entityManager.unwrap(any())).thenReturn(session);
        when(session.createQuery(anyString(), eq(Game.class))).thenReturn(query);
        when(query.setParameter("room", room)).thenReturn(query);
        when(query.getSingleResultOrNull()).thenReturn(game);

        Game foundGame = (Game) gameDao.getByRoom(room);

        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), foundGame.getId());
        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), foundGame.getRoom().getId());
        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), foundGame.getCreator().getId());
        Assertions.assertEquals(GameStatusEnum.CREATED, foundGame.getStatus());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAllGames() {
        List<Game> list = new ArrayList<>();
        list.add(game);

        TypedQuery<Game> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Game.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Game> games = gameDao.findAll();

        Assertions.assertEquals(1, games.size());

        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), games.get(0).getId());
        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), games.get(0).getRoom().getId());
        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), games.get(0).getCreator().getId());
        Assertions.assertEquals(GameStatusEnum.CREATED, games.get(0).getStatus());
    }

    @Test
    @SuppressWarnings("unchecked")
    void countAllGames() {
        TypedQuery<Game> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Game.class))).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.of(game));

        long total = gameDao.countAll();

        Assertions.assertEquals(1, total);
    }

    @Test
    void addDeletionVoteToTable() {
        when(entityManager.merge(any(Game.class))).thenReturn(game);

        game.getDeletionVotes().add(user);

        Game updatedGame = gameDao.createOrUpdate(game);

        Assertions.assertEquals(2, updatedGame.getDeletionVotes().size());
        verify(entityManager).merge(game);
    }

    @Test
    void getTableDeletionVotes() {
        when(entityManager.find(Game.class, game.getId())).thenReturn(game);

        Game foundGame = gameDao.findOne(game.getId());

        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), foundGame.getId());

        Assertions.assertNotNull(foundGame.getDeletionVotes());
        Assertions.assertEquals(1, foundGame.getDeletionVotes().size());
    }

    @Test
    void findPlayersInGame() {
        when(entityManager.find(Game.class, game.getId())).thenReturn(game);

        Game foundGame = gameDao.findOne(game.getId());

        Assertions.assertEquals(1, foundGame.getPlayers().size());
    }

}
