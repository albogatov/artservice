package com.example.highload.image.services;

import com.example.highload.image.model.inner.Image;
import com.example.highload.image.model.network.ImageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ImageService {

    Page<Image> findAllProfileImages(int profileId, Pageable pageable);

    Page<Image> findAllOrderImages(int orderId, Pageable pageable);

    Image saveImage(ImageDto imageDto);

    List<Image> saveImagesForOrder(List<ImageDto> imageDtos, int orderId, String token);

    List<Image> saveImageForProfile(List<ImageDto> imageDtos, int profileId, String token);

    void removeImageForOrder(int imageId, int orderId);

    void removeImageById(int imageId);

    void removeAllImagesForProfile(Integer profileId);

    void removeAllImagesForOrder(Integer orderId);

    void removeImageForProfile(int imageId, int profileId);

    Image changeMainImageOfProfile(ImageDto imageDto, int profileId, String token);

}
