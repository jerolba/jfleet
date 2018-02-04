
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.jfleet/jfleet/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/org.jfleet/jfleet)
[![Build Status](https://circleci.com/gh/jerolba/jfleet.svg?style=shield)](https://circleci.com/gh/jerolba/jfleet) 
[![Codecov](https://codecov.io/gh/jerolba/jfleet/branch/master/graph/badge.svg)](https://codecov.io/gh/jerolba/jfleet/)
[ ![Download](https://api.bintray.com/packages/jerolba/JFleet/jfleet/images/download.svg) ](https://bintray.com/jerolba/JFleet/jfleet/_latestVersion)

# JFleet

JFleet is a Java library that try to persist your information to a database as fast as possible using the best available technique in each database provider.

It is oriented to persist a large amount of information in batches in single table.  

Despite using JPA annotations to map Java objects to tables and columns, JFleet is not an ORM.

## Supported databases

Each database provides some technique to insert a bulk of information bypassing standard JDBC commands, but accessible from Java:
 - **MySql** : Using the [LOAD DATA](https://dev.mysql.com/doc/refman/5.7/en/load-data.html) statement. 
 - **PostgreSQL**: Using the [COPY](https://www.postgresql.org/docs/9.6/static/sql-copy.html) command.

In both cases, and in unsupported databases, you can use the default implementation based on the standard [JDBC executeBatch](https://docs.oracle.com/javase/8/docs/api/java/sql/Statement.html#executeBatch--) statement.

## Usage

JFleet needs to know how to map your Java objects or entities to a table. The mechanism used is standard [JPA annotations](https://docs.oracle.com/javaee/6/api/javax/persistence/package-summary.html) like [@Entity](https://docs.oracle.com/javaee/6/api/javax/persistence/Entity.html), [@Column](https://docs.oracle.com/javaee/6/api/javax/persistence/Column.html) or [@ManyToOne](https://docs.oracle.com/javaee/6/api/javax/persistence/ManyToOne.html). 

```java

import javax.persistence.*;

@Entity
@Table(name = "CustomerContact")
public class Customer {

    private Long id;

    private String contactName;
    
    @Column(name="CustomerName")
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "CityId")
    private City city;
    
    //Getters and setters
}

@Entity
@Table(name = "city")
public class City {
    
    @Id
    private Integer id;
    
    private String name;
    
    //Getters and setters
}

```

As JPA, JFleet follows the convention of using the field name if no @Column name is provided, or the class name if no @Table name is provided. 

Given a collection of objects Customer to persist in MySql with the Load Data technique, you only need to provide a JDBC Connection:


```java
    Collection<Customer> customers = buildLargeAmountOfCustomers();
    BulkInsert<Customer> bulkInsert = new LoadDataBulkInsert<>(Customer.class);
    bulkInsert.insertAll(connection, customers);
```

If you are using PostgreSQL the `BulkInsert` implementation is `PgCopyBulkInsert`. 
JFleet prefers Streams to Collections because it does not force you to instantiate all objects in memory, and allows you to create them lazily in some stream process: 

```java
    Stream<Customer> customers = createLongStreamOfCustomers();
    BulkInsert<Customer> bulkInsert = new PgCopyBulkInsert<>(Customer.class);
    bulkInsert.insertAll(connection, customers);
```
### IDs

JFleet does not manage the @Id of your entities as other ORMs do. You are responsible of it, and you have some strategies to deal with it:

- Use the mechanism provided by each database to autogenerate primary keys: 
   - **MySQL**: [AUTO_INCREMENT](https://dev.mysql.com/doc/refman/5.7/en/example-auto-increment.html) attribute
   - **PostgreSQL**: [serial](https://www.postgresql.org/docs/9.6/static/datatype-numeric.html) numeric type  

- Assign manually an Id to each object:
   - Use an [UUID generator](https://en.wikipedia.org/wiki/Universally_unique_identifier)
   - If your domain allows it, use a [natural key](https://en.wikipedia.org/wiki/Natural_key)
   - Use a composite key as primary key if the domain also allows it
   - If you control the concurrency access to the table, at the beginning of insertion process, get the max Id value and, from Java, increment and set a new Id value to each object

If you opt for an autogenerate strategy, you can avoid creating a field with the @Id column because it will be always null. But you can keep it if you want, or you are reusing a class from a existing JPA model. 

In an autogenerate strategy, ORMs like JPA populate the @Id field of your objects as they insert rows in the database, but due to the insertion technique used by JFleet, primary keys created by the database can not be retrieved for each inserted row, and is not possible to set it back to each object.


## Dependency

JFleet is uploaded to Maven Central Repository and to use it, you need to add the following Maven dependency:

```xml
<dependency>
    <groupId>org.jfleet</groupId>
    <artifactId>jfleet</artifactId>
    <version>0.5.6</version>
</dependency>
```

or download the single [jar](http://central.maven.org/maven2/org/jfleet/jfleet/0.5.6/jfleet-0.5.6.jar) from Maven repository.

You can always find the latest published version in the [MvnRepository searcher](https://mvnrepository.com/artifact/org.jfleet/jfleet).

As JFleed uses basic `javax.persistence` annotations, if you don't have any JPA implementation as a dependency in your project, you must need to add the Javax Persistence API dependency:

```xml
<dependency>
    <groupId>javax.persistence</groupId>
    <artifactId>persistence-api</artifactId>
    <version>1.0.2</version>
</dependency>
```
  
