package com.example.weather_app.dto;

public class ForecastResponse {
    private String zipcode;
    private String location;
    private String weather;
    private double high;
    private double low;
    private boolean fromCache;

    public boolean isFromCache() { return fromCache; }
    public void setFromCache(boolean fromCache) { this.fromCache = fromCache; }


    public ForecastResponse(String zipcode, String location, double high, double low,String weather) {
        this.zipcode = zipcode;
        this.location = location;
        this.high = high;
        this.low = low;
        this.weather = weather;
    }

    

	public String getZipcode() { return zipcode; }
    public String getLocation() { return location; }
    public double getHigh() { return high; }
    public double getLow() { return low; }
    public String getWeather() { return weather; }
}

