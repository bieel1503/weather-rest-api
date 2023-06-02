package br.com.weather.weatherrest.database;

import org.sqlite.SQLiteConnection;
import org.sqlite.SQLiteDataSource;

public class SQLiteStorage {
    private final SQLiteDataSource dataSource;
    private SQLiteConnection connection;

    public SQLiteStorage(String dbpath){
        this.dataSource = new SQLiteDataSource();
        this.connection = null;
        this.dataSource.setUrl("jdbc:sqlite:" + dbpath);
    }

    public SQLiteConnection getConnection(){
        if(this.connection != null) return this.connection;
        try {
            this.connection = (SQLiteConnection)this.dataSource.getConnection();
            return this.connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.connection = null;
        }
    }
}
