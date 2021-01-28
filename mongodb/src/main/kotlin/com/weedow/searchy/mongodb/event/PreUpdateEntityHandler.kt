package com.weedow.searchy.mongodb.event

interface PreUpdateEntityHandler {

    fun supports(entity: Any): Boolean

    fun handle(entity: Any)

}