package com.michelin.entity.reviewlike;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ReviewLikeId implements Serializable {
    private Long userId;
    private Long reviewId;
}
