package org.themarioga.engine.cah.services.impl.dictionaries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.themarioga.engine.cah.config.DictionariesConfig;
import org.themarioga.engine.cah.exceptions.dictionary.*;
import org.themarioga.commons.engine.models.Lang;
import org.themarioga.commons.engine.models.User;
import org.themarioga.engine.cah.dao.intf.dictionaries.DictionaryDao;
import org.themarioga.commons.engine.exceptions.ApplicationException;
import org.themarioga.engine.cah.models.dictionaries.Dictionary;
import org.themarioga.engine.cah.models.dictionaries.DictionaryCollaborator;
import org.themarioga.engine.cah.services.intf.dictionaries.CardService;
import org.themarioga.engine.cah.services.intf.dictionaries.DictionaryService;

import java.util.*;

@Service
public class DictionaryServiceImpl implements DictionaryService {

    private final Logger logger = LoggerFactory.getLogger(DictionaryServiceImpl.class);

    private final DictionaryDao dictionaryDao;
    private final CardService cardService;
    private final DictionariesConfig dictionariesConfig;

    @Autowired
    public DictionaryServiceImpl(DictionaryDao dictionaryDao, CardService cardService, DictionariesConfig dictionariesConfig) {
        this.dictionaryDao = dictionaryDao;
        this.cardService = cardService;
        this.dictionariesConfig = dictionariesConfig;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    public Dictionary create(String name, User creator) {
        logger.debug("Creating dictionary: {}", name);

        // Don't allow creating a dictionary with an already existing name
        if (dictionaryDao.countDictionariesByName(name) > 0)
            throw new DictionaryAlreadyExistsException();

        // Don't allow creating a dictionary if max number have already been exceeded
        if (dictionaryDao.countUnpublishedDictionariesByCreator(creator) >= dictionariesConfig.getMaxNumberOfUnfinishedDictionaries())
            throw new DictionaryAlreadyFilledException();

        // Create dictionary
        Dictionary dictionary = new Dictionary();
        dictionary.setName(name);
        dictionary.setCreator(creator);
        dictionary.setLang(creator.getLang());
        dictionary.setPublished(false);
        dictionary.setShared(false);
        dictionary.setCreationDate(new Date());

        // Set creator as collaborator
        DictionaryCollaborator dictionaryCollaborator = new DictionaryCollaborator();
        dictionaryCollaborator.setDictionary(dictionary);
        dictionaryCollaborator.setUser(creator);
        dictionaryCollaborator.setAccepted(true);
        dictionaryCollaborator.setCanEdit(true);
        dictionary.getCollaborators().add(dictionaryCollaborator);

        return dictionaryDao.createOrUpdate(dictionary);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    public Dictionary setName(Dictionary dictionary, String newName) {
        logger.debug("Setting name {} to dictionary {}", newName, dictionary);

        // Check if that name is already registered
        if (dictionaryDao.countDictionariesByName(newName) > 0)
            throw new DictionaryAlreadyExistsException();

        dictionary.setName(newName);

        return dictionaryDao.createOrUpdate(dictionary);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    public Dictionary setLanguage(Dictionary dictionary, Lang lang) {
        logger.debug("Setting lang {} to dictionary {}", lang, dictionary);

        dictionary.setLang(lang);

        return dictionaryDao.createOrUpdate(dictionary);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    public Dictionary togglePublished(Dictionary dictionary) {
        logger.debug("Toggling published to dictionary {}", dictionary);

        // Check if the dictionary is already shared
        if (Boolean.TRUE.equals(dictionary.getShared()))
            throw new DictionaryAlreadySharedException();

        // Check if the dictionary meets the criteria to be published
        if (!cardService.checkDictionaryCanBePublished(dictionary))
            throw new DictionaryNotCompletedException();

        dictionary.setPublished(!dictionary.getPublished());

        return dictionaryDao.createOrUpdate(dictionary);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    public Dictionary toggleShared(Dictionary dictionary) {
        logger.debug("Toggling shared to dictionary {}", dictionary);

        // Check if the dictionary is published
        if (Boolean.FALSE.equals(dictionary.getPublished()))
            throw new DictionaryNotPublishedException();

        dictionary.setShared(!dictionary.getShared());

        return dictionaryDao.createOrUpdate(dictionary);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    public void delete(Dictionary dictionary) {
        logger.debug("Delete dictionary: {}", dictionary);

        // Check if the dictionary is shared
        if (Boolean.TRUE.equals(dictionary.getShared()))
            throw new DictionaryAlreadySharedException();

        dictionaryDao.delete(dictionary);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Dictionary getDictionaryById(UUID id) {
        logger.debug("Getting dictionary with ID: {}", id);

        return dictionaryDao.findOne(id);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Dictionary> getDictionariesByCreator(User creator) {
        logger.debug("Getting dictionaries with user: {}", creator);

        return dictionaryDao.getDictionariesByCreator(creator);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Dictionary> getDictionariesPaginatedForTable(User creator, int firstResult, int maxResults) {
        logger.debug("Getting dictionary from {} to {}", firstResult, maxResults);

        return dictionaryDao.getDictionariesPaginatedForTable(creator, firstResult, maxResults);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Long getDictionaryCountForTable(User creator) {
        logger.debug("Get dictionary count {}", creator);

        return dictionaryDao.getDictionaryCountForTable(creator);
    }

    // //////// COLLABORATORS //////////

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    public DictionaryCollaborator addCollaborator(Dictionary dictionary, User user) {
        logger.debug("Add dictionary collaborator: {} {}", dictionary, user);

        // Check if dictionary have reached the max number of collaborators
        if (dictionary.getCollaborators().size() >= dictionariesConfig.getMaxNumberOfCollaborators())
            throw new DictionaryMaxCollaboratorsReached();

        // Check if the collaborator is already present
        Optional<DictionaryCollaborator> collaborator = dictionary.getCollaborators().stream().filter(dictionaryCollaborator -> Objects.equals(dictionaryCollaborator.getUser().getId(), user.getId())).findFirst();
        if (collaborator.isPresent())
            throw new DictionaryCollaboratorAlreadyExists();

        // Create the collaborator
        DictionaryCollaborator dictionaryCollaborator = new DictionaryCollaborator();
        dictionaryCollaborator.setDictionary(dictionary);
        dictionaryCollaborator.setUser(user);
        dictionaryCollaborator.setAccepted(false);
        dictionaryCollaborator.setCanEdit(false);
        dictionary.getCollaborators().add(dictionaryCollaborator);
        dictionaryDao.createOrUpdate(dictionary);

        return dictionaryCollaborator;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    public DictionaryCollaborator toggleAcceptedCollaborator(Dictionary dictionary, User user) {
        logger.debug("Toggling accept in dictionary collaborator: {} {}", dictionary, user);

        // Check if collaborator being toggled is the creator
        if (dictionary.getCreator().getId().equals(user.getId()))
            throw new DictionaryCollaboratorCreatorCantBeAltered();

        // Check if the collaborator exists for this dictionary
        Optional<DictionaryCollaborator> collaborator = dictionary.getCollaborators().stream().filter(dictionaryCollaborator -> Objects.equals(dictionaryCollaborator.getUser().getId(), user.getId())).findFirst();
        if (collaborator.isEmpty())
            throw new DictionaryCollaboratorDoesntExists();

        // Toggle accept in collaborator
        collaborator.ifPresent(dictionaryCollaborator -> dictionaryCollaborator.setAccepted(!dictionaryCollaborator.getAccepted()));
        dictionaryDao.createOrUpdate(dictionary);

        return collaborator.get();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    public DictionaryCollaborator toggleCanEditCollaborator(Dictionary dictionary, User user) {
        logger.debug("Toggling canEdit in dictionary collaborator: {} {}", dictionary, user);

        // Check if collaborator being toggled is the creator
        if (dictionary.getCreator().getId().equals(user.getId()))
            throw new DictionaryCollaboratorCreatorCantBeAltered();

        // Check if the collaborator exists for this dictionary
        Optional<DictionaryCollaborator> collaborator = dictionary.getCollaborators().stream().filter(dictionaryCollaborator -> Objects.equals(dictionaryCollaborator.getUser().getId(), user.getId())).findFirst();
        if (collaborator.isEmpty())
            throw new DictionaryCollaboratorDoesntExists();

        // Check if the collaborator have been accepted before
        if (Boolean.FALSE.equals(collaborator.get().getAccepted()))
            throw new DictionaryCollaboratorDoesntExists();

        // Toggle can edit to collaborator
        collaborator.ifPresent(dictionaryCollaborator -> dictionaryCollaborator.setCanEdit(!dictionaryCollaborator.getCanEdit()));
        dictionaryDao.createOrUpdate(dictionary);

        return collaborator.get();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    public void removeCollaborator(Dictionary dictionary, User user) {
        logger.debug("Delete dictionary collaborator: {} {}", dictionary, user);

        // Check if collaborator being toggled is the creator
        if (dictionary.getCreator().getId().equals(user.getId()))
            throw new DictionaryCollaboratorCreatorCantBeAltered();

        // Check if the collaborator exists for this dictionary
        Optional<DictionaryCollaborator> collaborator = dictionary.getCollaborators().stream().filter(dictionaryCollaborator -> Objects.equals(dictionaryCollaborator.getUser().getId(), user.getId())).findFirst();
        if (collaborator.isEmpty())
            throw new DictionaryCollaboratorDoesntExists();

        // Remove the collaborator
        collaborator.ifPresent(dictionaryCollaborator -> dictionary.getCollaborators().remove(dictionaryCollaborator));
        dictionaryDao.createOrUpdate(dictionary);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isDictionaryCollaborator(Dictionary dictionary, User user) {
        logger.debug("isDictionaryCollaborator {}", user);

        return dictionaryDao.isDictionaryCollaborator(dictionary, user);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isDictionaryEditor(Dictionary dictionary, User user) {
        logger.debug("isDictionaryEditor {}", user);

        return dictionaryDao.isDictionaryEditor(dictionary, user);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Dictionary> getDictionariesByCollaborator(User creator) {
        logger.debug("Getting dictionaries with user/collaborator: {}", creator);

        return dictionaryDao.getDictionariesByCollaborator(creator);
    }

}
