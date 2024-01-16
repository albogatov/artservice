package com.example.highload.profile.controller;

import com.example.highload.profile.mapper.ReviewMapper;
import com.example.highload.profile.model.inner.Profile;
import com.example.highload.profile.model.inner.Review;
import com.example.highload.profile.model.network.ReviewDto;
import com.example.highload.profile.services.ProfileService;
import com.example.highload.profile.services.ReviewService;
import com.example.highload.profile.utils.PaginationHeadersCreator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/api/profile/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    private final ProfileService profileService;
    private final PaginationHeadersCreator paginationHeadersCreator;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<?> save(@Valid @RequestBody ReviewDto data, @RequestHeader(value = "Authorization") String token){
        if(reviewService.saveReview(data, token) != null)
            return ResponseEntity.ok("");
        else return ResponseEntity.badRequest().body("Couldn't save review, check data");
    }

    @GetMapping("/all/{profileId}/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> getAllByProfile(@PathVariable int profileId, @PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 50);
        Profile entity = profileService.findById(profileId);
        Page<Review> entityList = reviewService.findAllProfileReviews(profileId, pageable);
        List<ReviewDto> dtoList = reviewMapper.reviewListToReviewDtoList(entityList.getContent());
        HttpHeaders responseHeaders = paginationHeadersCreator.pageWithTotalElementsHeadersCreate(entityList);
        return ResponseEntity.ok().headers(responseHeaders).body(dtoList);
    }

    @GetMapping("/single/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> getById(@PathVariable int id){
        Review entity = reviewService.findById(id);
        ReviewDto reviewDto = reviewMapper.reviewToDto(entity);
        return ResponseEntity.ok(reviewDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex){
        return ResponseEntity.badRequest().body("Request body validation failed! " + ex.getLocalizedMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleServiceExceptions(){
        return ResponseEntity.badRequest().body("Wrong ids in path!");
    }
}
