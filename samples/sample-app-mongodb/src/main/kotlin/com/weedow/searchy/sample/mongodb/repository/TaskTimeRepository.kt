package com.weedow.searchy.sample.mongodb.repository

import com.weedow.searchy.sample.mongodb.model.TaskTime
import org.springframework.data.mongodb.repository.MongoRepository

interface TaskTimeRepository : MongoRepository<TaskTime, String>