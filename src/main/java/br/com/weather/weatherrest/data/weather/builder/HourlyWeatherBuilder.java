package br.com.weather.weatherrest.data.weather.builder;

import br.com.weather.weatherrest.data.weather.HourlyWeather;
import br.com.weather.weatherrest.data.weather.WeatherCode;

public final class HourlyWeatherBuilder {
    private double temperature, appTemp;
    private double humidity, visibility;
    private double pressureSeaLevel, surfacePressure;
    private double cloudCover, windSpeed, windGusts, windDirection;
    private double precipitation,precipitationProbability, snowfall, rain, showers, snowDepth;
    private WeatherCode weatherCode;
    private double freezingLevelHeight;
    private boolean isDay;
    private long timestamp;

    public HourlyWeatherBuilder() {
    }

    public double getTemperature() {
        return temperature;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public double getAppTemp() {
        return appTemp;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getVisibility() {
        return this.visibility;
    }

    public double getPressureSeaLevel() {
        return pressureSeaLevel;
    }

    public double getSurfacePressure() {
        return surfacePressure;
    }

    public double getCloudcover() {
        return this.cloudCover;
    }

    public double getWindSpeed() {
        return this.windSpeed;
    }

    public double getWindGusts() {
        return this.windGusts;
    }

    public double getWindDirection() {
        return this.windDirection;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public double getPrecipitationProbability() {
        return this.precipitationProbability;
    }

    public double getSnowfall() {
        return snowfall;
    }

    public double getRain() {
        return rain;
    }

    public double getShowers() {
        return showers;
    }

    public double getSnowDepth() {
        return snowDepth;
    }

    public WeatherCode getWeatherCode() {
        return weatherCode;
    }

    public double getFreezingLevelHeight() {
        return freezingLevelHeight;
    }

    public boolean isDay() {
        return this.isDay;
    }

    public HourlyWeatherBuilder precipitationProbability(double precipitationProbability) {
        this.precipitationProbability = precipitationProbability;
        return this;
    }

    public HourlyWeatherBuilder isDay(boolean isDay) {
        this.isDay = isDay;
        return this;
    }

    public HourlyWeatherBuilder timestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public HourlyWeatherBuilder temperature(double temperature) {
        this.temperature = temperature;
        return this;
    }

    public HourlyWeatherBuilder appTemperature(double temperature) {
        this.appTemp = temperature;
        return this;
    }

    public HourlyWeatherBuilder humidity(double humidity) {
        this.humidity = humidity;
        return this;
    }

    public HourlyWeatherBuilder visibility(double visibility) {
        this.visibility = visibility;
        return this;
    }

    public HourlyWeatherBuilder pressureSeaLevel(double pressure) {
        this.pressureSeaLevel = pressure;
        return this;
    }

    public HourlyWeatherBuilder pressureSurface(double pressure) {
        this.surfacePressure = pressure;
        return this;
    }

    public HourlyWeatherBuilder cloudCover(double cloudcover) {
        this.cloudCover = cloudcover;
        return this;
    }

    public HourlyWeatherBuilder windSpeed(double windspeed) {
        this.windSpeed = windspeed;
        return this;
    }

    public HourlyWeatherBuilder windDirection(double direction) {
        this.windDirection = direction;
        return this;
    }

    public HourlyWeatherBuilder windGusts(double gusts) {
        this.windGusts = gusts;
        return this;
    }

    public HourlyWeatherBuilder precipitation(double precipitation) {
        this.precipitation = precipitation;
        return this;
    }

    public HourlyWeatherBuilder snowfall(double snowfall) {
        this.snowfall = snowfall;
        return this;
    }

    public HourlyWeatherBuilder rain(double rain) {
        this.rain = rain;
        return this;
    }

    public HourlyWeatherBuilder showers(double showers) {
        this.showers = showers;
        return this;
    }

    public HourlyWeatherBuilder snowDepth(double snowDepth) {
        this.snowDepth = snowDepth;
        return this;
    }

    public HourlyWeatherBuilder weatherCode(WeatherCode weatherCode) {
        this.weatherCode = weatherCode;
        return this;
    }

    public HourlyWeatherBuilder freezingLevelHeight(double level) {
        this.freezingLevelHeight = level;
        return this;
    }

    public HourlyWeather build() {
        return new HourlyWeather(this);
    }
}
