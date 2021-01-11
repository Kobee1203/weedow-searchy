package com.weedow.searchy.validation.validator

import com.weedow.searchy.expression.FieldExpression
import com.weedow.searchy.validation.SearchyValidator

/**
 * Base Class of [SearchyValidator] to validate [FieldExpressions][FieldExpression] according to their [field path][FieldExpression.fieldInfo].
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
 * This validator should be instantiated with the different [field paths][FieldExpression.fieldInfo], and added to a [SearchyDescriptor][com.weedow.searchy.descriptor.SearchyDescriptor]:
 * ```
 * fun personSearchyDescriptor(): SearchyDescriptor<Person> = SearchyDescriptorBuilder.builder<Person>()
 *          .validators(EmailValidator("email", "job.email"))
 *          .build()
 * ```
 *
 * @param fieldPaths Field paths to validate
 */
abstract class AbstractFieldPathValidator(
    internal vararg val fieldPaths: String
) : SearchyValidator {

    override fun supports(fieldExpression: FieldExpression): Boolean {
        return fieldPaths.contains(fieldExpression.fieldInfo.fieldPath)
    }

}