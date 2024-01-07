package com.example.highload.login.repos;

import com.example.highload.login.model.enums.RoleType;
import com.example.highload.login.model.inner.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(RoleType name);

}
