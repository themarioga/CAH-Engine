package org.themarioga.engine.cah.exceptions;

import org.themarioga.commons.engine.exceptions.ApplicationException;
import org.themarioga.engine.cah.enums.CAHErrorEnum;

/**
 * Error de negocio propio de CAH.
 * <p>
 * Extiende {@link ApplicationException} a propósito: antes heredaba de {@code RuntimeException} por
 * su cuenta, duplicando la misma clase, y eso hacía que un {@code catch (ApplicationException)} no
 * capturase ningún error de diccionarios ni de cartas. En la capa de bots eso se traducía en que
 * esos fallos no le llegaban al usuario: se los comía el catch genérico y el bot se quedaba callado.
 */
public class CAHApplicationException extends ApplicationException {

    public CAHApplicationException(CAHErrorEnum error) {
        super(error);
    }

    public CAHApplicationException(String message) {
        super(message);
    }

    @Override
    public CAHErrorEnum getErrorEnum() {
        return (CAHErrorEnum) super.getErrorEnum();
    }

}
