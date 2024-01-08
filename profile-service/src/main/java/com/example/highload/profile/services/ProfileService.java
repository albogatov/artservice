package com.example.highload.profile.services;

import com.example.highload.profile.model.inner.Image;
import com.example.highload.profile.model.inner.Profile;
import com.example.highload.profile.model.network.ProfileDto;
import com.example.highload.profile.model.network.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProfileService {

    Profile saveProfileForUser(ProfileDto profileDto, int userId);
    Profile editProfile(ProfileDto profileDto, int id);

    Profile findById(int id);

    Profile findByUserIdElseNull(int userId);

    Page<Profile> findAllProfiles(Pageable pageable);

    Image setNewMainImage(int profileId, Image newImage);
}
