# JFleet Samples

This project contains some examples of how to use JFleet. 

To simplify examples, all of it load information from files, but the source of the information persisted can be of any kind.  

These examples works with the dataset provided by Citi Bike NYC about each trip with their bikes.

The dataset can be downloaded from: `https://s3.amazonaws.com/tripdata/index.html`, and you can download as many files as data you need to load.

 ```bash
wget https://s3.amazonaws.com/tripdata/201709-citibike-tripdata.csv.zip -P /tmp
 ```

All examples read and parse all files located in `/tmp` directory and each CSV file is inserted into the database. You don't need to uncompress it, the code reads Zip files directly.


## Default Database engine and configuration

All examples use MySql, and can be easily changed to PostgreSql updating the `BulkInsert` implementation and the connection provider.

The file `./resources/*-test.properties` configures the database connection, user and password. The default configuration is:
 - **Host**: localhost
 - **Database**: testdb
 - **User**: test
 - **Password**: test
 
