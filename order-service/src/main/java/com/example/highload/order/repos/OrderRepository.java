package com.example.highload.order.repos;

import com.example.highload.order.model.enums.OrderStatus;
import com.example.highload.order.model.inner.ClientOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<ClientOrder, Integer> {

    Optional<List<ClientOrder>> findAllByUser_Id(Integer id);
    Optional<List<ClientOrder>> findAllByUser_IdAndStatus(Integer id, OrderStatus status);
    Optional<List<ClientOrder>> findAllByTags_Name(String name);
    Optional<List<ClientOrder>> findAllByTags_Id(Integer id);


    @Query(value = "select * from public.order where id in " +
            "(select order_id from order_tags " +
            "where tag_id in :tagIds " +
            "group by order_tags.order_id " +
            "having count(order_id) = :tagNum)", nativeQuery = true)
    Optional<List<ClientOrder>> findAllByMultipleTagsIds(@Param("tagIds") List<Integer> tagIds,
                                               @Param("tagNum") int tagNum);

    @Query(value = "select * from public.order where id in " +
            "(select order_id from order_tags " +
            "where tag_id in :tagIds " +
            "group by order_tags.order_id " +
            "having count(order_id) = :tagNum) " +
            "and public.order.status = :orderStatus", nativeQuery = true)
    Optional<List<ClientOrder>> findAllByMultipleTagsIdsAndStatus(@Param("tagIds") List<Integer> tagIds,
                                                        @Param("tagNum") int tagNum,
                                                        @Param("orderStatus") String status);



}
