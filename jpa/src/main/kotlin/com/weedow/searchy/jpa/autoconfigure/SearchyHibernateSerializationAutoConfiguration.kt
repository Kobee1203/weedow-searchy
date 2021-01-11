package com.weedow.searchy.jpa.autoconfigure

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnClass(Hibernate5Module::class)
internal class SearchyHibernateSerializationAutoConfiguration {

    /**
     * Add-on module for Jackson JSON processor which handles Hibernate (http://www.hibernate.org/) datatypes; and specifically aspects of lazy-loading.
     *
     * Can be useful when we use [com.weedow.searchy.dto.DtoMapper] while serializing the result of entities to JSON,
     * and manage lazy-loading automatically.
     *
     * To prevent the Jackson infinite recursion problem with bidirectional relationships, please use one of the following solutions:
     * - [@JsonManagedReference][com.fasterxml.jackson.annotation.JsonManagedReference] and [@JsonBackReference][com.fasterxml.jackson.annotation.JsonBackReference]
     * - [@JsonIdentityInfo][com.fasterxml.jackson.annotation.JsonIdentityInfo]
     * - [@JsonIgnoreProperties][com.fasterxml.jackson.annotation.JsonIgnoreProperties]
     * - [@JsonIgnore][com.fasterxml.jackson.annotation.JsonIgnore]
     *
     * @see com.weedow.searchy.dto.DtoMapper
     * @see com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module.Feature
     * @see <a href="https://github.com/FasterXML/jackson-datatype-hibernate">jackson-datatype-hibernate Github</a>
     * @see <a href="https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations">Jackson Annotations</a>
     */
    @Bean
    @ConditionalOnMissingBean
    fun hibernateModule(): Module {
        return Hibernate5Module()
            .enable(Hibernate5Module.Feature.FORCE_LAZY_LOADING)
            .enable(Hibernate5Module.Feature.WRITE_MISSING_ENTITIES_AS_NULL)
    }
}