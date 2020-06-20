# Spring Data Search
Spring Data Search allows to automatically expose endpoints in order to search for data related to JPA entities.

Spring Data Search provides an advanced search engine that does not require the creation of JPA repositories with custom methods needed to search on different fields of JPA entities.

We can search on any field, combine multiple criteria to refine the search, and even search on deep fields. 

## Why use Spring Data Search?
Spring Data Rest builds on top of the Spring Data repositories and automatically exports those as REST resources. 

However, when we want to search for JPA entities according to different criteria, we need to define several methods in the Repositories to perform different searches.

Moreover, by default REST endpoints return JPA Entities content directly to the client, without mapping with a dedicated DTO class.\
We can use Projections on Repositories, but this means that from the architecture level, we strongly associate the infrastructure layer with the application layer.

Spring Data Search allows to easily expose an endpoint for a JPA entity and thus be able to search on any fields of this entity, to combine several criteria and even search on fields belonging to sub-entities.

Let's say you manage Persons associated with Addresses, Vehicles and a Job.\
You want to allow customers to search for them, regardless of the search criteria:
* Search for Persons whose first name is "John" or "Jane"
* Search for Persons whose company where they work is "Acme", and own a car or a motorbike 
* Search for Persons who live in London

You could create a Repository with custom methods to perform all these searches, and you could add new custom methods according to the needs.

Alternatively, you can use Spring Data Search which allows you to perform all these searches with a minimum configuration, without the need of a custom Repository. If you want to do other different searches, you do not need to add new methods to do that.

