package com.michelin.entity.restaurant;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "restaurant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(nullable = false, length = 20)
    private String name;

	@Column(nullable = false, length = 100)
    private String address;

	@Column(length = 100) // 기존보다 더 길게!
	private String category;

	@Column(name = "kakao_place_id", unique = true)
	private String kakaoPlaceId;

	@Column(columnDefinition = "TEXT")
    private String mapUrl;

	@Column(columnDefinition = "FLOAT DEFAULT 0.0")
	private float avgRating;

	@Column(columnDefinition = "TEXT")
	private String imageUrl;

	@Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime created;

	@Column(columnDefinition = "TINYINT(1) DEFAULT 0")
	private int deleted;


}
