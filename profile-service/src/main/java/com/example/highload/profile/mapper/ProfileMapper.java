package com.example.highload.profile.mapper;

import com.example.highload.profile.model.inner.Profile;
import com.example.highload.profile.model.inner.Review;
import com.example.highload.profile.model.network.ProfileDto;
import com.example.highload.profile.model.network.ReviewDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ImageMapper.class})
public interface ProfileMapper {

    ProfileDto profileToDto(Profile profile);

    Profile profileDtoToProfile(ProfileDto profileDto);

    List<Profile> profileDtoListToProfileList(List<ProfileDto> profileDtos);
    List<ProfileDto> profileListToProfileDtoList(List<Profile> profiles);

}
