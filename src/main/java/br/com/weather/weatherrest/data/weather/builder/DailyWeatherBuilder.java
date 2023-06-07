package br.com.weather.weatherrest.data.weather.builder;

import java.util.List;
import java.util.Optional;

import br.com.weather.weatherrest.data.weather.DailyWeather;
import br.com.weather.weatherrest.data.weather.HourlyWeather;
import br.com.weather.weatherrest.data.weather.WeatherCode;

public final class DailyWeatherBuilder {
    private double maxTemp, minTemp, appMaxTemp, appMinTemp;
    private double precipitationSum, rainSum, showersSum, snowfallSum;
    private double precipitationHours;
    private WeatherCode weatherCode;
    private long sunrise, sunset;
    private double windSpeed, windGust, windDirection;
    private Optional<List<HourlyWeather>> hourlyWeather;
    private long timestamp;

    public DailyWeatherBuilder() {
        this.hourlyWeather = Optional.empty();
    }

    public DailyWeatherBuilder maxTemperature(double maxTemp) {
        this.maxTemp = maxTemp;
        return this;
    }

    public DailyWeatherBuilder minTemperature(double minTemp) {
        this.minTemp = minTemp;
        return this;
    }

    public DailyWeatherBuilder appMaxTemperature(double maxTemp) {
        this.appMaxTemp = maxTemp;
        return this;
    }

    public DailyWeatherBuilder appMinTemperature(double minTemp) {
        this.appMinTemp = minTemp;
        return this;
    }

    public DailyWeatherBuilder precipitationSum(double precipitationSum) {
        this.precipitationSum = precipitationSum;
        return this;
    }

    public DailyWeatherBuilder rainSum(double rainSum) {
        this.rainSum = rainSum;
        return this;
    }

    public DailyWeatherBuilder showersSum(double showersSum) {
        this.showersSum = showersSum;
        return this;
    }

    public DailyWeatherBuilder snowfallSum(double snowfallSum) {
        this.snowfallSum = snowfallSum;
        return this;
    }

    public DailyWeatherBuilder precipitationHours(double precipitationHours) {
        this.precipitationHours = precipitationHours;
        return this;
    }

    public DailyWeatherBuilder weatherCode(WeatherCode weatherCode) {
        this.weatherCode = weatherCode;
        return this;
    }

    public DailyWeatherBuilder sunrise(long sunrise) {
        this.sunrise = sunrise;
        return this;
    }

    public DailyWeatherBuilder sunset(long sunset) {
        this.sunset = sunset;
        return this;
    }

    public DailyWeatherBuilder windSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
        return this;
    }

    public DailyWeatherBuilder windGust(double windGust) {
        this.windGust = windGust;
        return this;
    }

    public DailyWeatherBuilder windDirection(double windDirection) {
        this.windDirection = windDirection;
        return this;
    }

    public DailyWeatherBuilder hourlyWeather(List<HourlyWeather> hourlyWeather) {
        if (hourlyWeather != null) {
            this.hourlyWeather = Optional.of(hourlyWeather);
        }

        return this;
    }

    public DailyWeatherBuilder timestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
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
        return hourlyWeather;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public DailyWeather build() {
        return new DailyWeather(this);
    }
}
