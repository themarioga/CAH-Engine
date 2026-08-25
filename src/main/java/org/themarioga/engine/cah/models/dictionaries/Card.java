package org.themarioga.engine.cah.models.dictionaries;

import jakarta.persistence.*;
import org.themarioga.engine.cah.enums.CardTypeEnum;
import org.themarioga.engine.commons.models.Base;

@Entity
public class Card extends Base {

    @Enumerated(EnumType.ORDINAL)
    @Column(length = 1, nullable = false)
    private CardTypeEnum type;
    @Column(length = 256, nullable = false)
    private String text;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn()
    private Dictionary dictionary;

    public CardTypeEnum getType() {
        return type;
    }

    public void setType(CardTypeEnum type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public String toString() {
        return "Card{id=" + getId() + ", type=" + type + ", text='" + text + '\'' + '}';
    }

}
