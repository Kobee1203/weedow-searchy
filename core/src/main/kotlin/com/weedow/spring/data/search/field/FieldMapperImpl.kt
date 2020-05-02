package com.weedow.spring.data.search.field

import com.weedow.spring.data.search.utils.NullValue
import com.weedow.spring.data.search.utils.klogger
import org.springframework.core.convert.ConversionService
import java.util.*
import java.util.stream.Collectors

class FieldMapperImpl(
        private val fieldPathResolver: FieldPathResolver,
        private val conversionService: ConversionService
) : FieldMapper {

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized FieldMapper: {}", this)
    }

    override fun toFieldInfos(params: Map<String, List<String>>, rootClass: Class<*>): List<FieldInfo> {
        val fieldInfos: MutableList<FieldInfo> = ArrayList()
        params.forEach { (paramName, paramValues) ->
            val fieldPathInfo = toFieldKey(rootClass, paramName)
            val fieldValues = toFieldValues(fieldPathInfo, paramValues)
            val fieldInfo = FieldInfo(fieldPathInfo.fieldPath, fieldPathInfo.parentClass, fieldPathInfo.field, fieldPathInfo.fieldClass, fieldValues)
            fieldInfos.add(fieldInfo)
        }
        return fieldInfos
    }

    private fun toFieldKey(rootClass: Class<*>, fieldPath: String): FieldPathInfo {
        return fieldPathResolver.resolveFieldPath(rootClass, fieldPath)
    }

    private fun toFieldValues(fieldPathInfo: FieldPathInfo, fieldValues: List<String>): List<*> {
        return fieldValues.stream()
                .map { fieldValue -> convert(fieldValue, fieldPathInfo.fieldClass) }
                .collect(Collectors.toList())
    }

    private fun convert(value: String, clazz: Class<*>): Any? {
        return if (!NullValue.NULL_VALUE.equals(value, ignoreCase = true)) conversionService.convert(value, clazz) else NullValue.INSTANCE
    }

}