package br.com.weather.weatherrest.data.weather;

public enum WeatherCode {
    CLEAR_SKY(0),
    MAINLY_CLEAR(1),
    PARTLY_CLOUDY(2),
    OVERCAST(3),
    FOG(45),
    DEPOSITING_RIME_FOG(48),
    LIGHT_DRIZZLE(51),
    MODERATE_DRIZZLE(53),
    DENSE_DRIZZLE(55),
    LIGHT_FREEZING_DRIZZLE(56),
    DENSE_FREEZING_DRIZZLE(57),
    SLIGHT_RAIN(61),
    MODERATE_RAIN(63),
    HEAVY_RAIN(65),
    LIGHT_FREEZING_RAIN(66),
    HEAVY_FREEZING_RAIN(67),
    SLIGHT_SNOW_FALL(71),
    MODERATE_SNOW_FALL(73),
    HEAVY_SNOW_FALL(75),
    SNOW_GRAINS(77),
    SLIGHT_RAIN_SHOWER(80),
    MODERATE_RAIN_SHOWER(81),
    VIOLENT_RAIN_SHOWER(82),
    SLIGHT_SNOW_SHOWER(85),
    HEAVY_RAIN_SHOWER(86),
    THUNDERSTORM(95),
    SLIGHT_THUNDERSTORM_HAIL(96),
    HEAVY_THUNDERSTORM_HAIL(99),
    UNKNOWN(666);

    private final int code;

    WeatherCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
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
