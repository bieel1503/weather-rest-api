package br.com.weather.weatherrest.data.weather;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import br.com.weather.weatherrest.data.weather.builder.DailyWeatherBuilder;

public class DailyWeather {
    private final double maxTemp, minTemp, appMaxTemp, appMinTemp;
    private final double precipitationSum, rainSum, showersSum, snowfallSum;
    private final double precipitationHours;
    private final WeatherCode weatherCode;
    private final long sunrise, sunset;
    private final double windSpeed, windGust, windDirection;
    private Optional<List<HourlyWeather>> hourlyWeather;
    private final long timestamp;

    public DailyWeather(DailyWeatherBuilder builder) {
        this.maxTemp = builder.getMaxTemp();
        this.minTemp = builder.getMinTemp();
        this.appMaxTemp = builder.getAppMaxTemp();
        this.appMinTemp = builder.getAppMinTemp();
        this.precipitationSum = builder.getPrecipitationSum();
        this.precipitationHours = builder.getPrecipitationHours();
        this.rainSum = builder.getRainSum();
        this.showersSum = builder.getShowersSum();
        this.snowfallSum = builder.getSnowfallSum();
        this.weatherCode = builder.getWeatherCode();
        this.sunrise = builder.getSunrise();
        this.sunset = builder.getSunset();
        this.windSpeed = builder.getWindSpeed();
        this.windGust = builder.getWindGust();
        this.windDirection = builder.getWindDirection();
        this.hourlyWeather = builder.getHourlyWeather();
        this.timestamp = builder.getTimestamp();
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getAppMaxTemp() {
        return appMaxTemp;
    }

    public double getAppMinTemp() {
        return appMinTemp;
    }

    public double getPrecipitationSum() {
        return precipitationSum;
    }

    public double getRainSum() {
        return rainSum;
    }

    public double getShowersSum() {
        return showersSum;
    }

    public double getSnowfallSum() {
        return snowfallSum;
    }

    public double getPrecipitationHours() {
        return precipitationHours;
    }

    public WeatherCode getWeatherCode() {
        return weatherCode;
    }

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getWindGust() {
        return windGust;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public Optional<List<HourlyWeather>> getHourlyWeather() {
        return this.hourlyWeather;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public LocalDate getLocalDate(String timezone) {
        return LocalDate.ofInstant(Instant.ofEpochSecond(this.timestamp), ZoneId.of(timezone));
    }

    public void setHourlyWeather(Optional<List<HourlyWeather>> hourlyWeather) {
        this.hourlyWeather = hourlyWeather;
    }

    public JsonObject toJsonObject() {
        var obj = new JsonObject();
        obj.addProperty("temperature_2m_max", this.maxTemp);
        obj.addProperty("temperature_2m_min", this.minTemp);
        obj.addProperty("apparent_temperature_max", this.appMaxTemp);
        obj.addProperty("apparent_temperature_min", this.appMinTemp);
        obj.addProperty("precipitation_sum", this.precipitationSum);
        obj.addProperty("rain_sum", this.rainSum);
        obj.addProperty("showers_sum", this.showersSum);
        obj.addProperty("snowfall_sum", this.snowfallSum);
        obj.addProperty("precipitation_hours", this.precipitationHours);
        obj.addProperty("sunrise", this.sunrise);
        obj.addProperty("sunset", this.sunset);
        obj.addProperty("windspeed_10m_max", this.windSpeed);
        obj.addProperty("windgusts_10m_max", this.windGust);
        obj.addProperty("winddirection_10m_dominant", this.windDirection);
        obj.addProperty("time", this.timestamp);
        obj.add("weathercode", this.weatherCode.toJsonObject());

        this.hourlyWeather.ifPresent(list -> {
            var hourly = new JsonArray();
            for (var hour : list) {
                hourly.add(hour.toJSONObject());
            }
            obj.add("hourly", hourly);
        });

        return obj;
    }

    public static Optional<List<DailyWeather>> fromJSONArray(JsonObject object) {
        if (object == null)
            return Optional.empty();

        var dailyArray = new ArrayList<DailyWeather>();
        var time = object.get("time").getAsJsonArray();
        var maxT = object.get("temperature_2m_max").getAsJsonArray();
        var minT = object.get("temperature_2m_min").getAsJsonArray();
        var appMaxT = object.get("apparent_temperature_max").getAsJsonArray();
        var appMinT = object.get("apparent_temperature_min").getAsJsonArray();
        var pSum = object.get("precipitation_sum").getAsJsonArray();
        var rSum = object.get("rain_sum").getAsJsonArray();
        var sSum = object.get("showers_sum").getAsJsonArray();
        var sfSum = object.get("snowfall_sum").getAsJsonArray();
        var pHours = object.get("precipitation_hours").getAsJsonArray();
        var code = object.get("weathercode").getAsJsonArray();
        var sunrise = object.get("sunrise").getAsJsonArray();
        var sunset = object.get("sunset").getAsJsonArray();
        var wSpeed = object.get("windspeed_10m_max").getAsJsonArray();
        var wGust = object.get("windgusts_10m_max").getAsJsonArray();
        var wDir = object.get("winddirection_10m_dominant").getAsJsonArray();

        for (int i = 0; i < time.size(); i++) {
            dailyArray.add(getBuilder()
                .timestamp(time.get(i).getAsLong())
                .maxTemperature(maxT.get(i).getAsDouble())
                .minTemperature(minT.get(i).getAsDouble())
                .appMaxTemperature(appMaxT.get(i).getAsDouble())
                .appMinTemperature(appMinT.get(i).getAsDouble())
                .precipitationSum(pSum.get(i).getAsDouble())
                .rainSum(rSum.get(i).getAsDouble())
                .showersSum(sSum.get(i).getAsDouble())
                .snowfallSum(sfSum.get(i).getAsDouble())
                .precipitationHours(pHours.get(i).getAsDouble())
                .weatherCode(WeatherCode.valueOf(code.get(i).getAsInt()))
                .sunrise(sunrise.get(i).getAsLong())
                .sunset(sunset.get(i).getAsLong())
                .windSpeed(wSpeed.get(i).getAsDouble())
                .windGust(wGust.get(i).getAsDouble())
                .windDirection(wDir.get(i).getAsDouble())
                .build()
            );
        }


        return Optional.of(dailyArray);
    }

    public static Optional<DailyWeather> fromJSON(JsonObject object) {
        if (object == null)
            return Optional.empty();

        return Optional.of(getBuilder()
                .maxTemperature(object.get("temperature_2m_max").getAsDouble())
                .minTemperature(object.get("temperature_2m_min").getAsDouble())
                .appMaxTemperature(object.get("apparent_temperature_max").getAsDouble())
                .appMinTemperature(object.get("apparent_temperature_min").getAsDouble())
                .precipitationSum(object.get("precipitation_sum").getAsDouble())
                .rainSum(object.get("rain_sum").getAsDouble())
                .showersSum(object.get("showers_sum").getAsDouble())
                .snowfallSum(object.get("snowfall_sum").getAsDouble())
                .precipitationHours(object.get("precipitation_hours").getAsDouble())
                .weatherCode(WeatherCode.valueOf(object.getAsJsonObject("weathercode").get("code").getAsInt()))
                .sunrise(object.get("sunrise").getAsLong())
                .sunset(object.get("sunset").getAsLong())
                .windSpeed(object.get("windspeed_10m_max").getAsDouble())
                .windGust(object.get("windgusts_10m_max").getAsDouble())
                .windDirection(object.get("winddirection_10m_dominant").getAsDouble())
                .timestamp(object.get("time").getAsLong())
                .build());

    }

    public static DailyWeatherBuilder getBuilder() {
        return new DailyWeatherBuilder();
    }
}
