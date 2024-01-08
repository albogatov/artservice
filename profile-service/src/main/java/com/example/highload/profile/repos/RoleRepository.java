package com.example.highload.profile.repos;

import com.example.highload.profile.model.enums.RoleType;
import com.example.highload.profile.model.inner.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(RoleType name);

}
