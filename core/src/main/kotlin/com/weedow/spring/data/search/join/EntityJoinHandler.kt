package com.weedow.spring.data.search.join

interface EntityJoinHandler<T> {

    fun supports(fieldJoinInfo: FieldJoinInfo<T>): Boolean

    fun handle(fieldJoinInfo: FieldJoinInfo<T>): FieldJoin
}