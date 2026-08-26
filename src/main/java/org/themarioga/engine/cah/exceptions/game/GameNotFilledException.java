package org.themarioga.engine.cah.exceptions.game;

import org.themarioga.commons.engine.enums.CommonErrorEnum;
import org.themarioga.commons.engine.exceptions.ApplicationException;

public class GameNotFilledException extends ApplicationException {

    public GameNotFilledException() {
        super(CommonErrorEnum.GAME_NOT_FILLED);
    }

}
