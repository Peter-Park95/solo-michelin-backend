package com.michelin.controller.google;

import com.michelin.service.GoogleGeoService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LocationController {

    private final GoogleGeoService googleGeoService;

    @PostMapping("/accurate-location")
    public Map<String, Object> getLocation() {
        return googleGeoService.getAccurateLocation();
    }
}
