package com.socialnetwork.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Friendship extends Entity<Pair<Long, Long>>{

    private Long id1;
    private Long id2;
    private LocalDate date;


    /**
     * Constructor
     * @param id1 -> id prim utilizator
     * @param id2 -> id second utilizator
     * @param date -> data
     */
    public Friendship(Long id1, Long id2, LocalDate date)
    {
        this.id1 = id1;
        this.id2 = id2;
        this.date = date;
    }
    public Friendship()
    {
        this.id1 = 0L;
        this.id2 = 0L;
    }


    /**
     *
     * @return id prim utilizator
     */
    public Long getId1()
    {
        return this.id1;
    }

    /**
     *
     * @return id utilizator 2
     */
    public Long getId2()
    {
        return this.id2;
    }

    /**
     *
     * @return data
     */
    public LocalDate getDate()
    {
        return this.date;
    }

}
