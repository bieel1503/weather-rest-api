package br.com.weather.weatherrest.data.weather;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private Optional<Integer> timezoneOffset;
    private Optional<String> admin1;
    private Optional<Integer> population;
    private Optional<CurrentWeather> currentWeather;
    private Optional<List<DailyWeather>> dailyWeather;
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
            this.timezoneOffset = Optional.of(tz.getOffset(System.currentTimeMillis()));
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

    public Optional<Integer> getTimezoneOffset() {
        return this.timezoneOffset;
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

    public Optional<List<DailyWeather>> getDailyWeather() {
        return this.dailyWeather;
    }

    public void setDailyWeather(Optional<List<DailyWeather>> dailyWeather) {
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
        // first check if there's data on DB
        if (this.currentWeather.isEmpty() || this.dailyWeather.isEmpty()) {
            this.setDataFromDB();
        }

        // then check if data is outdated/can be updated, if it is, then try to update
        if (this.canUpdateCurrent() || this.canUpdateDaily()) {
            MeteoAPI.requestLocationData(this)
                    .ifPresent((object) -> {
                        if (object.isJsonNull() || object.size() == 0) {
                            System.out.println("ué: " + this.getName());
                            return;
                        }

                        System.out.println("NOT FROM DB: " + this.name);

                        if (this.timezone.isEmpty() && object.has("timezone")) {
                            this.timezone = Optional.of(object.get("timezone").getAsString());

                            var tz = TimeZone.getTimeZone(this.timezone.get());
                            this.daylight = tz.inDaylightTime(new Date());
                            this.longTZ = Optional.of(tz.getDisplayName(this.daylight, TimeZone.LONG));
                            this.shortTZ = Optional.of(tz.getDisplayName(this.daylight, TimeZone.SHORT));
                        }

                        if (object.has("current_weather")) {
                            this.currentWeather = CurrentWeather.fromJSON(object.getAsJsonObject("current_weather"));
                        }

                        if (object.has("daily") && object.has("hourly")) {
                            this.dailyWeather = this.createDailyFromJSON(object);
                        }

                        this.lastUpdated = System.currentTimeMillis();
                    });
        }
    }

    public JsonObject toJsonObject(boolean include) {
        var obj = new JsonObject();
        obj.addProperty("id", this.id);
        obj.addProperty("name", this.name);
        obj.addProperty("latitude", this.latitude);
        obj.addProperty("longitude", this.longitude);

        if (this.country != null) {
            obj.addProperty("country", this.country);
        }

        if (this.countryCode != null) {
            obj.addProperty("country_code", this.countryCode);
        }

        this.timezone.ifPresent(t -> {
            obj.addProperty("timezone", t);
            obj.addProperty("timezone_short", this.shortTZ.get());
            obj.addProperty("timezone_long", this.longTZ.get());
            obj.addProperty("timezone_offset", (this.timezoneOffset.get() / 1000));
        });
        obj.addProperty("last_updated", this.lastUpdated);
        this.admin1.ifPresent((a) -> obj.addProperty("admin1", a));
        this.population.ifPresent((p) -> obj.addProperty("population", p));

        if (include) {
            var weather = new JsonObject();
            this.currentWeather.ifPresent((c) -> weather.add("current_weather", c.toJsonObject()));

            this.dailyWeather.ifPresent((d) -> {
                var daily = new JsonArray();
                for (var dailyWeather : d) {
                    daily.add(dailyWeather.toJsonObject());
                }
                weather.add("daily", daily);
            });

            if (weather.size() > 0) {
                obj.add("weather_data", weather);
            }
        }

        return obj;
    }

    private void setDataFromDB() {
        WeatherManager.getWeatherStorage()
                .queryWeatherData(this)
                .ifPresent(json -> {
                    var weather_data = json.getAsJsonObject("weather_data");
                    System.out.println("DATA FROM DB: " + this.name);

                    if (weather_data.has("current_weather")) {
                        this.currentWeather = CurrentWeather.fromJSON(weather_data.getAsJsonObject("current_weather"));
                    }

                    if (weather_data.has("daily")) {
                        this.dailyWeather = this.createDailyFromJSON(json);
                    }
                });

    }

    private Optional<List<DailyWeather>> createDailyFromJSON(JsonObject object) {

        // if it's from DB
        if (object.has("weather_data")) {
            var array = new ArrayList<DailyWeather>();
            var dailyArray = object.getAsJsonObject("weather_data").getAsJsonArray("daily");

            dailyArray.forEach(obj -> {
                var dailyObject = obj.getAsJsonObject();
                DailyWeather daily = DailyWeather.fromJSON(dailyObject).get();

                var hourlyArray = new ArrayList<HourlyWeather>();
                dailyObject.getAsJsonArray("hourly").forEach(h -> {
                    HourlyWeather.fromJSON(h.getAsJsonObject()).ifPresent(hr -> hourlyArray.add(hr));
                });

                daily.setHourlyWeather(Optional.of(hourlyArray));
                array.add(daily);
            });

            return Optional.of(array);
        }

        // now from meteo api
        var dailyArray = DailyWeather.fromJSONArray(object.getAsJsonObject("daily")).get();
        var hourlyArray = HourlyWeather.fromJSONArray(object.getAsJsonObject("hourly")).get();

        return setHourlyToCorrectDay(dailyArray, hourlyArray);
    }

    // sometimes, maybe, meteo doesn't give hourly for a full day, it gives 23 hours
    // sometimes, that's why
    // or maybe i'm dumb, but whatever
    private Optional<List<DailyWeather>> setHourlyToCorrectDay(List<DailyWeather> dailyArray,
            List<HourlyWeather> hourlyArray) {
        var map = new HashMap<String, List<HourlyWeather>>(7);

        hourlyArray.forEach(hour -> {
            var date = hour.getLocalDateTime(this.timezone.get()).toLocalDate().toString();
            var array = map.get(date);

            if (array == null) {
                array = new ArrayList<HourlyWeather>();
            }

            array.add(hour);
            map.put(date, array);
        });

        dailyArray.forEach(day -> {
            var date = day.getLocalDate(this.timezone.get()).toString();

            map.forEach((k, v) -> {
                if (k.equalsIgnoreCase(date)) {
                    day.setHourlyWeather(Optional.of(v));
                }
            });
        });

        return Optional.of(dailyArray);
    }

    // tried doing this with ternary, but it wasn't working, so lottas of IF it is!!
    public static WeatherLocation fromJSON(JsonObject object) {
        var builder = getBuilder();

        if (object.has("geonames")) {
            var geoLoc = object.getAsJsonArray("geonames").get(0).getAsJsonObject();

            builder.id(geoLoc.get("geonameId").getAsInt())
                    .name(geoLoc.get("name").getAsString())
                    .latitude(geoLoc.get("lat").getAsDouble())
                    .longitude(geoLoc.get("lng").getAsDouble())
                    .country(geoLoc.get("countryName").getAsString())
                    .countryCode(geoLoc.get("countryCode").getAsString());

            if (geoLoc.has("adminName1")) {
                builder.admin1(geoLoc.get("adminName1").getAsString());
            }
            if (geoLoc.has("population")) {
                builder.population(geoLoc.get("population").getAsInt());
            }

            return builder.build();
        }

        builder.id(object.get("id").getAsInt())
                .name(object.get("name").getAsString())
                .latitude(object.get("latitude").getAsDouble())
                .longitude(object.get("longitude").getAsDouble());

        if (object.has("country")) {
            builder.country(object.get("country").getAsString());
        }
        if (object.has("country_code")) {
            builder.countryCode(object.get("country_code").getAsString());
        }
        if (object.has("timezone")) {
            builder.timezone(object.get("timezone").getAsString());
        }
        if (object.has("admin1")) {
            builder.admin1(object.get("admin1").getAsString());
        }
        if (object.has("population")) {
            builder.population(object.get("population").getAsInt());
        }
        if (object.has("last_updated")) {
            builder.lastUpdated(object.get("last_updated").getAsInt());
        }

        return builder.build();
    }

    public static WeatherLocationBuilder getBuilder() {
        return new WeatherLocationBuilder();
    }
}
