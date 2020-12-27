package com.weedow.spring.data.search.querydsl.querytype

import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.*
import com.weedow.spring.data.search.context.DataSearchContext
import java.time.*
import java.util.*
import kotlin.reflect.KClass

/**
 * Enum of element types
 *
 * @param pathClass [Path] Class related to the ElementType
 * @param supportFunction Function to check if a given class is supported by the current ElementType
 */
enum class ElementType(
    val pathClass: Class<out Path<*>>,
    private val supportFunction: (Class<*>, dataSearchContext: DataSearchContext) -> Boolean
) {
    BOOLEAN(BooleanPath::class.java, { clazz, _ -> isAssignableFrom(Boolean::class, clazz) }),
    STRING(StringPath::class.java, { clazz, _ -> isAssignableFrom(String::class, clazz) }),
    NUMBER(NumberPath::class.java, { clazz, _ -> isAssignableFrom(Number::class, clazz) }),
    DATE(DatePath::class.java, { clazz, _ -> DATE_TYPE.contains(clazz.name) }),
    DATETIME(DateTimePath::class.java, { clazz, _ -> DATETIME_TYPE.contains(clazz.name) }),
    TIME(TimePath::class.java, { clazz, _ -> TIME_TYPE.contains(clazz.name) }),
    ENUM(EnumPath::class.java, { clazz, _ -> isAssignableFrom(Enum::class, clazz) }),
    ARRAY(ArrayPath::class.java, { clazz, _ -> clazz.isArray }),
    LIST(ListPath::class.java, { clazz, _ -> isAssignableFrom(List::class, clazz) }),
    SET(SetPath::class.java, { clazz, _ -> isAssignableFrom(Set::class, clazz) }),
    COLLECTION(CollectionPath::class.java, { clazz, _ -> isAssignableFrom(Collection::class, clazz) }),
    MAP(MapPath::class.java, { clazz, _ -> isAssignableFrom(Map::class, clazz) }),
    ENTITY(QEntityImpl::class.java, { clazz, context -> context.isEntity(clazz) }),
    COMPARABLE(ComparablePath::class.java, { clazz, _ -> isAssignableFrom(Comparable::class, clazz) }),
    SIMPLE(SimplePath::class.java, { _, _ -> true }),

    // Special Element Types to handle joins with Map key or Map value when they reference an Entity

    MAP_KEY(QEntityImpl::class.java, { _, _ -> false }),
    MAP_VALUE(QEntityImpl::class.java, { _, _ -> false });

    companion object {
        private val DATE_TYPE = listOf(
            java.sql.Date::class.java.name,
            LocalDate::class.java.name,
            "org.joda.time.LocalDate"
        )
        private val DATETIME_TYPE = listOf(
            Calendar::class.java.name,
            Date::class.java.name,
            java.sql.Timestamp::class.java.name,
            Instant::class.java.name,
            LocalDateTime::class.java.name,
            OffsetDateTime::class.java.name,
            ZonedDateTime::class.java.name,
            "org.joda.time.Instant",
            "org.joda.time.DateTime",
            "org.joda.time.LocalDateTime",
            "org.joda.time.DateMidnight"
        )

        private val TIME_TYPE = listOf(
            java.sql.Time::class.java.name,
            LocalTime::class.java.name,
            OffsetTime::class.java.name,
            "org.joda.time.LocalTime"
        )

        private fun isAssignableFrom(type: KClass<*>, clazz: Class<*>) = toJavaType(type).isAssignableFrom(toJavaType(clazz))
        private fun toJavaType(clazz: KClass<*>) = clazz.javaObjectType
        private fun toJavaType(clazz: Class<*>) = clazz.kotlin.javaObjectType

        fun get(clazz: Class<*>, dataSearchContext: DataSearchContext): ElementType {
            return values().first { elementType -> elementType.supports(clazz, dataSearchContext) }
        }
    }

    private fun supports(clazz: Class<*>, dataSearchContext: DataSearchContext): Boolean {
        return this.supportFunction(clazz, dataSearchContext)
    }

}