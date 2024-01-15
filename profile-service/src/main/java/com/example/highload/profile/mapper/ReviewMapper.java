package com.example.highload.profile.mapper;

import com.example.highload.profile.model.inner.Review;
import com.example.highload.profile.model.network.ReviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "profile.id", target = "profileId")
    @Mapping(source = "user.login", target = "userName")
    ReviewDto reviewToDto(Review review);

    @Mapping(target = "profile.id", source = "profileId")
    @Mapping(target = "user.login", source = "userName")
    Review reviewDtoToReview(ReviewDto reviewDto);
    List<Review> reviewDtoListToReviewList(List<ReviewDto> reviews);
    List<ReviewDto> reviewListToReviewDtoList(List<Review> reviews);

}
