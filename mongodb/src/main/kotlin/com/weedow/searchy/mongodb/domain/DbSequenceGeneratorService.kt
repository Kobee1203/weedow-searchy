package com.weedow.searchy.mongodb.domain


interface DbSequenceGeneratorService {

    fun getNextSequence(seqName: String): Long

}