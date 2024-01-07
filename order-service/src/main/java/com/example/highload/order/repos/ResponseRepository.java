package com.example.highload.order.repos;

import com.example.highload.order.model.inner.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Integer> {

    Optional<Page<Response>> findAllByUser_Id(Integer id, Pageable pageable);
    Optional<Page<Response>> findAllByOrder_Id(Integer id, Pageable pageable);


}
