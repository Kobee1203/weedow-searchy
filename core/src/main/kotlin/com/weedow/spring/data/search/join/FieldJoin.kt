package com.weedow.spring.data.search.join

import com.weedow.spring.data.search.utils.EntityUtils
import javax.persistence.criteria.JoinType

data class FieldJoin(
        private val fieldJoinInfo: FieldJoinInfo<*>,
        val joinType: JoinType = JoinType.INNER,
        val fetched: Boolean = false
) {

    val parentClass: Class<*> = fieldJoinInfo.entityClass
    val fieldClass: Class<*> = EntityUtils.getFieldClass(fieldJoinInfo.field)
    var fieldName: String = fieldJoinInfo.fieldName
    val joinName: String = fieldJoinInfo.joinName

}
