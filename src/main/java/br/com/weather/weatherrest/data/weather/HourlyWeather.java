package br.com.weather.weatherrest.data.weather;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonObject;

import br.com.weather.weatherrest.data.weather.builder.HourlyWeatherBuilder;

public class HourlyWeather {
    private final double temperature, appTemp;
    private final double humidity, visibility;
    private final double pressureSeaLevel, surfacePressure;
    private final double cloudCover, windSpeed, windGusts, windDirection;
    private final double precipitation, precipitationProbability, snowfall, rain, showers, snowDepth;
    private final WeatherCode weatherCode;
    private final double freezingLevelHeight;
    private final boolean isDay;
    private final long timestamp;

    public HourlyWeather(HourlyWeatherBuilder builder) {
        this.temperature = builder.getTemperature();
        this.appTemp = builder.getAppTemp();
        this.humidity = builder.getHumidity();
        this.visibility = builder.getVisibility();
        this.pressureSeaLevel = builder.getPressureSeaLevel();
        this.surfacePressure = builder.getSurfacePressure();
        this.cloudCover = builder.getCloudcover();
        this.windSpeed = builder.getWindSpeed();
        this.windGusts = builder.getWindGusts();
        this.windDirection = builder.getWindDirection();
        this.precipitation = builder.getPrecipitation();
        this.precipitationProbability = builder.getPrecipitationProbability();
        this.snowfall = builder.getSnowfall();
        this.rain = builder.getRain();
        this.showers = builder.getShowers();
        this.snowDepth = builder.getSnowDepth();
        this.weatherCode = builder.getWeatherCode();
        this.freezingLevelHeight = builder.getFreezingLevelHeight();
        this.isDay = builder.isDay();
        this.timestamp = builder.getTimestamp();
    }

    public double getPrecipitationProbability() {
        return this.precipitationProbability;
    }

    public boolean isDay() {
        return this.isDay;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public double getTemperature() {
        return temperature;
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

    public LocalDateTime getLocalDateTime(String timezone) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(this.timestamp), ZoneId.of(timezone));
    }

    public JsonObject toJSONObject() {
        var object = new JsonObject();

        object.addProperty("temperature_2m", this.temperature);
        object.addProperty("apparent_temperature", this.appTemp);
        object.addProperty("relativehumidity_2m", this.humidity);
        object.addProperty("visibility", this.visibility);
        object.addProperty("pressure_msl", this.pressureSeaLevel);
        object.addProperty("surface_pressure", this.surfacePressure);
        object.addProperty("cloudcover", this.cloudCover);
        object.addProperty("windspeed_10m", this.windSpeed);
        object.addProperty("winddirection_10m", this.windDirection);
        object.addProperty("windgusts_10m", this.windGusts);
        object.addProperty("precipitation", this.precipitation);
        object.addProperty("precipitation_probability", this.precipitationProbability);
        object.addProperty("snowfall", this.snowfall);
        object.addProperty("rain", this.rain);
        object.addProperty("showers", this.showers);
        object.addProperty("snow_depth", this.snowDepth);
        object.addProperty("weathercode", this.weatherCode.getCode());
        object.addProperty("freezinglevel_height", this.freezingLevelHeight);
        object.addProperty("is_day", this.isDay);
        object.addProperty("time", this.timestamp);

        return object;
    }

