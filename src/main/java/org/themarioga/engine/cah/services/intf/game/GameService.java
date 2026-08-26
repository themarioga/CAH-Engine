package org.themarioga.engine.cah.services.intf.game;

import org.themarioga.engine.cah.enums.VotationModeEnum;
import org.themarioga.engine.cah.models.dictionaries.Dictionary;
import org.themarioga.engine.cah.models.game.Game;
import org.themarioga.engine.cah.models.game.Player;
import org.themarioga.engine.cah.models.game.Round;

public interface GameService extends org.themarioga.commons.engine.services.intf.GameService<Game, Player> {

    Game setVotationMode(Game game, VotationModeEnum type);

    Game setMaxNumberOfPlayers(Game game, int maxNumberOfPlayers);

    Game setNumberOfPointsToWin(Game game, int numberOfCards);

    Game setNumberOfRoundsToEnd(Game game, int numberOfRoundsToEnd);

    Game setDictionary(Game game, Dictionary dictionary);

    Game setCurrentRound(Game game, Round round);

}
