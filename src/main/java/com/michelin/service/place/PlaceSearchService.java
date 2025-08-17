package com.michelin.service.place;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michelin.dto.PlaceDto;
import com.michelin.entity.wishlist.Wishlist;
import com.michelin.repository.wishlist.WishlistRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PlaceSearchService {

    @Value("${kakao.rest.api.key}")
    private String kakaoApiKey;
    private static final Logger log = LoggerFactory.getLogger(PlaceSearchService.class);
    private final WishlistRepository wishlistRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PlaceSearchService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    public List<PlaceDto> searchPlaces(String query, int page, double x, double y, Long userId) {
        log.info("kakaoApiKey present? {}", kakaoApiKey != null);
        log.info("kakaoApiKey head={}", kakaoApiKey != null ? kakaoApiKey.substring(0,6) : "null");
        try {
            RestTemplate restTemplate = new RestTemplate();
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + encodedQuery
                    + "&category_group_code=FD6"
                    + "&page=" + page
                    + "&x=" + x
                    + "&y=" + y
                    + "&sort=distance";

            URI uri = new URI(url);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            // JSON 파싱
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode documents = root.get("documents");

            // 사용자 위시리스트 조회
            Set<String> wishlistedIds = wishlistRepository
                    .findByUserId(userId)
                    .stream()
                    .map(Wishlist::getKakaoPlaceId)
                    .collect(Collectors.toSet());

            // 변환 및 위시 여부 표시
            List<PlaceDto> places = new ArrayList<>();
            for (JsonNode node : documents) {
                PlaceDto dto = objectMapper.treeToValue(node, PlaceDto.class);
                dto.setWishlisted(wishlistedIds.contains(dto.getId()));
                places.add(dto);
            }

            // 위시리스트 정렬
            places.sort((a, b) -> Boolean.compare(b.isWishlisted(), a.isWishlisted())); // true가 먼저

            return places;

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Kakao error: status={} body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception e) {
            log.error("Place search failed", e);
            throw new RuntimeException(e);
        }
    }
}
