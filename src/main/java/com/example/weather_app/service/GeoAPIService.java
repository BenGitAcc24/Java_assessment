package com.example.weather_app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class GeoAPIService {

    @Value("${geoapify.api.key}")
    private String apiKey;

    @PostConstruct
    public void checkKey() {
        System.out.println("API Key: " + apiKey);
    }

    public String extractZipFromAddress(String address) {
        String encoded = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String url = "https://api.geoapify.com/v1/geocode/search?text=" + encoded + "&apiKey=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        Map response = restTemplate.getForObject(url, Map.class);

        List features = (List) response.get("features");
        if (features == null || features.isEmpty()) {
            throw new IllegalArgumentException("No results found for address");
        }

        Map firstFeature = (Map) features.get(0);
        Map properties = (Map) firstFeature.get("properties");

        String postcode = (String) properties.get("postcode");
        if (postcode == null || postcode.isBlank()) {
            throw new IllegalArgumentException("ZIP code not found in address");
        }

        return postcode;
    }
}
