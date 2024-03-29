package com.example.highload.image.repos;

import com.example.highload.image.model.inner.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    Optional<Page<Image>> findAllByImageObject_Order_Id(Integer orderId, Pageable pageable);
    Optional<Page<Image>> findAllByImageObject_Profile_Id(Integer profileId, Pageable pageable);

    Optional<List<Image>> findAllByImageObject_Profile_Id(Integer profileId);

    Optional<List<Image>> findAllByImageObject_Order_Id(Integer orderId);

    void deleteAllByImageObject_OrderId(Integer orderId);
    void deleteAllByImageObject_ProfileId(Integer profileId);

}