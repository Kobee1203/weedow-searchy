package com.weedow.spring.data.search.querydsl.jpa.specification

import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.Templates
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.AbstractJPAQuery
import com.querydsl.jpa.impl.JPAProvider
import com.querydsl.jpa.impl.JPAQuery
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.querydsl.jpa.JpaQueryDslBuilder
import com.weedow.spring.data.search.querydsl.jpa.addMissingTemplates
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecification
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationExecutor
import org.springframework.data.jpa.repository.support.CrudMethodMetadata
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.Querydsl
import org.springframework.data.querydsl.EntityPathResolver
import javax.persistence.EntityManager

class JpaQueryDslSpecificationExecutor<T>(
        private val dataSearchContext: DataSearchContext,
        private val entityInformation: JpaEntityInformation<T, *>,
        private val entityManager: EntityManager,
        private val resolver: EntityPathResolver,
        private val metadata: CrudMethodMetadata?,
) : QueryDslSpecificationExecutor<T> {

    companion object {
        val TEMPLATES_INITIALIZED = mutableListOf<Templates>()
    }

    private val path: EntityPath<T> = resolver.createPath(entityInformation.javaType)
    private val querydsl: Querydsl = object : Querydsl(entityManager, PathBuilder(path.type, path.metadata)) {
        override fun <T> createQuery(): AbstractJPAQuery<T, JPAQuery<T>> {
            val templates = JPAProvider.getTemplates(entityManager)
            if (!TEMPLATES_INITIALIZED.contains(templates)) {
                templates.addMissingTemplates()
                TEMPLATES_INITIALIZED.add(templates)
            }
            return JPAQuery(entityManager)
        }
    }

    override fun findAll(specification: QueryDslSpecification<T>?): List<T> {
        return createQuery(specification).select(path).fetch()
    }

    /**
     * Creates a new [JPQLQuery] for the given [QueryDslSpecification].
     *
     * @param specification
     * @return the Querydsl [JPQLQuery].
     */
    protected fun createQuery(specification: QueryDslSpecification<T>?): JPQLQuery<*> {
        val query: AbstractJPAQuery<*, *> = doCreateQuery(specification)
        val metadata: CrudMethodMetadata = getRepositoryMethodMetadata() ?: return query
        val type = metadata.lockModeType
        return if (type == null) query else query.setLockMode(type)
    }

    private fun doCreateQuery(specification: QueryDslSpecification<T>?): AbstractJPAQuery<T, *> {
        var query: AbstractJPAQuery<T, *> = querydsl.createQuery(path) as AbstractJPAQuery<T, *>

        if (specification != null) {
            val predicate = specification.toPredicate(createQueryDslBuilder(query)) /*QueryDslSpecification.NO_PREDICATE*/
            if (predicate != QueryDslSpecification.NO_PREDICATE) {
                query = query.where(predicate) as AbstractJPAQuery<T, *>
            } else {
                /*
                val addressPath = Expressions.path(Address::class.java, builder, "addressEntities")
                val cityPath = Expressions.path(String::class.java, addressPath, "city")
                val pred = Expressions.predicate(Ops.EQ, cityPath, Expressions.constant("Plaisir"))
                query = query
                        .leftJoin(addressPath).fetchJoin()
                        .where(pred) as AbstractJPAQuery<T, *>
                */

                /*
                val qEntity1 = dataSearchContext.get(Person::class.java)
                val addressEntities = qEntity1.get("addressEntities") as CollectionExpression<*, *>
                val addressQEntity: QEntity<*> = dataSearchContext.get(addressEntities.getParameter(0))
                val query1 = query
                        .leftJoin(addressEntities as CollectionExpression<*, Address>, addressQEntity as QEntity<Address>).fetchJoin()
                        .where(
                                Expressions.predicate(Ops.EQ, qEntity1.get("firstName").path, Expressions.constant("John"))
                        ) as AbstractJPAQuery<T, *>
                query1.select(path).fetch()
                */

                /*
                val address = QAddress.address
                val query2 = query
                        .leftJoin(QPerson.person.addressEntities, address).fetchJoin()
                        .where(address.city.eq("Plaisr")) as AbstractJPAQuery<T, *>
                */

                // query = query.where(QPerson.person.addressEntities.any().city.eq("Plaisr")) as AbstractJPAQuery<T, *>
            }
        }

        return query
    }

    private fun createQueryDslBuilder(query: AbstractJPAQuery<T, *>) =
            JpaQueryDslBuilder(dataSearchContext, query, dataSearchContext.get(path.type))

    private fun getRepositoryMethodMetadata() = metadata

}