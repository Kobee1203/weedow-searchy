package com.weedow.searchy.sample.mongodb.repository

import com.weedow.searchy.sample.mongodb.model.Vehicle
import org.springframework.data.mongodb.repository.MongoRepository

interface VehicleRepository : MongoRepository<Vehicle, Long>