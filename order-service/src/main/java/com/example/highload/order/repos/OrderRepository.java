package com.example.highload.order.repos;

import com.example.highload.order.model.enums.OrderStatus;
import com.example.highload.order.model.inner.ClientOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<ClientOrder, Integer> {

    Optional<Page<ClientOrder>> findAllByUser_Id(Integer id, Pageable pageable);
    Optional<Page<ClientOrder>> findAllByUser_IdAndStatus(Integer id, OrderStatus status, Pageable pageable);
    Optional<Page<ClientOrder>> findAllByTags_Name(String name, Pageable pageable);
    Optional<Page<ClientOrder>> findAllByTags_Id(Integer id, Pageable pageable);


    @Query(value = "select * from public.order where id in " +
            "(select order_id from order_tags " +
            "where tag_id in :tagIds " +
            "group by order_tags.order_id " +
            "having count(order_id) = :tagNum)", nativeQuery = true)
    Optional<Page<ClientOrder>> findAllByMultipleTagsIds(@Param("tagIds") List<Integer> tagIds,
                                               @Param("tagNum") int tagNum,
                                               Pageable pageable);

    @Query(value = "select * from public.order where id in " +
            "(select order_id from order_tags " +
            "where tag_id in :tagIds " +
            "group by order_tags.order_id " +
            "having count(order_id) = :tagNum) " +
            "and public.order.status = :orderStatus", nativeQuery = true)
    Optional<Page<ClientOrder>> findAllByMultipleTagsIdsAndStatus(@Param("tagIds") List<Integer> tagIds,
                                                        @Param("tagNum") int tagNum,
                                                        @Param("orderStatus") String status,
                                                        Pageable pageable);



}
