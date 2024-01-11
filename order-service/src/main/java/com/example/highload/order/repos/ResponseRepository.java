package com.example.highload.order.repos;

import com.example.highload.order.model.inner.Response;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponseRepository extends CrudRepository<Response, Integer> {

    Optional<List<Response>> findAllByUser_Id(Integer id);
    Optional<List<Response>> findAllByOrder_Id(Integer id);


}
