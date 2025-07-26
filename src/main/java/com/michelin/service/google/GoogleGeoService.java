package com.michelin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;

@Service
public class GoogleGeoService {

    @Value("${google.geo.api-key}")
    private String apiKey;

    public Map<String, Object> getAccurateLocation() {
        String url = "https://www.googleapis.com/geolocation/v1/geolocate?key=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>("{}", headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        return response.getBody(); // {"location": {"lat": ..., "lng": ...}, "accuracy": ...}
    }
}