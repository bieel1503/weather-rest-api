package br.com.weather.weatherrest.database;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.weather.weatherrest.data.weather.MeteoAPI;
import br.com.weather.weatherrest.data.weather.WeatherLocation;

public class WeatherStorage {
    private final SQLiteStorage storage;
    private PreparedStatement[] statements; // 0 = insert, 1 = query, 2 = queryData

    public WeatherStorage(String filePath) {
        this.storage = new SQLiteStorage(filePath);
        this.statements = new PreparedStatement[3];
    }

    public Optional<List<WeatherLocation>> queryLocations() {
        try {
            var con = this.storage.getConnection();
            var query = "select " +
                    "id," +
                    "name," +
                    "country," +
                    "country_code," +
                    "latitude," +
                    "longitude," +
                    "timezone," +
                    "admin1," +
                    "population," +
                    "last_updated " +
                    "from locations;";

            var statement = con.prepareStatement(query);

            var resultSet = statement.executeQuery();
            List<WeatherLocation> locations = new ArrayList<>();
            while (resultSet.next()) {
                var location = WeatherLocation.getBuilder()
                        .id(resultSet.getInt(1))
                        .name(resultSet.getString(2))
                        .country(resultSet.getString(3))
                        .countryCode(resultSet.getString(4))
                        .latitude(resultSet.getDouble(5))
                        .longitude(resultSet.getDouble(6))
                        .timezone(resultSet.getString(7))
                        .admin1(resultSet.getString(8))
                        .population(resultSet.getInt(9))
                        .lastUpdated(resultSet.getLong(10))
                        .build();
                locations.add(location);
            }

            if (!locations.isEmpty()) {
                return Optional.of(locations);
            }

            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void store(Collection<WeatherLocation> locations) {
        if (locations.isEmpty())
            return;

        try {
            var con = this.storage.getConnection();
            con.setAutoCommit(false);

            if (this.statements[0] == null || this.statements[0].isClosed()) {
                var query = "insert into locations(" +
                        "id," +
                        "name," +
                        "normalized_name," +
                        "country," +
                        "country_code," +
                        "latitude," +
                        "longitude," +
                        "timezone," +
                        "admin1," +
                        "last_updated," +
                        "population," +
                        "weather_data) " +
                        "values(?,?,?,?,?,?,?,?,?,?,?,?) " +
                        "on conflict(id) " +
                        "do update " +
                        "set last_updated = excluded.last_updated, " +
                        "weather_data = excluded.weather_data;";

                this.statements[0] = con.prepareStatement(query);
            }
            var locArray = new ArrayList<WeatherLocation>(locations);

            var statement = this.statements[0];
            for (int i = 0; i < locArray.size(); i++) {
                var location = locArray.get(i);
                if (!location.canStore())
                    continue;

                var json = location.toJsonObject(true);

                System.out.println("store: " + location.getName());
                statement.setInt(1, location.getId());
                statement.setString(2, location.getName());
                statement.setString(3, location.getNormalizedName());
                statement.setString(4, location.getCountry());
                statement.setString(5, location.getCountryCode());
                statement.setDouble(6, location.getLatitude());
                statement.setDouble(7, location.getLongitude());
                statement.setString(8, location.getTimeZone().orElse("none"));
                statement.setString(9, location.getAdmin1().orElse("none"));
                statement.setLong(10, location.getLastUpdated());
                statement.setInt(11, location.getPopulation().orElse(0));
                statement.setString(12, json.has("weather_data") ? json.toString() : "{}");
                statement.addBatch();

                if (i % 500 == 0 || i == (locArray.size() - 1)) {
                    statement.executeBatch();
                }
            }
            con.commit();
            statement.clearBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<WeatherLocation> queryLocation(int id) {
        if (id < 0)
            Optional.empty();

        try {
            var con = this.storage.getConnection();
            if (this.statements[1] == null || this.statements[1].isClosed()) {
                var query = "select " +
                        "id," +
                        "name," +
                        "country," +
                        "country_code," +
                        "latitude," +
                        "longitude," +
                        "timezone," +
                        "admin1," +
                        "population," +
                        "last_updated " +
                        "from locations where id=?;";

                this.statements[1] = con.prepareStatement(query);
            }

            var statement = this.statements[1];
            statement.setInt(1, id);

            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                var location = WeatherLocation.getBuilder()
                        .id(resultSet.getInt(1))
                        .name(resultSet.getString(2))
                        .country(resultSet.getString(3))
                        .countryCode(resultSet.getString(4))
                        .latitude(resultSet.getDouble(5))
                        .longitude(resultSet.getDouble(6))
                        .timezone(resultSet.getString(7))
                        .admin1(resultSet.getString(8))
                        .population(resultSet.getInt(9))
                        .lastUpdated(resultSet.getLong(10))
                        .build();

                return Optional.of(location);

            }

            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<List<WeatherLocation>> queryLocation(String str) {
        var normalized = MeteoAPI.normalize(str);

        try {
            var con = this.storage.getConnection();
            var query = "select " +
                    "id," +
                    "name," +
                    "country," +
                    "country_code," +
                    "latitude," +
                    "longitude," +
                    "timezone," +
                    "last_updated," +
                    "admin1," +
                    "population " +
                    "from locations " +
                    "where normalized_name like '%" + normalized + "%';";

            var statement = con.prepareStatement(query);

            var resultSet = statement.executeQuery();
            List<WeatherLocation> list = new ArrayList<>();
            while (resultSet.next()) {
                var location = WeatherLocation.getBuilder()
                        .id(resultSet.getInt(1))
                        .name(resultSet.getString(2))
                        .country(resultSet.getString(3))
                        .countryCode(resultSet.getString(4))
                        .latitude(resultSet.getDouble(5))
                        .longitude(resultSet.getDouble(6))
                        .timezone(resultSet.getString(7))
                        .lastUpdated(resultSet.getLong(8))
                        .admin1(resultSet.getString(9))
                        .population(resultSet.getInt(10))
                        .build();

                list.add(location);
            }

            resultSet.close();
            statement.close();
            return Optional.of(list).filter((l) -> !l.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<JsonObject> queryWeatherData(WeatherLocation location) {
        try {
            var con = this.storage.getConnection();
            if (this.statements[2] == null || this.statements[2].isClosed()) {
                this.statements[2] = con.prepareStatement("select weather_data from locations where id=?;");
            }

            var statement = this.statements[2];
            statement.setInt(1, location.getId());

            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                var data = JsonParser.parseString(resultSet.getString(1)).getAsJsonObject();
                if (data.size() == 0) {
                    return Optional.empty();
                }
                return Optional.of(data);
            }

            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