## Build
![GitHub repo size](https://img.shields.io/github/repo-size/Kobee1203/spring-data-search)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/Kobee1203/spring-data-search)

[![Build](https://img.shields.io/github/workflow/status/Kobee1203/spring-data-search/Build%20and%20Analyze)](https://github.com/Kobee1203/spring-data-search/actions?query=workflow%3A%22Build+and+Analyze%22)
[![Libraries.io dependency status for GitHub repo](https://img.shields.io/librariesio/github/Kobee1203/spring-data-search)]()

[![Code Coverage](https://img.shields.io/sonar/coverage/spring-data-search?server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=spring-data-search)
[![Sonar Quality Gate](https://img.shields.io/sonar/quality_gate/spring-data-search?server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=spring-data-search)
[![Sonar Tech Debt](https://img.shields.io/sonar/tech_debt/spring-data-search?server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=spring-data-search)
[![Sonar Violations](https://img.shields.io/sonar/violations/spring-data-search?server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=spring-data-search)

### Built with:
* [Kotlin](https://kotlinlang.org/)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Maven](https://maven.apache.org/)

## Getting Started

### Prerequisites
* JDK 8 or more.
* Spring Boot

### Installation
[![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/Kobee1203/spring-data-search?include_prereleases)](https://github.com/Kobee1203/spring-data-search/releases)
[![Downloads](https://img.shields.io/github/downloads/Kobee1203/spring-data-search/total)](https://github.com/Kobee1203/spring-data-search/releases)
[![Maven Central](https://img.shields.io/maven-central/v/com.weedow/spring-data-search-core)]()

* You can download the [latest release](https://github.com/Kobee1203/spring-data-search/releases).
* If you have a [Maven](https://maven.apache.org/) project, you can add the following dependency in your `pom.xml` file:
  ```xml
  <dependency>
      <groupId>com.weedow</groupId>
      <artifactId>spring-data-search</artifactId>
      <version>0.0.1</version>
  </dependency>
  ```
* If you have a [Gradle](https://gradle.org/) project, you can add the following dependency in your `build.gradle` file:
  ```groovy
  implementation "com.weedow:spring-data-search:0.0.1"
  ```

### Getting Started in 5 minutes

* Go to https://start.spring.io/
* Generate a new Java project `sample-app-java` with the following dependencies:
    * Spring Web
    * Spring Data JPA
    * H2 Database
    ![start.spring.io](./docs/images/start.spring.io.png)
* Update the generated project by adding the dependency of Spring Data Search:
    * For [Maven](https://maven.apache.org/) project, add the dependency in the `pom.xml` file: 
    ```xml
    <dependency>
      <groupId>com.weedow</groupId>
      <artifactId>spring-data-search</artifactId>
      <version>0.0.1</version>
    </dependency>
    ```
    * For [Gradle](https://gradle.org/) project, add the dependency in the `build.gradle` file:
    ```groovy
    implementation "com.weedow:spring-data-search:0.0.1"
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
* Add the following Configuration class to add a new `SearchDescriptor`:
    ```java
    import com.example.sampleappjava.entity.Person;
    import com.weedow.spring.data.search.config.SearchConfigurer;
    import com.weedow.spring.data.search.descriptor.SearchDescriptor;
    import com.weedow.spring.data.search.descriptor.SearchDescriptorBuilder;
    import com.weedow.spring.data.search.descriptor.SearchDescriptorRegistry;
    import org.springframework.context.annotation.Configuration;
    
    @Configuration
    public class SampleAppJavaConfiguration implements SearchConfigurer {
    
        @Override
        public void addSearchDescriptors(SearchDescriptorRegistry registry) {
            registry.addSearchDescriptor(personSearchDescriptor());
        }
    
        private SearchDescriptor<Person> personSearchDescriptor() {
            return new SearchDescriptorBuilder<Person>(Person.class).build();
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
* You can filter the results by adding query parameters representing the JPA Entity fields:\
  Here is an example where the results are filtered by the first name:
![find-person-by-firstname](./docs/images/find-person-by-firstname.png)

## Usage

The examples in this section are based on the following entity model:

The `Person.java` Entity has relationships with the `Job.java` Entity and the `Vehicle.java` Entity. Here are the entities:
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

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private Job jobEntity;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vehicle> vehicles;

    // Getters/Setters
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

### Standard Query
You can search for entities by adding query parameters representing entity fields to the search URL.

This mode is limited to the use of the `AND` operator between each field criteria.\
Each field criteria is limited to the use of the `EQUALS` operator and the `IN` operator.

| What you want to query                                                                                                   | Example                                                                                |
| ------------------------------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------- |
| Persons with the firstName is _'John'_                                                                                   | `/search?firstName=John`                                                               |
| Persons with the firstName is _'John'_ or _'Jane'_<br/>_This will be result from a query with an `IN` operator_          | `/search?firstName=John&firstName=Jane`                                                |
| Persons with the firstName is _'John'_ and lastName is _'Doe'_                                                           | `/search?firstName=John&lastName=Doe`                                                  |
| Persons whose the vehicle brand is _'Renault'_                                                                           | `/search/person?vehicles.brand=Renault`                                                |
| Persons whose the vehicle brand is _'Renault'_ and the job company is _'Acme'_                                           | `/search/person?vehicles.brand=Renault&jobEntity.company=Acme`                         |
| Persons with the firstName is _'John'_ or _'Jane'_, and the vehicle brand is _'Renault'_ and the job company is _'Acme'_ | `/search?firstName=John&firstName=Jane&vehicles.brand=Renault&jobEntity.company=Acme` |
| Persons with the birthday is _'null'_                                                                                    | `/search?birthday=null`                                                                |
| Persons who don't have jobs                                                                                              | `/search?jobEntity=null`                                                               |

### Advanced Query
You can search for entities by using the query string `query`.

_Coming soon_

## Features

### Search Descriptor
The Search Descriptors allow exposing automatically search endpoints for JPA Entities.\
The new endpoints are mapped to `/search/{searchDescriptorId}` where `searchDescriptorId` is the [ID](#search-descriptor-id) defined for the `SearchDescriptor`.

The easiest way to create a Search Descriptor is to use the `com.weedow.spring.data.search.descriptor.SearchDescriptorBuilder` which provides every options available to configure a `SearchDescriptor`.

#### Configure a new Search Descriptor
You have to add the `SearchDescriptor`s to the Spring Data Search Configuration to expose the JPA Entity endpoint:
* Implement the `com.weedow.spring.data.search.config.SearchConfigurer` interface and override the `addSearchDescriptors` method:
    ```java
    @Configuration
    public class SearchDescriptorConfiguration implements SearchConfigurer {
    
        @Override
        public void addSearchDescriptors(SearchDescriptorRegistry registry) {
            SearchDescriptor searchDescriptor = new SearchDescriptorBuilder<Person>(Person.class).build();
            registry.addSearchDescriptor(searchDescriptor);
        }
    }
    ```
* Another solution is to add a new `@Bean`. This solution is useful when you want to create a `SearchDescriptor` which depends on other Beans:
    ```java
    @Configuration
    public class SearchDescriptorConfiguration {
        @Bean
        SearchDescriptor<Person> personSearchDescriptor(PersonRepository personRepository) {
            return new SearchDescriptorBuilder<Person>(Person.class)
                       .jpaSpecificationExecutor(personRepository)
                       .build();
        }
    }
    ```

#### Search Descriptor options
##### Search Descriptor ID
This is the Search Descriptor Identifier. Each identifier must be unique.\
Spring Data Search uses this identifier in the search endpoint URL which is mapped to `/search/{searchDescriptorId}`: `searchDescriptorId` is the Search Descriptor Identifier.

If the Search Descriptor ID is not set, Spring Data Search uses the JPA Entity Name in lowercase as Search Descriptor ID.\
Example: If the Entity is `Person.java`, the Search Descriptor ID is `person`

##### Entity Class
This is the Class of the Entity to be searched.\
When you use `com.weedow.spring.data.search.descriptor.SearchDescriptorBuilder`, the Entity Class is added during instantiation:
* In a Java project: `new SearchDescriptorBuilder<>(Person.class)`
* In a Kotlin project: `SearchDescriptorBuilder.builder<Person>().build()` or `SearchDescriptorBuilder(Address::class.java).build()`

##### DTO Mapper
This option allows to convert the Entity to a specific DTO before returning the HTTP response.\
This can be useful when you don't want to return all data of the entity.

To do this, you need to create a class which implements the `com.weedow.spring.data.search.dto.DtoMapper` interface:
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
     
If this option is not set, the entity is not converted and the HTTP response returns it directly.

##### JPA Specification Executor
Spring Data Search uses the [Spring Data JPA Specifications](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#specifications) to aggregate all expressions in query parameters and query the JPA Entities in the Database.

The base interface to use the Spring Data JPA Specifications is [JpaSpecificationExecutor](https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/JpaSpecificationExecutor.html).

Spring Data Search uses the following method of this interface:
```java
public interface JpaSpecificationExecutor<T> {
    ...
    List<T> findAll(Specification<T> spec);
    ...
}
```

If this option is not set, Spring Data Search instantiates a default implementation of `JpaSpecificationExecutor` according to the JPA Entity.\
This is normally sufficient for the majority of needs, but you can set this option with your own `JpaSpecificationExecutor` implementation if you need a specific implementation.

##### Entity Join Handlers
It is sometimes useful to optimize the number of SQL queries by specifying the data that you want to fetch during the first SQL query with the criteria.

This option allows to add `EntityJoinHandler` implementations to specify join types for any fields having _join annotation_.

The _join annotations_ detected by Spring Data Search are the following:
* javax.persistence.OneToOne
* javax.persistence.OneToMany
* javax.persistence.ManyToMany
* javax.persistence.ElementCollection
* javax.persistence.ManyToOne

You can add several `EntityJoinHandler` implementations. The first implementation that matches from the `support(...)` method will be used to specify the join type for the given field.

Spring Data Search provides the following default implementations:
* `FetchingAllEntityJoinHandler`: This implementation allows to query an entity by fetching all data related to this entity, i.e. all fields related to another Entity recursively.\
  _Example:_\
  _`A` has a relationship with `B` and `B` has a relationship with `C`._\
  _When we search for `A`, we retrieve `A` with data from `B` and `C`._
* `FetchingEagerEntityJoinHandler`: This implementation allows to query an entity by fetching all fields having a Join Annotation with the Fetch type defined as `EAGER`.\
  _Example:_\
  _`A` has a relationship with `B` using `@OneToMany` annotation and `FetchType.EAGER`, and `A` has a relationship with `C` using `@OneToMany` annotation and `FetchType.LAZY`._\
  _When we search for `A`, we retrieve `A` with just data from `B`, but not `C`._

You can create your own implementation to fetch the additional data you require.\
Just implement the `com.weedow.spring.data.search.join.handler.EntityJoinHandler` interface:
```java
/**
 * Fetch all fields annotated with @ElementCollection
 **/
public class MyEntityJoinHandler implements com.weedow.spring.data.search.join.handler.EntityJoinHandler {
    @Override
    public boolean supports(Class<?> entityClass, Class<?> fieldClass, String fieldName, Annotation joinAnnotation) {
        return joinAnnotation instanceof ElementCollection;
    }

    @Override
    public JoinInfo handle(Class<?> entityClass, Class<?> fieldClass, String fieldName, Annotation joinAnnotation) {
        return new JoinInfo(JoinType.LEFT, true);
    }
}
```

If this option is not set, the default Spring Data Search behavior is to create `LEFT JOIN` if needed.


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
    ...
    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private Job jobEntity;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vehicle> vehicles;
    ...
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

You want to search for the persons whose the vehicle brand is _Renault_, and the job company is _Acme_:
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

### Validation
_Coming soon_

### Aliases
Spring Data Search provides an alias management to replace any field name with another name in queries.

This can be useful when the name of a field is too technical or too long or simply to allow several possible names.

Let's say you manage Persons with following Entity:
```java
@Entity
public class Person {
    ...
    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private Job jobEntity;
    ...
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
public class SampleAppJavaConfiguration implements SearchConfigurer {

    @Override
    public void addAliasResolvers(AliasResolverRegistry registry) {
        registry.addAliasResolver(new MyAliasResolver());
    }
}
```

Another solution is to declare your AliasResolver as `@Bean`. This solution is useful when you want to create a AliasResolver which depends on other Beans.

By default, Spring Data Search registers the following Alias Resolvers:
* `DataSearchDefaultAliasConfigurerAutoConfiguration`: Creates an alias for all fields ending with the suffixes `Entity` or `Entities`.

### Converters
Spring Data Search converts the query parameter values from String to the correct type expected by the related field.

Spring Data Search uses the [Spring Converter Service](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#core-convert).\
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
public class SampleAppJavaConfiguration implements SearchConfigurer {

    @Override
    public void addConverters(ConverterRegistry registry) {
        registry.addConverter(new MyConverter());
    }
}
```

Another solution is to declare your Converter as `@Bean`. This solution is useful when you want to create a Converter which depends on other Beans.

---

## Issues
[![Issues](https://img.shields.io/github/issues/Kobee1203/spring-data-search)]()

## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Contact

Nicolas Dos Santos - [@Kobee1203](https://twitter.com/Kobee1203)

Project Link: <https://github.com/Kobee1203/spring-data-search>

## Social Networks
[![Tweets](https://img.shields.io/twitter/url?style=social&url=https%3A%2F%2Fgithub.com%2FKobee1203%2Fspring-data-search)]()

[![GitHub forks](https://img.shields.io/github/forks/Kobee1203/spring-data-search?style=social)]()
[![GitHub stars](https://img.shields.io/github/stars/Kobee1203/spring-data-search?style=social)]()
[![GitHub watchers](https://img.shields.io/github/watchers/Kobee1203/spring-data-search?style=social)]()

## License

[![MIT License](https://img.shields.io/github/license/Kobee1203/spring-data-search)](https://github.com/Kobee1203/spring-data-search/blob/master/LICENSE.txt) \
_Copyright (c) 2020 Nicolas Dos Santos and other contributors_