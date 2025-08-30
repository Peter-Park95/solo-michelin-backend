package com.michelin.service.review;

import com.michelin.dto.review.*;
import com.michelin.entity.restaurant.Restaurant;
import com.michelin.entity.review.Review;
import com.michelin.entity.reviewlike.ReviewLike;
import com.michelin.entity.reviewlike.ReviewLikeId;
import com.michelin.entity.user.User;
import com.michelin.repository.restaurant.RestaurantRepository;
import com.michelin.repository.review.ReviewRepository;
import com.michelin.repository.reviewlike.ReviewLikeRepository;
import com.michelin.repository.user.UserRepository;
import com.michelin.repository.wishlist.WishlistRepository;
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
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final S3Uploader s3Uploader;
    private final WishlistRepository wishlistRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
				            UserRepository userRepository,
				            RestaurantRepository restaurantRepository,
                            ReviewLikeRepository reviewLikeRepository,
				            S3Uploader s3Uploader,
				            WishlistRepository wishlistRepository) {
		this.reviewRepository = reviewRepository;
		this.userRepository = userRepository;
		this.restaurantRepository = restaurantRepository;
        this.reviewLikeRepository = reviewLikeRepository;
		this.s3Uploader = s3Uploader;
		this.wishlistRepository = wishlistRepository;
	}

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewAddRequest request, MultipartFile image) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("음식점을 찾을 수 없습니다."));
        float food = request.getFoodRating();
        float mood = request.getMoodRating();
        float service = request.getServiceRating();
        float rounded = calculateAverageRating(food, mood, service);
        
        Review review = new Review();
        review.setUser(user);
        review.setRestaurant(restaurant);
        review.setFoodRating(food);
        review.setMoodRating(mood);
        review.setServiceRating(service);
        review.setRating(rounded);
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
        return ReviewResponse.from(reviewRepository.save(review), false, 0);
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
        		.filter(r -> r.getDeleted() == 0)
                .map(r -> {
                    long likeCount = reviewLikeRepository.countByReviewIdAndDeleted(r.getId(), 0);
                    return ReviewResponse.from(r, false, likeCount); // 로그인 유저 없으니 liked=false
                })
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse getReviewById(Long id) {
    	Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        long likeCount = reviewLikeRepository.countByReviewIdAndDeleted(id, 0);
        return ReviewResponse.from(review, false, likeCount);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long id, ReviewUpdateRequest request, MultipartFile imageFile) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        // 별점과 코멘트 업데이트
        review.setFoodRating(request.getFoodRating());
        review.setMoodRating(request.getMoodRating());
        review.setServiceRating(request.getServiceRating());
        float avg = calculateAverageRating(request.getFoodRating(), request.getMoodRating(), request.getServiceRating());
        review.setRating(avg);// 평균값
        review.setComment(request.getComment()); // null 허용
        review.setModified(LocalDateTime.now());

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = s3Uploader.upload(imageFile); // 새 이미지 업로드
                review.setImageUrl(imageUrl); // 새 이미지로 교체
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 중 오류 발생", e);
            }
        }
        long likeCount = reviewLikeRepository.countByReviewIdAndDeleted(review.getId(), 0);
        return ReviewResponse.from(reviewRepository.save(review), false, likeCount);
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
    public Page<ReviewResponse> getReviewsByUserId(Long userId, int page, int size, String orderBy, Double minRating, Boolean withImage, String search) {
    	String sortField = "created";
        if ("rating".equals(orderBy)) {        
        	sortField = "rating";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortField));

        Page<Review> reviewPage;

        if (search != null && !search.isEmpty()) {   // ✅ 검색 먼저 체크
            reviewPage = reviewRepository.findByUserIdAndRestaurantNameContaining(userId, search, pageable);
        } else if (Boolean.TRUE.equals(withImage)) {
            reviewPage = reviewRepository.findWithImageByUserId(userId, pageable);
        } else if (minRating != null) {
            reviewPage = reviewRepository.findByUserIdWithMinRating(userId, minRating, pageable);
        } else {
            reviewPage = reviewRepository.findByUserIdWithSort(userId, pageable);
        }
        
        return reviewPage.map(r -> {
            long likeCount = reviewLikeRepository.countByReviewIdAndDeleted(r.getId(), 0);
            return ReviewResponse.from(r, false, likeCount);
        });
    }
    
    @Override
    public Page<ReviewResponse> getReviewsWithImageByUserId(Long userId, int page, int size) {
    	//이미지 포함 리뷰 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));
        Page<Review> reviewPage = reviewRepository.findWithImageByUserId(userId, pageable);
        return reviewPage.map(r -> {
            long likeCount = reviewLikeRepository.countByReviewIdAndDeleted(r.getId(), 0);
            return ReviewResponse.from(r, false, likeCount);
        });
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
        float food = request.getFoodRating();
        float mood = request.getMoodRating();
        float service = request.getServiceRating();
        
        float rounded = calculateAverageRating(food, mood, service);

        Review review = Review.builder()
                .user(user)
                .restaurant(restaurant)
                .foodRating(food)
                .moodRating(mood)
                .serviceRating(service)
                .rating(rounded)
                .comment(request.getComment())
                .imageUrl(imageUrl)
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .deleted(0)
                .build();

        return ReviewResponse.from(reviewRepository.save(review), false, 0);
    }

    @Override
    public void deleteReviewImage(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        review.setImageUrl(null);  // DB의 image_url 컬럼 null 처리
        review.setModified(LocalDateTime.now());
        reviewRepository.save(review);
    }

    public List<ReviewSummaryResponse> getHighlightedReviews(int limit, Long userId) {
        List<Review> reviews = reviewRepository.findRandomHighlightedReviews(limit)
                .stream()
                .filter(r -> r.getDeleted() == 0) // 삭제된 리뷰 제외
                .toList();

        return reviews.stream()
                .map(review -> {
                    long likeCount = reviewLikeRepository.countByReviewId(review.getId());
                    boolean likedByMe = (userId != null) &&
                            reviewLikeRepository.existsByUserIdAndReviewId(userId, review.getId());

                    return ReviewSummaryResponse.from(review, likeCount, likedByMe);
                })
                .collect(Collectors.toList());
    }
    
    private float calculateAverageRating(float food, float mood, float service) {
        float avg = (food + mood + service) / 3.0f;
        return Math.round(avg * 10) / 10.0f;
    }

    //검색 시 마크 인포윈도우에 전체 리뷰 및 위시 갯수 조회
    @Override
    public ReviewStatsDto getReviewStats(String kakaoPlaceId) {
        long reviewCount = reviewRepository.countByRestaurant_KakaoPlaceIdAndDeleted(kakaoPlaceId, 0);
        long wishlistCount = wishlistRepository.countByKakaoPlaceId(kakaoPlaceId);
        return new ReviewStatsDto(reviewCount, wishlistCount);
    }
    
	//전체 리뷰 조회
    public List<ReviewDto> getReviewsByPlace(String kakaoPlaceId) {
        List<Review> reviewEntities = reviewRepository.findAllByRestaurant_KakaoPlaceIdAndDeleted(kakaoPlaceId, 0);
        reviewEntities.forEach(r -> System.out.println("Review ID: " + r.getId()));
        return reviewEntities.stream()
        		.map(r -> {
        	        ReviewDto dto = new ReviewDto();
        	        dto.setReviewId(r.getId());
        	        dto.setUserName(r.getUser().getUsername());
        	        dto.setComment(r.getComment());
        	        dto.setRating(r.getRating());
        	        dto.setImageUrl(r.getImageUrl());
        	        dto.setFoodRating(r.getFoodRating());
        	        dto.setMoodRating(r.getMoodRating());
        	        dto.setServiceRating(r.getServiceRating());

        	        // 좋아요 정보 세팅
        	        dto.setLikeCount((long) r.getReviewLikes().size());
        	        dto.setLiked(false); // 로그인 유저 체크 시 true/false로 업데이트 가능

        	        return dto;
        	    })
        	    .collect(Collectors.toList());
    }
    
    @Transactional
    @Override
    public boolean toggleReviewLike(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return reviewLikeRepository.findByUserIdAndReviewIdAndDeleted(userId, reviewId, 0)
                .map(existing -> {
                	if (existing.isActive()) {
                        // 이미 좋아요 → 비활성화
                        existing.deactivate();
                        reviewLikeRepository.save(existing);
                        return false; // 좋아요 취소됨
                    } else {
                        // row 는 있지만 deleted=1 → 다시 활성화
                        existing.activate();
                        reviewLikeRepository.save(existing);
                        return true; // 좋아요 추가됨
                    }
                })
                .orElseGet(() -> {
                    // 없으면 → 추가
                	ReviewLike newLike = ReviewLike.builder()
                            .id(new ReviewLikeId(user.getId(), review.getId()))
                            .user(user)
                            .review(review)
                            .created(LocalDateTime.now())
                            .deleted(0)
                            .build();

                    reviewLikeRepository.save(newLike);
                    return true;
                });
    }

    @Override
    public long getReviewLikeCount(Long reviewId) {
        return reviewLikeRepository.countByReviewIdAndDeleted(reviewId, 0);
    }

    @Override
    public boolean hasUserLikedReview(Long reviewId, Long userId) {
        return reviewLikeRepository.findByUserIdAndReviewIdAndDeleted(userId, reviewId, 0).isPresent();
    }
    
    @Override
    public Page<ReviewResponse> getReviewsByUserAndSearch(Long userId, String search, int page, int size, String orderBy, Double minRating, String category) {
        String sortField = orderBy != null ? orderBy : "created";
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortField));

        Page<Review> reviewPage = reviewRepository.findByUserIdAndRestaurantNameContaining(userId, search, pageable);

        return reviewPage.map(ReviewResponse::from);
    }
}

