package com.example.highload.profile.repos;

import com.example.highload.profile.model.inner.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Optional<Page<Review>> findAllByProfile_Id(Integer id, Pageable pageable);
}
