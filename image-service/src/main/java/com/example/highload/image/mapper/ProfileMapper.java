package com.example.highload.image.mapper;

import com.example.highload.image.model.inner.ClientOrder;
import com.example.highload.image.model.inner.Profile;
import com.example.highload.image.model.network.OrderDto;
import com.example.highload.image.model.network.ProfileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    ProfileDto profileToDto(Profile profile);

    Profile profileDtoToProfile(ProfileDto profileDto);

    List<Profile> profileDtoListToProfileList(List<ProfileDto> profiles);
    List<ProfileDto> profileListToProfileDtoList(List<Profile> profiles);
}
