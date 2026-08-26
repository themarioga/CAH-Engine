package org.themarioga.engine.cah.models.dictionaries;

import jakarta.persistence.*;
import org.themarioga.commons.engine.models.User;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class DictionaryCollaborator implements Serializable {

    @Id
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Dictionary dictionary;

    @Id
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private Boolean canEdit;
    @Column(nullable = false)
    private Boolean accepted;

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictionaryCollaborator that = (DictionaryCollaborator) o;
        return Objects.equals(dictionary, that.dictionary) && Objects.equals(user, that.user) && Objects.equals(canEdit, that.canEdit) && Objects.equals(accepted, that.accepted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dictionary, user, canEdit, accepted);
    }

    @Override
    public String toString() {
        return "DeckCollaborator{canEdit=" + canEdit + ", accepted=" + accepted + '}';
    }

}
