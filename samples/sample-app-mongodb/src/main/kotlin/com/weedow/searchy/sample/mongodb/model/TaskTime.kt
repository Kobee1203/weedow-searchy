package com.weedow.searchy.sample.mongodb.model

import com.weedow.searchy.mongodb.domain.MongoPersistable
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
class TaskTime(
    val time: LocalDateTime,

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    var location: Point? = null
) : MongoPersistable<String>() {

    private constructor(id: String) : this(LocalDateTime.now()) {
        this.setId(id)
    }

    companion object {
        @JvmStatic
        fun ref(id: String) = TaskTime(id)
    }

}
