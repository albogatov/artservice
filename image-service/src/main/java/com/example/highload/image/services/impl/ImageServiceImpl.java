package com.example.highload.image.services.impl;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final OrderServiceFeignClient orderService;
    private final ProfileServiceFeignClient profileService;

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final ImageObjectRepository imageObjectRepository;

    private final OrderMapper orderMapper;
    private final ProfileMapper profileMapper;


    @Override
    public Page<Image> findAllProfileImages(int profileId, Pageable pageable) {
        return imageRepository.findAllByImageObject_Profile_Id(profileId, pageable).orElse(Page.empty());
    }

    @Override
    public Page<Image> findAllOrderImages(int orderId, Pageable pageable) {
        return imageRepository.findAllByImageObject_Order_Id(orderId, pageable).orElse(Page.empty());
    }

    @Override
    public Image saveImage(ImageDto imageDto) {
        return imageRepository.save(imageMapper.imageDtoToImage(imageDto));
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {NoSuchElementException.class, Exception.class})
    public List<Image> saveImagesForOrder(List<ImageDto> imageDtos, int orderId, String token) {
        ClientOrder order = orderMapper.orderDtoToOrder(orderService.getById(orderId, token).getBody());
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
        imageObjectRepository.deleteByImage_IdAndOrder_Id(imageId, orderId);
        imageRepository.deleteById(imageId);
    }

    @Override
    public void removeImageById(int imageId) {
        imageRepository.deleteById(imageId);
    }

    @Override
    public void removeAllImagesForProfile(Integer profileId) {
        imageRepository.deleteAllByImageObject_Profile(profileId);
    }

    @Override
    public void removeAllImagesForOrder(Integer orderId) {
        imageRepository.deleteAllByImageObject_Order(orderId);
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {Exception.class})
    public void removeImageForProfile(int imageId, int profileId) {
        imageObjectRepository.deleteByImage_IdAndProfile_Id(imageId, profileId);
        imageRepository.deleteById(imageId);
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {NoSuchElementException.class, Exception.class})
    public Image changeMainImageOfProfile(ImageDto imageDto, int profileId, String token) {
        Image newImage = imageRepository.save(imageMapper.imageDtoToImage(imageDto));
        Image oldImage = imageMapper.imageDtoToImage(profileService.setNewMainImage(profileId, imageDto, token).getBody());
        if (oldImage != null) {
            imageRepository.deleteById(oldImage.getId());
        }
        return newImage;
    }
}