    public static Optional<List<HourlyWeather>> fromJSONArray(JsonObject hourly) {
        if (hourly == null)
            return Optional.empty();

        var hourlyArray = new ArrayList<HourlyWeather>();
        var hours = hourly.get("time").getAsJsonArray();
        var temp = hourly.get("temperature_2m").getAsJsonArray();
        var humi = hourly.get("relativehumidity_2m").getAsJsonArray();
        var appTemp = hourly.get("apparent_temperature").getAsJsonArray();
        var precipitation = hourly.get("precipitation").getAsJsonArray();
        var precipitationProbability = hourly.get("precipitation_probability").getAsJsonArray();
        var rain = hourly.get("rain").getAsJsonArray();
        var showers = hourly.get("showers").getAsJsonArray();
        var snowfall = hourly.get("snowfall").getAsJsonArray();
        var snow_depth = hourly.get("snow_depth").getAsJsonArray();
        var freezeLvl = hourly.get("freezinglevel_height").getAsJsonArray();
        var code = hourly.get("weathercode").getAsJsonArray();
        var pressureMSL = hourly.get("pressure_msl").getAsJsonArray();
        var surface = hourly.get("surface_pressure").getAsJsonArray();
        var cloudcover = hourly.get("cloudcover").getAsJsonArray();
        var visibility = hourly.get("visibility").getAsJsonArray();
        var speed10m = hourly.get("windspeed_10m").getAsJsonArray();
        var dir10 = hourly.get("winddirection_10m").getAsJsonArray();
        var gust = hourly.get("windgusts_10m").getAsJsonArray();
        var isDay = hourly.get("is_day").getAsJsonArray();

        for (int i = 0; i < hours.size(); i++) {
            hourlyArray.add(getBuilder()
                    .timestamp(hours.get(i).getAsLong())
                    .temperature(temp.get(i).getAsDouble())
                    .appTemperature(appTemp.get(i).getAsDouble())
                    .humidity(humi.get(i).getAsDouble())
                    .visibility(visibility.get(i).getAsDouble())
                    .pressureSeaLevel(pressureMSL.get(i).getAsDouble())
                    .pressureSurface(surface.get(i).getAsDouble())
                    .cloudCover(cloudcover.get(i).getAsDouble())
                    .windSpeed(speed10m.get(i).getAsDouble())
                    .windDirection(dir10.get(i).getAsDouble())
                    .precipitation(precipitation.get(i).getAsDouble())
                    .precipitationProbability(precipitationProbability.get(i).getAsDouble())
                    .snowfall(snowfall.get(i).getAsDouble())
                    .rain(rain.get(i).getAsDouble())
                    .showers(showers.get(i).getAsDouble())
                    .snowDepth(snow_depth.get(i).getAsDouble())
                    .weatherCode(WeatherCode.valueOf(code.get(i).getAsInt()))
                    .freezingLevelHeight(freezeLvl.get(i).getAsDouble())
                    .windGusts(gust.get(i).getAsDouble())
                    .isDay(isDay.get(i).getAsBoolean())
                    .build());
        }

        return Optional.of(hourlyArray);
    }

    public static Optional<HourlyWeather> fromJSON(JsonObject object) {
        return Optional.of(getBuilder()
                .timestamp(object.get("time").getAsLong())
                .temperature(object.get("temperature_2m").getAsDouble())
                .appTemperature(object.get("apparent_temperature").getAsDouble())
                .humidity(object.get("relativehumidity_2m").getAsDouble())
                .visibility(object.get("visibility").getAsDouble())
                .pressureSeaLevel(object.get("pressure_msl").getAsDouble())
                .pressureSurface(object.get("surface_pressure").getAsDouble())
                .cloudCover(object.get("cloudcover").getAsDouble())
                .windSpeed(object.get("windspeed_10m").getAsDouble())
                .windDirection(object.get("winddirection_10m").getAsDouble())
                .precipitation(object.get("precipitation").getAsDouble())
                .precipitationProbability(object.get("precipitation_probability").getAsDouble())
                .snowfall(object.get("snowfall").getAsDouble())
                .rain(object.get("rain").getAsDouble())
                .showers(object.get("showers").getAsDouble())
                .snowDepth(object.get("snow_depth").getAsDouble())
                .weatherCode(WeatherCode.valueOf(object.get("weathercode").getAsInt()))
                .freezingLevelHeight(object.get("freezinglevel_height").getAsDouble())
                .windGusts(object.get("windgusts_10m").getAsDouble())
                .isDay(object.get("is_day").getAsBoolean())
                .build());
    }

    public static HourlyWeatherBuilder getBuilder() {
        return new HourlyWeatherBuilder();
    }
}
