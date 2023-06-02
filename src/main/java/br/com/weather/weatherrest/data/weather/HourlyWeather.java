package br.com.weather.weatherrest.data.weather;

import java.util.Optional;

import com.google.gson.JsonObject;

import br.com.weather.weatherrest.data.weather.builder.HourlyWeatherBuilder;

public class HourlyWeather {
    private final double temperature, appTemp;
    private final double humidity, visibility;
    private final double pressureSeaLevel, surfacePressure;
    private final double cloudcover, windSpeed, windGusts, windDirection;
    private final double precipitation, snowfall, rain, showers, snowDepth;
    private final WeatherCode weatherCode;
	private final double freezingLevelHeight;
    private final long timestamp;

    public HourlyWeather(HourlyWeatherBuilder builder) {
        this.temperature = builder.getTemperature();
        this.appTemp = builder.getAppTemp();
        this.humidity = builder.getHumidity();
        this.visibility = builder.getVisibility();
        this.pressureSeaLevel = builder.getPressureSeaLevel();
        this.surfacePressure = builder.getSurfacePressure();
        this.cloudcover = builder.getCloudcover();
        this.windSpeed = builder.getWindSpeed();
        this.windGusts = builder.getWindGusts();
        this.windDirection = builder.getWindDirection();
        this.precipitation = builder.getPrecipitation();
        this.snowfall = builder.getSnowfall();
        this.rain = builder.getRain();
        this.showers = builder.getShowers();
        this.snowDepth = builder.getSnowDepth();
        this.weatherCode = builder.getWeatherCode();
        this.freezingLevelHeight = builder.getFreezingLevelHeight();
        this.timestamp = builder.getTimestamp();
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
		return cloudcover;
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

    public static Optional<HourlyWeather[][]> fromJSON(JsonObject object) {
        if (object.has("hourly")) {
            var hourlyArray = new HourlyWeather[7][24];
            var hourly = object.get("hourly").getAsJsonObject();
            var hours = hourly.get("time").getAsJsonArray();
            var temp = hourly.get("temperature_2m").getAsJsonArray();
            var humi = hourly.get("relativehumidity_2m").getAsJsonArray();
            var appTemp = hourly.get("apparent_temperature").getAsJsonArray();
            var precipitation = hourly.get("precipitation").getAsJsonArray();
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

            for (int i = 0; i < hours.size(); i++) {
                hourlyArray[(int)i / 24][i % 24] = HourlyWeather.getBuilder()
                .timestamp(hours.get(i).getAsLong()*1000)
                .temperature(temp.get(i).getAsDouble())
                .appTemperature(appTemp.get(i).getAsDouble())
                .humidity(humi.get(i).getAsDouble())
                .visibility(visibility.get(i).getAsDouble())
                .pressureSeaLevel(pressureMSL.get(i).getAsDouble())
                .pressureSurface(surface.get(i).getAsDouble())
                .cloudCover(cloudcover.get(i).getAsDouble())
                .windSpeed(speed10m.get(i).getAsDouble())
                .windDirection(dir10.get(i).getAsDouble())
                .precipication(precipitation.get(i).getAsDouble())
                .snowfall(snowfall.get(i).getAsDouble())
                .rain(rain.get(i).getAsDouble())
                .showers(showers.get(i).getAsDouble())
                .snowDepth(snow_depth.get(i).getAsDouble())
                .weatherCode(WeatherCode.valueOf(code.get(i).getAsInt()))
                .freezingLevelHeight(freezeLvl.get(i).getAsDouble())
                .windGusts(gust.get(i).getAsDouble())
                .build();
            }

            return Optional.of(hourlyArray);
        }

        return Optional.empty();
    }

    public static HourlyWeatherBuilder getBuilder() {
        return new HourlyWeatherBuilder();
    }
}
