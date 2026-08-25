package org.themarioga.engine.cah.exceptions.dictionary;

import org.themarioga.engine.cah.enums.CAHErrorEnum;
import org.themarioga.engine.cah.exceptions.CAHApplicationException;

public class DictionaryCollaboratorCreatorCantBeAltered extends CAHApplicationException {

    public DictionaryCollaboratorCreatorCantBeAltered() {
        super(CAHErrorEnum.DICTIONARY_COLLAB_CREATOR_CANT_BE_ALTERED);
    }

}
