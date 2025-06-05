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

    public String searchPlaces(String query){
        try {
            RestTemplate restTemplate = new RestTemplate();
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            // category_group_code=FD6 추가하여 음식점만 검색
            String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + encodedQuery
                    + "&category_group_code=FD6";

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
