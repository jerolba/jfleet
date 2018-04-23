
[![Maven Central](https://img.shields.io/maven-central/v/org.jfleet/jfleet.svg)](https://maven-badges.herokuapp.com/maven-central/org.jfleet/jfleet)
[![Build Status](https://circleci.com/gh/jerolba/jfleet.svg?style=shield)](https://circleci.com/gh/jerolba/jfleet) 
[![Codecov](https://codecov.io/gh/jerolba/jfleet/branch/master/graph/badge.svg)](https://codecov.io/gh/jerolba/jfleet/)
[ ![Download](https://api.bintray.com/packages/jerolba/JFleet/jfleet/images/download.svg) ](https://bintray.com/jerolba/JFleet/jfleet/_latestVersion)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

# JFleet

JFleet is a Java library which persist in database large collections of Java POJOs as fast as possible, using the best available technique in each database provider, achieving it with alternate persistence methods from each JDBC driver implementation.

Its goal is to store a large amount of information in a **single table** using available batch persistence techniques.

Despite using basic JPA annotations to map Java objects to tables and columns, **JFleet is not an ORM**.

## Table of Contents

- [Supported databases](#supported-databases)
- [Benchmark](#benchmark)
- [Usage](#usage)
- [Dependency](#dependency)
- [Advanced topics](#advanced-topics)
    - [IDs](#ids)
    - [Annotations](#annotations)
    - [BulkInsert configuration](#bulkinsert-configuration)
    - [Avoid javax.persistence annotations and dependency](#avoid-javaxpersistence-annotations-and-dependency)
    - [Supported database versions](#supported-database-versions)
- [Running the tests](#running-the-tests)
- [Contribute](#contribute)
- [License](#license)

## Supported databases

Each database provides some technique to insert a bulk of information bypassing standard JDBC commands, but accessible from Java:
 - **MySQL** : Using the [LOAD DATA](https://dev.mysql.com/doc/refman/5.7/en/load-data.html) statement. 
 - **PostgreSQL**: Using the [COPY](https://www.postgresql.org/docs/9.6/static/sql-copy.html) command.

In both cases, and in unsupported databases, you can use the default implementation based on the standard [JDBC executeBatch](https://docs.oracle.com/javase/8/docs/api/java/sql/Statement.html#executeBatch--) statement.

## Benchmark 

**JFleet performance is comparable to using the native database import tool, and is between 2.1X and 3.8X faster than using the JDBC driver directly.**

[![mysql vs postgres](https://docs.google.com/spreadsheets/d/e/2PACX-1vTx61C0YNYlczo0S-ZTN56FH2mxvHPHf4jamTnY4wdMwjjF3TvxcW3Ti7VR83dd1R5EznB7xVhD1HD6/pubchart?oid=485493047&format=image)](https://docs.google.com/spreadsheets/d/e/2PACX-1vTx61C0YNYlczo0S-ZTN56FH2mxvHPHf4jamTnY4wdMwjjF3TvxcW3Ti7VR83dd1R5EznB7xVhD1HD6/pubchart?oid=485493047&format=interactive)

You can find all the benchmarks numbers and results [here](https://github.com/jerolba/jfleet-benchmark#jfleet-benchmark)


## Usage

JFleet needs to know how to map your Java objects or entities to a table. The default mechanism used is standard [JPA annotations](https://docs.oracle.com/javaee/6/api/javax/persistence/package-summary.html) like [@Entity](https://docs.oracle.com/javaee/6/api/javax/persistence/Entity.html), [@Column](https://docs.oracle.com/javaee/6/api/javax/persistence/Column.html) or [@ManyToOne](https://docs.oracle.com/javaee/6/api/javax/persistence/ManyToOne.html). 

```java

import javax.persistence.*;

@Entity
@Table(name = "customer_contact")
public class Customer {

    private Long id;

    private String contactName;
    
    @Column(name="customer_name")
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "city_id")
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

Like JPA, JFleet follows the convention of using the field name if no `@Column` name is provided, or the class name if no `@Table` name is provided. 

Given a collection of objects Customer to persist in MySQL with the Load Data technique, you only need to provide a JDBC Connection:


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

## Dependency

JFleet is uploaded to Maven Central Repository and to use it, you need to add the following Maven dependency:

```xml
<dependency>
    <groupId>org.jfleet</groupId>
    <artifactId>jfleet</artifactId>
    <version>0.5.11</version>
</dependency>
```

or download the single [jar](http://central.maven.org/maven2/org/jfleet/jfleet/0.5.11/jfleet-0.5.11.jar) from Maven repository.

You can always find the latest published version in the [MvnRepository searcher](https://mvnrepository.com/artifact/org.jfleet/jfleet).

By default JFleet uses basic `javax.persistence` annotations. If you don't have any JPA implementation as a dependency in your project, you must add the Javax Persistence API dependency:

```xml
<dependency>
    <groupId>javax.persistence</groupId>
    <artifactId>persistence-api</artifactId>
    <version>1.0.2</version>
</dependency>
```
Apart from `persistence-api` and [SLF4J](https://www.slf4j.org/) for logging, JFleet does not have any dependency.
JFleet has not been tested against all JDBC driver versions, but it is expected that any modern version will work properly.

## Advanced topics

### IDs

JFleet does not manage the `@Id` of your entities as other ORMs do and you are responsible of it. You have some strategies to deal with it:

- Use the mechanism provided by each database to autogenerate primary keys: 
   - **MySQL**: [AUTO_INCREMENT](https://dev.mysql.com/doc/refman/5.7/en/example-auto-increment.html) attribute
   - **PostgreSQL**: [SERIAL](https://www.postgresql.org/docs/9.6/static/datatype-numeric.html) numeric type  

- Assign manually an Id to each object:
   - Use an [UUID generator](https://en.wikipedia.org/wiki/Universally_unique_identifier)
   - If your domain allows it, use a [natural key](https://en.wikipedia.org/wiki/Natural_key)
   - Use a composite key as primary key if the domain also allows it
   - If you control the concurrency access to the table, at the beginning of insertion process, get the max Id value in database and, from Java, increment and set a new Id value to each object

If you opt for an autogenerate strategy, breaking the [JPA specification](http://download.oracle.com/otn-pub/jcp/persistence-2.0-fr-eval-oth-JSpec/persistence-2_0-final-spec.pdf?AuthParam=1517785731_05caec473636207cb2f5000798645aba), you can avoid creating a field with the @Id column because it will be always null. But you can keep it if you want, or you are reusing a class from a existing JPA model. 

In an autogenerate strategy, ORMs like JPA populate the @Id field of your objects as they insert rows in the database. But due to the insertion technique used by JFleet, primary keys created by the database can not be retrieved for each inserted row, and is not possible to set it back to each object.

In PostgreSQL, if you have a field in an entity which the corresponding database column is declared as `SERIAL`, you must annotate the field with `@Id` and `@GeneratedValue(strategy = GenerationType.IDENTITY)`. Otherwise JFleet will try to insert a null value and the database will raise an error. Internally `SERIAL` is an alias to NOT NULL with a DEFAULT value implemented as a sequence, and does not accept to insert a null value, even when afterwards it will assign one.

JFleet needs to know if a field is `SERIAL`, and the convention used is annotating it with `IDENTITY` strategy.    

### Annotations

By default JFleet reuses existing JPA annotations to map Java object to tables. 

JPA allows to define how to map your entities in [two ways](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#access):
- entity attributes (instance fields) 
- or the accessors (instance properties)

In JPA by default, the placement of the `@Id` annotation gives the default access strategy. 

**JFleet only supports access by entity attributes**, and it expects annotations on fields.

The supported annotations are:
- **[@Entity](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/Entity.java)**: Specifies that the class is an entity.
- **[@Table](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/Table.java)**: Specifies the table name. If no value is specified, the class name in lower case is used.
- **[@Column](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/Column.java)**: Is used to specify a mapped column for a persistent field. If no value is specified, the field name is used.
- **[@Id](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/Id.java)**: Specifies the primary key field of an entity. It is only used to fetch foreign key values in ManyToOne and OneToOne relationships.
- **[@MappedSuperclass](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/MappedSuperclass.java)**: Designates a class whose mapping information is applied to the entities that inherit from it. A mapped superclass has no separate table defined for it.
- **[@Transient](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/Transient.java)**: This annotation specifies that field is not persistent.
- **[@Embedded](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/Embedded.java)**: Defines a persistent field of an entity whose value is an instance of an embeddable class.
- **[@EmbeddedId](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/EmbeddedId.java)**: Is applied to a persistent field of an entity class or mapped superclass to denote a composite primary key that is an embeddable class.
- **[@AttributeOverrides](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/AttributeOverrides.java)**: Is used to override mappings of multiple fields.
- **[@AttributeOverride](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/AttributeOverride.java)**: Is used to override the mapping of a basic field or Id field. May be applied to an entity that extends a mapped superclass or to an embedded field to override a basic mapping defined by the mapped superclass or embeddable class.
- **[@ManyToOne](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/ManyToOne.java)**: Defines a single-valued association to another entity class that has many-to-one multiplicity. The `targetEntity` value is ignored if provided. Uses the field class annotated.
- **[@OneToOne](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/OneToOne.java)**: Defines a single-valued association to another entity that has one-to-one multiplicity. The `targetEntity` value is ignored if provided. Uses the field class annotated.
- **[@JoinColumn](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/JoinColumn.java)**: The name of the foreign key column. If no name is provided, the column name is the concatenation of the name of the referencing relationship field of the referencing entity, the char "\_", and the name of the referenced primary key column.
- **[@Enumerated](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/Enumerated.java)**: Specifies that a persistent field should be persisted as a enumerated type. The used value is specified by the EnumType value. If no annotation is used or no EnumType is used, the default enum type is ORDINAL.
- **[@Temporal](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/Temporal.java)**: This annotation must be specified for persistent fields of type `java.util.Date`. DATE, TIME and TIMESTAMP values are accepted.

Some common annotations which are not supported are: [@GeneratedValue](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/GeneratedValue.java), [@OneToMany](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/OneToMany.java), [@ManyToMany](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/ManyToMany.java), [@JoinColumns]() and [@JoinTable](https://github.com/eclipse/javax.persistence/blob/master/src/javax/persistence/JoinTable.java).

These annotations, and many configuration properties in _supported_ annotations, are ignored mainly because has no effect o meaning in the purpose and limitations of JFleet. If you find a relevant annotation or property which could be included create an issue.

### BulkInsert configuration

[Load Data](https://dev.mysql.com/doc/refman/5.7/en/load-data.html) and [Copy](https://www.postgresql.org/docs/9.6/static/sql-copy.html) methods are based on serializing a batch of rows to a _CSVlike_ StringBuilder, and when serialized information reach a limit of characters, flush it to the database. Depending on the available memory and the size of each row you can tune this limit.

In the JDBC batch insert method you can configure the numbers of rows of each batch operation.

You can also configure how transactions are managed persisting your Stream or Collection:
 - Let JFleet commit to database each time a batch of rows is flushed. 
 - Join to the existing transaction in the provided connection, and deciding on your own code when to commit or rollback it.

If you override the default values (10MB and autocommit), you must use a different BulkInsert constructor.

For `LoadDataBulkInsert` version, with 5MB batch size and no autocommit:

```java
import org.jfleet.mysql.LoadDataBulkInsert.Configuration;

Configuration<Employee> config = new Configuration<>(Employee.class)
        .batchSize(5 * 1024 * 1024)
        .autocommit(false);
BulkInsert<Employee> bulkInsert = new LoadDataBulkInsert<>(config);
bulkIsert.insertAll(connection, stream);
```

For `PgCopyBulkInsert` version, with 30MB batch size and autocommit after each batch:

```java
import org.jfleet.postgres.PgCopyBulkInsert.Configuration;

Configuration<Employee> config = new Configuration<>(Employee.class)
        .batchSize(30 * 1024 * 1024)
        .autocommit(true);
BulkInsert<Employee> bulkInsert = new PgCopyBulkInsert<>(config);
bulkInsert.insertAll(connection, stream);
```

For `JdbcBulkInsert` version, with 1000 rows batch size and autocommit after each batch:

```java
import org.jfleet.jdbc.JdbcBulkInsert.Configuration;

Configuration<Employee> config = new Configuration<>(Employee.class)
        .batchSize(1000)
        .autocommit(true);
BulkInsert<Employee> bulkInsert = new JdbcBulkInsert<>(config);
bulkInsert.insertAll(connection, stream);
```
### Avoid javax.persistence annotations and dependency

If you have any problem using JPA annotations in your domain objects or directly you don't want to add `javax.persistence` dependency to your project, you can configure it manually mapping each column to a field path.

Given the same domain objects:

```java

public class Customer {

    private Long id;
    private String contactName;
    private String name;
    private City city;
    
    //Getters and setters
}

public class City {
    private Integer id;
    private String name;
    
    //Getters and setters
}

```

You configure JFleet with the mapping info:

```java

EntityInfo customerMap = new EntityInfoBuilder(Customer.class, "customer_contact")
	.addField("id", "id")
	.addField("contactName", "contactname")
	.addField("name", "customer_name")
	.addField("city.id", "city_id")
	.build();
    
Configuration<Customer> config = new Configuration<>(customerMap);
BulkInsert<Customer> bulkInsert = new LoadDataBulkInsert<>(config);

```

#### MySQL LOAD DATA error handling

In MySQL [LOAD DATA](https://dev.mysql.com/doc/refman/5.7/en/load-data.html) command, data-interpretation, duplicate-key errors or foreign key errors become warnings and the operation continues until finish the whole data. Rows with errors are discarded and no SQLException is thrown by database or JDBC driver.

If your business logic is sensitive to these errors you can configure JFleet to detect when some row 
is missing and throw an exception:

```java
import org.jfleet.mysql.LoadDataBulkInsert.Configuration;

Configuration<Employee> config = new Configuration<>(Employee.class)
    .errorOnMissingRow(true);
BulkInsert<Employee> bulkInsert = new LoadDataBulkInsert<>(config);
try {
    bulkInsert.insertAll(connection, employeesWithForeignKeyError);
} catch (JFleetException e) {
    logger.info("Expected error on missed FK");
}
```

### Supported database versions

JFleet is configured to execute continuous integration tests against [CircleCI](https://circleci.com/gh/jerolba/jfleet) service, using the latest stable release of **MySQL 5.7** and the latest stable release of **PostgreSQL 9.6**.

PostgreSQL 10.1 release has been manually tested without any problem.

MySQL 8 is not yet supported by JFleet because requires the latest JDBC driver which it is completely rewritten, and all internal classes used by JFleet change from 5.x versions.

Any database engine with a standard JDBC driver should be used with the `JdbcBulkInsert` implementation.

## Running the tests

Tests need a MySQL and a PostgreSQL instances running in localhost. A database called `testdb` must exist and an user `test` with password `test` must have `CREATE TABLE` and `DROP TABLE` permissions.

You can modify this settings changing locally [mysql-test.properties](https://github.com/jerolba/jfleet/blob/master/jfleet-core/src/test/resources/mysql-test.properties) and [postgres-test.properties](https://github.com/jerolba/jfleet/blob/master/jfleet-core/src/test/resources/postgres-test.properties) files.

To execute all test you must execute the command:

```bash
$ gradle test
```

You can also fork the project and test it in your [CircleCI](https://circleci.com/signup/) free account.

## Contribute
Feel free to dive in! [Open an issue](https://github.com/jerolba/jfleet/issues/new) or submit PRs.

Any contributor and maintainer of this project follows the [Contributor Covenant Code of Conduct](https://github.com/jerolba/jfleet/blob/master/CODE_OF_CONDUCT.md).

## License
[Apache 2](https://github.com/jerolba/jfleet/blob/master/LICENSE.txt) © Jerónimo López
