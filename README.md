# Weedow Searchy

<p align="center">
    <a href="./docs/images/logos/logo.png">
        <img src="./docs/images/logos/logo.png" width="200" alt="Searchy Logo" />
    </a>
</p>

## About
Searchy is a Spring-based library that allows to automatically expose endpoints in order to search for data related to Entities.

Searchy provides an advanced search engine that does not require the creation of Repositories with custom methods needed to search on different fields of Entities.

We can search on any field, combine multiple criteria to refine the search, and even search on nested fields. 

![Query GIF](./docs/images/query.gif)

## Why use Searchy?
[Spring Data Rest](https://spring.io/projects/spring-data-rest) builds on top of the Spring Data repositories and automatically exports those as REST resources.

* Each time we need to search with different criteria, we will have to add new methods (findByFirstName, findByFirstAndLastName, ...).
* If we need to make more complex queries or handle specific fetch joins, we use the `@Query` annotation which takes as attribute a String representing the query to be executed.
This String is written in the language supported by the data access layer (JPQL, SQL, Mongo JSON ...).

Here is an JPA example:
```java
@RepositoryRestResource
public interface PersonRepository extends Repository<Person, Long> {
    List<Person> findAll();
    List<Person> findByLastName(@Param("name") String name);
    
    @Query("SELECT p FROM Person " +
           "LEFT JOIN FETCH p.addressEntities a " +
           "WHERE p.lastName='Doe' AND a.city='Paris'")
    List<Person> findPersonsWithAddresses();
}
```

We realize that we cannot be exhaustive in order to search for Person entities whatever the search criteria: a single field, nested fields, multiple fields, AND/OR conjunctions...\
We also realize that each time we use `@Query`, we add a dependency to the data access layer since we write the query string in the language of the database we are querying. This can sometimes make it tedious to migrate to another type of database.\
All this requires adding more code, releasing new versions ...

Searchy allows to easily expose an endpoint for an Entity and thus be able to search on any fields of this entity, combine several criteria and even search on fields belonging to sub-entities.

Let's say you manage Persons associated with Addresses, Vehicles and a Job.\
You want to allow customers to search for them, regardless of the search criteria:
* Search for Persons whose first name is "John" or "Jane"
* Search for Persons whose company where they work is "Acme", and own a car or a motorbike 
* Search for Persons who live in London

Searchy allows you to perform all these searches with a minimum configuration, without the need of a custom `Repository`.\
If you want to do other different searches, you do not need to add code to do that.
The library provides a query language that allows to create queries on any field of the entity and sub-entities, and agnostic queries regarding the database used. 

## Build
![GitHub repo size](https://img.shields.io/github/repo-size/Kobee1203/weedow-searchy)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/Kobee1203/weedow-searchy)

[![Build](https://img.shields.io/github/workflow/status/Kobee1203/weedow-searchy/Build%20and%20Analyze)](https://github.com/Kobee1203/weedow-searchy/actions?query=workflow%3A%22Build+and+Analyze%22)
[![Libraries.io dependency status for GitHub repo](https://img.shields.io/librariesio/github/Kobee1203/weedow-searchy)]()

[![Code Coverage](https://img.shields.io/sonar/coverage/weedow-searchy?server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=weedow-searchy)
[![Sonar Quality Gate](https://img.shields.io/sonar/quality_gate/weedow-searchy?server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=weedow-searchy)
[![Sonar Tech Debt](https://img.shields.io/sonar/tech_debt/weedow-searchy?server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=weedow-searchy)
[![Sonar Violations](https://img.shields.io/sonar/violations/weedow-searchy?server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=weedow-searchy)

### Built with:
* [Kotlin](https://kotlinlang.org/)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Querydsl](http://www.querydsl.com/)  
* [ANTLR](https://www.antlr.org/)
* [Maven](https://maven.apache.org/)

## Getting Started

### Prerequisites
* JDK 11 or more.
* Spring Boot

### Installation
[![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/Kobee1203/weedow-searchy?include_prereleases)](https://github.com/Kobee1203/weedow-searchy/releases)
[![Downloads](https://img.shields.io/github/downloads/Kobee1203/weedow-searchy/total)](https://github.com/Kobee1203/weedow-searchy/releases)
[![Maven Central](https://img.shields.io/maven-central/v/com.weedow/weedow-searchy-core)](https://search.maven.org/search?q=g:com.weedow%20AND%20a:weedow-searchy-*)

* You can download the [latest release](https://github.com/Kobee1203/weedow-searchy/releases).
* If you have a [Maven](https://maven.apache.org/) project, you can add the following dependency in your `pom.xml` file:
  ```xml
  <dependency>
      <groupId>com.weedow</groupId>
      <artifactId>weedow-searchy-jpa</artifactId>
      <version>0.0.1</version>
  </dependency>
  ```
* If you have a [Gradle](https://gradle.org/) project, you can add the following dependency in your `build.gradle` file:
  ```groovy
  implementation "com.weedow:weedow-searchy-jpa:0.0.1"
  ```

### Getting Started in 5 minutes

* Go to https://start.spring.io/
* Generate a new Java project `sample-app-java` with the following dependencies:
    * Spring Web
    * Spring Data JPA
    * H2 Database
    ![start.spring.io](./docs/images/start.spring.io.png)
* Update the generated project by adding the dependency of Searchy:
    * For [Maven](https://maven.apache.org/) project, add the dependency in the `pom.xml` file: 
    ```xml
    <dependency>
      <groupId>com.weedow</groupId>
      <artifactId>weedow-searchy-jpa</artifactId>
      <version>0.0.1</version>
    </dependency>
    ```
    * For [Gradle](https://gradle.org/) project, add the dependency in the `build.gradle` file:
    ```groovy
    implementation "com.weedow:weedow-searchy-jpa:0.0.1"
    ```
* Create a new file `Person.java` to add a new JPA Entity `Person` with the following content:
    ```java
    import javax.persistence.*;
    import java.time.LocalDateTime;
    import java.util.Set;
    
    @Entity
    public class Person {
    
        @Id
        @GeneratedValue
        private Long id;
    
        @Column(nullable = false)
        private String firstName;
    
        @Column(nullable = false)
        private String lastName;
    
        @Column(unique = true, length = 100)
        private String email;
    
        @Column
        private LocalDateTime birthday;
    
        @Column
        private Double height;
    
        @Column
        private Double weight;
    
        @ElementCollection(fetch = FetchType.EAGER)
        private Set<String> nickNames;
    
        @ElementCollection
        @CollectionTable(name = "person_phone_numbers", joinColumns = {@JoinColumn(name = "person_id")})
        @Column(name = "phone_number")
        private Set<String> phoneNumbers;
    
        public Long getId() {
            return id;
        }
    
        public Person setId(Long id) {
            this.id = id;
            return this;
        }
    
        public String getFirstName() {
            return firstName;
        }
    
        public String getLastName() {
            return lastName;
        }
    
        public String getEmail() {
            return email;
        }
    
        public LocalDateTime getBirthday() {
            return birthday;
        }
    
        public Double getHeight() {
            return height;
        }
    
        public Double getWeight() {
            return weight;
        }
    
        public Set<String> getNickNames() {
            return nickNames;
        }
    
        public Person setNickNames(Set<String> nickNames) {
            this.nickNames = nickNames;
            return this;
        }
    
        public Set<String> getPhoneNumbers() {
            return phoneNumbers;
        }
    
        public Person setPhoneNumbers(Set<String> phoneNumbers) {
            this.phoneNumbers = phoneNumbers;
            return this;
        }
    
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            if (!super.equals(object)) {
                return false;
            }
    
            Person person = (Person) object;
    
            if (!firstName.equals(person.firstName)) {
                return false;
            }
            if (!lastName.equals(person.lastName)) {
                return false;
            }
    
            return true;
        }
    
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + firstName.hashCode();
            result = 31 * result + lastName.hashCode();
            return result;
        }
    }
    ```
* Add the following Configuration class to add a new `SearchyDescriptor`:
    ```java
    import com.example.sampleappjava.entity.Person;
    import com.weedow.searchy.config.SearchyConfigurer;
    import com.weedow.searchy.descriptor.SearchyDescriptor;
    import com.weedow.searchy.descriptor.SearchyDescriptorBuilder;
    import com.weedow.searchy.descriptor.SearchyDescriptorRegistry;
    import org.springframework.context.annotation.Configuration;
    
    @Configuration
    public class SampleAppJavaConfiguration implements SearchyConfigurer {
    
        @Override
        public void addSearchyDescriptors(SearchyDescriptorRegistry registry) {
            registry.addSearchyDescriptor(personSearchyDescriptor());
        }
    
        private SearchyDescriptor<Person> personSearchyDescriptor() {
            return new SearchyDescriptorBuilder<Person>(Person.class).build();
        }
    }
    ```
* Create a new file `data.sql` in `/src/main/resources`, and add the following content:
  ```sql
  INSERT INTO PERSON (id, first_name, last_name, email, birthday, height, weight)
      VALUES (1, 'John', 'Doe', 'john.doe@acme.com', '1981-03-12 10:36:00', 174.0, 70.5);
  INSERT INTO PERSON (id, first_name, last_name, email, birthday, height, weight)
      VALUES (2, 'Jane', 'Doe', 'jane.doe@acme.com', '1981-11-26 12:30:00', 165.0, 68.0);
  
  INSERT INTO PERSON_PHONE_NUMBERS (person_id, phone_number) VALUES (1, '+33612345678');
  INSERT INTO PERSON_PHONE_NUMBERS (person_id, phone_number) VALUES (2, '+33687654321');
  
  INSERT INTO PERSON_NICK_NAMES (person_id, nick_names) VALUES (1, 'Johnny');
  INSERT INTO PERSON_NICK_NAMES (person_id, nick_names) VALUES (1, 'Joe');
  ```
* Run the application:
    * For Maven Project: `./mvnw spring-boot:run`
    * For Gradle Project: `./gradlew bootRun`
    * From your IDE: Run the Main Class `com.example.sampleappjava.SampleAppJavaApplication`
* Open your browser and go to the URL `http://localhost:8080/search/person`
![find-all-persons](./docs/images/find-all-persons.png)
* You can filter the results by adding query parameters representing the Entity fields:\
  Here is an example where the results are filtered by the first name:
![find-person-by-firstname](./docs/images/find-person-by-firstname.png)

## Usage

The examples in this section are based on the following entity model:

The `Person.java` Entity has relationships with the `Address.java` Entity, the `Job.java` Entity and the `Vehicle.java` Entity. Here are the entities:
```java
@Entity
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private LocalDateTime birthday;

    @Column
    private Double height;

    @Column
    private Double weight;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> nickNames;

    @ElementCollection
    @CollectionTable(name = "person_phone_numbers", joinColumns = {@JoinColumn(name = "person_id")})
    @Column(name = "phone_number")
    private Set<String> phoneNumbers;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "person_address", joinColumns = {JoinColumn(name = "personId")}, inverseJoinColumns = {JoinColumn(name = "addressId")})
    @JsonIgnoreProperties("persons")
    private Set<Address> addressEntities;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private Job jobEntity;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vehicle> vehicles;

    @ElementCollection
    @CollectionTable(
            name = "characteristic_mapping",
            joinColumns = {@JoinColumn(name = "person_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "characteristic_name")
    @Column(name = "value")
    private Map<String, String> characteristics;

    @ElementCollection
    @CollectionTable(
            name = "person_tasks",
            joinColumns = {@JoinColumn(name = "person_id", referencedColumnName = "id")})
    @MapKeyJoinColumn(name = "task_id")
    @Column(name = "task_date")
    private Map<Task, LocalDateTime> tasks;


  // Getters/Setters
}

@Entity
public class Address {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @ManyToOne(optional = false)
    private String zipCode;

    @Enumerated(EnumType.STRING)
    private CountryCode country;

    @ManyToMany(mappedBy = "addressEntities")
    @JsonIgnoreProperties("addressEntities")
    private Set<Person> persons;

    // Getters/Setters
}


@Entity
public class Job {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String company;
    
    @Column(nullable = false)
    private Integer salary;
    
    @Column(nullable = false)
    private OffsetDateTime hireDate;
    
    @OneToOne(optional = false)
    private Person person;

    // Getters/Setters

}

@Entity
public class Vehicle {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @ManyToOne(optional = false)
    private String person;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "feature_mapping",
            joinColumns = {@JoinColumn(name = "vehicle_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "feature_id", referencedColumnName = "id")})
    @MapKey(name = "name") // Feature name
    private Map<String, Feature> features;

    // Getters/Setters
}

@Entity
public class Task {

    @Id
    @GeneratedValue
    private Long id;
      
    @Column(nullable = false)
    private String name;
  
    @Column
    private String description;
  
    // Getters/Setters
  
    // Override toString() method to get a JSON representation used by the HTTP response
    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}

@Entity
public class Feature {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @ElementCollection
    @CollectionTable(
            name = "metadata_mapping",
            joinColumns = {@JoinColumn(referencedColumnName = "id", name = "feature_id")}
    )
    @MapKeyColumn(name = "metadata_name")
    @Column(name = "value")
    private Map<String, String> metadata;

    // Getters/Setters
}

public enum VehicleType {
    CAR, MOTORBIKE, SCOOTER, VAN, TRUCK
}
```

### Query
There are two methods to search for entities: [Standard Query](#standard-query) and [Advanced Query](#advanced-query)

All search methods use the same operation described below.

To search for all the Database rows of an entity type, you must concatenate the [Base Path](#changing-the-base-path) (default is `/search`) and the [Search Descriptor ID](#changing-the-base-path) (default is the Entity name in lower case).\
_Example: Search all `Person` Entities_\
`/search/person`

To search on nested fields, you must concatenate the deep fields separated by the dot '`.`'.\
_Example: The `Person` Entity contains a property of the `Address` Entity that is named `addressEntities`, and we search for Persons who live in 'Paris'_:\
/search/person?`addressEntities.city=Paris`

To search on fields with a `Map` type, you have to use the special keys `key` or `value` to query the keys or values respectively.\
_Example 1: The `Person` Entity contains a property of type `Map` that is named `characteristics`, and we search for Persons who have 'blue eyes':_\
/search/person?`characteristics.key=eyes&characteristics.value=blue`

_Example 2: The `Person` Entity contains a property of type `Map` that is named `tasks`, and we search for Persons who have a task whose name is 'shopping':_\
/search/person?`tasks.key.name=shopping`

_Example 3: The `Vehicle` Entity contains a property of type `Map` that is named `features`, and we search for Persons who have vehicles with the 'GPS' feature:_\
/search/person?`vehicles.features.value.name=gps`

### Standard Query
You can search for entities by adding query parameters representing entity fields to the search URL.

To search on nested fields, you must concatenate the deep fields separated by the dot '`.`'.\
_Example: The `Person` Entity contains a property of the `Address` Entity that is named `addressEntities`, and we search for Persons who live in 'Paris'_:\
/search?`addressEntities.city=Paris`

To search on fields with a `Map` type, you have to use the special keys `key` or `value` to query the keys or values respectively.
_Example: The `Person` Entity contains a property of type `Map` that is named `characteristics`, and we search for Persons who have 'blue eyes':_\
/search?`characteristics.key=eyes&characteristics.value=blue`

This mode is limited to the use of the `AND` operator between each field criteria.\
Each field criteria is limited to the use of the `EQUALS` operator and the `IN` operator.

| What you want to query                                                                                                   | Example                                                                                |
| ------------------------------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------- |
| Persons with the firstName is _'John'_                                                                                   | `/search?firstName=John`                                                               |
| Persons with the firstName is _'John'_ or _'Jane'_<br/>_This will be result from a query with an `IN` operator_          | `/search?firstName=John&firstName=Jane`                                                |
| Persons with the firstName is _'John'_ and lastName is _'Doe'_                                                           | `/search?firstName=John&lastName=Doe`                                                  |
| Persons whose the vehicle brand is _'Renault'_                                                                           | `/search/person?vehicles.brand=Renault`                                                |
| Persons whose the vehicle brand is _'Renault'_ and the job company is _'Acme'_                                           | `/search/person?vehicles.brand=Renault&jobEntity.company=Acme`                         |
| Persons with the firstName is _'John'_ or _'Jane'_, and the vehicle brand is _'Renault'_ and the job company is _'Acme'_ | `/search?firstName=John&firstName=Jane&vehicles.brand=Renault&jobEntity.company=Acme`  |
| Persons who have a vehicle with _'GPS'_<br/>_This will be result from a query on the `feature` field of type `Map`_      | `/search?vehicles.features.value.name=gps`                                             |
| Persons with the birthday is _'null'_                                                                                    | `/search?birthday=null`                                                                |
| Persons who don't have jobs                                                                                              | `/search?jobEntity=null`                                                               |
| Persons who have a vehicle without defined feature in database                                                           | `/search?vehicles.features=null`                                                       |
| Persons who were born at current date                                                                                    | `/search?birthday=CURRENT_DATE`                                                        |
| Persons who were born at current time                                                                                    | `/search?birthday=CURRENT_TIME`                                                        |
| Persons who were born at current datetime                                                                                | `/search?birthday=CURRENT_DATE_TIME`                                                   |

### Advanced Query
You can search for entities by using the query string `query`.

`query` supports a powerful query language to perform advanced searches for the Entities.

You can combine logical operators and operators to create complex queries.

The value types are the following:
* `String`: must be surrounded by single quotes or double quotes.\
  _Example: `firstName='John'`, `firstName="John"`_
* `Number`: could be an integer or a decimal number.\
  _Example: `height=174`, `height=175.2`_
* `Boolean`: could be true or false and is case-insensitive
  _Example: `active=true`, `active=FALSE`_
* `Date`: must be surrounded by single quotes or double quotes, or use special keywords `CURRENT_DATE`, `CURRENT_TIME`, `CURRENT_DATE_TIME`.\
  _Example: `birthday='1981-03-12T10:36:00'`, `job.hireDate='2019-09-01T09:00:00Z'`, `birthday=CURRENT_DATE_TIME`_

> Note: The examples use the unencoded 'query' parameter, where **firstName = 'John'** is encoded as **firstName+%3d+%27John%27**.
>
> Remember to manage this encoding when making requests from your code.

1. <a name="equals-operator"></a> Equals operator `=`

| What you want to query                                              | Example                                                                  |
| ------------------------------------------------------------------- | ------------------------------------------------------------------------ |
| Persons with the first name 'John'                                  | /person?query=`firstName='John'`                                         |
| Persons with the birthday equals to the given `LocalDateTime`       | /person?query=`birthday='1981-03-12T10:36:00'`                           |
| Persons with the hire date equals to the given `OffsetDateTime`     | /person?query=`job.hireDate='2019-09-01T09:00:00Z'`                      |
| Persons with the birthday equals to the current `LocalDateTime`     | /person?query=`birthday=CURRENT_DATE_TIME`                               |
| Persons who own a car (VehicleType is an Enum)                      | /person?query=`vehicle.vehicleType='CAR'`                                |
| Persons who are 1,74 m tall                                         | /person?query=`height=174`                                               |
| Persons who are actively employed                                   | /person?query=`job.active=true`                                          |
| Persons who have brown hair<br/>_It uses a field of `Map` type_     | /person?query=`characteristics.key=hair AND characteristics.value=brown` |

1. <a name="not-equals-operator"></a> Not Equals operator `!=`

| What you want to query                                              | Example                                               |
| ------------------------------------------------------------------- | ----------------------------------------------------- |
| Persons who are not named 'John'                                    | /person?query=`firstName!='John'`                     |
| Persons with the birthday not equals to the given `LocalDateTime`   | /person?query=`birthday!='1981-03-12T10:36:00'`       |
| Persons with the birthday not equals to the current `LocalDateTime` | /person?query=`birthday=CURRENT_DATE_TIME`            |
| Persons with the hire date not equals to the given `OffsetDateTime` | /person?query=`job.hireDate!='2019-09-01T09:00:00Z'`  |
| Persons who don't own a car (VehicleType is an Enum)                | /person?query=`vehicle.vehicleType!='CAR'`            |
| Persons who are not 1,74 m tall                                     | /person?query=`height!=174`                           |
| Persons who are not actively employed                               | /person?query=`job.active!=true`                      |

1. <a name="less-than-operator"></a> Less than operator `<`

| What you want to query                                              | Example                                               |
| ------------------------------------------------------------------- | ----------------------------------------------------- |
| Persons who were born before the given `LocalDateTime`              | /person?query=`birthday<'1981-03-12T10:36:00'`        |
| Persons who are hired before the given `OffsetDateTime`             | /person?query=`job.hireDate<'2019-09-01T09:00:00Z'`   |
| Persons who are hired before current datetime                       | /person?query=`job.hireDate<CURRENT_DATE_TIME`        |
| Persons who are smaller than 1,74 m                                 | /person?query=`height<174`                            |

1. <a name="less-than-or-equals-operator"></a> Less than or equals operator `<=`

| What you want to query                                              | Example                                               |
| ------------------------------------------------------------------- | ----------------------------------------------------- |
| Persons who were born before or on the given `LocalDateTime`        | /person?query=`birthday<='1981-03-12T10:36:00'`       |
| Persons who are hired before or on the given `OffsetDateTime`       | /person?query=`job.hireDate<='2019-09-01T09:00:00Z'`  |
| Persons who are hired before or on current datetime                 | /person?query=`job.hireDate<=CURRENT_DATE_TIME`       |
| Persons who are smaller than or equal to 1,74 m                     | /person?query=`height<=174`                           |

1. <a name="greater-than-operator"></a> Greater than operator `>`

| What you want to query                                              | Example                                               |
| ------------------------------------------------------------------- | ----------------------------------------------------- |
| Persons who were born after the given `LocalDateTime`               | /person?query=`birthday>'1981-03-12T10:36:00'`        |
| Persons who are hired after the given `OffsetDateTime`              | /person?query=`job.hireDate>'2019-09-01T09:00:00Z'`   |
| Persons who are hired after the current datetime                    | /person?query=`job.hireDate>CURRENT_DATE_TIME`        |
| Persons who are taller than 1,74 m                                  | /person?query=`height>174`                            |

1. <a name="greater-than-or-equals-operator"></a> Greater than or equals operator `>=`

| What you want to query                                              | Example                                               |
| ------------------------------------------------------------------- | ----------------------------------------------------- |
| Persons who were born after or on the given `LocalDateTime`         | /person?query=`birthday>='1981-03-12T10:36:00'`       |
| Persons who are hired after or on the given `OffsetDateTime`        | /person?query=`job.hireDate>='2019-09-01T09:00:00Z'`  |
| Persons who are hired after or on current datetime                  | /person?query=`job.hireDate>=CURRENT_DATE_TIME`       |
| Persons who are taller than or equal to 1,74 m                      | /person?query=`height>=174`                           |

1. <a name="matches-operator"></a> Matches operator `MATCHES`

_Use the wildcard character `*` to match any string with zero or more characters._

| What you want to query                                              | Example                                               |
| ------------------------------------------------------------------- | ----------------------------------------------------- |
| Persons with the first name starting with 'Jo'                      | /person?query=`firstName MATCHES 'Jo*'`               |
| Persons with the first name ending with 'hn'                        | /person?query=`firstName MATCHES '*hn'`               |
| Persons with the first name containing 'oh'                         | /person?query=`firstName MATCHES '*oh*'`              |
| Persons with the first name that does not start with 'Jo'           | /person?query=`firstName NOT MATCHES 'Jo*'`           |

1. <a name="imatches-operator"></a> Case-insensitive matches operator `IMATCHES`

This operator has the same behaviour as ['MATCHES'](#matches-operator) except that it is not case-sensitive.

_Use the wildcard character `*` to match any string with zero or more characters._

| What you want to query                                                             | Example                                               |
| ---------------------------------------------------------------------------------- | ----------------------------------------------------- |
| Persons with the first name starting with 'JO', ignoring case-sensitive            | /person?query=`firstName IMATCHES 'JO*'`              |
| Persons with the first name ending with 'HN', ignoring case-sensitive              | /person?query=`firstName IMATCHES '*HN'`              |
| Persons with the first name containing 'OH', ignoring case-sensitive               | /person?query=`firstName IMATCHES '*OH*'`             |
| Persons with the first name that does not start with 'JO', ignoring case-sensitive | /person?query=`firstName NOT IMATCHES 'JO*'`          |

1. <a name="in-operator"></a> `ÌN` operator

| What you want to query                                                  | Example                                                              |
| ----------------------------------------------------------------------- | -------------------------------------------------------------------- |
| Persons who are named 'John' or 'Jane'                                  | /person?query=`firstName IN ('John', 'Jane')`                        |
| Persons with the height is one the given values                         | /person?query=`height IN (168, 174, 185)`                            |
| Persons who own one of the given vehicle types (VehicleType is an Enum) | /person?query=`vehicle.vehicleType IN ('CAR', 'MOTORBIKE', 'TRUCK')` |
| Persons who are not named 'John' or 'Jane'                              | /person?query=`firstName NOT IN ('John', 'Jane')`                    |

1. <a name="date-comparison"></a> Date comparison

The fields representing a date, time, or datetime can be compared with a string having a valid format according to the type of the field.

| What you want to query                                          | Example                                             |
| --------------------------------------------------------------- | --------------------------------------------------- |
| Persons with the birthday equals to the given `LocalDateTime`   | /person?query=`birthday='1981-03-12T10:36:00'`      |
| Persons with the hire date equals to the given `OffsetDateTime` | /person?query=`job.hireDate='2019-09-01T09:00:00Z'` |

Also, the fields representing a date, time, or datetime can be compared with the following keywords:
* `CURRENT_DATE`: keyword representing the current date
* `CURRENT_TIME`: keyword representing the current time
* `CURRENT_DATE_TIME`: keyword representing the current date and time

| What you want to query                                      | Example                                     |
| ----------------------------------------------------------- | ------------------------------------------- |
| Persons with the birthday is at current datetime            | /person?query=`birthday=CURRENT_DATE_TIME`  |
| Persons with the birthday is not at current datetime        | /person?query=`birthday!=CURRENT_DATE_TIME` |
| Persons with the birthday is after current datetime         | /person?query=`birthday>CURRENT_DATE_TIME`  |
| Persons with the birthday is after or at current datetime   | /person?query=`birthday>=CURRENT_DATE_TIME` |
| Persons with the birthday is before current datetime        | /person?query=`birthday<CURRENT_DATE_TIME`  |
| Persons with the birthday is before or at current datetime  | /person?query=`birthday<=CURRENT_DATE_TIME` |

1. <a name="null-comparison"></a> `NULL` comparison

| What you want to query                                  | Example                                                                 |
| ------------------------------------------------------- | ----------------------------------------------------------------------- |
| Persons with the birthday is 'null'                     | /person?query=`birthday=null`<br/>/person?query=`birthday IS NULL`      |
| Persons with the birthday is not 'null'                 | /person?query=`birthday!=null`<br/>/person?query=`birthday IS NOT NULL` |
| Persons who don't have jobs                             | /person?query=`job=null`<br/>/person?query=`job IS NULL`                |
| Persons who have jobs                                   | /person?query=`job!=null`<br/>/person?query=`job IS NOT NULL`           |

1. <a name="and-logical-operator"></a> `AND` logical operator

| What you want to query                                                                                                                                         | Example                                                                                                                                                                  |
| -------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Persons with the first name 'John', with blue eyes, with a height greater than 1,60 m, the birthday is the given `LocalDateTime` and who are actively employed | /person?query=`firstName='John' AND characteristics.key='eyes' AND characteristics.value='blue' AND height > 160 and birthday='1981-03-12T10:36:00' AND job.active=true` |

1. <a name="or-logical-operator"></a> `OR` logical operator

| What you want to query                                                  | Example                                                                      |
| ----------------------------------------------------------------------- | ---------------------------------------------------------------------------- |
| Persons who are named 'John' or 'Jane'                                  | /person?query=`firstName='John' OR firstName='Jane'`                         |
| Persons with the height is 1,68 m, 1,74 m or 1,85 m                     | /person?query=`height=168 OR height=174 OR height=185`                       |
| Persons who own a car or a motorbike (VehicleType is an Enum)           | /person?query=`vehicle.vehicleType='CAR' OR vehicle.vehicleType='MOTORBIKE'` |

1. <a name="not-operator"></a> `NOT` operator

| What you want to query                                                  | Example                                                                      |
| ----------------------------------------------------------------------- | ---------------------------------------------------------------------------- |
| Persons with the first name is not 'John' or 'Jane'                     | /person?query=`NOT (firstName='John' OR firstName='Jane'`)                   |
| Persons who don't live in France and is not actively employed           | /person?query=`NOT (address.country='FR' AND job.active=true`                |
| Persons who don't own a car (VehicleType is an Enum)                    | /person?query=`NOT vehicle.vehicleType='CAR'`                                |

1. <a name="parentheses"></a> Parentheses

The precedence of operators determines the order of evaluation of terms in an expression.

[AND](#and-logical-operator) operator has precedence over the [OR](#or-logical-operator) operator.

To override this order and group terms explicitly, you can use parentheses.

| What you want to query                                                           | Example                                                                                                                   |
| -------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------- |
| Persons who are named 'John' or 'Jane', and own a car or a motorbike             | /person?query=`(firstName='John' OR firstName='Jane') AND (vehicle.vehicleType='CAR' OR vehicle.vehicleType='MOTORBIKE')` |

1. Nested fields

To search on nested fields, you must concatenate the deep fields separated by the dot '`.`'.\
_Example: The `Person` Entity contains a property of the `Address` Entity that is named `addressEntities`, and we search for Persons who live in 'Paris'_:\
/search?`addressEntities.city='Paris'`

| What you want to query                                                           | Example                                                          |
| -------------------------------------------------------------------------------- | ---------------------------------------------------------------- |
| Persons who own a car                                                            | /person?query=`vehicle.vehicleType='CAR'`                        |
| Persons who live in 'France' or in Italy                                         | /person?query=`address.country='FR' OR address.country='IT'`     |
| Persons who work job company is `Acme` and are actively employed                 | /person?query=`job.company='Acme' AND job.active=true`           |

## Features

### Javadoc
| Module       | Javadoc                                                                                                                                                |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Core         | [![javadoc-core](https://javadoc.io/badge2/com.weedow/weedow-searchy-core/javadoc.svg)](https://javadoc.io/doc/com.weedow/weedow-searchy-core) |
| JPA          | [![javadoc-jpa](https://javadoc.io/badge2/com.weedow/weedow-searchy-jpa/javadoc.svg)](https://javadoc.io/doc/com.weedow/weedow-searchy-jpa)    |

### Search Descriptor
The Search Descriptors allow exposing automatically search endpoints for Entities.\
The new endpoints are mapped to `/search/{searchyDescriptorId}` where `searchyDescriptorId` is the [ID](#search-descriptor-id) defined for the `SearchyDescriptor`.

_Note: You can change the default base path `/search`. See [Changing the Base Path](#changing-the-base-path)._ 

The easiest way to create a Search Descriptor is to use the `com.weedow.searchy.descriptor.SearchyDescriptorBuilder` which provides every options available to configure a `SearchyDescriptor`.

#### Configure a new Search Descriptor
You have to add the `SearchyDescriptor`s to the Searchy Configuration to expose the Entity endpoint:
* Implement the `com.weedow.searchy.config.SearchyConfigurer` interface and override the `addSearchyDescriptors` method:
    ```java
    @Configuration
    public class SearchyDescriptorConfiguration implements SearchyConfigurer {
    
        @Override
        public void addSearchyDescriptors(SearchyDescriptorRegistry registry) {
            SearchyDescriptor searchyDescriptor = new SearchyDescriptorBuilder<Person>(Person.class).build();
            registry.addSearchyDescriptor(searchyDescriptor);
        }
    }
    ```

* Another solution is to add a new `@Bean`. This solution is useful when you want to create a `SearchyDescriptor` which depends on other Beans:
    ```java
    @Configuration
    public class SearchyDescriptorConfiguration {
        @Bean
        SearchyDescriptor<Person> personSearchyDescriptor(PersonRepository personRepository) {
            return new SearchyDescriptorBuilder<Person>(Person.class)
                       .specificationExecutor(personRepository)
                       .build();
        }
    }
    ```

#### Search Descriptor options
##### Search Descriptor ID
This is the Search Descriptor Identifier. Each identifier must be unique.\
Searchy uses this identifier in the search endpoint URL which is mapped to `/search/{searchyDescriptorId}`: `searchyDescriptorId` is the Search Descriptor Identifier.

If the Search Descriptor ID is not set, Searchy uses the Entity Name in lowercase as Search Descriptor ID.\
_If the Entity is `Person.java`, the Search Descriptor ID is `person`_

Example with a custom Search Descriptor ID:
```java
@Configuration
public class SearchyDescriptorConfiguration implements SearchyConfigurer {

    @Override
    public void addSearchyDescriptors(SearchyDescriptorRegistry registry) {
        registry.addSearchyDescriptor(personSearchyDescriptor());
    }
    
    SearchyDescriptor<Person> personSearchyDescriptor() {
        return new SearchyDescriptorBuilder<Person>(Person.class)
                        .id("people")
                        .build();
    }
}
```

##### Entity Class
This is the Class of the Entity to be searched.\
When you use `com.weedow.searchy.descriptor.SearchyDescriptorBuilder`, the Entity Class is added during instantiation:
* In a Java project: `new SearchyDescriptorBuilder<>(Person.class)`
* In a Kotlin project: `SearchyDescriptorBuilder.builder<Person>().build()` or `SearchyDescriptorBuilder(Address::class.java).build()`

##### DTO Mapper
This option allows to convert the Entity to a specific DTO before returning the HTTP response.\
This can be useful when you don't want to return all data of the entity.

To do this, you need to create a class which implements the `com.weedow.searchy.dto.DtoMapper` interface:
```java
public class PersonDtoMapper implements DtoMapper<Person, PersonDto> {
    @Override
    public PersonDto map(Person source) {
        return PersonDto.Builder()
                .firstName(source.firstName)
                .lastName(source.lastName)
                .email(source.email)
                .nickNames(source.nickNames)
                .phoneNumbers(source.phoneNumbers)
                .build();
    }
}
```
Then you add this DTO Mapper to the `SearchyDescriptor`:
```java
@Configuration
public class SearchyDescriptorConfiguration implements SearchyConfigurer {

    @Override
    public void addSearchyDescriptors(SearchyDescriptorRegistry registry) {
        registry.addSearchyDescriptor(personSearchyDescriptor());
    }
    
    SearchyDescriptor<Person> personSearchyDescriptor() {
        return new SearchyDescriptorBuilder<Person>(Person.class)
                        .dtoMapper(new PersonDtoMapper())
                        .build();
    }
}
```

If this option is not set, a default DTO Mapper is used. This default DTO Mapper may be different according to the database implementation used.
The `Core` provides a default DTO Mapper `com.weedow.searchy.dto.DefaultDtoMapper` that does not convert the entity, and the HTTP response returns it directly.

##### Validators
Searchy provides a validation service to validate the Field Expressions.

A `Field Expression` is a representation of a query parameter which evaluates an Entity field.\
_Example: `/search/person?job.company=Acme` : the query parameter `job.company=Acme` is converted to a Field Expression where the `company` field from the `Job` Entity must be equals to `Acme`._

**Note:** The validation service does not validate the Type of the query parameter values.
This is already supported when Searchy converts the query parameter values from String to the correct type expected by the related field.
(See [Converters](#converters))

The validators is used to validate whether:
* A value matches a specific Regular Expression,
* A number is between a minimum and maximum value
* There is at least one query parameter in the request
* A query parameter for a specific field is present or absent in the request
* ...

To do this, you need to create a new class which implements the `com.weedow.searchy.validation.SearchyValidator` interface:
```java
public class EmailValidator implements SearchyValidator {

    private static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    @Override
    public void validate(Collection<? extends FieldExpression> fieldExpressions, SearchyErrors errors) {
        fieldExpressions
                .stream()
                .filter(fieldExpression -> "email".equals(fieldExpression.getFieldInfo().getField().getName()))
                .forEach(fieldExpression -> {
                    final Object value = fieldExpression.getValue();
                    if (value instanceof String) {
                        if (!value.toString().matches(EMAIL_REGEX)) {
                            errors.reject("email", "Invalid email value");
                        }
                    }
                });
    }
}
```
Then you need to add the validators to a [Search Descriptor](#search-descriptor):
```java
@Configuration
public class SearchyDescriptorConfiguration implements SearchyConfigurer {

    @Override
    public void addSearchyDescriptors(SearchyDescriptorRegistry registry) {
        registry.addSearchyDescriptor(personSearchyDescriptor());
    }
    
    SearchyDescriptor<Person> personSearchyDescriptor() {
        return new SearchyDescriptorBuilder<Person>(Person.class)
                        .validators(new NotEmptyValidator(), new EmailValidator("email"))
                        .build();
    }
}
```

Searchy provides the following `SearchyValidator` implementations:
* `com.weedow.searchy.validation.validator.NotEmptyValidator`: Checks if there is at least one field expression.
* `com.weedow.searchy.validation.validator.NotNullValidator`: Checks if the field expression value is not `null`.
* `com.weedow.searchy.validation.validator.RequiredValidator`: Checks if all specified required `fieldPaths` are present. The validator iterates over the field expressions and compare the related `fieldPath` with the required `fieldPaths`.
* `com.weedow.searchy.validation.validator.PatternValidator`: Checks if the field expression value matches the specified `pattern`.
* `com.weedow.searchy.validation.validator.UrlValidator`: Checks if the field expression value matches a valid `URL`.
* `com.weedow.searchy.validation.validator.EmailValidator`: Checks if the field expression value matches the email format.
* `com.weedow.searchy.validation.validator.MaxValidator`: Checks if the field expression value is less or equals to the specified `maxValue`.
* `com.weedow.searchy.validation.validator.MinValidator`: Checks if the field expression value is greater or equals to the specified `minValue`.
* `com.weedow.searchy.validation.validator.RangeValidator`: Checks if the field expression value is between the specified `minValue` and `maxValue`.

##### Specification Executor
Searchy defines the class `com.weedow.searchy.query.specification.Specification`, inspired by [Spring Data JPA Specifications](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#specifications).
It is used to aggregate all expressions in query parameters and query the Entities in the Database.

Searchy defines the following interface to allow execution of `Specifications`:
```java
public interface SpecificationExecutor<T> {
    //...//
    List<T> findAll(Specification<T> spec);
    //...//
}
```

This interface is already implemented for each Database implementation (JPA, MongoDB ...).
This is normally sufficient for the majority of needs, but you can set this option with your own `SpecificationExecutor` implementation if you need a specific implementation.

To ease integration with Spring Repositories, there is the `com.weedow.searchy.repository.SearchyBaseRepository` interface.

* Extending an annotated `@Repository` interface with the `SearchyBaseRepository` interface
  ```java
  @Repository
  public interface PersonRepository extends SearchyBaseRepository {
  }
  ```
* Set the `SearchyDescriptor` with the previous interface
  ```java
  @Configuration
  public class SearchyDescriptorConfiguration {
      @Bean
      SearchyDescriptor<Person> personSearchyDescriptor(PersonRepository personRepository) {
          return new SearchyDescriptorBuilder<Person>(Person.class)
                     .specificationExecutor(personRepository)
                     .build();
      }
  }
  ```
* If the annotated @Repository interface has a specific implementation, implement the `List<T> findAll(Specification<T> specification)` method
  ```java
  public class PersonRepositoryImpl implements PersonRepository {
    public List<Person> findAll(Specification<Person> specification) {
      // ...
    }
  }
  ```
* If the annotated @Repository interface does not have a specific implementation, it means that it uses a default Spring implementation that will not support the `List<T> findAll(Specification<T> specification)` method.
  It is therefore necessary to override this behavior by specifying another [FactoryBean](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/FactoryBean.html) class to be used for each repository instance.
  For example, if it's a JPA Repository, you have to specify that the `repositoryFactoryBeanClass` is `com.weedow.searchy.jpa.repository.JpaSearchyRepositoryFactoryBean`:
  ```java
  @SpringBootApplication
  @EnableJpaRepositories(value = {"com.sample.repository"}, repositoryFactoryBeanClass = JpaSearchyRepositoryFactoryBean.class)
  public class SampleAppJavaApplication {
 
     public static void main(String[] args) {
         SpringApplication.run(SampleAppJavaApplication.class, args);
     }
 
  }
  ```

##### Entity Join Handlers
It is sometimes useful to optimize the number of SQL queries by specifying the data that you want to fetch during the first SQL query with the criteria.

This option allows adding `EntityJoinHandler` implementations to specify join types for any fields having _join annotation_.

You can add several `EntityJoinHandler` implementations. The first implementation that matches from the `support(...)` method will be used to specify the join type for the given field.

```java
@Configuration
public class SearchyDescriptorConfiguration {
  @Bean
  public SearchyDescriptor<Person> personSearchyDescriptor(SearchyContext searchyContext) {
    return new SearchyDescriptorBuilder<>(Person.class)
            .entityJoinHandlers(new MyEntityJoinHandler(), new JpaFetchingEagerEntityJoinHandler(searchyContext))
            .build();
  }
}
```

Searchy provides the following default implementations:
* `FetchingAllEntityJoinHandler`: This implementation allows to query an entity by fetching all data related to this entity, i.e. all fields related to another Entity recursively.\
  _Example:_\
  _`A` has a relationship with `B` and `B` has a relationship with `C`._\
  _When we search for `A`, we retrieve `A` with data from `B` and `C`._
* `JpaFetchingEagerEntityJoinHandler`: This specific JPA implementation allows to query an entity by fetching all fields having a Join Annotation with the Fetch type defined as `EAGER`.\
  _Example:_\
  _`A` has a relationship with `B` using `@OneToMany` annotation and `FetchType.EAGER`, and `A` has a relationship with `C` using `@OneToMany` annotation and `FetchType.LAZY`._\
  _When we search for `A`, we retrieve `A` with just data from `B`, but not `C`._

You can create your own implementation to fetch the additional data you require.\
Just implement the `com.weedow.searchy.join.handler.EntityJoinHandler` interface:
```java
/**
 * Fetch all fields annotated with @ElementCollection
 */
public class MyEntityJoinHandler implements EntityJoinHandler {

  @Override
  public boolean supports(PropertyInfos propertyInfos) {
    return propertyInfos.getAnnotations().stream().anyMatch(annotation -> annotation instanceof ElementCollection);
  }

  @Override
  public JoinInfo handle(PropertyInfos propertyInfos) {
    return new JoinInfo(JoinType.LEFTJOIN, true);
  }
}
```

If this option is not set, the default Searchy behavior is to create `LEFT JOIN` if needed.


_For more details about joins handling, please read the following explanations._

---

If the result contains the root Entity with the related Entities, there will be multiple SQL queries:
* One SQL query with your criteria
* One query by joined Entity to retrieve the related data

Let's say you want to have an endpoint to search any `Person`s.\
The endpoint response returns the `Person`s found with their `Job`s and `Vehicle`s.

The `Person.java` Entity has relationships with the `Job.java` Entity and the `Vehicle.java` Entity. Here are the entities :

```java
@Entity
public class Person {
    //...//
    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private Job jobEntity;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vehicle> vehicles;
    //...//
}

@Entity
public class Vehicle {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @ManyToOne(optional = false)
    private String person;

    // Getters/Setters
}

@Entity
public class Job {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String company;
    
    @Column(nullable = false)
    private Integer salary;
    
    @Column(nullable = false)
    private OffsetDateTime hireDate;
    
    @OneToOne(optional = false)
    private Person person;

    // Getters/Setters
}
```

You want to search for the persons who's the vehicle brand is _Renault_, and the job company is _Acme_:
```
/search/person?vehicles.brand=Renault&jobEntity.company=Acme
```
If you have any persons who match your query, you should get an HTTP response that looks like the following:
```json
[
    {
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@acme.com",
        "birthday": "1981-03-12T10:36:00",
        "jobEntity": {
            "title": "Lab Technician",
            "company": "Acme",
            "salary": 50000,
            "hireDate": "2019-09-01T11:00:00+02:00",
            "id": 1,
            "createdOn": "2020-03-12T11:36:00+01:00",
            "updatedOn": "2020-04-17T14:00:00+02:00"
        },
        "vehicles": [
            {
                "vehicleType": "CAR",
                "brand": "Renault",
                "model": "Clio",
                "id": 1,
                "createdOn": "2020-03-12T11:36:00+01:00",
                "updatedOn": "2020-04-17T14:00:00+02:00"
            }
        ],
        "id": 1,
        "createdOn": "2020-03-12T11:36:00+01:00",
        "updatedOn": "2020-04-17T14:00:00+02:00"
    }
]
```
To get this result, there were several SQL queries:
- The SQL query with your criteria:
    ```sql
    select
        distinct p.id,
        p.created_on,
        p.updated_on,
        p.birthday,
        p.email,
        p.first_name,
        p.height,
        p.last_name,
        p.weight
    from person p 
    left outer join vehicle v on p.id=v.person_id 
    left outer join job j on p.id=j.person_id 
    where
        j.company='Acme' 
        and v.brand='Renault';
    ```
- The following SQL query executed for each Person returned by the first SQL query:
    ```sql
    select j.*, p.*
    from job j 
    inner join person p on j.person_id=p.id 
    where
        j.person_id={PERSON_ID};
    ```
  These SQL queries occur because the field `jobEntity` present on the `Person` Entity is annotated with the `@OneToOne` annotation whose the default fetch type is `EAGER`.
  - The following SQL query executed for each Person returned by the first SQL query:
    ```sql
    select v.*
    from vehicle v 
    where
        v.person_id={PERSON_ID}
    ```
    The `vehicles` field present on the `Person` Entity is annotated with the `@OneToMany` annotation (default fetch type is `LAZY`).\
    However, these SQL queries occur because vehicle information must be returned in the HTTP response.

It is therefore sometimes useful to optimize the number of SQL queries by specifying the data that you want to fetch during the first SQL query with the criteria.

To do this, you can use the [EntityJoinHandlers](#entity-join-handlers) to specify the join type for each Entity field having a relationship with another Entity.

### Aliases
Searchy provides an alias management to replace any field name with another name in queries.

This can be useful when the name of a field is too technical or too long or simply to allow several possible names.

Let's say you manage Persons with following Entity:
```java
@Entity
public class Person {
    //...//
    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private Job jobEntity;
    //...//
}
```

You want to search for persons with their job company is `Acme`. The request looks like: `/search/person?jobEntity.company=Acme`\
However you don't want use the `jobEntity` string but `job` into the URL: `/search/person?job.company=Acme`

To do this, you need to create a `AliasResolver` implementation:
```java
/**
 * Create an alias for all fields ending with 'Entity'.
 **/
class MyAliasResolver implements AliasResolver {
    private static final String SUFFIX = "Entity";

    @Override
    public Boolean supports(Class<?> entityClass, Field field) {
        return field.name.endsWith(SUFFIX);
    }

    @Override
    List<String> resolve(Class<?> entityClass, Field field) {
        return Arrays.asList(StringUtils.substringBefore(fieldName, SUFFIX));        
    }
}
```
You must then register it in the Alias Resolver Registry:
```java
@Configuration
public class SampleAppJavaConfiguration implements SearchyConfigurer {

    @Override
    public void addAliasResolvers(AliasResolverRegistry registry) {
        registry.addAliasResolver(new MyAliasResolver());
    }
}
```

Another solution is to declare your AliasResolver as `@Bean`. This solution is useful when you want to create a AliasResolver which depends on other Beans.

By default, Searchy registers the following Alias Resolvers:
* `SearchyDefaultAliasConfigurerAutoConfiguration`: Creates an alias for all fields ending with the suffixes `Entity` or `Entities`.

### Converters
Searchy converts the query parameter values from String to the correct type expected by the related field.

Searchy uses the [Spring Converter Service](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#core-convert). \
Spring Converter Service provides several converter implementations in the `core.convert.support` package.

To create your own converter, implement the `Converter` interface and parameterize S as the `java.lang.String` type and T as the type you are converting to.
```java
public class MyConverter implements Converter<String, MyObject> {

    @Override
    public MyObject convert(String s) {
        return MyObject.of(s);
    }

} 
```
You must then register it in the Converter registry:
```java
@Configuration
public class SampleAppJavaConfiguration implements SearchyConfigurer {

    @Override
    public void addConverters(ConverterRegistry registry) {
        registry.addConverter(new MyConverter());
    }
}
```

Another solution is to declare your Converter as `@Bean`. This solution is useful when you want to create a Converter which depends on other Beans.

### Changing the Base Path

By default, Searchy defines the Base Path as `/search` and add the Search Descriptor ID. Example: `/search/person`

You can do change the Base Path by setting a single property in application.properties, as follows:

````properties
weedow.searchy.base-path=/api
````

This changes the Base Path to `/api`. Example: `/api/person`

---

## Issues
[![Issues](https://img.shields.io/github/issues/Kobee1203/weedow-searchy)](https://github.com/Kobee1203/weedow-searchy/issues)

## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## How to make a new Release
Page to describe the process of creating a new release: [Make a new release](./docs/release.md)

## Contact

Nicolas Dos Santos - [@Kobee1203](https://twitter.com/Kobee1203)

Project Link: <https://github.com/Kobee1203/weedow-searchy>

## Social Networks
[![Tweets](https://img.shields.io/twitter/url?style=social&url=https%3A%2F%2Fgithub.com%2FKobee1203%2Fweedow-searchy)]()

[![GitHub forks](https://img.shields.io/github/forks/Kobee1203/weedow-searchy?style=social)]()
[![GitHub stars](https://img.shields.io/github/stars/Kobee1203/weedow-searchy?style=social)]()
[![GitHub watchers](https://img.shields.io/github/watchers/Kobee1203/weedow-searchy?style=social)]()

## License

[![MIT License](https://img.shields.io/github/license/Kobee1203/weedow-searchy)](https://github.com/Kobee1203/weedow-searchy/blob/master/LICENSE.txt) \
_Copyright (c) 2020 Nicolas Dos Santos and other contributors_
