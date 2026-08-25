package org.themarioga.engine.cah.models.game;

import jakarta.persistence.*;
import org.themarioga.engine.cah.enums.PunctuationModeEnum;
import org.themarioga.engine.cah.enums.VotationModeEnum;
import org.themarioga.engine.cah.models.dictionaries.Card;
import org.themarioga.engine.cah.models.dictionaries.Dictionary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Game extends org.themarioga.engine.commons.models.Game implements Serializable {

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private VotationModeEnum votationMode;
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private PunctuationModeEnum punctuationMode;
    @Column(nullable = false)
    private Integer numberOfPointsToWin;
    @Column(nullable = false)
    private Integer numberOfRoundsToEnd;
    @Column(nullable = false)
    private Integer maxNumberOfPlayers;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Dictionary dictionary;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "game", orphanRemoval = true)
    private List<Player> players = new ArrayList<>(0);

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "game_black_cards_deck", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "card_id"))
    private List<Card> blackCardsDeck = new ArrayList<>(0);

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "game_white_cards_deck", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "card_id"))
    private List<Card> whiteCardsDeck = new ArrayList<>(0);

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "game", orphanRemoval = true)
    private Round currentRound;

    public VotationModeEnum getVotationMode() {
        return votationMode;
    }

    public void setVotationMode(VotationModeEnum type) {
        this.votationMode = type;
    }

    public PunctuationModeEnum getPunctuationMode() {
        return punctuationMode;
    }

    public void setPunctuationMode(PunctuationModeEnum punctuationType) {
        this.punctuationMode = punctuationType;
    }

    public Integer getNumberOfPointsToWin() {
        return numberOfPointsToWin;
    }

    public void setNumberOfPointsToWin(Integer numberOfCardsToWin) {
        this.numberOfPointsToWin = numberOfCardsToWin;
    }

    public Integer getNumberOfRoundsToEnd() {
        return numberOfRoundsToEnd;
    }

    public void setNumberOfRoundsToEnd(Integer numberOfRounds) {
        this.numberOfRoundsToEnd = numberOfRounds;
    }

    public Integer getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    public void setMaxNumberOfPlayers(Integer maxNumberOfPlayers) {
        this.maxNumberOfPlayers = maxNumberOfPlayers;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Card> getBlackCardsDeck() {
        return blackCardsDeck;
    }

    public void setBlackCardsDeck(List<Card> deckBlackCards) {
        this.blackCardsDeck = deckBlackCards;
    }

    public List<Card> getWhiteCardsDeck() {
        return whiteCardsDeck;
    }

    public void setWhiteCardsDeck(List<Card> deckWhiteCards) {
        this.whiteCardsDeck = deckWhiteCards;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Round currentRound) {
        this.currentRound = currentRound;
    }

    @Override
    public String toString() {
        return "Game{" + super.toString() + "currentRound=" + currentRound + ", votationMode=" + votationMode + ", punctuationMode=" + punctuationMode + ", numberOfCardsToWin=" + numberOfPointsToWin + ", numberOfRounds=" + numberOfRoundsToEnd + ", maxNumberOfPlayers=" + maxNumberOfPlayers + ", dictionary=" + dictionary + '}';
    }

}
