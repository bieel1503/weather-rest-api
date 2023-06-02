package br.com.weather.weatherrest.database;

import br.com.weather.weatherrest.data.weather.WeatherManager;

public class StorageTask extends Thread {
    public static final int TASK_SECONDS = 60;

    public void run() {
        while (true) {
            try {
                sleep(TASK_SECONDS*1000);
                WeatherManager.storeLocations();
                WeatherManager.storePatterns();
                WeatherManager.clearLocations();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
