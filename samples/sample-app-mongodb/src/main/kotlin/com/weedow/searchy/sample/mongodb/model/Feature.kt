package com.weedow.searchy.sample.mongodb.model

import com.weedow.searchy.mongodb.domain.MongoPersistable
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Feature(
    val name: String,

    val description: String,

    val metadata: Map<String, String>? = null

) : MongoPersistable<String>()