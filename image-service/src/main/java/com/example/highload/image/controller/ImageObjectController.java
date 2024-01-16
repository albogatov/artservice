package com.example.highload.image.controller;

import com.example.highload.image.feign.UserServiceFeignClient;
import com.example.highload.image.mapper.ImageMapper;
import com.example.highload.image.model.inner.Image;
import com.example.highload.image.model.network.ImageDto;
import com.example.highload.image.services.ImageService;
import com.example.highload.image.utils.PaginationHeadersCreator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
    private final ImageMapper imageMapper;
    private final UserServiceFeignClient userService;
    private final PaginationHeadersCreator paginationHeadersCreator;

    @PreAuthorize("hasAuthority('CLIENT')")
    @PostMapping("/add/order/{orderId}")
    public ResponseEntity<?> addImagesToOrder(@Valid @RequestBody List<ImageDto> imageDtos, @PathVariable int orderId, @RequestHeader(value = "Authorization") String token) {
        imageService.saveImagesForOrder(imageDtos, orderId, token);
        return ResponseEntity.ok("Images added");
    }

    @GetMapping("/order/single/{orderId}/images/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> getOrderImages(@Valid @PathVariable int orderId, @PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 50);
        Page<Image> entityList = imageService.findAllOrderImages(orderId, pageable);
        HttpHeaders responseHeaders = paginationHeadersCreator.pageWithTotalElementsHeadersCreate(entityList);
        return ResponseEntity.ok().headers(responseHeaders).body(imageMapper.imageListToImageDtoList(entityList.getContent()));
    }

    @PreAuthorize("hasAuthority('ARTIST')")
    @PostMapping("/add/profile")
    public ResponseEntity<?> addImagesToProfile(@Valid @RequestBody List<ImageDto> imageDtos, @RequestHeader(value = "Authorization") String token) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        int profileId = userService.findByLoginElseNull(login, token).getBody().getProfileId();
        imageService.saveImageForProfile(imageDtos, profileId, token);
        return ResponseEntity.ok("Images added");
    }

    @PostMapping("/change/profile")
    public ResponseEntity<?> changeMainImageOfProfile(@Valid @RequestBody ImageDto imageDto,@RequestHeader(value = "Authorization") String token) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        int profileId = userService.findByLoginElseNull(login, token).getBody().getProfileId();
        imageService.changeMainImageOfProfile(imageDto, profileId, token);
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
    public ResponseEntity<?> removeImageForProfile(@PathVariable int imageId, @RequestHeader(value = "Authorization") String token) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        int profileId = userService.findByLoginElseNull(login, token).getBody().getProfileId();
        imageService.removeImageForProfile(imageId, profileId);
        return ResponseEntity.ok("Image removed");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/removeAll/profile/{profileId}")
    public ResponseEntity<?> removeAllImagesForProfile(@PathVariable int profileId, @RequestHeader(value = "Authorization") String token) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        imageService.removeAllImagesForProfile(profileId);
        return ResponseEntity.ok("Images removed");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/removeAll/order/{orderId}")
    public ResponseEntity<?> removeAllImagesForOrder(@PathVariable int orderId, @RequestHeader(value = "Authorization") String token) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        imageService.removeAllImagesForOrder(orderId);
        return ResponseEntity.ok("Images removed");
    }

    // This was moved from profile service
    @GetMapping("/profile/single/{id}/images/{page}")
    public ResponseEntity<?> getProfileImagesByIdAndPageNumber(@PathVariable int id, @PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 50);
        Page<Image> images = imageService.findAllProfileImages(id, pageable);
        HttpHeaders responseHeaders = paginationHeadersCreator.pageWithTotalElementsHeadersCreate(images);
        return ResponseEntity.ok().headers(responseHeaders).body(imageMapper.imageListToImageDtoList(images.getContent()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("Request body validation failed! " + ex.getLocalizedMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleServiceExceptions() {
        return ResponseEntity.badRequest().body("Wrong ids in path!");
    }

}
