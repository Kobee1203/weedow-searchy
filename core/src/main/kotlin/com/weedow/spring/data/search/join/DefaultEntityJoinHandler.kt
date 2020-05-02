package com.weedow.spring.data.search.join

class DefaultEntityJoinHandler<T> : EntityJoinHandler<T> {

    override fun supports(fieldJoinInfo: FieldJoinInfo<T>): Boolean {
        return true
    }

    override fun handle(fieldJoinInfo: FieldJoinInfo<T>): FieldJoin {
        return FieldJoin(fieldJoinInfo)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}