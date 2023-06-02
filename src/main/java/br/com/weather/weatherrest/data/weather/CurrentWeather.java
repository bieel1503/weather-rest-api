package br.com.weather.weatherrest.data.weather;

import java.util.Optional;
import com.google.gson.JsonObject;

public class CurrentWeather {
    private final double temperature;
    private final double windSpeed, windDirection;
    private final WeatherCode code;
    private final long timestamp;

    public CurrentWeather(double temperature, double windSpeed, double windDirection, WeatherCode code, long timestamp) {
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

    public JsonObject toJsonObject() {
        var obj = new JsonObject();
        obj.addProperty("temperature", this.temperature);
        obj.addProperty("windspeed", this.windSpeed);
        obj.addProperty("winddirection", this.windDirection);
        obj.addProperty("weathercode", this.code.getCode());
        obj.addProperty("time", this.timestamp);
        return obj;
    }

    public static Optional<CurrentWeather> fromJSON(JsonObject object) {
        var current_data = object.has("current_weather") ? object.get("current_weather").getAsJsonObject() : object.has("weather_data") ? object.get("weather_data").getAsJsonObject().get("current_weather").getAsJsonObject() : null;

        if (current_data != null && current_data.size() > 0) {
            var temp = current_data.get("temperature").getAsDouble();
            var speed = current_data.get("windspeed").getAsDouble();
            var direction = current_data.get("winddirection").getAsDouble();
            var code = current_data.get("weathercode").getAsInt();
            var time = current_data.get("time").getAsLong();

            return Optional.of(new CurrentWeather(temp, speed, direction, WeatherCode.valueOf(code), time));
        }

        return Optional.empty();
    }
}
