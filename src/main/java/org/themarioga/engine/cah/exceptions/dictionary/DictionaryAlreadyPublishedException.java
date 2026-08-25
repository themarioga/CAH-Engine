package org.themarioga.engine.cah.exceptions.dictionary;

import org.themarioga.engine.cah.enums.CAHErrorEnum;
import org.themarioga.engine.cah.exceptions.CAHApplicationException;

public class DictionaryAlreadyPublishedException extends CAHApplicationException {

    public DictionaryAlreadyPublishedException() {
        super(CAHErrorEnum.DICTIONARY_ALREADY_PUBLISHED);
    }

}
