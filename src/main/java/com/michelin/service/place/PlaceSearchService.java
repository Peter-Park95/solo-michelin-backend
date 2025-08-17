package com.michelin.service.place;

import com.michelin.repository.wishlist.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PlaceSearchService {

    private final WishlistRepository wishlistRepository;

    @Value("${kakao.rest.api.key}")
    private String kakaoApiKey;

    /**
     * @param userId     로그인 사용자 ID (없으면 null)
     * @param query      검색어
     * @param page       페이지 (1~)
     * @param x,y        근처 검색 시 좌표 (nationwide=false일 때만 사용)
     * @param nationwide true면 전국 검색(좌표 미사용), false면 근처 검색(반경 20km, 정확도순)
     */
    public Map<String, Object> searchPlaces(Long userId, String query, int page, Double x, Double y, boolean nationwide) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // 정확 매칭 강화: "아웃백" 식으로 쌍따옴표 감싸기
            String encodedQuery = URLEncoder.encode("\"" + query.trim() + "\"", StandardCharsets.UTF_8);

            StringBuilder url = new StringBuilder("https://dapi.kakao.com/v2/local/search/keyword.json")
                    .append("?query=").append(encodedQuery)
                    .append("&category_group_code=FD6")
                    .append("&page=").append(page);

            if (!nationwide && x != null && y != null) {
                // 근처 검색: 최대 반경 + 정확도순 (거리순 보다 매칭 우선)
                url.append("&x=").append(x)
                   .append("&y=").append(y)
                   .append("&radius=20000")
                   .append("&sort=accuracy");
            } else {
                // 전국 검색: 좌표 미사용, 정확도순
                url.append("&sort=accuracy");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    URI.create(url.toString()),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> body = response.getBody();
            if (body == null) {
                return Map.of("documents", List.of(), "meta", Map.of("is_end", true));
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> docs = (List<Map<String, Object>>) body.getOrDefault("documents", List.of());

            // Kakao place id 모으기
            List<String> kakaoIds = new ArrayList<>();
            for (Map<String, Object> d : docs) {
                Object id = d.get("id");
                if (id != null) kakaoIds.add(id.toString());
            }

            // 사용자 위시리스트에 있는 id 조회 (없으면 빈 Set)
            Set<String> wished = (userId != null && !kakaoIds.isEmpty())
                    ? new HashSet<>(wishlistRepository
                        .findKakaoPlaceIdsByUserIdAndRestaurant_KakaoPlaceIdIn(userId, kakaoIds))
                    : Collections.emptySet();

            // documents에 isWishlisted 주입
            for (Map<String, Object> d : docs) {
                String id = String.valueOf(d.get("id"));
                d.put("isWishlisted", wished.contains(id));
            }

            return body;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}