package com.weedow.searchy.sample.mongodb.repository

import com.weedow.searchy.sample.mongodb.model.Job
import org.springframework.data.mongodb.repository.MongoRepository
import java.math.BigInteger

interface JobRepository : MongoRepository<Job, BigInteger>