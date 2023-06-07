package br.com.weather.weatherrest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;

import br.com.weather.weatherrest.data.weather.WeatherManager;

@RestController
@SpringBootApplication
public class WeatherRestApplication {
    public static final Logger logger = LoggerFactory.getLogger(WeatherRestApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WeatherRestApplication.class, args);
        WeatherManager.initialize();
    }

    /*
     * name parm or lat & long required, else return error json.
     */
    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(@RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "lat", required = false) String latitude,
            @RequestParam(name = "long", required = false) String longitude) {

        if (name != null) {
            var locations = WeatherManager.getByName(name);
            if (locations.isPresent()) {
                var json = new JsonArray();
                locations.get().forEach((w) -> {
                    json.add(w.toJsonObject(false));
                });

                return new ResponseEntity<String>(json.toString(), HttpStatus.FOUND);
            }
        } else if (latitude != null && longitude != null) {
            var location = WeatherManager.getByCoords(latitude, longitude);

            if (location.isPresent()) {
                return new ResponseEntity<String>(location.get().toJsonObject(false).toString(), HttpStatus.FOUND);
            }
        }

        return new ResponseEntity<String>("{}", HttpStatus.NOT_FOUND);
    }

    @GetMapping(path = "/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> get(@RequestParam(name = "id") String id) {
        if (id == null)
            return new ResponseEntity<String>("{}", HttpStatus.BAD_REQUEST);

        try {
            var pId = Integer.valueOf(id);
            var location = WeatherManager.getById(pId);

            if (location.isPresent()) {
                return new ResponseEntity<String>(location.get().toJsonObject(true).toString(), HttpStatus.FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<String>("{}", HttpStatus.NOT_FOUND);
    }
}
