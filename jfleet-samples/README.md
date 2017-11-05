JFleet Samples
======

This project contains some examples of how to use JFleet. 

Citi Bike NYC
------

This examples works with the dataset provided by Citi Bike NYC about each trip with their bikes.

The dataset can be downloaded from: `https://s3.amazonaws.com/tripdata/index.html`, and you can download as many files as data you need to load.

 ```bash
wget https://s3.amazonaws.com/tripdata/201709-citibike-tripdata.csv.zip /tmp
 ```

All examples read and parse all files located in /tmp directory and each CSV file is inserted into the database. You don't need to uncompress it, the code reads Zip files directly.


