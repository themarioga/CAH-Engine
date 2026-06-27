package org.themarioga.engine.cah.service.dictionaries;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.themarioga.engine.cah.config.DictionariesConfig;
import org.themarioga.engine.cah.dao.intf.dictionaries.CardDao;
import org.themarioga.engine.cah.enums.CardTypeEnum;
import org.themarioga.engine.cah.exceptions.card.CardAlreadyExistsException;
import org.themarioga.engine.cah.exceptions.card.CardTextExcededLength;
import org.themarioga.engine.cah.exceptions.dictionary.DictionaryAlreadyFilledException;
import org.themarioga.engine.cah.models.dictionaries.Card;
import org.themarioga.engine.cah.models.dictionaries.Dictionary;
import org.themarioga.engine.cah.services.impl.dictionaries.CardServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardDao cardDao;

    @Mock
    private DictionariesConfig dictionariesConfig;

    private Dictionary dictionary;
    private Card card;

    @BeforeEach
    void setUp() {
        dictionary = new Dictionary();
        dictionary.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        card = new Card();
        card.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        card.setDictionary(dictionary);
        card.setText("First black card");
        card.setType(CardTypeEnum.BLACK);
    }

    @Test
    void testCreateCard() {
        when(cardDao.checkCardExistsByDictionaryTypeAndText(dictionary, CardTypeEnum.WHITE, "Test card")).thenReturn(false);
        when(cardDao.countCardsByDictionaryAndType(dictionary, CardTypeEnum.WHITE)).thenReturn(0);
        when(dictionariesConfig.getMaxNumberOfWhiteCards()).thenReturn(100);
        when(dictionariesConfig.getMinWhiteCardLength()).thenReturn(1);
        when(dictionariesConfig.getMaxWhiteCardLength()).thenReturn(50);
        when(cardDao.createOrUpdate(any(Card.class))).thenAnswer(invocation -> {
            Card c = invocation.getArgument(0);
            c.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            return c;
        });

        Card createdCard = cardService.create(dictionary, CardTypeEnum.WHITE, "Test card");

        Assertions.assertNotNull(createdCard);
        Assertions.assertEquals(UUID.fromString("11111111-1111-1111-1111-111111111111"), createdCard.getId());
        Assertions.assertEquals("Test card", createdCard.getText());
        Assertions.assertEquals(CardTypeEnum.WHITE, createdCard.getType());
        verify(cardDao).createOrUpdate(any(Card.class));
    }

    @Test
    void testCreateCard_AlreadyExists() {
        when(cardDao.checkCardExistsByDictionaryTypeAndText(dictionary, CardTypeEnum.WHITE, "Another white card")).thenReturn(true);

        Assertions.assertThrows(CardAlreadyExistsException.class, () -> cardService.create(dictionary, CardTypeEnum.WHITE, "Another white card"));
    }

    @Test
    void testCreateCard_AlreadyFilled() {
        when(cardDao.checkCardExistsByDictionaryTypeAndText(dictionary, CardTypeEnum.WHITE, "Test card")).thenReturn(false);
        when(cardDao.countCardsByDictionaryAndType(dictionary, CardTypeEnum.WHITE)).thenReturn(100);
        when(dictionariesConfig.getMaxNumberOfWhiteCards()).thenReturn(100);

        Assertions.assertThrows(DictionaryAlreadyFilledException.class, () -> cardService.create(dictionary, CardTypeEnum.WHITE, "Test card"));
    }

    @Test
    void testCreateCard_TextLengthExceeded() {
        when(cardDao.checkCardExistsByDictionaryTypeAndText(dictionary, CardTypeEnum.WHITE, "This test card have a very long text")).thenReturn(false);
        when(cardDao.countCardsByDictionaryAndType(dictionary, CardTypeEnum.WHITE)).thenReturn(0);
        when(dictionariesConfig.getMaxNumberOfWhiteCards()).thenReturn(100);
        when(dictionariesConfig.getMinWhiteCardLength()).thenReturn(1);
        when(dictionariesConfig.getMaxWhiteCardLength()).thenReturn(10);

        Assertions.assertThrows(CardTextExcededLength.class, () -> cardService.create(dictionary, CardTypeEnum.WHITE, "This test card have a very long text"));
    }

    @Test
    void testChangeText() {
        when(cardDao.checkCardExistsByDictionaryTypeAndText(dictionary, CardTypeEnum.BLACK, "New text")).thenReturn(false);
        when(dictionariesConfig.getMinBlackCardLength()).thenReturn(1);
        when(dictionariesConfig.getMaxBlackCardLength()).thenReturn(50);
        when(cardDao.createOrUpdate(any(Card.class))).thenReturn(card);

        Card updatedCard = cardService.changeText(card, "New text");

        Assertions.assertEquals("New text", updatedCard.getText());
        verify(cardDao).createOrUpdate(card);
    }

    @Test
    void testChangeText_CardAlreadyExists() {
        when(cardDao.checkCardExistsByDictionaryTypeAndText(dictionary, CardTypeEnum.BLACK, "Second black card")).thenReturn(true);

        Assertions.assertThrows(CardAlreadyExistsException.class, () -> cardService.changeText(card, "Second black card"));
    }

    @Test
    void testChangeText_TextLengthExceeded() {
        when(cardDao.checkCardExistsByDictionaryTypeAndText(dictionary, CardTypeEnum.BLACK, "This test card will have a very long text")).thenReturn(false);
        when(dictionariesConfig.getMinBlackCardLength()).thenReturn(1);
        when(dictionariesConfig.getMaxBlackCardLength()).thenReturn(10);

        Assertions.assertThrows(CardTextExcededLength.class, () -> cardService.changeText(card, "This test card will have a very long text"));
    }

    @Test
    void testDelete() {
        doNothing().when(cardDao).delete(card);

        cardService.delete(card);

        verify(cardDao).delete(card);
    }

    @Test
    void testGetCardById() {
        when(cardDao.findOne(card.getId())).thenReturn(card);

        Card foundCard = cardService.getCardById(card.getId());

        Assertions.assertNotNull(foundCard);
        Assertions.assertEquals("First black card", foundCard.getText());
    }

    @Test
    void testFindCardsByDictionaryAndType() {
        List<Card> list = new ArrayList<>();
        list.add(card);

        when(cardDao.findCardsByDictionaryAndType(dictionary, CardTypeEnum.BLACK)).thenReturn(list);

        List<Card> blackCards = cardService.findCardsByDictionaryAndType(dictionary, CardTypeEnum.BLACK);
        Assertions.assertEquals(1, blackCards.size());
    }

    @Test
    void testCountCardsByDictionaryAndType() {
        when(cardDao.countCardsByDictionaryAndType(dictionary, CardTypeEnum.BLACK)).thenReturn(3);

        Assertions.assertEquals(3, cardService.countCardsByDictionaryAndType(dictionary, CardTypeEnum.BLACK));
    }

    @Test
    void testCheckDictionaryCanBePublished() {
        when(cardDao.countCardsByDictionaryAndType(dictionary, CardTypeEnum.WHITE)).thenReturn(10);
        when(dictionariesConfig.getMinNumberOfWhiteCards()).thenReturn(10);
        when(cardDao.countCardsByDictionaryAndType(dictionary, CardTypeEnum.BLACK)).thenReturn(5);
        when(dictionariesConfig.getMinNumberOfBlackCards()).thenReturn(5);

        Assertions.assertTrue(cardService.checkDictionaryCanBePublished(dictionary));
    }

}
