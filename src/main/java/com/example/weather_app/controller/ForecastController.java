package com.example.weather_app.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.weather_app.dto.ForecastResponse;
import com.example.weather_app.service.GeoAPIService;
import com.example.weather_app.service.WeatherService;

@RestController
@RequestMapping("/api")
public class ForecastController {

    private final WeatherService weatherService;
    private final GeoAPIService geoAPIService;
    private final CacheManager cacheManager;


    public ForecastController(WeatherService weatherService, GeoAPIService geoAPIService,CacheManager cacheManager) {
        this.weatherService = weatherService;
		this.geoAPIService = geoAPIService;
        this.cacheManager = cacheManager;

    }
    
	/*
	 * @GetMapping("/forecast") public ForecastResponse getForecast(@RequestParam
	 * String address) { String zip = extractZipFromAddress(address); return
	 * weatherService.getForecast(zip); }
	 */


    @GetMapping("/forecast")
    public ForecastResponse getForecast(@RequestParam String address) {
    	String zip = geoAPIService.extractZipFromAddress(address);
    	 Cache cache = cacheManager.getCache("cachedWeatherForecast");
    	    ForecastResponse cached = cache != null ? cache.get(zip, ForecastResponse.class) : null;

    	    ForecastResponse response = weatherService.getForecast(zip);
    	    response.setFromCache(cached != null);

        return response;
    }
    
    private String extractZipFromAddress(String address) {
        Pattern zipPattern = Pattern.compile("\\b\\d{5}\\b");
        Matcher matcher = zipPattern.matcher(address);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new IllegalArgumentException("ZIP code not found in address");
    }

}

