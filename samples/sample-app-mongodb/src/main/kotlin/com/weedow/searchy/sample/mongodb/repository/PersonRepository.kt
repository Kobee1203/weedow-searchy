package com.weedow.searchy.sample.mongodb.repository

import com.weedow.searchy.repository.SearchyBaseRepository
import com.weedow.searchy.sample.mongodb.dto.PersonDto
import com.weedow.searchy.sample.mongodb.model.Address
import com.weedow.searchy.sample.mongodb.model.Person
import com.weedow.searchy.sample.mongodb.model.Sex
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.data.domain.*
import org.springframework.data.geo.*
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.repository.*
import org.springframework.data.repository.query.Param
import org.springframework.lang.Nullable
import java.time.OffsetDateTime
import java.util.*
import java.util.regex.Pattern
import java.util.stream.Stream

@Suppress("UNUSED")
interface PersonRepository : MongoRepository<Person, ObjectId>/*, SearchyBaseRepository<Person, ObjectId>*/ {
    /**
     * Returns all [Person]s with the given lastName.
     *
     * @param lastName
     * @return
     */
    fun findByLastName(lastName: String): List<Person>
    fun findByLastNameStartsWith(prefix: String): List<Person>
    fun findByLastNameEndsWith(postfix: String): List<Person>

    /**
     * Returns all [Person]s with the given lastName ordered by their firstName.
     *
     * @param lastName
     * @return
     */
    fun findByLastNameOrderByFirstNameAsc(lastName: String): List<Person>

    /**
     * Returns the [Person]s with the given firstName. Uses [Query] annotation to define the query to be
     * executed.
     *
     * @param firstName
     * @return
     */
    @Query(value = "{ 'firstName' : ?0 }", fields = "{ 'firstName': 1, 'lastName': 1}")
    fun findByThePersonsFirstName(firstName: String): List<Person>

    // DATAMONGO-871
    @Query(value = "{ 'firstName' : ?0 }")
    fun findByThePersonsFirstNameAsArray(firstName: String): Array<Person>

    /**
     * Returns all [Person]s with a firstName matching the given one (*-wildcard supported).
     *
     * @param firstName
     * @return
     */
    fun findByFirstNameLike(@Nullable firstName: String): List<Person>
    fun findByFirstNameNotContains(firstName: String): List<Person>

    /**
     * Returns all [Person]s with a firstName not matching the given one (*-wildcard supported).
     *
     * @param firstName
     * @return
     */
    fun findByFirstNameNotLike(firstName: String): List<Person>
    fun findByFirstNameLikeOrderByLastNameAsc(firstName: String, sort: Sort): List<Person>
    fun findByNickNamesContains(nickNames: List<String>): List<Person>
    fun findByNickNamesNotContains(nickNames: List<String>): List<Person>

    @Query("{'age' : { '\$lt' : ?0 } }")
    fun findByAgeLessThan(age: Int, sort: Sort): List<Person>

    /**
     * Returns a page of [Person]s with a lastName mathing the given one (*-wildcards supported).
     *
     * @param lastName
     * @param pageable
     * @return
     */
    fun findByLastNameLike(lastName: String, pageable: Pageable): Page<Person>

    @Query("{ 'lastName' : { '\$regex' : '?0', '\$options' : 'i'}}")
    fun findByLastNameLikeWithPageable(lastName: String, pageable: Pageable): Page<Person>

    /**
     * Returns all [Person]s with a firstName contained in the given varargs.
     *
     * @param firstNames
     * @return
     */
    fun findByFirstNameIn(vararg firstNames: String): List<Person>

    /**
     * Returns all [Person]s with a firstName not contained in the given collection.
     *
     * @param firstNames
     * @return
     */
    fun findByFirstNameNotIn(firstNames: Collection<String>): List<Person>
    fun findByFirstNameAndLastName(firstName: String, lastName: String): List<Person>

    /**
     * Returns all [Person]s with an age between the two given values.
     *
     * @param from
     * @param to
     * @return
     */
    fun findByAgeBetween(from: Int, to: Int): List<Person>

    /**
     * Returns all [Person]s with the given [Address].
     *
     * @param address
     * @return
     */
    fun findByAddressEntities(address: Address): List<Person>
    fun findByAddressEntitiesZipCode(zipCode: String): List<Person>
    fun findByLastNameLikeAndAgeBetween(lastName: String, from: Int, to: Int): List<Person>
    fun findByAgeOrLastNameLikeAndFirstNameLike(age: Int, lastName: String, firstName: String): List<Person>
    fun findByLocationNear(point: Point): List<Person>
    fun findByLocationWithin(circle: Circle): List<Person>
    fun findByLocationWithin(box: Box): List<Person>
    fun findByLocationWithin(polygon: Polygon): List<Person>
    fun findBySex(sex: Sex): List<Person>
    fun findBySex(sex: Sex, pageable: Pageable): List<Person>
    fun findByLocationNear(point: Point?, maxDistance: Distance): GeoResults<Person>

