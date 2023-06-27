package br.com.weather.weatherrest.data.weather;

import java.math.RoundingMode;
import java.text.DecimalFormat;
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
            location.updateData();
            location.setLastAccessed(System.currentTimeMillis());
            return Optional.of(location);
        }

        return Optional.empty();
    }

    public static Optional<List<WeatherLocation>> getByName(String name) {
        var normalized = MeteoAPI.normalize(name);
        List<WeatherLocation> list = locations.values()
                .stream()
                .filter((w) -> Pattern.matches("(?i)^.*" + normalized + ".*$", w.getNormalizedName()))
                .collect(Collectors.toList());

        if (list.isEmpty() || !patterns.contains(normalized.toLowerCase())) {
            MeteoAPI.requestLocationByName(normalized)
                    .ifPresent((l) -> {
                        l.forEach((w) -> {
                            if (!locations.containsKey(w.getId())) {
                                System.out.println("new search by name = " + w.getName());
                                list.add(w);
                                locations.put(w.getId(), w);
                            }
                        });
                    });

            patterns.add(normalized);
        }

        return Optional.of(list).filter((l) -> !l.isEmpty());
    }

    public static Optional<WeatherLocation> getByCoords(String latitude, String longitude) {
        List<WeatherLocation> found;
        String fLat, fLong;
        try {
            var dFormat = new DecimalFormat("#.##");
            dFormat.setRoundingMode(RoundingMode.DOWN);

            fLat = dFormat.format(dFormat.parse(latitude));
            fLong = dFormat.format(dFormat.parse(longitude));

            found = locations.values().stream().filter(l -> {
                var wL = dFormat.format(l.getLatitude());
                var wLn = dFormat.format(l.getLongitude());

                return (wL.equals(fLat) && wLn.equals(fLong));
            }).toList();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }

        if (!found.isEmpty()) {
            return Optional.of(found.get(0));
        }

        var loc = MeteoAPI.requestLocationByCoods(fLat, fLong);

        if (loc.isPresent()) {
            var cachedLoc = WeatherManager.getById(loc.get().getId());
            if (cachedLoc.isPresent()) {
                return cachedLoc;
            }

            locations.put(loc.get().getId(), loc.get());
            return loc;
        }

        return Optional.empty();
    }

    public static WeatherStorage getWeatherStorage() {
        return storage;
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
        storage.queryLocations()
                .ifPresent(l -> {
                    l.forEach(loc -> {
                        locations.put(loc.getId(), loc);
                    });

                    var after = System.currentTimeMillis() - before;
                    WeatherRestApplication.logger.info("loaded " + l.size() + " locations in " + after + " ms.");
                });
    }
}
