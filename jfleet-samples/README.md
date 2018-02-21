JFleet Samples
======

This project contains some examples of how to use JFleet. 

The most completed one is the [Citi Bike NYC](https://github.com/jerolba/jfleet/tree/master/jfleet-samples/src/main/java/org/jfleet/citibikenyc) with different techniques (MySQL Load Data, Batch JDBC or JPA with Hibernate), or persisted entity as a _flat_ POJO vs with embedded fields.   

All examples load information from files, but JFleet is not a file loader.

Citi Bike NYC
------

These examples works with the dataset provided by Citi Bike NYC about each trip with their bikes.

The dataset can be downloaded from: `https://s3.amazonaws.com/tripdata/index.html`, and you can download as many files as data you need to load.

 ```bash
wget https://s3.amazonaws.com/tripdata/201709-citibike-tripdata.csv.zip -P /tmp
 ```

All examples read and parse all files located in `/tmp` directory and each CSV file is inserted into the database. You don't need to uncompress it, the code reads Zip files directly.


Chicago Taxi Trips
------

This example works with the dataset provided by Chicago Data Portal about Taxi trips reported to the City of Chicago.

The dataset information can be found in: `https://data.cityofchicago.org/Transportation/Taxi-Trips/wrvz-psew`, and there is only one CSV file to download: 

 ```bash
curl https://data.cityofchicago.org/api/views/wrvz-psew/rows.csv?accessType=DOWNLOAD | gzip > /tmp/Taxi_Trips.gz
 ```
(downloaded info is around 46GB and final Taxi_Trips.gz file size is around 16GB )

Code example reads and parses the file located in `/tmp` directory.


EU Emissions Trading System
------

Dataset about the EU emission trading system (ETS). More information can be found in: `https://datahub.io/core/eu-emissions-trading-system` 

There is only one small CSV file to download (5MB): 

 ```bash
curl https://datahub.io/core/eu-emissions-trading-system/r/eu-ets.csv > /tmp/eu-ets.csv
 ```

Code example reads and parses the file located in `/tmp` directory.


Default Database engine and configuration
------

All examples use MySql, and can be easily changed to PostgreSql updating the `BulkInsert` implementation and the connection provider.

The file `./resources/*-test.properties` configures the database connection, user and password. The default configuration is:
 - **Host**: localhost
 - **Database**: testdb
 - **User**: test
 - **Password**: test
 
