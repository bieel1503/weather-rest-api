package br.com.weather.weatherrest.data.weather;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import br.com.weather.weatherrest.WeatherRestApplication;

public final class MeteoAPI {
    private static final String geoUsername = "bieel1503";

    public static Optional<List<WeatherLocation>> requestLocationByName(String name) {
        try {
            var encodedName = URLEncoder.encode(name, "UTF-8");
            var apiUrl = URI
                    .create("https://geocoding-api.open-meteo.com/v1/search?name=" + encodedName + "&count=100");
            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder(apiUrl).GET().build();
            var response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                WeatherRestApplication.logger
                        .error("searchByName: statusCode = " + response.statusCode() + ", body = " + response.body());
                return Optional.empty();
            }

            var object = JsonParser.parseString(response.body()).getAsJsonObject();
            if (!object.isJsonNull() && object.has("results")) {
                var results = object.get("results").getAsJsonArray();
                if (!results.isEmpty()) {
                    var list = new ArrayList<WeatherLocation>();
                    for (JsonElement e : results) {
                        list.add(WeatherLocation.fromJSON(e.getAsJsonObject()));
                    }
                    return Optional.of(list);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static Optional<JsonObject> requestLocationData(WeatherLocation location) {
        var link = new StringBuilder("https://api.open-meteo.com/v1/forecast?timeformat=unixtime")
                .append("&timezone=auto")
                .append("&latitude=" + location.getLatitude())
                .append("&longitude=" + location.getLongitude())
                .append("&windspeed_unit=ms")
                .append("&forecast_days=7");

        if (location.canUpdateCurrent()) {
            link.append("&current_weather=true");
        }

        if (location.canUpdateDaily()) {
            link.append(
                    "&daily=temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,precipitation_sum,")
                    .append("rain_sum,showers_sum,snowfall_sum,precipitation_hours,weathercode,sunrise,sunset,windspeed_10m_max,windgusts_10m_max,")
                    .append("winddirection_10m_dominant&hourly=temperature_2m,relativehumidity_2m,apparent_temperature,pressure_msl,surface_pressure,cloudcover,")
                    .append("windspeed_10m,winddirection_10m,windgusts_10m,precipitation,precipitation_probability,snowfall,rain,showers,weathercode,")
                    .append("snow_depth,freezinglevel_height,visibility,is_day");
        }

        try {
            var apiUrl = URI.create(link.toString());
            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder(apiUrl)
                    .GET()
                    .timeout(Duration.ofSeconds(3))
                    .build();

            var response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                WeatherRestApplication.logger.error(
                        "updateLocationData: statusCode = " + response.statusCode() + ", body = " + response.body());
                return Optional.empty();
            }

            return Optional.of(JsonParser.parseString(response.body()).getAsJsonObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static Optional<WeatherLocation> requestLocationByCoods(String latitude, String longitude) {
        try {
            var apiUrl = URI.create("http://api.geonames.org/findNearbyPlaceNameJSON?lat=" + latitude + "&lng="
                    + longitude + "&username=" + geoUsername);
            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder(apiUrl)
                    .GET()
                    .timeout(Duration.ofSeconds(3))
                    .build();

            var response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                WeatherRestApplication.logger.error("requestLocationByCoods: statusCode = " + response.statusCode()
                        + ", body = " + response.body());
                return Optional.empty();
            }

            var object = JsonParser.parseString(response.body()).getAsJsonObject();
            if (object.has("geonames") && !object.getAsJsonArray("geonames").isEmpty()) {
                return Optional.of(WeatherLocation.fromJSON(object));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static String normalize(String str) {
        if (Normalizer.isNormalized(str, Form.NFKD))
            return str;
        return Normalizer.normalize(str, Form.NFKD).replaceAll("\\p{M}", "");
    }
}
