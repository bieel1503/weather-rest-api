package br.com.weather.weatherrest.data.weather;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import br.com.weather.weatherrest.WeatherRestApplication;
import br.com.weather.weatherrest.database.StorageTask;
import br.com.weather.weatherrest.database.WeatherStorage;

public final class WeatherManager {
    private final static StorageTask task = new StorageTask();
    private final static HashMap<Integer, WeatherLocation> locations = new HashMap<>();
    private final static SearchPatterns patterns = new SearchPatterns(
            System.getProperty("user.dir") + "/patterns.json");
    private final static WeatherStorage storage = new WeatherStorage(System.getProperty("user.dir") + "/locations.db");

    public static Optional<WeatherLocation> getById(int id) {
        var location = locations.get(id);
        if (location != null) {
            location.getDailyWeather().ifPresentOrElse(d -> {
                System.out.println("update attempt");
                location.updateData();
            }, () -> {
                storage.queryWeatherData(location).ifPresentOrElse(json -> {
                    System.out.println("data from DB: " + location.getName());
                    location.setDailyWeather(DailyWeather.fromJSON(json));
                    location.setCurrentWeather(CurrentWeather.fromJSON(json));
                }, () -> {
                    location.updateData();
                });
            });

            return Optional.of(location);
        }

        /*
         * var query = storage.queryLocation(id);
         * if (query.isPresent()) {
         * location = query.get();
         * locations.put(location.getId(), location);
         * location.updateData();
         * return query;
         * }
         */

        return Optional.empty();
    }

    public static Optional<List<WeatherLocation>> getByName(String name) {
        var normalized = MeteoAPI.normalize(name);
        List<WeatherLocation> list = locations.values()
                .stream()
                .filter((w) -> Pattern.matches("(?i)^.*" + normalized + ".*$", w.getNormalizedName()))
                .collect(Collectors.toList());

        /*
         * storage.queryLocation(normalized).ifPresent((l) -> {
         * l.forEach((w) -> {
         * if (!locations.containsKey(w.getId())) {
         * locations.put(w.getId(), w);
         * list.add(w);
         * System.out.println("new object from SQL: " + w.getName());
         * }
         * });
         * });
         */

        if (list.isEmpty() || !patterns.contains(normalized.toLowerCase())) {
            var search = MeteoAPI.requestLocationByName(normalized);
            search.ifPresent((l) -> {
                l.forEach((w) -> {
                    System.out.println("new search by name = " + w.getName());
                    list.add(w);
                    locations.put(w.getId(), w);
                });
            });

            patterns.add(normalized);
        }

        return Optional.of(list).filter((l) -> !l.isEmpty());
    }

    public static Optional<WeatherLocation> getByCoords(double latitude, double longitude) {
        return MeteoAPI.requestLocationByCoods(latitude, longitude);
    }

    public static void initialize() {
        loadLocations();
        task.start();
    }

    public static void storeLocations() {
        storage.store(locations.values());
    }

    public static void storePatterns() {
        patterns.storePatterns();
    }

    public static void clearLocations() {
        locations.values().forEach((w) -> {
            if (w.canClear()) {
                w.setCurrentWeather(Optional.empty());
                w.setDailyWeather(Optional.empty());
            }
        });
    }

    private static void loadLocations() {
        var before = System.currentTimeMillis();
        var locs = storage.queryLocations();
        locs.ifPresent(l -> {
            l.forEach(loc -> {
                locations.put(loc.getId(), loc);
            });

            var after = System.currentTimeMillis() - before;
            WeatherRestApplication.logger.info("loaded " + l.size() + " locations in " + after + " ms.");

        });
    }
}
