package com.example.highload.profile.mapper;

import com.example.highload.profile.model.inner.Review;
import com.example.highload.profile.model.network.ReviewDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewDto reviewToDto(Review review);

    Review reviewDtoToReview(ReviewDto reviewDto);
    List<Review> reviewDtoListToReviewList(List<ReviewDto> reviews);
    List<ReviewDto> reviewListToReviewDtoList(List<Review> reviews);

}
