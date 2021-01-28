package com.weedow.searchy.sample.mongodb.repository

import com.weedow.searchy.sample.mongodb.model.Task
import org.springframework.data.mongodb.repository.MongoRepository

interface TaskRepository : MongoRepository<Task, Long>