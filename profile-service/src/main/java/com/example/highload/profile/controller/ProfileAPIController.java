package com.example.highload.profile.controller;

import com.example.highload.profile.feign.ImageServiceFeignClient;
import com.example.highload.profile.feign.UserServiceFeignClient;
import com.example.highload.profile.model.inner.Image;
import com.example.highload.profile.model.inner.Profile;
import com.example.highload.profile.model.network.ProfileDto;
import com.example.highload.profile.services.ImageService;
import com.example.highload.profile.services.ProfileService;
import com.example.highload.profile.utils.DataTransformer;
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

@RestController
@RequestMapping(value = "/api/profile/client")
@RequiredArgsConstructor
public class ProfileAPIController {

    private final ProfileService profileService;
    private final ImageServiceFeignClient imageService;
    private final UserServiceFeignClient userService;
    private final PaginationHeadersCreator paginationHeadersCreator;
    private final DataTransformer dataTransformer;

//    @PostMapping("/edit/{id}")
//    public ResponseEntity edit(@Valid @RequestBody ProfileDto data, @PathVariable int id){
//        profileService.editProfile(data, id);
//        return ResponseEntity.ok("Profile edited");
//    }

    @PostMapping("/profile/add/{userId}")
    public ResponseEntity<?> addProfile(@Valid @RequestBody ProfileDto profile, @PathVariable int userId) {

        if (profileService.findByUserIdElseNull(userId) == null) {
            profileService.saveProfileForUser(profile, userId);
            return new ResponseEntity<>("Profile successfully added", HttpStatus.OK);
        }
        return new ResponseEntity<>("Profile already added", HttpStatus.BAD_REQUEST);
    }
    @PostMapping("/edit")
    public ResponseEntity<?> edit(@Valid @RequestBody ProfileDto data) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        profileService.editProfile(data, userService.findByLoginElseNull(login).getBody().getProfile().getId());
        return ResponseEntity.ok("Profile edited");
    }

    @GetMapping("/all/{page}")
    public ResponseEntity<?> getAll(@PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 50);
        Page<Profile> entityList = profileService.findAllProfiles(pageable);
        List<ProfileDto> dtoList = dataTransformer.profileListToDto(entityList.getContent());
        HttpHeaders responseHeaders = paginationHeadersCreator.pageWithTotalElementsHeadersCreate(entityList);
        return ResponseEntity.ok().headers(responseHeaders).body(dtoList);
    }

    @GetMapping("/single/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        Profile entity = profileService.findById(id);
        return ResponseEntity.ok(dataTransformer.profileToDto(entity));
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
