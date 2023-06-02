package br.com.weather.weatherrest.data.weather;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    private final HourlyWeather[] hourlyWeather;
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

    public HourlyWeather[] getHourlyWeather() {
        return this.hourlyWeather;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public LocalDateTime getDateTime() {
        return Instant.ofEpochMilli(this.timestamp).atZone(ZoneOffset.UTC).toLocalDateTime();
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
        obj.addProperty("weathercode", this.weatherCode.getCode());
        obj.addProperty("sunrise", this.sunrise);
        obj.addProperty("sunset", this.sunset);
        obj.addProperty("windspeed_10m_max", this.windSpeed);
        obj.addProperty("windgusts_10m_max", this.windGust);
        obj.addProperty("winddirection_10m_dominant", this.windDirection);
        obj.addProperty("time", this.timestamp);

        var hourly = new JsonArray();
        JsonObject hourlyObj;
        for (var weather : this.hourlyWeather) {
            hourlyObj = new JsonObject();
            hourlyObj.addProperty("temperature_2m", weather.getTemperature());
            hourlyObj.addProperty("apparent_temperature", weather.getAppTemp());
            hourlyObj.addProperty("relativehumidity_2m", weather.getHumidity());
            hourlyObj.addProperty("visibility", weather.getVisibility());
            hourlyObj.addProperty("pressure_msl", weather.getPressureSeaLevel());
            hourlyObj.addProperty("surface_pressure", weather.getSurfacePressure());
            hourlyObj.addProperty("cloudcover", weather.getCloudcover());
            hourlyObj.addProperty("windspeed_10m", weather.getWindSpeed());
            hourlyObj.addProperty("windgusts_10m", weather.getWindGusts());
            hourlyObj.addProperty("winddirection_10m", weather.getWindDirection());
            hourlyObj.addProperty("precipitation", weather.getPrecipitation());
            hourlyObj.addProperty("snowfall", weather.getSnowfall());
            hourlyObj.addProperty("rain", weather.getRain());
            hourlyObj.addProperty("showers", weather.getShowers());
            hourlyObj.addProperty("snow_depth", weather.getSnowDepth());
            hourlyObj.addProperty("weathercode", weather.getWeatherCode().getCode());
            hourlyObj.addProperty("freezinglevel_height", weather.getFreezingLevelHeight());
            hourlyObj.addProperty("time", weather.getTimestamp());
            hourly.add(hourlyObj);
        }
        obj.add("hourly", hourly);
        return obj;
    }

    public static Optional<DailyWeather[]> fromJSON(JsonObject object) {
        if (!object.has("weather_data") && !object.has("daily"))
            return Optional.empty();

        var dailyArray = new DailyWeather[7];

        if (object.has("weather_data")) {
            var weather_data = object.get("weather_data").getAsJsonObject();
            if (!weather_data.has("daily"))
                return Optional.empty();

            var daily = weather_data.get("daily").getAsJsonArray();
            for (int i = 0; i < daily.size(); i++) {
                var dailyObject = daily.get(i).getAsJsonObject();
                var hourly = dailyObject.get("hourly").getAsJsonArray();

                var hourlyWeather = new HourlyWeather[23];
                for (int j = 0; j < (hourly.size() - 1); j++) {
                    var hourlyObject = hourly.get(j).getAsJsonObject();

                    hourlyWeather[j] = HourlyWeather.getBuilder()
                            .timestamp(hourlyObject.get("time").getAsLong())
                            .temperature(hourlyObject.get("temperature_2m").getAsDouble())
                            .appTemperature(hourlyObject.get("apparent_temperature").getAsDouble())
                            .humidity(hourlyObject.get("relativehumidity_2m").getAsDouble())
                            .visibility(hourlyObject.get("visibility").getAsDouble())
                            .pressureSeaLevel(hourlyObject.get("pressure_msl").getAsDouble())
                            .pressureSurface(hourlyObject.get("surface_pressure").getAsDouble())
                            .cloudCover(hourlyObject.get("cloudcover").getAsDouble())
                            .windSpeed(hourlyObject.get("windspeed_10m").getAsDouble())
                            .windDirection(hourlyObject.get("winddirection_10m").getAsDouble())
                            .precipication(hourlyObject.get("precipitation").getAsDouble())
                            .snowfall(hourlyObject.get("snowfall").getAsDouble())
                            .rain(hourlyObject.get("rain").getAsDouble())
                            .showers(hourlyObject.get("showers").getAsDouble())
                            .snowDepth(hourlyObject.get("snow_depth").getAsDouble())
                            .weatherCode(WeatherCode.valueOf(hourlyObject.get("weathercode").getAsInt()))
                            .freezingLevelHeight(hourlyObject.get("freezinglevel_height").getAsDouble())
                            .windGusts(hourlyObject.get("windgusts_10m").getAsDouble())
                            .build();
                }

                dailyArray[i] = getBuilder()
                        .maxTemperature(dailyObject.get("temperature_2m_max").getAsDouble())
                        .minTemperature(dailyObject.get("temperature_2m_min").getAsDouble())
                        .appMaxTemperature(dailyObject.get("apparent_temperature_max").getAsDouble())
                        .appMinTemperature(dailyObject.get("apparent_temperature_min").getAsDouble())
                        .precipitationSum(dailyObject.get("precipitation_sum").getAsDouble())
                        .rainSum(dailyObject.get("rain_sum").getAsDouble())
                        .showersSum(dailyObject.get("showers_sum").getAsDouble())
                        .snowfallSum(dailyObject.get("snowfall_sum").getAsDouble())
                        .precipitationHours(dailyObject.get("precipitation_hours").getAsDouble())
                        .weatherCode(WeatherCode.valueOf(dailyObject.get("weathercode").getAsInt()))
                        .sunrise(dailyObject.get("sunrise").getAsLong())
                        .sunset(dailyObject.get("sunset").getAsLong())
                        .windSpeed(dailyObject.get("windspeed_10m_max").getAsDouble())
                        .windGust(dailyObject.get("windgusts_10m_max").getAsDouble())
                        .windDirection(dailyObject.get("winddirection_10m_dominant").getAsDouble())
                        .timeStamp(dailyObject.get("time").getAsLong())
                        .hourlyWeather(hourlyWeather)
                        .build();

            }

            return Optional.of(dailyArray);
        }

        var hourly = HourlyWeather.fromJSON(object).get();
        var daily = object.get("daily").getAsJsonObject();
        var days = daily.get("time").getAsJsonArray();
        var codes = daily.get("weathercode").getAsJsonArray();
        var tMax = daily.get("temperature_2m_max").getAsJsonArray();
        var tMin = daily.get("temperature_2m_min").getAsJsonArray();
        var appMax = daily.get("apparent_temperature_max").getAsJsonArray();
        var appMin = daily.get("apparent_temperature_min").getAsJsonArray();
        var sunrise = daily.get("sunrise").getAsJsonArray();
        var sunset = daily.get("sunset").getAsJsonArray();
        var precSum = daily.get("precipitation_sum").getAsJsonArray();
        var rainSum = daily.get("rain_sum").getAsJsonArray();
        var showersSum = daily.get("showers_sum").getAsJsonArray();
        var snowSum = daily.get("snowfall_sum").getAsJsonArray();
        var precHrs = daily.get("precipitation_hours").getAsJsonArray();
        var windSpeed = daily.get("windspeed_10m_max").getAsJsonArray();
        var windGust = daily.get("windgusts_10m_max").getAsJsonArray();
        var windDir = daily.get("winddirection_10m_dominant").getAsJsonArray();

        for (int i = 0; i < 7; i++) {
            dailyArray[i] = DailyWeather.getBuilder()
                    .timeStamp(days.get(i).getAsLong() * 1000)
                    .weatherCode(WeatherCode.valueOf(codes.get(i).getAsInt()))
                    .maxTemperature(tMax.get(i).getAsDouble())
                    .minTemperature(tMin.get(i).getAsDouble())
                    .appMaxTemperature(appMax.get(i).getAsDouble())
                    .appMinTemperature(appMin.get(i).getAsDouble())
                    .sunrise(sunrise.get(i).getAsLong())
                    .sunset(sunset.get(i).getAsLong())
                    .precipitationSum(precSum.get(i).getAsDouble())
                    .rainSum(rainSum.get(i).getAsDouble())
                    .showersSum(showersSum.get(i).getAsDouble())
                    .snowfallSum(snowSum.get(i).getAsDouble())
                    .precipitationHours(precHrs.get(i).getAsDouble())
                    .windSpeed(windSpeed.get(i).getAsDouble())
                    .windGust(windGust.get(i).getAsDouble())
                    .windDirection(windDir.get(i).getAsDouble())
                    .hourlyWeather(hourly[i])
                    .build();
        }

        return Optional.of(dailyArray);
    }

    public static DailyWeatherBuilder getBuilder() {
        return new DailyWeatherBuilder();
    }
}
