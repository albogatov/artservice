package com.example.highload.image.repos;

import com.example.highload.image.model.inner.ImageObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ImageObjectRepository extends JpaRepository<ImageObject, Integer> {

    void deleteByImage_IdAndOrder_Id(Integer imageId, Integer orderId);

    void deleteByImage_IdAndProfile_Id(Integer imageId, Integer profileId);

}
