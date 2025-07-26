package com.michelin.service.place;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class PlaceSearchService {

    @Value("${kakao.rest.api.key}")
    private String kakaoApiKey;

    public String searchPlaces(String query, int page, double x, double y) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            // 페이지 파라미터 추가
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
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
