package org.themarioga.engine.cah.dao.dictionaries;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.themarioga.engine.cah.dao.impl.dictionaries.DictionaryDaoImpl;
import org.themarioga.engine.cah.models.dictionaries.Dictionary;
import org.themarioga.engine.cah.models.dictionaries.DictionaryCollaborator;
import org.themarioga.commons.engine.models.Lang;
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
class DictionaryDaoTest {

    private DictionaryDaoImpl dictionaryDao;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Session session;

    private Dictionary dictionary;
    private User user;
    private Lang lang;

    @BeforeEach
    void setUp() {
        dictionaryDao = new DictionaryDaoImpl();
        dictionaryDao.setEntityManager(entityManager);

        user = new User();
        user.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        lang = new Lang();
        lang.setId("es");

        dictionary = new Dictionary();
        dictionary.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        dictionary.setName("First");
        dictionary.setShared(true);
        dictionary.setPublished(true);
        dictionary.setCreator(user);
        dictionary.setLang(lang);
        dictionary.setCreationDate(new Date());

        DictionaryCollaborator collaborator = new DictionaryCollaborator();
        collaborator.setDictionary(dictionary);
        collaborator.setUser(user);
        collaborator.setAccepted(true);
        collaborator.setCanEdit(true);

        dictionary.getCollaborators().add(collaborator);
    }

    @Test
    void createDictionary() {
        when(entityManager.merge(any(Dictionary.class))).thenReturn(dictionary);

        Dictionary newDictionary = new Dictionary();
        newDictionary.setName("Test deck");
        newDictionary.setShared(true);
        newDictionary.setPublished(true);
        newDictionary.setCreator(user);
        newDictionary.setLang(lang);
        newDictionary.setCreationDate(new Date());

        Dictionary createdDictionary = dictionaryDao.createOrUpdate(newDictionary);

        Assertions.assertNotNull(createdDictionary.getId());
        verify(entityManager).merge(newDictionary);
    }

    @Test
    void updateDictionary() {
        when(entityManager.merge(any(Dictionary.class))).thenReturn(dictionary);

        dictionary.setName("Otro nombre");
        dictionary.setShared(false);
        dictionary.setPublished(false);

        Dictionary updatedDictionary = dictionaryDao.createOrUpdate(dictionary);

        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), updatedDictionary.getId());
        verify(entityManager).merge(dictionary);
    }

    @Test
    void deleteDictionary() {
        doNothing().when(entityManager).remove(dictionary);

        dictionaryDao.delete(dictionary);

        verify(entityManager).remove(dictionary);
    }

    @Test
    void findDictionary() {
        when(entityManager.find(Dictionary.class, dictionary.getId())).thenReturn(dictionary);

        Dictionary foundDictionary = dictionaryDao.findOne(dictionary.getId());

        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), foundDictionary.getId());
        Assertions.assertEquals("First", foundDictionary.getName());
        Assertions.assertEquals(true, foundDictionary.getShared());
        Assertions.assertEquals(true, foundDictionary.getPublished());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAllDictionarys() {
        List<Dictionary> list = new ArrayList<>();
        list.add(dictionary);

        TypedQuery<Dictionary> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Dictionary.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Dictionary> dictionaries = dictionaryDao.findAll();

        Assertions.assertEquals(1, dictionaries.size());

        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), dictionaries.get(0).getId());
        Assertions.assertEquals("First", dictionaries.get(0).getName());
        Assertions.assertEquals(true, dictionaries.get(0).getShared());
        Assertions.assertEquals(true, dictionaries.get(0).getPublished());
    }

    @Test
    @SuppressWarnings("unchecked")
    void countAllDictionarys() {
        TypedQuery<Dictionary> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Dictionary.class))).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.of(dictionary));

        long total = dictionaryDao.countAll();

        Assertions.assertEquals(1, total);
    }

    @Test
    void addDictionaryCollaborator() {
        when(entityManager.merge(any(Dictionary.class))).thenReturn(dictionary);

        DictionaryCollaborator dictionaryCollaborator = new DictionaryCollaborator();
        dictionaryCollaborator.setDictionary(dictionary);
        dictionaryCollaborator.setUser(user);
        dictionaryCollaborator.setAccepted(true);
        dictionaryCollaborator.setCanEdit(true);
        dictionary.getCollaborators().add(dictionaryCollaborator);

        Dictionary updatedDictionary = dictionaryDao.createOrUpdate(dictionary);

        Assertions.assertEquals(2, updatedDictionary.getCollaborators().size());
        verify(entityManager).merge(dictionary);
    }

    @Test
    void updateDictionaryCollaborator() {
        when(entityManager.merge(any(Dictionary.class))).thenReturn(dictionary);

        dictionary.getCollaborators().get(0).setAccepted(false);

        Dictionary updatedDictionary = dictionaryDao.createOrUpdate(dictionary);

        Assertions.assertEquals(1, updatedDictionary.getCollaborators().size());
        verify(entityManager).merge(dictionary);
    }

    @Test
    void getDictionaryCollaborators() {
        when(entityManager.find(Dictionary.class, dictionary.getId())).thenReturn(dictionary);

        Dictionary foundDictionary = dictionaryDao.findOne(dictionary.getId());

        Assertions.assertEquals(true, foundDictionary.getCollaborators().get(0).getAccepted());
    }

}
