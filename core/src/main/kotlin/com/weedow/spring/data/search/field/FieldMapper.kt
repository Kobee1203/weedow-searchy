package com.weedow.spring.data.search.field

interface FieldMapper {

    fun toFieldInfos(params: Map<String, List<String>>, rootClass: Class<*>): List<FieldInfo>

}