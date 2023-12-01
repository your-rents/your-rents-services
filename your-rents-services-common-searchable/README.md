# Searchable

This project adds the possibility to use a `Searchable` parameter into a method of a Spring `@Controller` or a `@RestController` class to automatically add a search functionality to the method.

The approach is similar to the one used by [Spring Data Commons](https://docs.spring.io/spring-data/commons/reference/repositories/core-extensions.html#core.web.basic.paging-and-sorting) for the `Pageable` and `Sort` parameters, supporting pagination and sorting.

For example:

```java
package com.benfante.examples.searchable.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.benfante.examples.searchable.model.Person;
import com.benfante.examples.searchable.service.PeopleService;
import com.yourrents.services.common.searchable.Searchable;

@RestController
@RequestMapping("/ex1/people")
public class PeopleControllerEx1 {

    @Autowired
    private PeopleService peopleService;
    
    @GetMapping
    public List<Person> getPeople(Searchable searchable) {
        return peopleService.searchPeople(searchable);
    }
}
```

Now you can send a GET request to `/ex1/people` with one or more of query parameters in the form:

```properties
filter.<field>.value
```

where `<field>` is the name of a field you want to use for filtering the result. For example:

```properties
filter.firstName.value
```

For example, using cURL:

```bash
curl  -X GET \
  'http://localhost:8080/ex1/people?filter.firstName.value=John' \
  --header 'Accept: */*'
```

The resulting `Searchable` object will contain a list of `SearchCondition` objects, one for each field you used in the query parameters:

```text
FilterCriteria [
    conditions=[
        FilterCondition [field=firstName, operator=containsIgnoreCase, value=John]
    ],
    combinator=AND]
```

`FilterCriteria` and `FilterCondition` are the current default implementations of `Searchable` and `SearchCondition` interfaces.

Of course the use of the `Searchable` object for obtaining the desired result is in charge of the application logic of your application (in the example above, the `PeopleService` class).

By default the `<field>` part of the parameter is used for the `field` property of the `FilterCondition`, the `containsIgnoreCase` operator is used for the `operator` property and the value of the parameter is used for the `value` property.

You can pass and customize these parts passing three separate parameters:

```properties
filter.k.field
filter.k.operator
filter.k.value
```

where `k` is a key you can freely choose.

So the following CURL request will produce the same result as the previous one:

```bash
curl  -X GET \
  'http://localhost:8080/ex1/people?filter.k.field=firstName&filter.k.operator=containsIgnoreCase&filter.k.value=John' \
  --header 'Accept: */*'
```

If you are using [springdoc-openapi/swagger-ui](https://springdoc.org) you can instruct the Swagger UI to show the `Searchable` object as a set of parameters of the method. Just add the `@ParameterObject` annotation to the method parameter:

```java
@GetMapping
public List<Person> getPeople(@ParameterObject Searchable searchable) {
```

The Swagger UI will show the `Searchable` object as a set of parameters, including the one for the condition combinator (AND or OR):

![Swagger with generated parameters](https://github.com/your-rents/your-rents-services/assets/134066/8e70bf87-7cd5-4d86-94f2-60f2480f862e)

Look at [this project](https://github.com/benfante/searchable-example) for a complete example.

## Project configuration

For using `Searchable` you need to add the following dependency to your project:

```xml
<dependency>
    <groupId>com.your-rents.services</groupId>
    <artifactId>your-rents-services-common-searchable</artifactId>
    <version>0.0.1</version>
</dependency>
```

Then add the following configuration to your Spring Boot application:

```java
package com.benfante.examples.searchable;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yourrents.services.common.searchable.config.SearchableArgumentResolverConfigurer;

@Configuration
public class SearchableConfiguration {
    
    @Bean
    public SearchableArgumentResolverConfigurer searchableArgumentResolverConfigurer() {
        return new SearchableArgumentResolverConfigurer();
    }

}
```

If you like to use the springdoc-openapi/swagger-ui integration, you can add the following configuration:

```java
package com.benfante.examples.searchable;

import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.yourrents.services.common.searchable.springdoc.SearchableOpenAPIConverter;
import com.yourrents.services.common.searchable.springdoc.customizer.SearchableOperationCustomizer;

@Configuration
public class SpringdocConfiguration {

    @Bean
    SearchableOpenAPIConverter searchableOpenAPIConverter(ObjectMapperProvider objectMapperProvider) {
        return new SearchableOpenAPIConverter(objectMapperProvider);
    }

    @Bean
    SearchableOperationCustomizer searchableOperationCustomizer() {
        return new SearchableOperationCustomizer();
    }
    
}
```

## Customization

You can customize various aspects of the `Searchable` object generation, and the way it is supported in the Swagger UI.

Mostly of the customizations are done by annotating the `Searchable` parameter with the `@SearchableDefault` annotation.

### Explicitly expected fields

You can specify the list of fields you want to support in the `Searchable` object. For example:

```java
@GetMapping
public List<Person> getPeople(
        @ParameterObject
        @SearchableDefault(supportedFields = {
            @SearchableField("firstName"),
            @SearchableField("lastName"),
            @SearchableField("uuid") }) Searchable searchable) {
    return peopleService.searchPeople(searchable);
}
```

In the Swagger UI you will see a set of filter params for each specified field:

![Swagger UI with supported fields](https://github.com/your-rents/your-rents-services/assets/134066/6c6d88a9-7d7a-481a-aaf9-1b938e05dcdf)

You can still pass other fields in the query parameters. They will not be ignored.

### Field value type

By default the value of the field is an object of type `String`. You can specify a different type using the `type` property of the `@SearchableField` annotation. For example:

```java
@GetMapping
public List<Person> getPeople(
        @ParameterObject
        @SearchableDefault(supportedFields = {
            @SearchableField("firstName"),
            @SearchableField("lastName"),
            @SearchableField(name = "uuid", type = UUID.class) }) Searchable searchable) {
    return peopleService.searchPeople(searchable);
}
```

The value of the `uuid` field will be converted to a `UUID` object. Moreover, the default operator for the field will be `eq` (equals).

### Changing the default operator

By default the default operator for a field of type `String` is `containsIgnoreCase`. For all other types the default operator is `eq`.

You can change the default operator for a type using the `defaultOperators` property of the `@SearchableDefault` annotation. For example:

```java
@GetMapping
public List<Person> getPeople(
        @ParameterObject
        @SearchableDefault(defaultOperators = {
            @OperatorDefault(type = String.class, operator = "eq")
        }) Searchable searchable) {
    return peopleService.searchPeople(searchable);
}
```

### Passing a different combinator

The business logic of your application will combine the conditions of a `Searchable` using the `combinator` property of the `Searchable` object.

At present it simply returns an enumeration value of the `EnumCombinator` enumeration, which has two values: `AND` and `OR`.

You can choose a combinator value passing the `filter.config.combinator` query parameter. For example:

```bash
curl  -X GET \
  'http://localhost:8080/ex1/people?filter.config.combinator=OR&filter.firstName.value=John&filter.lastName.value=Smith' \
  --header 'Accept: */*'
```

The reserved `config` key is used for passing configuration parameters, for example the `combinator` parameter.

You can change the name of the reserved key using the `configKey` property of the `@SearchableDefault` annotation. For example:

```java
@GetMapping
public List<Person> getPeople(
        @ParameterObject
        @SearchableDefault(configKey = "customConfig") Searchable searchable) {
    return peopleService.searchPeople(searchable);
}
```

Now you must use the `filter.customConfig.combinator` query parameter for passing the combinator:

```bash
curl  -X GET \
  'http://localhost:8080/ex4/people?filter.customConfig.combinator=OR&filter.firstName.value=John&filter.lastName.value=Smith' \
  --header 'Accept: */*'
```

### Changing the prefix

By default the prefix for the query parameters is `filter`. You can change it using the `prefix` property of the `@SearchableDefault` annotation. For example:

```java
@GetMapping
public List<Person> getPeople(
        @ParameterObject
        @SearchableDefault(prefix = "customFilter") Searchable searchable) {
    return peopleService.searchPeople(searchable);
}
```

Now you must use query parameters with `customFilter.` prefix. For example:

```bash
curl  -X GET \
  'http://localhost:8080/ex5/people?customFilter.firstName.value=John&customFilter.lastName.value=Smith' \
  --header 'Accept: */*'
```

### Repeating parameters in Swagger UI

By default the Swagger UI shows only one set of parameters for the `Searchable` object. You can change this behavior using the `@SearchableDefault` annotation with the `repeatingParams` property. For example:

```java
@GetMapping
public List<Person> getPeople(
        @ParameterObject
        @SearchableDefault(repeatDefault = 2) Searchable searchable) {
    return peopleService.searchPeople(searchable);
}
```

Now the Swagger UI will show two sets of parameters for the `Searchable` object:

You can also controll it individually for each supported field using the `@SearchableField` annotation. For example:

```java
@GetMapping
public List<Person> getPeople(
        @ParameterObject
        @SearchableDefault(supportedFields = {
            @SearchableField(name = "firstName", repeat = 2),
            @SearchableField(name = "lastName", repeat = 3)}) Searchable searchable) {
    return peopleService.searchPeople(searchable);
}
```

### Hide the Searchable parameter in Swagger UI

By default the Swagger UI shows the `Searchable` parameter in the list of parameters. You can hide it using the `@SearchableDefault` annotation with the `hidden` property. For example:

```java
@GetMapping
public List<Person> getPeople(
        @ParameterObject
        @SearchableDefault(hidden = true) Searchable searchable) {
    return peopleService.searchPeople(searchable);
}
```
