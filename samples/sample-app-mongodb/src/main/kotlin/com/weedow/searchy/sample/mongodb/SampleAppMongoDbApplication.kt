package com.weedow.searchy.sample.mongodb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SampleAppMongoDbApplication

fun main(args: Array<String>) {
	runApplication<SampleAppMongoDbApplication>(*args)
}
