package com.weedow.spring.data.search.querydsl.querytype

import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.*
import com.weedow.spring.data.search.context.DataSearchContext
import java.util.*

enum class ElementType(
    val pathClass: Class<out Path<*>>,
    private val supportFunction: (Class<*>, dataSearchContext: DataSearchContext) -> Boolean,
) {
    BOOLEAN(BooleanPath::class.java, { clazz, _ -> Boolean::class.javaObjectType.isAssignableFrom(clazz.kotlin.javaObjectType) }),
    STRING(StringPath::class.java, { clazz, _ -> String::class.java.isAssignableFrom(clazz) }),
    NUMBER(NumberPath::class.java, { clazz, _ -> Number::class.java.isAssignableFrom(clazz) }),
    DATE(DatePath::class.java, { clazz, _ -> DATE_TYPE.contains(clazz.name) }),
    DATETIME(DateTimePath::class.java, { clazz, _ -> DATETIME_TYPE.contains(clazz.name) }),
    TIME(TimePath::class.java, { clazz, _ -> TIME_TYPE.contains(clazz.name) }),
    ENUM(EnumPath::class.java, { clazz, _ -> Enum::class.java.isAssignableFrom(clazz) }),
    ARRAY(ArrayPath::class.java, { clazz, _ -> clazz.isArray }),
    LIST(ListPath::class.java, { clazz, _ -> List::class.java.isAssignableFrom(clazz) }),
    SET(SetPath::class.java, { clazz, _ -> Set::class.java.isAssignableFrom(clazz) }),
    COLLECTION(CollectionPath::class.java, { clazz, _ -> Collection::class.java.isAssignableFrom(clazz) }),
    MAP(MapPath::class.java, { clazz, _ -> Map::class.java.isAssignableFrom(clazz) }),
    ENTITY(QEntityImpl::class.java, { clazz, context -> context.isEntity(clazz) }),
    COMPARABLE(ComparablePath::class.java, { clazz, _ -> Comparable::class.java.isAssignableFrom(clazz) }),
    SIMPLE(SimplePath::class.java, { _, _ -> true }),

    // Special Element Types to handle joins with Map key or Map value when they reference an Entity

    MAP_KEY(QEntityImpl::class.java, { _, _ -> false }),
    MAP_VALUE(QEntityImpl::class.java, { _, _ -> false });

    companion object {
        private val DATE_TYPE = listOf(
            java.sql.Date::class.java.name,
            "java.time.LocalDate",
            "org.joda.time.LocalDate"
        )

        private val DATETIME_TYPE = listOf(
            Calendar::class.java.name,
            Date::class.java.name,
            java.sql.Timestamp::class.java.name,
            "java.time.Instant",
            "java.time.LocalDateTime",
            "java.time.OffsetDateTime",
            "java.time.ZonedDateTime",
            "org.joda.time.Instant",
            "org.joda.time.DateTime",
            "org.joda.time.LocalDateTime",
            "org.joda.time.DateMidnight"
        )

        private val TIME_TYPE = listOf(
            java.sql.Time::class.java.name,
            "java.time.LocalTime",
            "java.time.OffsetTime",
            "org.joda.time.LocalTime"
        )

        fun get(clazz: Class<*>, dataSearchContext: DataSearchContext): ElementType {
            values().forEach { elementType ->
                if (elementType.supports(clazz, dataSearchContext)) {
                    return elementType
                }
            }
            return SIMPLE
        }
    }

    private fun supports(clazz: Class<*>, dataSearchContext: DataSearchContext): Boolean {
        return this.supportFunction(clazz, dataSearchContext)
    }

}