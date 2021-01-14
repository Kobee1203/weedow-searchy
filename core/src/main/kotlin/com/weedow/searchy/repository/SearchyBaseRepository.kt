package com.weedow.searchy.repository

import com.weedow.searchy.query.specification.SpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import java.io.Serializable

/**
 * Searchy specific extension of [org.springframework.data.repository.Repository][org.springframework.data.repository.Repository].
 */
@NoRepositoryBean
interface SearchyBaseRepository<T, ID : Serializable> : Repository<T, ID>, SpecificationExecutor<T>