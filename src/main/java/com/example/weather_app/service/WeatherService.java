package com.example.weather_app.service;

import java.util.Map;
import java.util.List;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.weather_app.dto.ForecastResponse;

@Service
public class WeatherService {

	private final WebClient webClient = WebClient.create();

	@Value("${openweathermap.api.key}")
	private String openweathermapApiKey;

	@Cacheable(value = "cachedWeatherForecast", key = "#zip")
	public ForecastResponse getForecast(String zip) {
		double high = 0;
		double low = 0;
		String weather = null;
		// Step 1: Get GeoCodes from ZIP
		Map geoCodes = webClient.get()
				.uri("https://api.openweathermap.org/geo/1.0/zip?zip={zip},US&appid={key}", zip, openweathermapApiKey).retrieve()
				.bodyToMono(Map.class).block();
		System.out.println("Geo response: " + geoCodes);

		double lat = ((Number) geoCodes.get("lat")).doubleValue();
		double lon = ((Number) geoCodes.get("lon")).doubleValue();
		String name = (String) geoCodes.get("name");

		// Step 2: Get weather forecast complex resp from Weather api
		Map weatherCmplxResponse = webClient.get().uri(
				"https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&exclude=hourly,minutely,alerts&units=imperial&appid={key}",
				lat, lon, openweathermapApiKey).retrieve().bodyToMono(Map.class).block();
		System.out.println("Weather response: " + weatherCmplxResponse);
		
		Object mainObj = weatherCmplxResponse.get("main");

		if (mainObj instanceof Map) {
		    Map<String, Object> mainMap = (Map<String, Object>) mainObj;
		    System.out.println("Temperature: " + mainMap.get("temp"));
		     high = ((Number) mainMap.get("temp_max")).doubleValue(); 
			 low =  ((Number) mainMap.get("temp_min")).doubleValue(); 
		}
		
		Object weatherObj = weatherCmplxResponse.get("weather");
		if (weatherObj instanceof List) {
		    List<Map<String, Object>> weatherList = (List<Map<String, Object>>) weatherObj;
		    for (Map<String, Object> w : weatherList) {
		        System.out.println(w.get("main"));
		        weather = (String)w.get("main");
		    }
		}

		return new ForecastResponse(zip, name, high, low, weather);
	}
}