package org.themarioga.engine.cah.exceptions.game;

import org.themarioga.commons.engine.enums.CommonErrorEnum;
import org.themarioga.commons.engine.exceptions.ApplicationException;

public class GameAlreadyFilledException extends ApplicationException {

    public GameAlreadyFilledException() {
        super(CommonErrorEnum.GAME_ALREADY_FILLED);
    }

}
