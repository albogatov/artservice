package com.example.highload.profile.mapper;

import com.example.highload.profile.model.inner.Image;
import com.example.highload.profile.model.network.ImageDto;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageDto imageToDto(Image image);

    Image imageDtoToImage(ImageDto imageDto);

    List<Image> imageDtoListToImageList(List<ImageDto> images);
    List<ImageDto> imageListToImageDtoList(List<Image> images);
}
