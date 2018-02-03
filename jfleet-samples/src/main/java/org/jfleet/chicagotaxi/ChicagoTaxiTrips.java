package org.jfleet.chicagotaxi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import org.jfleet.BulkInsert;
import org.jfleet.JFleetException;
import org.jfleet.mysql.LoadDataBulkInsert;
import org.jfleet.util.MySqlTestConnectionProvider;

public class ChicagoTaxiTrips {

    public static void main(String[] args) throws JFleetException, IOException, SQLException {
        MySqlTestConnectionProvider connectionSuplier = new MySqlTestConnectionProvider();
        try (Connection connection = connectionSuplier.get()) {
            createTable(connection);
            try (InputStream is = new FileInputStream(new File("/tmp/Taxi_Trips.gz"));
                    GZIPInputStream gis = new GZIPInputStream(is);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(gis))) {

                TaxiTripParser parser = new TaxiTripParser();
                Stream<TaxiTrip> tripsStream = reader.lines().skip(1).map(parser::parse);

                BulkInsert<TaxiTrip> bulkInsert = new LoadDataBulkInsert<>(TaxiTrip.class);
                bulkInsert.insertAll(connection, tripsStream);
            }
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS taxi_trip");
            stmt.execute("CREATE TABLE taxi_trip (id VARCHAR(40) NOT NULL, taxi_id VARCHAR(128) NOT NULL, "
                    + "start_time DATETIME , end_time DATETIME, time_seconds INT, distance_miles FLOAT, "
                    + "pickup_tract BIGINT, pickup_community BIGINT, dropoff_tract BIGINT, dropoff_community BIGINT, "
                    + "fare FLOAT, tips FLOAT, tolls FLOAT, extras FLOAT, total FLOAT, "
                    + "payment_type VARCHAR (20), company VARCHAR (64), "
                    + "pickup_latitude DOUBLE, pickup_longitude DOUBLE, dropoff_latitude DOUBLE, dropoff_longitude DOUBLE, "
                    + "PRIMARY KEY (id))");
        }
    }

}
