package com.socialnetwork.domain;

import java.time.LocalDate;
import java.util.Objects;

public class FriendRequest extends Entity<Long>{
    private Long idFrom;
    private Long idTo;
    private Long state;
    private LocalDate date;

    /// 0 - pending
    /// 1 - acceptata
    /// 2 - refuzata


    /**
     * Constructor
     * @param from id from
     * @param to id tpo
     */
    public FriendRequest(Long from, Long to, LocalDate date)
    {
        idFrom = from;
        idTo = to;
        state = 0L;
        this.date = date;
    }

    /**
     * Gettere
     * @return
     */
    public Long getIdFrom(){
        return this.idFrom;
    }
    public Long getIdTo(){
        return this.idTo;
    }
    public Long getState()
    {
        return this.state;
    }
    public LocalDate getDate(){return this.date;}

    /**
     * Set state
     * @param s new state
     */
    public void setState(Long s)
    {
        this.state = s;
    }

    /**
     * Override to string function
     * @return
     */
    @Override
    public String toString() {
        return "FriendRequest{" +
                "idFrom=" + idFrom +
                ", idTo=" + idTo +
                ", state=" + state +
                '}';
    }

    /**
     * Override equals and hashCode functions
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FriendRequest that = (FriendRequest) o;
        return Objects.equals(idFrom, that.idFrom) && Objects.equals(idTo, that.idTo) && Objects.equals(state, that.state);
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idFrom, idTo, state);
    }
}