    // DATAMONGO-1110
    fun findPersonByLocationNear(point: Point, distance: Range<Distance>): GeoResults<Person>
    fun findByLocationNear(point: Point, maxDistance: Distance, pageable: Pageable): GeoPage<Person>

    // DATAMONGO-425
    fun findByCreatedOnLessThan(date: OffsetDateTime): List<Person>

    // DATAMONGO-425
    fun findByCreatedOnGreaterThan(date: OffsetDateTime): List<Person>

    // DATAMONGO-425
    @Query("{ 'createdOn' : { '\$lt' : ?0 }}")
    fun findByCreatedOnLessThanManually(date: OffsetDateTime): List<Person>

    // DATAMONGO-427
    fun findByCreatedOnBefore(date: OffsetDateTime): List<Person>

    // DATAMONGO-427
    fun findByCreatedOnAfter(date: OffsetDateTime): List<Person>

    // DATAMONGO-472
    fun findByLastNameNot(lastName: String): List<Person>

    // DATAMONGO-636
    fun countByLastName(lastName: String): Long

    // DATAMONGO-636
    fun countByFirstName(firstName: String): Int

    // DATAMONGO-636
    @Query(value = "{ 'lastName' : ?0 }", count = true)
    fun someCountQuery(lastName: String): Long

    // DATAMONGO-1454
    fun existsByFirstName(firstName: String): Boolean

    // DATAMONGO-1454
    @ExistsQuery(value = "{ 'lastName' : ?0 }")
    fun someExistQuery(lastName: String): Boolean

    // DATAMONGO-770
    fun findByFirstNameIgnoreCase(@Nullable firstName: String): List<Person>

    // DATAMONGO-770
    fun findByFirstNameNotIgnoreCase(firstName: String): List<Person>

    // DATAMONGO-770
    fun findByFirstNameStartingWithIgnoreCase(firstName: String): List<Person>

    // DATAMONGO-770
    fun findByFirstNameEndingWithIgnoreCase(firstName: String): List<Person>

    // DATAMONGO-770
    fun findByFirstNameContainingIgnoreCase(firstName: String): List<Person>

    // DATAMONGO-870
    fun findByAgeGreaterThan(age: Int, pageable: Pageable): Slice<Person>

    // DATAMONGO-821
    @Query("{ creator : { \$exists : true } }")
    fun findByHavingCreator(page: Pageable): Page<Person>

    // DATAMONGO-566
    fun deleteByLastName(lastName: String): List<Person>

    // DATAMONGO-566
    fun deletePersonByLastName(lastName: String): Long?

    // DATAMONGO-1997
    fun deleteOptionalByLastName(lastName: String): Optional<Person>

    // DATAMONGO-566
    @Query(value = "{ 'lastName' : ?0 }", delete = true)
    fun removeByLastNameUsingAnnotatedQuery(lastName: String): List<Person>

    // DATAMONGO-566
    @Query(value = "{ 'lastName' : ?0 }", delete = true)
    fun removePersonByLastNameUsingAnnotatedQuery(lastName: String): Long?

    // DATAMONGO-893
    fun findByAddressEntitiesIn(address: List<Address>, page: Pageable): Page<Person>

    // DATAMONGO-745
    @Query("{firstName:{\$in:?0}, lastName:?1}")
    fun findByCustomQueryfirstNamesAndlastName(firstNames: List<String>, lastName: String, page: Pageable): Page<Person>

    // DATAMONGO-745
    @Query("{lastName:?0, 'address.street':{\$in:?1}}")
    fun findByCustomQuerylastNameAndAddressStreetInList(
        lastName: String, streetNames: List<String>,
        page: Pageable?
    ): Page<Person>

    // DATAMONGO-950
    fun findTop3ByLastNameStartingWith(lastName: String): List<Person>

    // DATAMONGO-950
    fun findTop3ByLastNameStartingWith(lastName: String, pageRequest: Pageable): Page<Person>

    // DATAMONGO-1865
    fun findFirstBy(): Person // limits to 1 result if more, just return the first one

    // DATAMONGO-1865
    fun findPersonByLastNameLike(firstName: String): Person // single person, error if more than one

    // DATAMONGO-1865
    fun findOptionalPersonByLastNameLike(firstName: String): Optional<Person> // optional still, error when more than one

    // DATAMONGO-1030
    fun findSummaryByLastName(lastName: String): PersonDto

