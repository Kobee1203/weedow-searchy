package com.weedow.spring.data.search.common.model

import javax.persistence.*

@Entity
class Feature(
    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = false)
    val description: String,

    @ElementCollection
    @CollectionTable(
        name = "metadata_mapping",
        joinColumns = [JoinColumn(name = "feature_id", referencedColumnName = "id")]
    )
    @MapKeyColumn(name = "metadata_name")
    @Column(name = "value")
    val metadata: Map<String, String>? = null

) : JpaPersistable<Long>()