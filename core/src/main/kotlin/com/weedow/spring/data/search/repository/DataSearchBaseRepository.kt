package com.weedow.spring.data.search.repository

import com.weedow.spring.data.search.query.specification.SpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import java.io.Serializable

/**
 * Spring Data Search specific extension of [org.springframework.data.repository.Repository][org.springframework.data.repository.Repository].
 */
@NoRepositoryBean
interface DataSearchBaseRepository<T, ID : Serializable> : Repository<T, ID>, SpecificationExecutor<T>