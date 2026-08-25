package org.themarioga.engine.cah.exceptions.dictionary;

import org.themarioga.engine.cah.enums.CAHErrorEnum;
import org.themarioga.engine.cah.exceptions.CAHApplicationException;

public class DictionaryMaxCollaboratorsReached extends CAHApplicationException {

    public DictionaryMaxCollaboratorsReached() {
        super(CAHErrorEnum.DICTIONARY_MAX_COLLABORATORS_REACHED);
    }

}
