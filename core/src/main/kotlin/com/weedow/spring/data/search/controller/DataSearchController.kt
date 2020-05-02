package com.weedow.spring.data.search.controller

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.descriptor.SearchDescriptorService
import com.weedow.spring.data.search.dto.DtoMapper
import com.weedow.spring.data.search.exception.SearchDescriptorNotFound
import com.weedow.spring.data.search.field.FieldMapper
import com.weedow.spring.data.search.service.DataSearchService
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import java.util.stream.Collectors

@RestController
@RequestMapping("/search")
class DataSearchController(
        private val searchDescriptorService: SearchDescriptorService,
        private val fieldMapper: FieldMapper,
        private val dataSearchService: DataSearchService
) {

    @GetMapping("/{searchDescriptorId}")
    fun search(@PathVariable searchDescriptorId: String, @RequestParam params: MultiValueMap<String, String>): ResponseEntity<List<*>> {
        // Find Entity Search Descriptor
        val searchDescriptor = searchDescriptorService.getSearchDescriptor(searchDescriptorId)
                ?: throw SearchDescriptorNotFound(searchDescriptorId)

        val result = doSearch(params, searchDescriptor)

        // Map result result as defined DTO
        @Suppress("UNCHECKED_CAST")
        val dtoMapper = searchDescriptor.dtoMapper as DtoMapper<Any?, Any?>
        return ResponseEntity.ok(convertToDto(result, dtoMapper))
    }

    private fun <T> doSearch(params: MultiValueMap<String, String>, searchDescriptor: SearchDescriptor<T>): List<T> {
        // Mapping the given request parameters to the associated fields
        val fieldInfos = fieldMapper.toFieldInfos(params, searchDescriptor.entityClass)

        // Validate the given parameters with the found Search Descriptor
        // dataSearchValidator.validate(searchInfos, searchDescriptor)

        // Find entities according to field infos
        return dataSearchService.findAll(fieldInfos, searchDescriptor)
    }

    private fun <T, R> convertToDto(result: List<T>, dtoMapper: DtoMapper<T, R>): List<R> {
        return result.stream()
                .map { entity -> dtoMapper.map(entity) }
                .collect(Collectors.toList())
    }

}