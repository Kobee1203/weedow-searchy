package com.weedow.spring.data.search.validation.validator

import com.weedow.spring.data.search.expression.FieldExpression
import com.weedow.spring.data.search.validation.DataSearchValidator

/**
 * Base Class of [DataSearchValidator] to validate [FieldExpressions][FieldExpression] according to their [field path][FieldExpression.fieldInfo].
 *
 * **Example:**
 *
 * Let's assume the following model:
 * ```
 * @Entity
 * class Person(
 *     ...
 *     @Column(unique = true, length = 100)
 *     val email: String? = null,
 *     ...
 *     @OneToOne(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true)
 *     @JsonIgnoreProperties("person")
 *     val jobEntity: Job? = null,
 *     ...
 * )
 *
 * @Entity
 * class Job(
 *     ...
 *     @Column(unique = true, length = 100)
 *     val email: String? = null,
 *     ...
 * )
 * ```
 *
 * Let's assume a Validator to validate Person email and Job email.
 *
 * This validator should be instantiated with the different [field paths][FieldExpression.fieldInfo], and added to a [SearchDescriptor][com.weedow.spring.data.search.descriptor.SearchDescriptor]:
 * ```
 * fun personSearchDescriptor(): SearchDescriptor<Person> = SearchDescriptorBuilder.builder<Person>()
 *          .validators(EmailValidator("email", "job.email"))
 *          .build()
 * ```
 */
abstract class AbstractFieldPathValidator(
        internal vararg val fieldPaths: String
) : DataSearchValidator {

    override fun supports(fieldExpression: FieldExpression): Boolean {
        return fieldPaths.contains(fieldExpression.fieldInfo.fieldPath)
    }

}