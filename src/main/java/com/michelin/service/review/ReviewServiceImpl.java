package com.michelin.service.review;

import com.michelin.dto.review.ReviewRequest;
import com.michelin.dto.review.ReviewResponse;
import com.michelin.dto.review.ReviewWithKakaoRequest;
import com.michelin.entity.restaurant.Restaurant;
import com.michelin.entity.review.Review;
import com.michelin.entity.user.User;
import com.michelin.repository.restaurant.RestaurantRepository;
import com.michelin.repository.review.ReviewRepository;
import com.michelin.repository.user.UserRepository;
import com.michelin.service.aws.S3Uploader;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final S3Uploader s3Uploader;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository, RestaurantRepository restaurantRepository, S3Uploader s3Uploader) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.s3Uploader = s3Uploader;
    }

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request, MultipartFile image) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("음식점을 찾을 수 없습니다."));

        Review review = new Review();
        review.setUser(user);
        review.setRestaurant(restaurant);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreated(LocalDateTime.now());
        review.setDeleted(0);

        if (image != null && !image.isEmpty()) {
            try {
                String uploadedUrl = s3Uploader.upload(image); // ✅ S3에 업로드하고 URL 저장
                review.setImageUrl(uploadedUrl);
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
            }
        }
        return ReviewResponse.from(reviewRepository.save(review));
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getDeleted() == 0)
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        return ReviewResponse.from(review);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long id, ReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setModified(LocalDateTime.now());
        return ReviewResponse.from(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        review.setDeleted(1);
        reviewRepository.save(review);
    }

    @Override
    public Page<ReviewResponse> getReviewsByUserId(Long userId, int page, int size, String orderBy, Double minRating) {
        String sortField = "created";
        if ("rating".equals(orderBy)) {
            sortField = "rating";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortField));
        Page<Review> reviewPage;

        if (minRating != null) {
            reviewPage = reviewRepository.findByUserIdWithMinRating(userId, minRating, pageable);
        } else {
            reviewPage = reviewRepository.findByUserIdWithSort(userId, pageable);
        }

        return reviewPage.map(ReviewResponse::from);
    }
    @Transactional
    public ReviewResponse createReviewWithKakaoPlace(ReviewWithKakaoRequest request, MultipartFile image, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        Restaurant restaurant = restaurantRepository.findByKakaoPlaceId(request.getKakaoPlaceId())
                .orElseGet(() -> restaurantRepository.save(
                        Restaurant.builder()
                                .name(request.getRestaurantName())
                                .address(request.getAddress())
                                .mapUrl(request.getMapUrl())
                                .category(request.getCategory())
                                .kakaoPlaceId(request.getKakaoPlaceId())
                                .avgRating(0.0f)
                                .created(LocalDateTime.now())
                                .deleted(0)
                                .build()
                ));

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = s3Uploader.upload(image);
            } catch (Exception e) {
                throw new RuntimeException("이미지 업로드 실패: " + e.getMessage());
            }
        }

        Review review = Review.builder()
                .user(user)
                .restaurant(restaurant)
                .rating(request.getRating())
                .comment(request.getComment())
                .imageUrl(imageUrl)
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .deleted(0)
                .build();

        return ReviewResponse.from(reviewRepository.save(review));
    }


}

