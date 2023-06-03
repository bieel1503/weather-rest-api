package br.com.weather.weatherrest.data.weather;

import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import br.com.weather.weatherrest.data.weather.builder.WeatherLocationBuilder;
import br.com.weather.weatherrest.database.StorageTask;

public class WeatherLocation {
    private final int id;
    private final String name, normalizedName, country, countryCode;
    private final double latitude, longitude;
    private Optional<String> timezone, shortTZ, longTZ;
    private boolean daylight;
    private Optional<String> admin1;
    private Optional<Integer> population;
    private Optional<CurrentWeather> currentWeather;
    private Optional<DailyWeather[]> dailyWeather;
    private long lastUpdated, lastAccessed;

    public WeatherLocation(WeatherLocationBuilder builder) {
        this.id = builder.getId();
        this.name = builder.getName();
        this.normalizedName = MeteoAPI.normalize(this.name);
        this.country = builder.getCountry();
        this.countryCode = builder.getCountryCode();
        this.latitude = builder.getLatitude();
        this.longitude = builder.getLongitude();
        this.timezone = builder.getTimezone();
        this.population = builder.getPopulation();
        this.admin1 = builder.getAdmin1();
        this.currentWeather = builder.getCurrentWeather();
        this.dailyWeather = builder.getDailyWeather();
        this.lastUpdated = builder.getLastUpdated();
        this.lastAccessed = System.currentTimeMillis();

        this.timezone.ifPresent(t -> {
            var tz = TimeZone.getTimeZone(t);
            this.daylight = tz.inDaylightTime(new Date());
            this.longTZ = Optional.of(tz.getDisplayName(this.daylight, TimeZone.LONG));
            this.shortTZ = Optional.of(tz.getDisplayName(this.daylight, TimeZone.SHORT));
        });
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getNormalizedName() {
        return this.normalizedName;
    }

    public String getCountry() {
        return this.country;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public Optional<String> getTimeZone() {
        return this.timezone;
    }

    public Optional<String> getLongTimezone() {
        return this.longTZ;
    }

    public Optional<String> getShortTimezone() {
        return this.shortTZ;
    }

    public boolean getDaylight() {
        return this.daylight;
    }

    public Optional<String> getAdmin1() {
        return this.admin1;
    }

    public Optional<Integer> getPopulation() {
        return this.population;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public long getLastUpdated() {
        return this.lastUpdated;
    }

    public long getLastAcessed() {
        return this.lastAccessed;
    }

    public Optional<CurrentWeather> getCurrentWeather() {
        return this.currentWeather;
    }

    public Optional<DailyWeather[]> getDailyWeather() {
        return this.dailyWeather;
    }

    public void setDailyWeather(Optional<DailyWeather[]> dailyWeather) {
        this.dailyWeather = dailyWeather;
    }

    public void setCurrentWeather(Optional<CurrentWeather> currentWeather) {
        this.currentWeather = currentWeather;
    }

    public void setLastAccessed(long lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public boolean canUpdateDaily() {
        return !this.dailyWeather.isPresent()
                || System.currentTimeMillis() > (this.lastUpdated + TimeUnit.DAYS.toMillis(1));
    }

    public boolean canUpdateCurrent() {
        return !this.currentWeather.isPresent()
                || System.currentTimeMillis() > (this.lastUpdated + TimeUnit.HOURS.toMillis(1));
    }

    public boolean canClear() {
        return System.currentTimeMillis() > (this.lastAccessed + TimeUnit.HOURS.toMillis(3));
    }

    public boolean canStore() {
        return TimeUnit.MILLISECONDS
                .toSeconds(System.currentTimeMillis() - this.lastUpdated) < StorageTask.TASK_SECONDS;
    }

    public void updateData() {
        if (!this.canUpdateDaily() && !this.canUpdateCurrent())
            return;

        var data = MeteoAPI.requestLocationData(this);

        data.ifPresent((object) -> {
            if (object.isJsonNull() || object.size() == 0) {
                System.out.println("ué: " + this.getName());
                return;
            }

            if (this.timezone.isEmpty() && object.has("timezone")) {
                this.timezone = Optional.of(object.get("timezone").getAsString());

                var tz = TimeZone.getTimeZone(this.timezone.get());
                this.daylight = tz.inDaylightTime(new Date());
                this.longTZ = Optional.of(tz.getDisplayName(this.daylight, TimeZone.LONG));
                this.shortTZ = Optional.of(tz.getDisplayName(this.daylight, TimeZone.SHORT));
            }

            if (object.has("current_weather")) {
                this.currentWeather = CurrentWeather.fromJSON(object);
            }

            if (object.has("daily") && object.has("hourly")) {
                this.dailyWeather = DailyWeather.fromJSON(object);
            }

            this.lastUpdated = System.currentTimeMillis();
        });
    }

    public JsonObject toJsonObject(boolean include) {
        var obj = new JsonObject();
        obj.addProperty("id", this.id);
        obj.addProperty("name", this.name);
        obj.addProperty("latitude", this.latitude);
        obj.addProperty("longitude", this.longitude);
        obj.addProperty("country", this.country);
        obj.addProperty("country_code", this.countryCode);

        this.timezone.ifPresent(t -> {
            obj.addProperty("timezone", t);
            obj.addProperty("timezone_short", this.shortTZ.get());
            obj.addProperty("timezone_long", this.longTZ.get());
        });
        obj.addProperty("last_updated", this.lastUpdated);
        this.admin1.ifPresent((a) -> obj.addProperty("admin1", a));
        this.population.ifPresent((p) -> obj.addProperty("population", p));

        if (include) {
            var weather = new JsonObject();
            this.currentWeather.ifPresent((c) -> weather.add("current_weather", c.toJsonObject()));

            var daily = new JsonArray();
            this.dailyWeather.ifPresent((d) -> {
                for (var dailyWeather : d) {
                    daily.add(dailyWeather.toJsonObject());
                }
                weather.add("daily", daily);
            });
            if (weather.size() > 0)
                obj.add("weather_data", weather);
        }

        return obj;
    }

    public static WeatherLocation fromJSON(JsonObject object) {
        var builder = getBuilder();
        if (object.has("geonames")) {
            var geoLoc = object.getAsJsonArray("geonames").get(0).getAsJsonObject();

            builder.id(geoLoc.get("geonameId").getAsInt())
                    .name(geoLoc.get("name").getAsString())
                    .latitude(geoLoc.get("lat").getAsDouble())
                    .longitude(geoLoc.get("lng").getAsDouble())
                    .country(geoLoc.has("countryName") ? geoLoc.get("countryName").getAsString() : "none")
                    .countryCode(geoLoc.has("countryCode") ? geoLoc.get("countryCode").getAsString() : "none")
                    .admin1(geoLoc.has("adminName1") ? geoLoc.get("adminName1").getAsString() : "none")
                    .population(geoLoc.get("population").getAsInt());

        } else {
            builder.id(object.get("id").getAsInt())
                    .name(object.get("name").getAsString())
                    .latitude(object.get("latitude").getAsDouble())
                    .longitude(object.get("longitude").getAsDouble())
                    .country(object.has("country") ? object.get("country").getAsString() : "none")
                    .countryCode(object.has("country_code") ? object.get("country_code").getAsString() : "none")
                    .timezone(object.get("timezone").getAsString())
                    .lastUpdated(object.has("last_updated") ? object.get("last_updated").getAsLong()
                            : System.currentTimeMillis())
                    .admin1(object.has("admin1") ? object.get("admin1").getAsString() : null)
                    .currentWeather(CurrentWeather.fromJSON(object).orElse(null))
                    .dailyWeather(DailyWeather.fromJSON(object).orElse(null));

            if (object.has("population")) {
                builder.population(object.get("population").getAsInt());
            }
        }

        return builder.build();
    }

    public static WeatherLocationBuilder getBuilder() {
        return new WeatherLocationBuilder();
    }
}
