package com.example.highload.profile.services.impl;

import com.example.highload.profile.mapper.ProfileMapper;
import com.example.highload.profile.model.inner.Image;
import com.example.highload.profile.model.inner.Profile;
import com.example.highload.profile.model.network.ProfileDto;
import com.example.highload.profile.services.ProfileService;
import com.example.highload.profile.repos.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @Override
    public Profile saveProfileForUser(ProfileDto profileDto, int userId) {
        profileDto.setUserId(userId);
        return profileRepository.save(profileMapper.profileDtoToProfile(profileDto));
    }

    @Override
    public Profile editProfile(ProfileDto profileDto, int id) {
        Profile profile = profileRepository.findById(id).orElseThrow();
        profile.setAbout(profileDto.getAbout());
        profile.setEducation(profileDto.getEducation());
        profile.setExperience(profileDto.getExperience());
        profile.setMail(profileDto.getMail());
        profile.setName(profileDto.getName());
        profileRepository.save(profile);
        return profile;
    }

    @Override
    public Profile findById(int id) {
        return profileRepository.findById(id).orElseThrow();
    }

    @Override
    public Profile findByIdOrElseNull(int id) {
        return profileRepository.findById(id).orElse(null);
    }

    @Override
    public Profile findByUserIdElseNull(int userId) {
        return profileRepository.findByUser_Id(userId).orElse(null);
    }

    @Override
    public Page<Profile> findAllProfiles(Pageable pageable) {
        return profileRepository.findAll(pageable);
    }

    @Override
    public Image setNewMainImage(int profileId, Image newImage) {
        Profile profile = profileRepository.findById(profileId).orElseThrow();
        Image oldImage = profile.getImage();
        profile.setImage(newImage);
        profileRepository.save(profile);
        return oldImage;
    }
}
