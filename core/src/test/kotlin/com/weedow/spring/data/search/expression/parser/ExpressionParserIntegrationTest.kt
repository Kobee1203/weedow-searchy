package com.weedow.spring.data.search.expression.parser

import com.weedow.spring.data.search.common.model.Job
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.common.model.Vehicle
import com.weedow.spring.data.search.expression.*
import com.weedow.spring.data.search.utils.NullValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.stream.Stream

@SpringBootTest
internal class ExpressionParserIntegrationTest {

    @Autowired
    private lateinit var expressionParser: ExpressionParser

    companion object {
        @JvmStatic
        @Suppress("unused")
        private fun equals_parameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("jobEntity.active=true", "jobEntity.active", "active", Job::class.java, true),
                Arguments.of("height=174", "height", "height", Person::class.java, 174.0),
                Arguments.of("firstName='John'", "firstName", "firstName", Person::class.java, "John"),
                Arguments.of("birthday='1981-03-12T10:36:00'", "birthday", "birthday", Person::class.java, LocalDateTime.of(1981, 3, 12, 10, 36, 0)),
                Arguments.of(
                    "jobEntity.hireDate='2019-09-01T09:00:00Z'",
                    "jobEntity.hireDate",
                    "hireDate",
                    Job::class.java,
                    OffsetDateTime.of(2019, 9, 1, 9, 0, 0, 0, ZoneOffset.UTC)
                )
            )
        }

        @JvmStatic
        @Suppress("unused")
        private fun not_equals_parameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("jobEntity.active!=true", "jobEntity.active", "active", Job::class.java, true),
                Arguments.of("height!=174", "height", "height", Person::class.java, 174.0),
                Arguments.of("firstName!='John'", "firstName", "firstName", Person::class.java, "John"),
                Arguments.of("birthday!='1981-03-12T10:36:00'", "birthday", "birthday", Person::class.java, LocalDateTime.of(1981, 3, 12, 10, 36, 0)),
                Arguments.of(
                    "jobEntity.hireDate!='2019-09-01T09:00:00Z'",
                    "jobEntity.hireDate",
                    "hireDate",
                    Job::class.java,
                    OffsetDateTime.of(2019, 9, 1, 9, 0, 0, 0, ZoneOffset.UTC)
                )
            )
        }

        @JvmStatic
        @Suppress("unused")
        private fun less_than_parameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("height<174", "height", "height", Person::class.java, 174.0),
                Arguments.of("birthday<'1981-03-12T10:36:00'", "birthday", "birthday", Person::class.java, LocalDateTime.of(1981, 3, 12, 10, 36, 0)),
                Arguments.of(
                    "jobEntity.hireDate<'2019-09-01T09:00:00Z'",
                    "jobEntity.hireDate",
                    "hireDate",
                    Job::class.java,
                    OffsetDateTime.of(2019, 9, 1, 9, 0, 0, 0, ZoneOffset.UTC)
                )
            )
        }

        @JvmStatic
        @Suppress("unused")
        private fun less_than_or_equals_parameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("height<=174", "height", "height", Person::class.java, 174.0),
                Arguments.of("birthday<='1981-03-12T10:36:00'", "birthday", "birthday", Person::class.java, LocalDateTime.of(1981, 3, 12, 10, 36, 0)),
                Arguments.of(
                    "jobEntity.hireDate<='2019-09-01T09:00:00Z'",
                    "jobEntity.hireDate",
                    "hireDate",
                    Job::class.java,
                    OffsetDateTime.of(2019, 9, 1, 9, 0, 0, 0, ZoneOffset.UTC)
                )
            )
        }

        @JvmStatic
        @Suppress("unused")
        private fun greater_than_parameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("height>174", "height", "height", Person::class.java, 174.0),
                Arguments.of("birthday>'1981-03-12T10:36:00'", "birthday", "birthday", Person::class.java, LocalDateTime.of(1981, 3, 12, 10, 36, 0)),
                Arguments.of(
                    "jobEntity.hireDate>'2019-09-01T09:00:00Z'",
                    "jobEntity.hireDate",
                    "hireDate",
                    Job::class.java,
                    OffsetDateTime.of(2019, 9, 1, 9, 0, 0, 0, ZoneOffset.UTC)
                )
            )
        }

        @JvmStatic
        @Suppress("unused")
        private fun greater_than_or_equals_parameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("height>=174", "height", "height", Person::class.java, 174.0),
                Arguments.of("birthday>='1981-03-12T10:36:00'", "birthday", "birthday", Person::class.java, LocalDateTime.of(1981, 3, 12, 10, 36, 0)),
                Arguments.of(
                    "jobEntity.hireDate>='2019-09-01T09:00:00Z'",
                    "jobEntity.hireDate",
                    "hireDate",
                    Job::class.java,
                    OffsetDateTime.of(2019, 9, 1, 9, 0, 0, 0, ZoneOffset.UTC)
                )
            )
        }

        @JvmStatic
        @Suppress("unused")
        private fun not_parameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("NOT (jobEntity.active=true)", "jobEntity.active", "active", Job::class.java, true, Operator.EQUALS),
                Arguments.of(
                    "NOT birthday<'1981-03-12T10:36:00'",
                    "birthday",
                    "birthday",
                    Person::class.java,
                    LocalDateTime.of(1981, 3, 12, 10, 36, 0),
                    Operator.LESS_THAN
                ),
                Arguments.of(
                    "NOT jobEntity.hireDate<='2019-09-01T09:00:00Z'",
                    "jobEntity.hireDate",
                    "hireDate",
                    Job::class.java,
                    OffsetDateTime.of(2019, 9, 1, 9, 0, 0, 0, ZoneOffset.UTC),
                    Operator.LESS_THAN_OR_EQUALS
                ),
                Arguments.of("NOT height>=174", "height", "height", Person::class.java, 174.0, Operator.GREATER_THAN_OR_EQUALS),
                Arguments.of("NOT (height>174)", "height", "height", Person::class.java, 174.0, Operator.GREATER_THAN)
            )
        }
    }

    @ParameterizedTest
    @MethodSource("equals_parameters")
    fun test_equals(query: String, fieldPath: String, fieldName: String, parentClass: Class<*>, value: Any) {
        val rootClass = Person::class.java

        val expression = expressionParser.parse(query, rootClass)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.EQUALS, FieldInfo(fieldPath, fieldName, parentClass), value))
    }

    @ParameterizedTest
    @MethodSource("not_equals_parameters")
    fun test_not_equals(query: String, fieldPath: String, fieldName: String, parentClass: Class<*>, value: Any) {
        val rootClass = Person::class.java

        val expression = expressionParser.parse(query, rootClass)

        assertThat(expression).isEqualTo(NotExpression(SimpleExpression(Operator.EQUALS, FieldInfo(fieldPath, fieldName, parentClass), value)))
    }

    @ParameterizedTest
    @MethodSource("less_than_parameters")
    fun test_less_than(query: String, fieldPath: String, fieldName: String, parentClass: Class<*>, value: Any) {
        val rootClass = Person::class.java

        val expression = expressionParser.parse(query, rootClass)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.LESS_THAN, FieldInfo(fieldPath, fieldName, parentClass), value))
    }

    @ParameterizedTest
    @MethodSource("less_than_or_equals_parameters")
    fun test_less_than_or_equals(query: String, fieldPath: String, fieldName: String, parentClass: Class<*>, value: Any) {
        val rootClass = Person::class.java

        val expression = expressionParser.parse(query, rootClass)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.LESS_THAN_OR_EQUALS, FieldInfo(fieldPath, fieldName, parentClass), value))
    }

    @ParameterizedTest
    @MethodSource("greater_than_parameters")
    fun test_greater_than(query: String, fieldPath: String, fieldName: String, parentClass: Class<*>, value: Any) {
        val rootClass = Person::class.java

        val expression = expressionParser.parse(query, rootClass)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.GREATER_THAN, FieldInfo(fieldPath, fieldName, parentClass), value))
    }

    @ParameterizedTest
    @MethodSource("greater_than_or_equals_parameters")
    fun test_greater_than_or_equals(query: String, fieldPath: String, fieldName: String, parentClass: Class<*>, value: Any) {
        val rootClass = Person::class.java

        val expression = expressionParser.parse(query, rootClass)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.GREATER_THAN_OR_EQUALS, FieldInfo(fieldPath, fieldName, parentClass), value))
    }

    @ParameterizedTest
    @MethodSource("not_parameters")
    fun test_not(query: String, fieldPath: String, fieldName: String, parentClass: Class<*>, value: Any, operator: Operator) {
        val rootClass = Person::class.java

        val expression = expressionParser.parse(query, rootClass)

        assertThat(expression).isInstanceOf(NotExpression::class.java)
        assertThat(expression)
            .extracting("expression")
            .isEqualTo(SimpleExpression(operator, FieldInfo(fieldPath, fieldName, parentClass), value))
    }

    @Test
    fun test_not_with_not_equals() {
        val rootClass = Person::class.java
        val query = "NOT (firstName!='John')"
        val fieldPath = "firstName"
        val fieldName = "firstName"
        val parentClass = Person::class.java
        val fieldValue = "John"

        val expression = expressionParser.parse(query, rootClass)

        assertThat(expression).isInstanceOf(NotExpression::class.java)
        assertThat(expression)
            .extracting("expression")
            .isInstanceOf(NotExpression::class.java)
            .extracting("expression")
            .isEqualTo(SimpleExpression(Operator.EQUALS, FieldInfo(fieldPath, fieldName, parentClass), fieldValue))
    }

    @Test
    fun test_null_comparison() {
        val rootClass = Person::class.java
        val query = "firstName IS NULL"

        val expression = expressionParser.parse(query, rootClass)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.EQUALS, FieldInfo("firstName", "firstName", rootClass), NullValue))
    }

    @Test
    fun test_not_null_comparison() {
        val rootClass = Person::class.java
        val query = "firstName IS NOT NULL"

        val expression = expressionParser.parse(query, rootClass)

        assertThat(expression).isInstanceOf(NotExpression::class.java)
        assertThat(expression)
            .extracting("expression")
            .isEqualTo(SimpleExpression(Operator.EQUALS, FieldInfo("firstName", "firstName", rootClass), NullValue))
    }

    @Test
    fun test_in_expression() {
        val rootClass = Person::class.java
        val query = "firstName IN ('John', 'Jane')"

        val expression = expressionParser.parse(query, rootClass)

        assertThat(expression).isEqualTo(SimpleExpression(Operator.IN, FieldInfo("firstName", "firstName", rootClass), listOf("John", "Jane")))
    }

    @Test
    fun test_not_in_expression() {
        val rootClass = Person::class.java
        val query = "firstName NOT IN ('John', 'Jane')"

        val expression = expressionParser.parse(query, rootClass)

        assertThat(expression).isInstanceOf(NotExpression::class.java)
        assertThat(expression)
            .extracting("expression")
            .isEqualTo(SimpleExpression(Operator.IN, FieldInfo("firstName", "firstName", rootClass), listOf("John", "Jane")))
    }

    @Test
    fun test_matches_expression() {
        val rootClass = Person::class.java
        val query = "firstName MATCHES 'Jo*'"

        val expression = expressionParser.parse(query, rootClass)

        assertThat(expression)
            .isEqualTo(SimpleExpression(Operator.MATCHES, FieldInfo("firstName", "firstName", rootClass), "Jo*"))
    }

    @Test
    fun test_imatches_expression() {
        val rootClass = Person::class.java
        val query = "firstName IMATCHES 'JO*'"

        val expression = expressionParser.parse(query, rootClass)

        assertThat(expression)
            .isEqualTo(SimpleExpression(Operator.IMATCHES, FieldInfo("firstName", "firstName", rootClass), "JO*"))
    }

    @Test
    fun test_complex_query() {
        val query = "firstName='John' AND (vehicles.brand='Porsche' OR vehicles.brand='Ferrari')"
        val rootClass = Person::class.java

        val expression = expressionParser.parse(query, rootClass)

        assertThat(expression).isInstanceOf(LogicalExpression::class.java)
        assertThat(expression).extracting("logicalOperator").isEqualTo(LogicalOperator.AND)
        assertThat(expression)
            .extracting("expressions")
            .asList()
            .containsExactly(
                SimpleExpression(Operator.EQUALS, FieldInfo("firstName", "firstName", rootClass), "John"),
                LogicalExpression(
                    LogicalOperator.OR,
                    listOf(
                        SimpleExpression(Operator.EQUALS, FieldInfo("vehicles.brand", "brand", Vehicle::class.java), "Porsche"),
                        SimpleExpression(Operator.EQUALS, FieldInfo("vehicles.brand", "brand", Vehicle::class.java), "Ferrari")
                    )
                )
            )
    }
}