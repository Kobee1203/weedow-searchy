package com.weedow.spring.data.search.utils

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * This generic abstract class is used for obtaining full generics type information by sub-classing.
 *
 * Class is based on ideas from
 * [Jackson TypeReference](https://github.com/FasterXML/jackson-core/blob/master/src/main/java/com/fasterxml/jackson/core/type/TypeReference.java)
 *
 * Usage is by sub-classing: here is one way to instantiate reference to generic type `List<Integer>`:
 * ```
 * TypeReference ref = new TypeReference&lt;List&lt;Integer&gt;&gt;() { };
 * ```
 */
abstract class TypeReference<T> protected constructor() : Comparable<TypeReference<T>> {
    /** Parameterized type */
    val type: Type

    init {
        val superClass = javaClass.genericSuperclass
        require(superClass !is Class<*>) {  // sanity check, should never happen
            "Internal error: TypeReference constructed without actual type information"
        }

        /* 22-Dec-2008, tatu: Not sure if this case is safe -- I suspect
         *   it is possible to make it fail?
         *   But let's deal with specific
         *   case when we know an actual use case, and thereby suitable
         *   workarounds for valid case(s) and/or error to throw
         *   on invalid one(s).
         */
        type = (superClass as ParameterizedType).actualTypeArguments[0]
    }

    /**
     * The only reason we define this method (and require implementation of `Comparable`) is to prevent constructing a reference without type information.
     */
    override operator fun compareTo(other: TypeReference<T>): Int {
        // just need an implementation, not a good one... hence ^^^
        return 0
    }
}