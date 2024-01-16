package com.example.highload.profile;

import com.example.highload.profile.feign.UserServiceFeignClient;
import com.example.highload.profile.mapper.ProfileMapper;
import com.example.highload.profile.model.inner.Profile;
import com.example.highload.profile.model.inner.User;
import com.example.highload.profile.model.network.ProfileDto;
import com.example.highload.profile.repos.ProfileRepository;
import com.example.highload.profile.services.impl.ProfileServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private ProfileMapper profileMapper;

    @Mock
    private ProfileRepository profileRepository;
    @InjectMocks
    private ProfileServiceImpl profileService;

    @Test
    public void save() {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setAbout("About");
        profileDto.setName("Alex");
        profileDto.setEducation("ITMO");
        profileDto.setExperience("Freelance");
        profileDto.setMail("abcde@gmail.com");
        Profile expectedProfile = new Profile();
        expectedProfile.setAbout("About");
        expectedProfile.setName("Alex");
        expectedProfile.setEducation("ITMO");
        expectedProfile.setExperience("Freelance");
        expectedProfile.setMail("abcde@gmail.com");
        when(profileMapper.profileDtoToProfile(profileDto)).thenReturn(expectedProfile);
        expectedProfile.setId(1);
        when(profileRepository.save(expectedProfile)).thenReturn(expectedProfile);
        Profile profile = profileService.saveProfileForUser(profileDto, 1);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(profile.getId()),
                () -> Assertions.assertEquals("Alex", profile.getName()),
                () -> Assertions.assertEquals("About", profile.getAbout()),
                () -> Assertions.assertEquals("ITMO", profile.getEducation()),
                () -> Assertions.assertEquals("Freelance", profile.getExperience()),
                () -> Assertions.assertEquals("abcde@gmail.com", profile.getMail())
        );
    }

    @Test
    public void edit() {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setAbout("About");
        profileDto.setName("Alex");
        profileDto.setEducation("ITMO");
        profileDto.setExperience("Freelance");
        profileDto.setMail("abcde@gmail.com");
        Profile expectedProfile = new Profile();
        expectedProfile.setAbout("About");
        expectedProfile.setName("Alex");
        expectedProfile.setEducation("ITMO");
        expectedProfile.setExperience("Freelance");
        expectedProfile.setMail("abcde@gmail.com");
        expectedProfile.setId(1);
        when(profileRepository.save(expectedProfile)).thenReturn(expectedProfile);
        when(profileRepository.findById(1)).thenReturn(Optional.of(expectedProfile));
        Profile profile = profileService.editProfile(profileDto, 1);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(profile.getId()),
                () -> Assertions.assertEquals("Alex", profile.getName()),
                () -> Assertions.assertEquals("About", profile.getAbout()),
                () -> Assertions.assertEquals("ITMO", profile.getEducation()),
                () -> Assertions.assertEquals("Freelance", profile.getExperience()),
                () -> Assertions.assertEquals("abcde@gmail.com", profile.getMail())
        );
    }

    @Test
    public void findAll() {
        Pageable pageable = Mockito.mock(Pageable.class);
        Profile expectedProfile1 = new Profile();
        expectedProfile1.setAbout("About");
        expectedProfile1.setName("Alex");
        expectedProfile1.setEducation("ITMO");
        expectedProfile1.setExperience("Freelance");
        expectedProfile1.setMail("abcde@gmail.com");
        expectedProfile1.setId(1);
        Profile expectedProfile2 = new Profile();
        expectedProfile2.setAbout("About");
        expectedProfile2.setName("Alex");
        expectedProfile2.setEducation("ITMO");
        expectedProfile2.setExperience("Freelance");
        expectedProfile2.setMail("abcde@gmail.com");
        expectedProfile2.setId(2);
        Page<Profile> profiles = new PageImpl<>(List.of(expectedProfile1, expectedProfile2));
        when(profileRepository.findAll(pageable)).thenReturn(profiles);
        Page<Profile> profile = profileService.findAllProfiles(pageable);

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, profile.getTotalElements()),
                () -> Assertions.assertEquals(1, profile.getTotalPages())
        );
    }


}
