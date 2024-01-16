package com.example.highload.profile;

import com.example.highload.profile.feign.UserServiceFeignClient;
import com.example.highload.profile.mapper.ProfileMapper;
import com.example.highload.profile.mapper.ReviewMapper;
import com.example.highload.profile.model.inner.Profile;
import com.example.highload.profile.model.inner.Review;
import com.example.highload.profile.model.inner.User;
import com.example.highload.profile.model.network.ProfileDto;
import com.example.highload.profile.model.network.ReviewDto;
import com.example.highload.profile.model.network.UserDto;
import com.example.highload.profile.repos.ProfileRepository;
import com.example.highload.profile.repos.ReviewRepository;
import com.example.highload.profile.services.impl.ProfileServiceImpl;
import com.example.highload.profile.services.impl.ReviewServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private UserServiceFeignClient userService;

    @Mock
    private ReviewRepository reviewRepository;
    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    public void save() {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setText("Nice job");
        reviewDto.setProfileId(1);
        reviewDto.setUserName("client");
        Review review = new Review();
        User user = new User();
        user.setLogin("client");
        Profile profile = new Profile();
        profile.setId(1);
        review.setText("Nice job");
        review.setUser(user);
        review.setProfile(profile);
        when(reviewMapper.reviewDtoToReview(reviewDto)).thenReturn(review);
        review.setId(1);
        when(reviewRepository.save(review)).thenReturn(review);
        user.setId(2);
        when(userService.findByLoginElseNull(user.getLogin(), "mock")).thenReturn(ResponseEntity.ok(new UserDto()));
        Review review1 = reviewService.saveReview(reviewDto, "mock");

        Assertions.assertAll(
                () -> Assertions.assertNotNull(review1.getId()),
                () -> Assertions.assertEquals("Nice job", review1.getText()),
                () -> Assertions.assertEquals("client", review1.getUser().getLogin())
        );
    }

    @Test
    public void findAllForProfile() {
        Pageable pageable = Mockito.mock(Pageable.class);
        Review review = new Review();
        User user = new User();
        user.setLogin("client");
        Profile profile = new Profile();
        profile.setId(1);
        review.setText("Nice job");
        review.setUser(user);
        review.setProfile(profile);
        Page<Review> reviews = new PageImpl<>(List.of(review));
        when(reviewRepository.findAllByProfile_Id(1, pageable)).thenReturn(Optional.of(reviews));
        Page<Review> pageReviews = reviewService.findAllProfileReviews(1, pageable);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, pageReviews.getTotalElements()),
                () -> Assertions.assertEquals(1, pageReviews.getTotalPages())
        );
    }

}
