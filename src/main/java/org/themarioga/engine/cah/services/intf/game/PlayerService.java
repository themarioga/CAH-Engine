package org.themarioga.engine.cah.services.intf.game;

import org.themarioga.engine.cah.models.game.Game;
import org.themarioga.engine.cah.models.game.Player;
import org.themarioga.engine.cah.models.dictionaries.Card;

import java.util.List;

public interface PlayerService extends org.themarioga.commons.engine.services.intf.PlayerService<Player, Game> {

    void insertWhiteCardsIntoPlayerHand(Player player, List<Card> cardsToTransfer);

    Player incrementPoints(Player player);

    Player removeCardFromHand(Player player, Card card);

}
