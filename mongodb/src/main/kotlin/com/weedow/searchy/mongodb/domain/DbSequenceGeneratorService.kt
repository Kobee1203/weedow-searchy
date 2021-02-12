package com.weedow.searchy.mongodb.domain

/**
 * Interface to retrieve the next [Long] sequence from a sequence name.
 */
interface DbSequenceGeneratorService {

    /**
     * Get the the [Long] sequence from the given sequence name.
     *
     * @param seqName sequence name
     * @return Next [Long] sequence
     */
    fun getNextSequence(seqName: String): Long

}