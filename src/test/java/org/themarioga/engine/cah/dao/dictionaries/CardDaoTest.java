package org.themarioga.engine.cah.dao.dictionaries;

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
import org.themarioga.engine.cah.dao.impl.dictionaries.CardDaoImpl;
import org.themarioga.engine.cah.enums.CardTypeEnum;
import org.themarioga.engine.cah.models.dictionaries.Card;
import org.themarioga.engine.cah.models.dictionaries.Dictionary;

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
class CardDaoTest {

    private CardDaoImpl cardDao;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Session session;

    private Card card;
    private Dictionary dictionary;

    @BeforeEach
    void setUp() {
        cardDao = new CardDaoImpl();
        cardDao.setEntityManager(entityManager);

        dictionary = new Dictionary();
        dictionary.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        card = new Card();
        card.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        card.setText("First");
        card.setType(CardTypeEnum.BLACK);
        card.setDictionary(dictionary);
        card.setCreationDate(new Date());
    }

    @Test
    void createCard() {
        when(entityManager.merge(any(Card.class))).thenReturn(card);

        Card newCard = new Card();
        newCard.setText("Test card");
        newCard.setType(CardTypeEnum.WHITE);
        newCard.setDictionary(dictionary);
        newCard.setCreationDate(new Date());

        Card createdCard = cardDao.createOrUpdate(newCard);

        Assertions.assertNotNull(createdCard.getId());
        Assertions.assertEquals("First", createdCard.getText());
        verify(entityManager).merge(newCard);
    }

    @Test
    void updateCard() {
        when(entityManager.merge(any(Card.class))).thenReturn(card);

        card.setText("Test card updated");
        card.setType(CardTypeEnum.WHITE);

        Card updatedCard = cardDao.createOrUpdate(card);

        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), updatedCard.getId());
        Assertions.assertEquals("Test card updated", updatedCard.getText());
        verify(entityManager).merge(card);
    }

    @Test
    void deleteCard() {
        doNothing().when(entityManager).remove(card);

        cardDao.delete(card);

        verify(entityManager).remove(card);
    }

    @Test
    void findCard() {
        when(entityManager.find(Card.class, card.getId())).thenReturn(card);

        Card foundCard = cardDao.findOne(card.getId());

        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), foundCard.getId());
        Assertions.assertEquals("First", foundCard.getText());
        Assertions.assertEquals(CardTypeEnum.BLACK, foundCard.getType());
        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), foundCard.getDictionary().getId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAllCards() {
        List<Card> list = new ArrayList<>();
        list.add(card);

        TypedQuery<Card> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Card.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Card> cards = cardDao.findAll();

        Assertions.assertEquals(1, cards.size());

        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), cards.get(0).getId());
        Assertions.assertEquals("First", cards.get(0).getText());
        Assertions.assertEquals(CardTypeEnum.BLACK, cards.get(0).getType());
        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), cards.get(0).getDictionary().getId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void countAllCards() {
        TypedQuery<Card> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Card.class))).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.of(card));

        long total = cardDao.countAll();

        Assertions.assertEquals(1, total);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFindCardsByDictionaryAndType() {
        List<Card> list = new ArrayList<>();
        list.add(card);

        Query<Card> query = mock(Query.class);
        when(entityManager.unwrap(any())).thenReturn(session);
        when(session.createQuery(anyString(), eq(Card.class))).thenReturn(query);
        when(query.setParameter("dictionary", dictionary)).thenReturn(query);
        when(query.setParameter("type", CardTypeEnum.BLACK)).thenReturn(query);
        when(query.getResultList()).thenReturn(list);

        Query<Long> queryLong = mock(Query.class);
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter("dictionary", dictionary)).thenReturn(queryLong);
        when(queryLong.setParameter("type", CardTypeEnum.BLACK)).thenReturn(queryLong);
        when(queryLong.getSingleResultOrNull()).thenReturn(1L);

        List<Card> cards = cardDao.findCardsByDictionaryAndType(dictionary, CardTypeEnum.BLACK);
        int cardNumber = cardDao.countCardsByDictionaryAndType(dictionary, CardTypeEnum.BLACK);

        Assertions.assertEquals(1, cards.size());
        Assertions.assertEquals(1, cardNumber);
    }

}
