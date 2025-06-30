package com.michelin.entity.wishlist;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.michelin.entity.restaurant.Restaurant;
import com.michelin.entity.user.User;

@Entity
@Table(name = "wishlist")
@IdClass(WishlistId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wishlist {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "kakao_place_id")
    private String kakaoPlaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kakao_place_id", referencedColumnName = "kakao_place_id", insertable = false, updatable = false)
    private Restaurant restaurant;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime created;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private int deleted;
    
}