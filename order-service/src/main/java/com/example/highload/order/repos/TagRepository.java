package com.example.highload.order.repos;

import com.example.highload.order.model.inner.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface TagRepository extends ReactiveCrudRepository<Tag, Integer> {

    Mono<Tag> findByName(String name);

}
