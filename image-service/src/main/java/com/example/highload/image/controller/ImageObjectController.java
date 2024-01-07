package com.example.highload.image.controllers;

import com.example.highload.image.model.network.ImageDto;
import com.example.highload.image.services.ImageService;
import com.example.highload.image.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/api/image")
@RequiredArgsConstructor
public class ImageObjectController {

    private final ImageService imageService;
    private final UserService userService;

    @PreAuthorize("hasAuthority('CLIENT')")
    @PostMapping("/add/order/{orderId}")
    public ResponseEntity<?> addImagesToOrder(@Valid @RequestBody List<ImageDto> imageDtos, @PathVariable int orderId) {
        imageService.saveImagesForOrder(imageDtos, orderId);
        return ResponseEntity.ok("Images added");
    }

    @PreAuthorize("hasAuthority('ARTIST')")
    @PostMapping("/add/profile")
    public ResponseEntity<?> addImagesToProfile(@Valid @RequestBody List<ImageDto> imageDtos) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        int profileId = userService.findByLoginElseNull(login).getProfile().getId();
        imageService.saveImageForProfile(imageDtos, profileId);
        return ResponseEntity.ok("Images added");
    }

    @PostMapping("/change/profile")
    public ResponseEntity<?> changeMainImageOfProfile(@Valid @RequestBody ImageDto imageDto) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        int profileId = userService.findByLoginElseNull(login).getProfile().getId();
        imageService.changeMainImageOfProfile(imageDto, profileId);
        return ResponseEntity.ok("Main image changed");
    }

    @PreAuthorize("hasAuthority('CLIENT')")
    @PostMapping("/remove/order/{orderId}/{imageId}")
    public ResponseEntity<?> removeImageForOrder(@PathVariable int imageId, @PathVariable int orderId) {
        imageService.removeImageForOrder(imageId, orderId);
        return ResponseEntity.ok("Image removed");
    }

    @PreAuthorize("hasAuthority('ARTIST')")
    @PostMapping("/remove/profile/{imageId}")
    public ResponseEntity<?> removeImageForProfile(@PathVariable int imageId) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        int profileId = userService.findByLoginElseNull(login).getProfile().getId();
        imageService.removeImageForProfile(imageId, profileId);
        return ResponseEntity.ok("Image removed");
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
