package org.themarioga.engine.cah.exceptions.card;

import org.themarioga.engine.cah.enums.CAHErrorEnum;
import org.themarioga.engine.cah.exceptions.CAHApplicationException;

public class CardNotPlayedException extends CAHApplicationException {

    public CardNotPlayedException() {
        super(CAHErrorEnum.CARD_NOT_PLAYED);
    }

}
