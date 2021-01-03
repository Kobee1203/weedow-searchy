# Migrating from 1.x to 2.x

[Spring Data Search Core](https://github.com/Kobee1203/spring-data-search/tree/master/core) has changed a lot in order to be able to support different databases.\
[JPA](https://jakarta.ee/specifications/persistence/) is the first supported data access layer by Spring Data Search. It is available from the [JPA Module](https://github.com/Kobee1203/spring-data-search/tree/master/jpa).\
In the future there will be other implementations (e.g. MongoDB).

To upgrade a project using Spring Data Search to version 2.x, the project needs to be updated.

## Update the pom.xml file

Replace the dependency of `spring-data-search-core` by `spring-data-search-jpa`:
```
<dependency>
    <groupId>com.weedow</groupId>
    <artifactId>spring-data-search-jpa</artifactId>
    <version>1.0.1</version>
</dependency>
```
**_replaced by_**
```# 
<dependency>
    <groupId>com.weedow</groupId>
    <artifactId>spring-data-search-jpa</artifactId>
    <version>2.0.0</version>
</dependency>
```

## Update Search Descriptor initialization

### JPA Specification Executor
This API has been changed because the `core` is now agnostic about the data access layer. It uses the new interface `com.weedow.spring.data.search.query.specification.SpecificationExecutor`.

Change `SearchDescriptor` declaration in order to use `specificationExecutor` instead of `jpaSpecificationExecutor`:
```java
new SearchDescriptorBuilder<Person>(Person.class)
    .specificationExecutor(personRepository)
    .build();
```

But this is not enough.\
To know how to use this new API, please read the section about the [Specification Executor option](../../README.md#specification-executor).

### About JpaSpecificationExecutorFactory
`JpaSpecificationExecutorFactory` has been moved to the [JPA Module](https://github.com/Kobee1203/spring-data-search/tree/master/jpa), and has been modified. It no longer uses Spring's [JpaSpecificationExecutor](https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/JpaSpecificationExecutor.html).
It uses the new class `com.weedow.spring.data.search.jpa.query.specification.JpaSpecificationExecutor`.

So the case where _an exception may be thrown if the SearchDescriptor Bean is initialized before JpaSpecificationExecutorFactory_ is no longer relevant.
The following declaration must be deleted everywhere:
```
@DependsOn("jpaSpecificationExecutorFactory")
```

### Entity Join Handlers
This API has been changed. So, if you have `SearchDescriptor` with declared `EntityJoinHandlers`, you have to migrate to the new API.

#### Migrate the old API to the new API
The old API was the following:
```java
public interface EntityJoinHandler<T> {
    Boolean supports(Class<?> entityClass, Class<?> fieldClass, String fieldName, Annotation joinAnnotation);

    JoinInfo handle(Class<?> entityClass, Class<?> fieldClass, String fieldName, Annotation joinAnnotation);
}
```

The new API is the following:
```java
public interface EntityJoinHandler {
   boolean supports(PropertyInfos propertyInfos);

   JoinInfo handle(PropertyInfos propertyInfos);
}
```

We can see that the names of the methods are the same, but the parameters have changed.

To migrate with the new API, here is the equivalent data of the old parameters:
* `Class<*> entityClass` -> `propertyInfos.getParentClass()`
* `Class<*> fieldClass` -> `propertyInfos.getType()`
* `String fieldName` -> `propertyInfos.getFieldName()`
* `Annotation joinAnnotation` -> `propertyInfos` does not have a direct equivalent to this parameter. But `propertyInfos` has got the list of Annotations declared for the current Entity field: `propertyInfos.getAnnotations()`. So if we want to retrieve the `joinAnnotation`, we can do the following:
```java
Annotation joinAnnotation = propertyInfos.getAnnotations()
                                         .stream()
                                         .filter(annotation -> dataSearchContext.isJoinAnnotation(annotation.getClass()))
                                         .findFirst().orElse(null)
```
We can see that the EntityJoinHandler is dependent on `DataSearchContext`. Here is a solution to pass the DataSearchContext to the EntityJoinHandler:
```java
@Configuration
public class SearchDescriptorConfiguration {
  @Bean
  public SearchDescriptor<Person> personSearchDescriptor(DataSearchContext dataSearchContext) {
    return new SearchDescriptorBuilder<>(Person.class)
            .entityJoinHandlers(new MyEntityJoinHandler(dataSearchContext))
            .build();
  }
}
```

#### Migrate from `FetchingEagerEntityJoinHandler` to `JpaFetchingEagerEntityJoinHandler`
The specific JPA implementation `com.weedow.spring.data.search.join.handler.FetchingEagerEntityJoinHandler` has been replaced by `com.weedow.spring.data.search.jpa.join.handler.JpaFetchingEagerEntityJoinHandler`.\
Update the `SearchDescriptor` declaration as the following:
```java
@Configuration
public class SearchDescriptorConfiguration {
  @Bean
  public SearchDescriptor<Person> personSearchDescriptor(DataSearchContext dataSearchContext) {
    return new SearchDescriptorBuilder<>(Person.class)
            .entityJoinHandlers(new JpaFetchingEagerEntityJoinHandler(dataSearchContext))
            .build();
  }
}
```