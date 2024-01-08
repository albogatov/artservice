package com.example.highload.image.repos;

import com.example.highload.image.model.enums.RoleType;
import com.example.highload.image.model.inner.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(RoleType name);

}
