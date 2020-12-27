package com.weedow.spring.data.search.sample.repository

import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import java.io.Serializable

/**
 * Spring Data Search specific extension of [org.springframework.data.repository.Repository][org.springframework.data.repository.Repository].
 */
@NoRepositoryBean
interface DataSearchBaseRepository<T, ID : Serializable> : Repository<T, ID>, QueryDslSpecificationExecutor<T>