package com.michelin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PlaceDto {
    private String id;
    private String place_name;
    private String road_address_name;
    private String address_name;
    private String category_name;
    private String place_url;
    private String x;
    private String y;
    private boolean isWishlisted;

}
