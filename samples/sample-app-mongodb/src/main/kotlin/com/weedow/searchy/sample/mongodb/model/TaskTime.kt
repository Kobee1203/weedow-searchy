package com.weedow.searchy.sample.mongodb.model

import com.weedow.searchy.mongodb.domain.MongoPersistable
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
class TaskTime(
    val time: LocalDateTime
) : MongoPersistable<String>()
