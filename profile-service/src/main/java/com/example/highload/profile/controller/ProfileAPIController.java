package com.example.highload.profile.controller;

import com.example.highload.profile.feign.UserServiceFeignClient;
import com.example.highload.profile.mapper.ImageMapper;
import com.example.highload.profile.mapper.ProfileMapper;
import com.example.highload.profile.model.inner.Image;
import com.example.highload.profile.model.inner.Profile;
import com.example.highload.profile.model.network.ImageDto;
import com.example.highload.profile.model.network.ProfileDto;
import com.example.highload.profile.model.network.UserDto;
import com.example.highload.profile.services.ProfileService;
import com.example.highload.profile.utils.PaginationHeadersCreator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/api/profile/core")
@RequiredArgsConstructor
public class ProfileAPIController {

    private final ProfileService profileService;
    private final UserServiceFeignClient userService;
    private final PaginationHeadersCreator paginationHeadersCreator;
    private final ProfileMapper profileMapper;
    private final ImageMapper imageMapper;

    @PostMapping("/profile/add/{userId}")
    public ResponseEntity<?> addProfile(@Valid @RequestBody ProfileDto profile, @PathVariable int userId) {

        if (profileService.findByUserIdElseNull(userId) == null) {
            profileService.saveProfileForUser(profile, userId);
            return new ResponseEntity<>("Profile successfully added", HttpStatus.OK);
        }
        return new ResponseEntity<>("Profile already added", HttpStatus.BAD_REQUEST);
    }
    @PostMapping("/edit/{id}")
    public ResponseEntity<?> edit(@Valid @RequestBody ProfileDto data, @PathVariable int id, @RequestHeader(value = "Authorization") String token) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto user = userService.findByLoginElseNull(login, token).getBody();
        if (user != null) {
            int userId = user.getId();
            if (data.getUserId() == userId) {
                profileService.editProfile(data, id);
                return ResponseEntity.ok("Profile edited");
            }

        }
        return ResponseEntity.badRequest().body("Not allowed to edit profile!");
    }

    @GetMapping("/all/{page}")
    public ResponseEntity<?> getAll(@PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 50);
        Page<Profile> entityList = profileService.findAllProfiles(pageable);
        List<ProfileDto> dtoList = profileMapper.profileListToProfileDtoList(entityList.getContent());
        HttpHeaders responseHeaders = paginationHeadersCreator.pageWithTotalElementsHeadersCreate(entityList);
        return ResponseEntity.ok().headers(responseHeaders).body(dtoList);
    }

    @GetMapping("/user/single/{userId}")
    public ResponseEntity<?> findByUserId(@PathVariable int userId) {
        Profile profile = profileService.findByUserIdElseNull(userId);
        return ResponseEntity.ok().body(profileMapper.profileToDto(profile));
    }

    @GetMapping("/single/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        Profile entity = profileService.findById(id);
        return ResponseEntity.ok(profileMapper.profileToDto(entity));
    }

    @PostMapping("/all/exists")
    public ResponseEntity<Boolean> checkProfileExistsByIds(@RequestBody List<Integer> ids) {
        Boolean result = Boolean.valueOf(ids.stream().allMatch(id -> {
            Profile entity = profileService.findByIdOrElseNull(id);
            if (entity == null) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        }));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/single/{id}/image")
    public ResponseEntity<ImageDto> setNewMainImage(@PathVariable int id, @RequestBody ImageDto imageDto) {
        Image old = profileService.setNewMainImage(id, imageMapper.imageDtoToImage(imageDto));
        return ResponseEntity.ok(imageMapper.imageToDto(old));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions() {
        return ResponseEntity.badRequest().body("Request body validation failed!");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleServiceExceptions() {
        return ResponseEntity.badRequest().body("Wrong ids in path!");
    }

}
