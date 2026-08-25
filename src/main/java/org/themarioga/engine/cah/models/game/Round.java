package org.themarioga.engine.cah.models.game;

import jakarta.persistence.*;
import org.themarioga.engine.cah.enums.RoundStatusEnum;
import org.themarioga.engine.cah.models.dictionaries.Card;
import org.themarioga.engine.commons.models.Base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Round extends Base implements Serializable {

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Game game;

    @Column(nullable = false)
    private Integer roundNumber;
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private RoundStatusEnum status;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Card roundBlackCard;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn
    private Player roundPresident;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "round", orphanRemoval = true)
    private List<PlayedCard> playedCards = new ArrayList<>(0);

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "round", orphanRemoval = true)
    private List<VotedCard> votedCards = new ArrayList<>(0);

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public RoundStatusEnum getStatus() {
        return status;
    }

    public void setStatus(RoundStatusEnum status) {
        this.status = status;
    }

    public Card getRoundBlackCard() {
        return roundBlackCard;
    }

    public void setRoundBlackCard(Card currentBlackCard) {
        this.roundBlackCard = currentBlackCard;
    }

    public Player getRoundPresident() {
        return roundPresident;
    }

    public void setRoundPresident(Player currentPresident) {
        this.roundPresident = currentPresident;
    }

    public List<PlayedCard> getPlayedCards() {
        return playedCards;
    }

    public void setPlayedCards(List<PlayedCard> playedCards) {
        this.playedCards = playedCards;
    }

    public List<VotedCard> getVotedCards() {
        return votedCards;
    }

    public void setVotedCards(List<VotedCard> votedCards) {
        this.votedCards = votedCards;
    }

    @Override
    public String toString() {
        return "Round{id=" + getId() + ", roundBlackCard=" + roundBlackCard + ", roundNumber=" + roundNumber + ", status=" + status + '}';
    }

}
