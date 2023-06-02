package br.com.weather.weatherrest.data.weather.builder;

import java.util.Optional;

import br.com.weather.weatherrest.data.weather.CurrentWeather;
import br.com.weather.weatherrest.data.weather.DailyWeather;
import br.com.weather.weatherrest.data.weather.WeatherLocation;

public final class WeatherLocationBuilder {
    private int id;
    private String name, country, countryCode;
    private double latitude, longitude;
    private Optional<String> timezone;
    private Optional<String> admin1;
    private Optional<Integer> population;
    private Optional<CurrentWeather> currentWeather;
    private Optional<DailyWeather[]> dailyWeather;
    private long lastUpdated;

    public WeatherLocationBuilder() {
        this.admin1 = Optional.empty();
        this.population = Optional.empty();
        this.currentWeather = Optional.empty();
        this.dailyWeather = Optional.empty();
        this.timezone = Optional.empty();
        this.lastUpdated = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public Optional<String> getTimezone() {
        return timezone;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Optional<String> getAdmin1() {
        return admin1;
    }

    public Optional<Integer> getPopulation() {
        return population;
    }

    public Optional<CurrentWeather> getCurrentWeather() {
        return currentWeather;
    }

    public Optional<DailyWeather[]> getDailyWeather() {
        return dailyWeather;
    }

    public long getLastUpdated() {
        return this.lastUpdated;
    }

    public WeatherLocationBuilder id(int id) {
        this.id = id;
        return this;
    }

    public WeatherLocationBuilder name(String name) {
        this.name = name;
        return this;
    }

    public WeatherLocationBuilder country(String country) {
        this.country = country;
        return this;
    }

    public WeatherLocationBuilder countryCode(String code) {
        this.countryCode = code;
        return this;
    }

    public WeatherLocationBuilder timezone(String timezone) {
        this.timezone = Optional.of(timezone);
        return this;
    }

    public WeatherLocationBuilder latitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public WeatherLocationBuilder longitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public WeatherLocationBuilder admin1(String admin1) {
        if (admin1 != null && admin1 != "none") {
            this.admin1 = Optional.of(admin1);
        }

        return this;
    }

    public WeatherLocationBuilder population(int population) {
        this.population = Optional.of(population)
                .filter((p) -> p > 0);

        return this;
    }

    public WeatherLocationBuilder currentWeather(CurrentWeather currentWeather) {
        if (currentWeather != null) {
            this.currentWeather = Optional.of(currentWeather);
        }

        return this;
    }

    public WeatherLocationBuilder dailyWeather(DailyWeather[] dailyWeather) {
        if (dailyWeather != null) {
            this.dailyWeather = Optional.of(dailyWeather);
        }

        return this;
    }

    public WeatherLocationBuilder lastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public WeatherLocation build() {
        return new WeatherLocation(this);
    }
}
