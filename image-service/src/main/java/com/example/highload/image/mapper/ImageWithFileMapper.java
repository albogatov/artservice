package com.example.highload.image.mapper;

import com.example.highload.image.model.inner.Image;
import com.example.highload.image.model.inner.ImageWithFile;
import com.example.highload.image.model.network.ImageDto;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ImageWithFileMapper {
    Image imageWithFileToImage(ImageWithFile imageDto);

    ImageDto imageWithFileToImageDto(ImageWithFile imageDto);

    List<Image> imageWithFileListToImageList(List<ImageWithFile> images);
}
