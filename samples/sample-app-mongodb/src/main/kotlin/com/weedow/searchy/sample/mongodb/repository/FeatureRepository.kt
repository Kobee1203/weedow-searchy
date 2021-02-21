package com.weedow.searchy.sample.mongodb.repository

import com.weedow.searchy.sample.mongodb.model.Feature
import org.springframework.data.mongodb.repository.MongoRepository

interface FeatureRepository : MongoRepository<Feature, String>