    @Query("{ ?0 : ?1 }")
    fun findByKeyValue(key: String, value: String): List<Person>

    // DATAMONGO-1165
    @Query("{ firstName : { \$in : ?0 }}")
    fun findByCustomQueryWithStreamingCursorByFirstNames(firstNames: List<String>): Stream<Person>

    // DATAMONGO-990
    @Query("{ firstName : ?#{[0]}}")
    fun findWithSpelByFirstNameForSpELExpressionWithParameterIndexOnly(firstName: String): List<Person>

    // DATAMONGO-990
    @Query("{ firstName : ?#{[0]}, email: ?#{principal.email} }")
    fun findWithSpelByFirstNameAndCurrentUserWithCustomQuery(firstName: String): List<Person>

    // DATAMONGO-990
    @Query("{ firstName : :#{#firstName}}")
    fun findWithSpelByFirstNameForSpELExpressionWithParameterVariableOnly(@Param("firstName") firstName: String): List<Person>

    // DATAMONGO-1911
    @Query("{ uniqueId: ?0}")
    fun findByUniqueId(uniqueId: UUID): Person

    /**
     * Returns the count of [Person] with the given firstName. Uses [CountQuery] annotation to define the
     * query to be executed.
     *
     * @param firstName
     * @return
     */
    @CountQuery("{ 'firstName' : ?0 }")
    fun  // DATAMONGO-1539
            countByThePersonsFirstName(firstName: String): Long

    /**
     * Deletes [Person] entities with the given firstName. Uses [DeleteQuery] annotation to define the query
     * to be executed.
     *
     * @param firstName
     */
    @DeleteQuery("{ 'firstName' : ?0 }")
    fun  // DATAMONGO-1539
            deleteByThePersonsFirstName(firstName: String)

    @Query(sort = "{ age : -1 }")
    fun findByAgeGreaterThan(age: Int): List<Person>

    @Query(sort = "{ age : -1 }")
    fun findByAgeGreaterThan(age: Int, sort: Sort): List<Person>
    fun findByFirstNameRegex(pattern: Pattern): List<Person>

    @Query(value = "{ 'id' : ?0 }", fields = "{ 'fans': { '\$slice': [ ?1, ?2 ] } }")
    fun findWithSliceInProjection(id: String, skip: Int, limit: Int): Person

    @Query(value = "{ 'shippingAddresses' : { '\$elemMatch' : { 'city' : { '\$eq' : 'lnz' } } } }", fields = "{ 'shippingAddresses.$': ?0 }")
    fun findWithArrayPositionInProjection(position: Int): Person

    @Query(value = "{ 'fans' : { '\$elemMatch' : { '\$ref' : 'user' } } }", fields = "{ 'fans.$': ?0 }")
    fun findWithArrayPositionInProjectionWithDbRef(position: Int): Person

    @Aggregation("{ '\$project': { '_id' : '\$lastName' } }")
    fun findAlllastNames(): List<String>

    @Aggregation("{ '\$group': { '_id' : '\$lastName', names : { \$addToSet : '$?0' } } }")
    fun groupByLastNameAnd(property: String): List<PersonAggregate>

    @Aggregation("{ '\$group': { '_id' : '\$lastName', names : { \$addToSet : '$?0' } } }")
    fun groupByLastNameAnd(property: String, sort: Sort): List<PersonAggregate>

    @Aggregation("{ '\$group': { '_id' : '\$lastName', names : { \$addToSet : '$?0' } } }")
    fun groupByLastNameAnd(property: String, page: Pageable): List<PersonAggregate>

    @Aggregation(pipeline = ["{ '\$group' : { '_id' : null, 'total' : { \$sum: '\$weight' } } }"])
    fun sumWeight(): Int

    @Aggregation(pipeline = ["{ '\$group' : { '_id' : null, 'total' : { \$sum: '\$weight' } } }"])
    fun sumAgeAndReturnAggregationResultWrapper(): AggregationResults<Document>

    @Query(value = "{_id:?0}")
    fun findDocumentById(id: String): Optional<Document>

    @Query(
        value = "{ 'firstName' : ?0, 'lastName' : ?1, 'email' : ?2 , 'age' : ?3, "
                + "'createdAt' : ?5, 'nickNames' : ?6, 'address.street' : ?7, 'address.zipCode' : ?8, " //
                + "'address.city' : ?9, 'uniqueId' : ?10, 'credentials.username' : ?11, 'credentials.password' : ?12 }"
    )
    fun findPersonByManyArguments(
        firstName: String, lastName: String, email: String, age: Int?,
        createdAt: Date?, nickNames: List<String>, street: String, zipCode: String,  //
        city: String, uniqueId: UUID?, username: String, password: String
    ): Person
}