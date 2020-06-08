package com.weedow.spring.data.search.expression

enum class Operator {
    EQUALS,
    CONTAINS,
    ICONTAINS,
    LESS_THAN,
    LESS_THAN_OR_EQUALS,
    GREATER_THAN,
    GREATER_THAN_OR_EQUALS,
    // BETWEEN, // Not Necessary, we use LESS_THAN AND GREATER_THAN
    IN
}

