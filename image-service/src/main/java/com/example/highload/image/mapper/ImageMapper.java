package com.example.highload.image.mapper;

import com.example.highload.image.model.enums.RoleType;
import com.example.highload.image.model.inner.ClientOrder;
import com.example.highload.image.model.inner.Image;
import com.example.highload.image.model.inner.Role;
import com.example.highload.image.model.inner.User;
import com.example.highload.image.model.network.ImageDto;
import com.example.highload.image.model.network.OrderDto;
import com.example.highload.image.model.network.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageDto imageToDto(Image image);

    Image imageDtoToImage(ImageDto imageDto);

    List<Image> imageDtoListToImageList(List<ImageDto> images);
    List<ImageDto> imageListToImageDtoList(List<Image> images);
}
