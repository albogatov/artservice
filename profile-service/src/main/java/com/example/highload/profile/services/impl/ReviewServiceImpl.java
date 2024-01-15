package com.example.highload.profile.services.impl;

import com.example.highload.profile.feign.UserServiceFeignClient;
import com.example.highload.profile.mapper.ReviewMapper;
import com.example.highload.profile.model.inner.Review;
import com.example.highload.profile.model.network.ReviewDto;
import com.example.highload.profile.services.ReviewService;
import com.example.highload.profile.repos.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserServiceFeignClient userService;

    @Override
    public Page<Review> findAllProfileReviews(int profileId, Pageable pageable) {
        return reviewRepository.findAllByProfile_Id(profileId, pageable).orElse(Page.empty());
    }

    @Override
    public Review findById(int id) {
        return reviewRepository.findById(id).orElse(null);
    }

    @Override
    public Review saveReview(ReviewDto reviewDto, String token) {
        Review review = reviewMapper.reviewDtoToReview(reviewDto);
        review.getUser().setId(userService.findByLoginElseNull(review.getUser().getLogin(), token).getBody().getId());
        return reviewRepository.save(review);
    }
}
