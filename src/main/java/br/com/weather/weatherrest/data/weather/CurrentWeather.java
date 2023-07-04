package br.com.weather.weatherrest.data.weather;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonObject;

public class CurrentWeather {
    private final double temperature;
    private final double windSpeed, windDirection;
    private final WeatherCode code;
    private final long timestamp;

    public CurrentWeather(double temperature, double windSpeed, double windDirection, WeatherCode code,
            long timestamp) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.code = code;
        this.timestamp = timestamp;
    }

    public double getTemperature() {
        return this.temperature;
    }

    public double getWindSpeed() {
        return this.windSpeed;
    }

    public double getWindDirection() {
        return this.windDirection;
    }

    public WeatherCode getCode() {
        return this.code;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public boolean canUpdate() {
        return (System.currentTimeMillis() - this.timestamp) > TimeUnit.HOURS.toMillis(1);
    }

    public JsonObject toJsonObject() {
        var obj = new JsonObject();
        obj.addProperty("temperature", this.temperature);
        obj.addProperty("windspeed", this.windSpeed);
        obj.addProperty("winddirection", this.windDirection);
        obj.addProperty("time", this.timestamp);
        obj.add("weathercode", this.code.toJsonObject());
        return obj;
    }

    public static Optional<CurrentWeather> fromJSON(JsonObject object) {
        if (object == null)
            return Optional.empty();

        var time = object.get("time").getAsLong();
        var temp = object.get("temperature").getAsDouble();

        var wcode = object.get("weathercode");
        var code = wcode.isJsonObject() ? wcode.getAsJsonObject().get("code").getAsInt() : wcode.getAsInt();

        var speed = object.get("windspeed").getAsDouble();
        var direction = object.get("winddirection").getAsDouble();

        return Optional.of(new CurrentWeather(temp, speed, direction, WeatherCode.valueOf(code), time));
    }
}
