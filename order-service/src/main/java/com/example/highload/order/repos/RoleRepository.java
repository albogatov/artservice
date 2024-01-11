package com.example.highload.order.repos;

import com.example.highload.order.model.enums.RoleType;
import com.example.highload.order.model.inner.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface RoleRepository extends ReactiveCrudRepository<Role, Integer> {

    Mono<Role> findByName(RoleType name);

}
