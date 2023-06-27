package br.com.weather.weatherrest.data.weather;

import com.google.gson.JsonObject;

public enum WeatherCode {
    CLEAR_SKY(0, "clear sky"),
    MAINLY_CLEAR(1, "mainly clear"),
    PARTLY_CLOUDY(2, "partly cloudy"),
    OVERCAST(3, "overcast clouds"),
    FOG(45, "fog"),
    DEPOSITING_RIME_FOG(48, "depositing rime fog"),
    LIGHT_DRIZZLE(51, "light drizzle"),
    MODERATE_DRIZZLE(53, "moderate drizzle"),
    DENSE_DRIZZLE(55, "dense drizzle"),
    LIGHT_FREEZING_DRIZZLE(56, "freezing drizzle"),
    DENSE_FREEZING_DRIZZLE(57, "freezing drizzle"),
    SLIGHT_RAIN(61, "slight rain"),
    MODERATE_RAIN(63, "moderate rain"),
    HEAVY_RAIN(65, "heavy rain"),
    LIGHT_FREEZING_RAIN(66, "freezing rain"),
    HEAVY_FREEZING_RAIN(67, "freezing rain"),
    SLIGHT_SNOW_FALL(71, "slight snow fall"),
    MODERATE_SNOW_FALL(73, "moderate snow fall"),
    HEAVY_SNOW_FALL(75, "heavy snow fall"),
    SNOW_GRAINS(77, "snow grains"),
    SLIGHT_RAIN_SHOWER(80, "slight rain shower"),
    MODERATE_RAIN_SHOWER(81, "moderate rain shower"),
    VIOLENT_RAIN_SHOWER(82, "violent rain shower"),
    SLIGHT_SNOW_SHOWER(85, "slight snow shower"),
    HEAVY_RAIN_SHOWER(86, "heavy rain shower"),
    THUNDERSTORM(95, "thunderstorm"),
    SLIGHT_THUNDERSTORM_HAIL(96, "slight thunderstorm hail"),
    HEAVY_THUNDERSTORM_HAIL(99, "heavy thunderstorm hail"),
    UNKNOWN(666, "UNKNOWN");

    private final int code;
    private final String description;

    WeatherCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public JsonObject toJsonObject() {
        var json = new JsonObject();
        json.addProperty("code", this.code);
        json.addProperty("description", this.description);
        return json;
    }

    public static WeatherCode valueOf(int code) {
        var values =  WeatherCode.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].getCode() == code) {
                return values[i];
            }
        }
        return WeatherCode.UNKNOWN;
    }
}
