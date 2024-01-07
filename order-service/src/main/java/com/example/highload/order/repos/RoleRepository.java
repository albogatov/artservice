package com.example.highload.order.repos;

import com.example.highload.order.model.enums.RoleType;
import com.example.highload.order.model.inner.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(RoleType name);

}
