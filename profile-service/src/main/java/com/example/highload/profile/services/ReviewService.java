package com.example.highload.profile.services;

import com.example.highload.profile.model.inner.Review;
import com.example.highload.profile.model.network.ReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    Page<Review> findAllProfileReviews(int profileId, Pageable pageable);

    Review findById(int id);

    Review saveReview(ReviewDto reviewDto, String token);

}
