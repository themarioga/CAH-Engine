package org.themarioga.engine.cah.service.dictionaries;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.themarioga.engine.cah.config.DictionariesConfig;
import org.themarioga.engine.cah.dao.intf.dictionaries.DictionaryDao;
import org.themarioga.engine.cah.exceptions.dictionary.*;
import org.themarioga.engine.cah.models.dictionaries.Dictionary;
import org.themarioga.engine.cah.models.dictionaries.DictionaryCollaborator;
import org.themarioga.engine.cah.services.impl.dictionaries.DictionaryServiceImpl;
import org.themarioga.engine.cah.services.intf.dictionaries.CardService;
import org.themarioga.commons.engine.models.Lang;
import org.themarioga.commons.engine.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DictionaryServiceTest {

    @InjectMocks
    private DictionaryServiceImpl dictionaryService;

    @Mock
    private DictionaryDao dictionaryDao;

    @Mock
    private CardService cardService;

    @Mock
    private DictionariesConfig dictionariesConfig;

    private User creator;
    private User otherUser;
    private Lang lang;
    private Dictionary dictionary;
    private DictionaryCollaborator creatorCollaborator;

    @BeforeEach
    void setUp() {
        lang = new Lang();
        lang.setId("es");

        creator = new User();
        creator.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        creator.setLang(lang);

        otherUser = new User();
        otherUser.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        otherUser.setLang(lang);

        dictionary = new Dictionary();
        dictionary.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        dictionary.setName("First");
        dictionary.setCreator(creator);
        dictionary.setLang(lang);
        dictionary.setShared(false);
        dictionary.setPublished(false);

        creatorCollaborator = new DictionaryCollaborator();
        creatorCollaborator.setDictionary(dictionary);
        creatorCollaborator.setUser(creator);
        creatorCollaborator.setAccepted(true);
        creatorCollaborator.setCanEdit(true);

        java.util.Date now = new java.util.Date();
        creator.setCreationDate(now);
        otherUser.setCreationDate(now);
        dictionary.setCreationDate(now);

        dictionary.getCollaborators().add(creatorCollaborator);
    }

    @Test
    void testCreateDictionary() {
        when(dictionaryDao.countDictionariesByName("Dictionary 1")).thenReturn(0L);
        when(dictionaryDao.countUnpublishedDictionariesByCreator(creator)).thenReturn(0L);
        when(dictionariesConfig.getMaxNumberOfUnfinishedDictionaries()).thenReturn(5);
        when(dictionaryDao.createOrUpdate(any(Dictionary.class))).thenAnswer(i -> {
            Dictionary d = i.getArgument(0);
            d.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            return d;
        });

        Dictionary createdDictionary = dictionaryService.create("Dictionary 1", creator);

        Assertions.assertNotNull(createdDictionary);
        Assertions.assertEquals("Dictionary 1", createdDictionary.getName());
        Assertions.assertEquals(creator, createdDictionary.getCreator());
        verify(dictionaryDao).createOrUpdate(any(Dictionary.class));
    }

    @Test
    void testCreateDictionary_NameAlreadyExists() {
        when(dictionaryDao.countDictionariesByName("First")).thenReturn(1L);

        Assertions.assertThrows(DictionaryAlreadyExistsException.class, () -> dictionaryService.create("First", creator));
    }

    @Test
    void testCreateDictionary_TooManyDictionaries() {
        when(dictionaryDao.countDictionariesByName("Dictionary 1")).thenReturn(0L);
        when(dictionaryDao.countUnpublishedDictionariesByCreator(creator)).thenReturn(5L);
        when(dictionariesConfig.getMaxNumberOfUnfinishedDictionaries()).thenReturn(5);

        Assertions.assertThrows(DictionaryAlreadyFilledException.class, () -> dictionaryService.create("Dictionary 1", creator));
    }

    @Test
    void testSetName() {
        when(dictionaryDao.countDictionariesByName("New Name")).thenReturn(0L);
        when(dictionaryDao.createOrUpdate(dictionary)).thenReturn(dictionary);

        Dictionary updatedDictionary = dictionaryService.setName(dictionary, "New Name");

        Assertions.assertEquals("New Name", updatedDictionary.getName());
    }

    @Test
    void testSetName_NameAlreadyExists() {
        when(dictionaryDao.countDictionariesByName("First")).thenReturn(1L);

        Assertions.assertThrows(DictionaryAlreadyExistsException.class, () -> dictionaryService.setName(dictionary, "First"));
    }

    @Test
    void testSetLanguage() {
        Lang newLang = new Lang();
        newLang.setId("en");

        when(dictionaryDao.createOrUpdate(dictionary)).thenReturn(dictionary);

        Dictionary updatedDictionary = dictionaryService.setLanguage(dictionary, newLang);

        Assertions.assertEquals("en", updatedDictionary.getLang().getId());
    }

    @Test
    void testTogglePublished() {
        when(cardService.checkDictionaryCanBePublished(dictionary)).thenReturn(true);
        when(dictionaryDao.createOrUpdate(dictionary)).thenReturn(dictionary);

        Dictionary updatedDictionary = dictionaryService.togglePublished(dictionary);

        Assertions.assertEquals(true, updatedDictionary.getPublished());
    }

    @Test
    void testTogglePublished_AlreadyShared() {
        dictionary.setShared(true);

        Assertions.assertThrows(DictionaryAlreadySharedException.class, () -> dictionaryService.togglePublished(dictionary));
    }

    @Test
    void testTogglePublished_DictionaryNotCompleted() {
        when(cardService.checkDictionaryCanBePublished(dictionary)).thenReturn(false);

        Assertions.assertThrows(DictionaryNotCompletedException.class, () -> dictionaryService.togglePublished(dictionary));
    }

    @Test
    void testToggleShared() {
        dictionary.setPublished(true);
        when(dictionaryDao.createOrUpdate(dictionary)).thenReturn(dictionary);

        Dictionary updatedDictionary = dictionaryService.toggleShared(dictionary);

        Assertions.assertEquals(true, updatedDictionary.getShared());
    }

    @Test
    void testToggleShared_NotPublished() {
        Assertions.assertThrows(DictionaryNotPublishedException.class, () -> dictionaryService.toggleShared(dictionary));
    }

    @Test
    void testDelete() {
        doNothing().when(dictionaryDao).delete(dictionary);

        dictionaryService.delete(dictionary);

        verify(dictionaryDao).delete(dictionary);
    }

    @Test
    void testDelete_AlreadyShared() {
        dictionary.setShared(true);

        Assertions.assertThrows(DictionaryAlreadySharedException.class, () -> dictionaryService.delete(dictionary));
    }

    @Test
    void testGetDictionaryByUUID() {
        when(dictionaryDao.findOne(dictionary.getId())).thenReturn(dictionary);

        Dictionary foundDictionary = dictionaryService.getDictionaryById(dictionary.getId());

        Assertions.assertNotNull(foundDictionary);
        Assertions.assertEquals("First", foundDictionary.getName());
    }

    @Test
    void testGetDictionariesByCreator() {
        List<Dictionary> list = new ArrayList<>();
        list.add(dictionary);

        when(dictionaryDao.getDictionariesByCreator(creator)).thenReturn(list);

        List<Dictionary> dictionaryList = dictionaryService.getDictionariesByCreator(creator);

        Assertions.assertEquals(1, dictionaryList.size());
    }

    @Test
    void testGetDictionariesPaginatedForTable() {
        List<Dictionary> list = new ArrayList<>();
        list.add(dictionary);

        when(dictionaryDao.getDictionariesPaginatedForTable(creator, 0, 1)).thenReturn(list);

        List<Dictionary> dictionaryList = dictionaryService.getDictionariesPaginatedForTable(creator, 0, 1);

        Assertions.assertEquals(1, dictionaryList.size());
    }

    @Test
    void testCountDictionariesPaginatedForTable() {
        when(dictionaryDao.getDictionaryCountForTable(creator)).thenReturn(1L);

        Long nDic = dictionaryService.getDictionaryCountForTable(creator);

        Assertions.assertEquals(1L, nDic);
    }

    // //////// COLLABORATORS //////////

    @Test
    void testAddCollaborator() {
        when(dictionariesConfig.getMaxNumberOfCollaborators()).thenReturn(10);
        when(dictionaryDao.createOrUpdate(dictionary)).thenReturn(dictionary);

        DictionaryCollaborator dictionaryCollaborator = dictionaryService.addCollaborator(dictionary, otherUser);

        Assertions.assertNotNull(dictionaryCollaborator);
        Assertions.assertEquals(otherUser, dictionaryCollaborator.getUser());
        Assertions.assertEquals(2, dictionary.getCollaborators().size());
    }

    @Test
    void testAddCollaborator_MaxCollaboratorsReached() {
        when(dictionariesConfig.getMaxNumberOfCollaborators()).thenReturn(1);

        Assertions.assertThrows(DictionaryMaxCollaboratorsReached.class, () -> dictionaryService.addCollaborator(dictionary, otherUser));
    }

    @Test
    void testAddCollaborator_CollaboratorAlreadyExists() {
        when(dictionariesConfig.getMaxNumberOfCollaborators()).thenReturn(10);

        Assertions.assertThrows(DictionaryCollaboratorAlreadyExists.class, () -> dictionaryService.addCollaborator(dictionary, creator));
    }

    @Test
    void testToggleAcceptedCollaborator() {
        DictionaryCollaborator collaborator = new DictionaryCollaborator();
        collaborator.setDictionary(dictionary);
        collaborator.setUser(otherUser);
        collaborator.setAccepted(true);
        dictionary.getCollaborators().add(collaborator);

        when(dictionaryDao.createOrUpdate(dictionary)).thenReturn(dictionary);

        DictionaryCollaborator updatedCollaborator = dictionaryService.toggleAcceptedCollaborator(dictionary, otherUser);

        Assertions.assertEquals(false, updatedCollaborator.getAccepted());
    }

    @Test
    void testToggleAcceptedCollaborator_CreatorCantBeAltered() {
        Assertions.assertThrows(DictionaryCollaboratorCreatorCantBeAltered.class, () -> dictionaryService.toggleAcceptedCollaborator(dictionary, creator));
    }

    @Test
    void testToggleAcceptedCollaborator_CollaboratorDoesntExists() {
        Assertions.assertThrows(DictionaryCollaboratorDoesntExists.class, () -> dictionaryService.toggleAcceptedCollaborator(dictionary, otherUser));
    }

    @Test
    void testToggleCanEditCollaborator() {
        DictionaryCollaborator collaborator = new DictionaryCollaborator();
        collaborator.setDictionary(dictionary);
        collaborator.setUser(otherUser);
        collaborator.setAccepted(true);
        collaborator.setCanEdit(false);
        dictionary.getCollaborators().add(collaborator);

        when(dictionaryDao.createOrUpdate(dictionary)).thenReturn(dictionary);

        DictionaryCollaborator updatedCollaborator = dictionaryService.toggleCanEditCollaborator(dictionary, otherUser);

        Assertions.assertEquals(true, updatedCollaborator.getCanEdit());
    }

    @Test
    void testToggleCanEditCollaborator_CreatorCantBeAltered() {
        Assertions.assertThrows(DictionaryCollaboratorCreatorCantBeAltered.class, () -> dictionaryService.toggleCanEditCollaborator(dictionary, creator));
    }

    @Test
    void testToggleCanEditCollaborator_CollaboratorDoesntExists() {
        Assertions.assertThrows(DictionaryCollaboratorDoesntExists.class, () -> dictionaryService.toggleCanEditCollaborator(dictionary, otherUser));
    }

    @Test
    void testToggleCanEditCollaborator_CollaboratorNotAccepted() {
        DictionaryCollaborator collaborator = new DictionaryCollaborator();
        collaborator.setDictionary(dictionary);
        collaborator.setUser(otherUser);
        collaborator.setAccepted(false);
        collaborator.setCanEdit(false);
        dictionary.getCollaborators().add(collaborator);

        Assertions.assertThrows(DictionaryCollaboratorDoesntExists.class, () -> dictionaryService.toggleCanEditCollaborator(dictionary, otherUser));
    }

    @Test
    void testRemoveCollaborator() {
        DictionaryCollaborator collaborator = new DictionaryCollaborator();
        collaborator.setDictionary(dictionary);
        collaborator.setUser(otherUser);
        collaborator.setAccepted(true);
        dictionary.getCollaborators().add(collaborator);

        when(dictionaryDao.createOrUpdate(dictionary)).thenReturn(dictionary);

        dictionaryService.removeCollaborator(dictionary, otherUser);

        Assertions.assertEquals(1, dictionary.getCollaborators().size());
        verify(dictionaryDao).createOrUpdate(dictionary);
    }

    @Test
    void testRemoveCollaborator_CreatorCantBeAltered() {
        Assertions.assertThrows(DictionaryCollaboratorCreatorCantBeAltered.class, () -> dictionaryService.removeCollaborator(dictionary, creator));
    }

    @Test
    void testRemoveCollaborator_CollaboratorDoesntExists() {
        Assertions.assertThrows(DictionaryCollaboratorDoesntExists.class, () -> dictionaryService.removeCollaborator(dictionary, otherUser));
    }

    @Test
    void testIsCollaborator() {
        when(dictionaryDao.isDictionaryCollaborator(dictionary, creator)).thenReturn(true);

        Assertions.assertTrue(dictionaryService.isDictionaryCollaborator(dictionary, creator));
    }

    @Test
    void testIsCollaborator_IsNotCollaborator() {
        when(dictionaryDao.isDictionaryCollaborator(dictionary, otherUser)).thenReturn(false);

        Assertions.assertFalse(dictionaryService.isDictionaryCollaborator(dictionary, otherUser));
    }

    @Test
    void testIsEditor() {
        when(dictionaryDao.isDictionaryEditor(dictionary, creator)).thenReturn(true);

        Assertions.assertTrue(dictionaryService.isDictionaryEditor(dictionary, creator));
    }

    @Test
    void testIsEditor_IsNotEditor() {
        when(dictionaryDao.isDictionaryEditor(dictionary, otherUser)).thenReturn(false);

        Assertions.assertFalse(dictionaryService.isDictionaryEditor(dictionary, otherUser));
    }

    @Test
    void testGetDictionariesByCollaborator() {
        List<Dictionary> list = new ArrayList<>();
        list.add(dictionary);

        when(dictionaryDao.getDictionariesByCollaborator(creator)).thenReturn(list);

        Assertions.assertEquals(1, dictionaryService.getDictionariesByCollaborator(creator).size());
    }

}
