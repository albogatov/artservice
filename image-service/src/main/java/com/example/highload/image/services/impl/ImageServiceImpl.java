package com.example.highload.image.services.impl;

import com.example.highload.image.config.MinioConfig;
import com.example.highload.image.feign.OrderServiceFeignClient;
import com.example.highload.image.feign.ProfileServiceFeignClient;
import com.example.highload.image.mapper.ImageMapper;
import com.example.highload.image.mapper.OrderMapper;
import com.example.highload.image.mapper.ProfileMapper;
import com.example.highload.image.model.inner.ClientOrder;
import com.example.highload.image.model.inner.Profile;
import com.example.highload.image.services.ImageService;
import com.example.highload.image.model.enums.ImageObjectType;
import com.example.highload.image.model.inner.Image;
import com.example.highload.image.model.inner.ImageObject;
import com.example.highload.image.model.network.ImageDto;
import com.example.highload.image.repos.ImageObjectRepository;
import com.example.highload.image.repos.ImageRepository;
import com.example.highload.image.utils.FileTypeUtils;
import com.example.highload.image.utils.MinioUtil;
import io.minio.MinioClient;
import io.minio.messages.Bucket;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final MinioConfig minioConfig;
    private final MinioUtil minioUtil;

    private final OrderServiceFeignClient orderService;
    private final ProfileServiceFeignClient profileService;

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final ImageObjectRepository imageObjectRepository;

    private final OrderMapper orderMapper;
    private final ProfileMapper profileMapper;


    @Override
    public Page<Image> findAllProfileImages(int profileId, Pageable pageable) {
        Page<Image> images = imageRepository.findAllByImageObject_Profile_Id(profileId, pageable).orElse(Page.empty());
        images.stream().map(imageDto -> {
            imageDto.setUrl(minioUtil.getObjectUrl(minioConfig.getDefaultBucketName(), imageDto.getUrl()));
            return imageDto;
        });
        return images;
    }

    @Override
    public Page<Image> findAllOrderImages(int orderId, Pageable pageable) {
        Page<Image> images = imageRepository.findAllByImageObject_Order_Id(orderId, pageable).orElse(Page.empty());
        images.stream().map(imageDto -> {
            imageDto.setUrl(minioUtil.getObjectUrl(minioConfig.getDefaultBucketName(), imageDto.getUrl()));
            return imageDto;
        });
        return images;
    }

    @Override
    public Image saveImage(ImageDto imageDto) {
        String uuid = UUID.randomUUID().toString();
        minioUtil.putObject(minioConfig.getDefaultBucketName(), imageDto.getImage(),
                uuid + '/' + imageDto.getImage().getOriginalFilename(), FileTypeUtils.getFileType(imageDto.getImage()));
        imageDto.setUrl(uuid + '/' + imageDto.getImage().getOriginalFilename());
        return imageRepository.save(imageMapper.imageDtoToImage(imageDto));
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {NoSuchElementException.class, Exception.class})
    public List<Image> saveImagesForOrder(List<ImageDto> imageDtos, int orderId, String token) {
        ClientOrder order = orderMapper.orderDtoToOrder(orderService.getById(orderId, token).getBody());
        imageDtos.stream().forEach(imageDto -> {
            String uuid = UUID.randomUUID().toString();
            minioUtil.putObject(minioConfig.getDefaultBucketName(), imageDto.getImage(),
                    uuid + '/' + imageDto.getImage().getOriginalFilename(), FileTypeUtils.getFileType(imageDto.getImage()));
            imageDto.setUrl(uuid + '/' + imageDto.getImage().getOriginalFilename());
        });
        List<Image> images = imageRepository.saveAll(imageDtos.stream().map(imageMapper::imageDtoToImage).toList());
        List<ImageObject> imageObjects = images.stream().map(image ->
                {
                    ImageObject imageObject = new ImageObject();
                    imageObject.setImage(image);
                    imageObject.setOrder(order);
                    imageObject.setProfile(null);
                    imageObject.setType(ImageObjectType.ORDER_IMAGE);
                    return imageObject;
                }
        ).toList();
        imageObjectRepository.saveAll(imageObjects);
        return images;
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {NoSuchElementException.class, Exception.class})
    public List<Image> saveImageForProfile(List<ImageDto> imageDtos, int profileId, String token) {
        Profile profile = profileMapper.profileDtoToProfile(profileService.getById(profileId, token).getBody());
        imageDtos.stream().forEach(imageDto -> {
            String uuid = UUID.randomUUID().toString();
            minioUtil.putObject(minioConfig.getDefaultBucketName(), imageDto.getImage(),
                    uuid + '/' + imageDto.getImage().getOriginalFilename(), FileTypeUtils.getFileType(imageDto.getImage()));
            imageDto.setUrl(uuid + '/' + imageDto.getImage().getOriginalFilename());
        });
        List<Image> images = imageRepository.saveAll(imageDtos.stream().map(imageMapper::imageDtoToImage).toList());
        List<ImageObject> imageObjects = images.stream().map(image ->
                {
                    ImageObject imageObject = new ImageObject();
                    imageObject.setImage(image);
                    imageObject.setOrder(null);
                    imageObject.setProfile(profile);
                    imageObject.setType(ImageObjectType.PROFILE_IMAGE);
                    return imageObject;
                }
        ).toList();
        imageObjectRepository.saveAll(imageObjects);
        return images;
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {Exception.class})
    public void removeImageForOrder(int imageId, int orderId) {
        Image image = imageRepository.findById(imageId).orElseThrow();
        minioUtil.removeObject(minioConfig.getDefaultBucketName(), image.getUrl());
        imageObjectRepository.deleteByImage_IdAndOrder_Id(imageId, orderId);
        imageRepository.deleteById(imageId);
    }

    @Override
    public void removeImageById(int imageId) {
        Image image = imageRepository.findById(imageId).orElseThrow();
        minioUtil.removeObject(minioConfig.getDefaultBucketName(), image.getUrl());
        imageRepository.deleteById(imageId);
    }

    @Override
    public void removeAllImagesForProfile(Integer profileId) {
        List<Image> images = imageRepository.findAllByImageObject_Profile_Id(profileId).orElseThrow();
        images.stream().forEach(image -> {
            minioUtil.removeObject(minioConfig.getDefaultBucketName(), image.getUrl());
        });
        imageRepository.deleteAllByImageObject_ProfileId(profileId);
    }

    @Override
    public void removeAllImagesForOrder(Integer orderId) {
        List<Image> images = imageRepository.findAllByImageObject_Order_Id(orderId).orElseThrow();
        images.stream().forEach(image -> {
            minioUtil.removeObject(minioConfig.getDefaultBucketName(), image.getUrl());
        });
        imageRepository.deleteAllByImageObject_OrderId(orderId);
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {Exception.class})
    public void removeImageForProfile(int imageId, int profileId) {
        Image image = imageRepository.findById(imageId).orElseThrow();
        minioUtil.removeObject(minioConfig.getDefaultBucketName(), image.getUrl());
        imageObjectRepository.deleteByImage_IdAndProfile_Id(imageId, profileId);
        imageRepository.deleteById(imageId);
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {NoSuchElementException.class, Exception.class})
    public Image changeMainImageOfProfile(ImageDto imageDto, int profileId, String token) {
        String uuid = UUID.randomUUID().toString();
        minioUtil.putObject(minioConfig.getDefaultBucketName(), imageDto.getImage(),
                uuid + '/' + imageDto.getImage().getOriginalFilename(), FileTypeUtils.getFileType(imageDto.getImage()));
        imageDto.setUrl(uuid + '/' + imageDto.getImage().getOriginalFilename());
        Image newImage = imageRepository.save(imageMapper.imageDtoToImage(imageDto));
        Image oldImage = imageMapper.imageDtoToImage(profileService.setNewMainImage(profileId, imageDto, token).getBody());
        if (oldImage != null) {
            minioUtil.removeObject(minioConfig.getDefaultBucketName(), oldImage.getUrl());
            imageRepository.deleteById(oldImage.getId());
        }
        return newImage;
    }
}
