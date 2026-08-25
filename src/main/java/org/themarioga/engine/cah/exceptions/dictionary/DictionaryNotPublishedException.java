package org.themarioga.engine.cah.exceptions.dictionary;

import org.themarioga.engine.cah.enums.CAHErrorEnum;
import org.themarioga.engine.cah.exceptions.CAHApplicationException;

public class DictionaryNotPublishedException extends CAHApplicationException {

    public DictionaryNotPublishedException() {
        super(CAHErrorEnum.DICTIONARY_NOT_PUBLISHED);
    }

}
