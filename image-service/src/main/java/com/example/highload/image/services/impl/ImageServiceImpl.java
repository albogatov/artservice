package com.example.highload.image.services.impl;

import com.example.highload.image.services.ImageService;
import com.example.highload.image.model.enums.ImageObjectType;
import com.example.highload.image.model.inner.Image;
import com.example.highload.image.model.inner.ImageObject;
import com.example.highload.image.model.network.ImageDto;
import com.example.highload.image.repos.ImageObjectRepository;
import com.example.highload.image.repos.ImageRepository;
import com.example.highload.image.services.ImageService;
import com.example.highload.utils.DataTransformer;
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

    private final OrderService orderService;
    private final ProfileService profileService;

    private final ImageRepository imageRepository;
    private final ImageObjectRepository imageObjectRepository;

    private final DataTransformer dataTransformer;

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
        return imageRepository.save(dataTransformer.imageFromDto(imageDto));
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {NoSuchElementException.class, Exception.class})
    public List<Image> saveImagesForOrder(List<ImageDto> imageDtos, int orderId) {
        ClientOrder order = orderService.getOrderById(orderId);
        List<Image> images = imageRepository.saveAll(imageDtos.stream().map(dataTransformer::imageFromDto).toList());
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
    public List<Image> saveImageForProfile(List<ImageDto> imageDtos, int profileId) {
        Profile profile = profileService.findById(profileId);
        List<Image> images = imageRepository.saveAll(imageDtos.stream().map(dataTransformer::imageFromDto).toList());
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
    public void removeAllImagesForProfile(Profile profile) {
        imageRepository.deleteAllByImageObject_Profile(profile);
    }

    @Override
    public void removeAllImagesForOrder(ClientOrder order) {
        imageRepository.deleteAllByImageObject_Order(order);
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {Exception.class})
    public void removeImageForProfile(int imageId, int profileId) {
        imageObjectRepository.deleteByImage_IdAndProfile_Id(imageId, profileId);
        imageRepository.deleteById(imageId);
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {NoSuchElementException.class, Exception.class})
    public Image changeMainImageOfProfile(ImageDto imageDto, int profileId) {
        Image newImage = imageRepository.save(dataTransformer.imageFromDto(imageDto));
        Image oldImage = profileService.setNewMainImage(profileId, newImage);
        if (oldImage != null) {
            imageRepository.deleteById(oldImage.getId());
        }
        return newImage;
    }
}
