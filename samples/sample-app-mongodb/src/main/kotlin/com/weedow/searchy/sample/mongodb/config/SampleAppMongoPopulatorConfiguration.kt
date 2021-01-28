package com.weedow.searchy.sample.mongodb.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.neovisionaries.i18n.CountryCode
import com.weedow.searchy.sample.mongodb.model.*
import com.weedow.searchy.sample.mongodb.repository.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.io.Resource
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean
import java.time.LocalDateTime
import java.time.OffsetDateTime


@Configuration
class SampleAppMongoPopulatorConfiguration {

    @Value("classpath:/data.json")
    private lateinit var data: Resource

    // @Bean
    fun repositoryPopulator(objectMapper: ObjectMapper): Jackson2RepositoryPopulatorFactoryBean {
        val factory = Jackson2RepositoryPopulatorFactoryBean()
        factory.setMapper(objectMapper)
        factory.setResources(arrayOf(data))
        return factory
    }

    @EventListener
    fun handleContextRefreshEvent(cre: ContextRefreshedEvent) {
        val personRepository = cre.applicationContext.getBean(PersonRepository::class.java)
        val featureRepository = cre.applicationContext.getBean(FeatureRepository::class.java)
        val vehicleRepository = cre.applicationContext.getBean(VehicleRepository::class.java)
        val jobRepository = cre.applicationContext.getBean(JobRepository::class.java)
        val taskRepository = cre.applicationContext.getBean(TaskRepository::class.java)
        val taskTimeRepository = cre.applicationContext.getBean(TaskTimeRepository::class.java)

        val person1 = personRepository.insert(
            Person(
                "John",
                "Doe"
            )
        )

        val person2 = personRepository.insert(
            Person(
                "Jane",
                "Doe"
            )
        )

        val feature1 = featureRepository.save(
            Feature(
                "gps",
                "Connected navigation with cartography and connected navigation services (3 years): Google search for addresses and points of interest, real-time traffic information, fuel prices, warning zones."
            )
        )
        val feature2 = featureRepository.save(
            Feature(
                "airbag",
                "Dual deployment driver front airbag and passenger front airbag. Front seat side airbags and front and rear side curtain airbags for optimum safety of all occupants."
            )
        )
        val feature3 = featureRepository.save(
            Feature(
                "overspeed",
                "Your vehicle is on watch at all times: its camera reads traffic signs and informs you of the latest speed limits. In the event of overspeeding, your dashboard and navigation system will give you a visual warning when you exceed the speed limit."
            )
        )
        val feature4 = featureRepository.save(
            Feature(
                "eba",
                "Emergency Brake Assist (EBA) applies maximum braking power when it detects a sudden braking maneuver by the driver. This helps reduce braking distances."
            )
        )
        val feature5 = featureRepository.save(
            Feature(
                "airConditioning",
                "Automatic air conditioning: select your temperature and the system will then manage the other parameters for your comfort."
            )
        )
        val feature6 = featureRepository.save(
            Feature("backCamera", "")
        )

        val vehicle1 = vehicleRepository.save(
            Vehicle(
                VehicleType.CAR,
                "Renault",
                "Clio E-Tech",
                person1,
                mapOf(feature1.name to feature1, feature2.name to feature2, feature3.name to feature3)
            )
        )
        val vehicle2 = vehicleRepository.save(
            Vehicle(
                VehicleType.MOTORBIKE,
                "Harley-Davidson",
                "Livewire",
                person1,
                mapOf(feature4.name to feature4, feature5.name to feature5, feature6.name to feature6)
            )
        )
        val vehicle3 = vehicleRepository.save(Vehicle(VehicleType.CAR, "Porsche", "911 Carrera S", person2))

        val vehicles1 = setOf(
            vehicle1,
            vehicle2
        )
        val vehicles2 = setOf(
            vehicle3
        )

        val characteristics1 = mapOf(
            "eyes" to "blue",
            "hair" to "brown"
        )
        val characteristics2 = mapOf(
            "eyes" to "green",
            "hair" to "blond"
        )

        val address1 = Address("Rue des Peupliers", "78370", CountryCode.FR, City("Plaisir", 48.8167, 1.95))
        val address2 = Address("Rue des Petits Pois", "17051", CountryCode.FR, City("Le-Bois-Plage-En-Ré", 46.1833, -1.3833))
        val address3 = Address("Allée du sable fin", "20166", CountryCode.FR, City("Porticcio", 41.8833, 8.7833))
        val address4 = Address("10 Mathew St", "L2 6RE", CountryCode.UK, City("Liverpool", 53.4167, -3.0))
        val address5 = Address("Westermarkt 20", "1016 GV", CountryCode.NL, City("Amsterdam", 52.3676, 4.9041))

        val job1 = jobRepository.save(
            Job(true, "Lab Technician", "Acme", 50000, OffsetDateTime.parse("2019-09-01T09:00:00Z"), person1)
        )
        val job2 = jobRepository.save(
            Job(true, "Commercial Fisherman", "Fishing & Co", 60000, OffsetDateTime.parse("2019-09-01T09:00:00Z"), person2)
        )


        val task1 = taskRepository.save(Task("Go shopping", "Go shopping with the family"))
        val task2 = taskRepository.save(Task("Go to the doctor's appointment", "Go to the doctor's appointment next Monday"))
        val task3 = taskRepository.save(Task("Clean up the garage", null))
        val taskTime1 = taskTimeRepository.save(TaskTime(LocalDateTime.parse("2020-12-24T12:00:00")))
        val taskTime2 = taskTimeRepository.save(TaskTime(LocalDateTime.parse("2020-12-28T12:00:00")))
        val taskTime3 = taskTimeRepository.save(TaskTime(LocalDateTime.parse("2020-12-30T12:00:00")))
        val tasks1 = mapOf(
            task1 to taskTime1,
            task2 to taskTime2,
            task3 to taskTime3
        )
        val tasks2 = mapOf(task1 to taskTime1)
        val location = null

        person1.email = "john.doe@acme.com"
        person1.birthday = LocalDateTime.parse("1981-03-12T10:36:00")
        person1.age = 39
        person1.height = 174.0
        person1.weight = 70.5
        person1.sex = Sex.MALE
        person1.nickNames = setOf("Joe", "Johnny")
        person1.phoneNumbers = setOf("+33612345678")
        person1.mainAddress = address1
        person1.otherAddresses = setOf(address2, address3)
        person1.jobEntity = job1
        person1.vehicles = vehicles1
        person1.characteristics = characteristics1
        person1.tasks = tasks1
        person1.location = location
        personRepository.save(person1)

        person2.email = "jane.doe@acme.com"
        person2.birthday = LocalDateTime.parse("1981-11-26T12:30:00")
        person2.age = 39
        person2.height = 165.0
        person2.weight = 68.0
        person2.sex = Sex.FEMALE
        person2.nickNames = null
        person2.phoneNumbers = setOf("+33687654321")
        person2.mainAddress = address4
        person2.otherAddresses = setOf(address5)
        person2.jobEntity = job2
        person2.vehicles = vehicles2
        person2.characteristics = characteristics2
        person2.tasks = tasks2
        person2.location = location
        personRepository.save(person2)
    }

}