package com.michelin.entity.restaurant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "restaurant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(nullable = false, length = 20)
    private String name;

	@Column(nullable = false, length = 100)
    private String address;

	@Column(nullable = false, length = 8)
    private String category;

	@Column(columnDefinition = "TEXT")
    private String map_url;

	@Column(columnDefinition = "FLOAT DEFAULT 0.0")
	private float avg_rating;

	@Column(columnDefinition = "TEXT")
	private String imageUrl;

	@Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime created;

	@Column(columnDefinition = "TINYINT(1) DEFAULT 0")
	private int deleted;


	public void setMapUrl(String mapUrl) {
		this.map_url = mapUrl;
	}
	public String getMapUrl() {
		return this.map_url;
	}

	public void setAvgRating(float avgRating) {
		this.avg_rating = avgRating;
	}
	public float getAvgRating() {
		return this.avg_rating;
	}
}
