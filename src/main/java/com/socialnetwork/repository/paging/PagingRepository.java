package com.socialnetwork.repository.paging;


import com.socialnetwork.domain.Entity;
import com.socialnetwork.repository.CrudRepository;
public interface PagingRepository<ID ,
        E extends Entity<ID>>
        extends CrudRepository<ID, E> {

    Page<E> findAll(Pageable pageable);   // Pageable e un fel de paginator
}
