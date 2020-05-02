package com.weedow.spring.data.search.example.model

import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToOne

@Entity
class Job(
        @Column(nullable = false)
        val title: String,

        @Column(nullable = false)
        val company: String,

        @Column(nullable = false)
        val salary: Integer,

        @Column(nullable = false)
        val hireDate: OffsetDateTime,

        @OneToOne(optional = false)
        val person: Person

) : JpaPersistable<Long>() {

}
