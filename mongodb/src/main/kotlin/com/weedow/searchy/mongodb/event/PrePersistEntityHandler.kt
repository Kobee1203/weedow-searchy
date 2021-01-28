package com.weedow.searchy.mongodb.event

interface PrePersistEntityHandler {

    fun supports(entity: Any): Boolean

    fun handle(entity: Any)

}