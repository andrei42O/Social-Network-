package com.socialnetwork.domain;

import java.io.Serializable;
import java.util.Objects;

public class Entity<ID> implements Serializable {

    private static final long serialVersionUID = 7331115341259248461L;
    private ID id;

    /**
     * Getter si setter pentru Id
     */
    public ID getId() {
        return id;
    }
    public void setId(ID id) {
        this.id = id;
    }

    /**
     * equal
     *
     * @param o
     * @return true if the entities have the same id
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;
        Entity<?> entity = (Entity<?>) o;
        return getId().equals(entity.getId());
    }

    /**
     * hashcode
     *
     * @return the hashcode of the entity
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    /**
     * toString method
     *
     * @return
     */
    @Override
    public String toString() {
        return "Entity{" + "id=" + id + '}';
    }
}