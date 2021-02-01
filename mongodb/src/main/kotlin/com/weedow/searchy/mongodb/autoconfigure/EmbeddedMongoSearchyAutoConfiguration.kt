package com.weedow.searchy.mongodb.autoconfigure

import com.github.dockerjava.api.command.CreateContainerCmd
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import com.github.dockerjava.api.model.RestartPolicy
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.weedow.searchy.utils.klogger
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.testcontainers.containers.ContainerLaunchException
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.utility.TestcontainersConfiguration
import java.io.IOException
import java.net.Socket
import java.util.stream.Collectors


@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(MongoAutoConfiguration::class)
@EnableConfigurationProperties(MongoProperties::class, EmbeddedMongoProperties::class)
@ConditionalOnClass(MongoClient::class, MongoClientSettings::class, MongoDBContainer::class)
class EmbeddedMongoSearchyAutoConfiguration {

    companion object {
        private val log by klogger()
    }

    @Bean
    fun mongoDBContainer(embeddedMongoProperties: EmbeddedMongoProperties): MongoDBContainer {
        embeddedMongoProperties.updateTestcontainersConfig(TestcontainersConfiguration.getInstance())

        val container = MongoDBContainer(embeddedMongoProperties.mongoImage)
        container.withReuse(embeddedMongoProperties.containerReusable)
        container.withCreateContainerCmdModifier { cmd: CreateContainerCmd ->
            cmd
                .withName(embeddedMongoProperties.containerName)
                .withTty(embeddedMongoProperties.tty)
                .withStdinOpen(embeddedMongoProperties.stdinOpen)
                .withStdInOnce(embeddedMongoProperties.stdinOnce)
                .withAttachStdin(embeddedMongoProperties.attachStdin)
                .withAttachStdout(embeddedMongoProperties.attachStdout)
                .withAttachStderr(embeddedMongoProperties.attachStderr)
            cmd.hostConfig!!
                .withPrivileged(embeddedMongoProperties.privilegedMode)
                .withRestartPolicy(RestartPolicy.alwaysRestart())
                .withPortBindings(
                    PortBinding(
                        Ports.Binding.bindPort(embeddedMongoProperties.hostPort),
                        ExposedPort(embeddedMongoProperties.containerExposedPort)
                    )
                )
        }

        val logConsumer = Slf4jLogConsumer(log).withSeparateOutputStreams()
        container.withLogConsumer(logConsumer)

        container.start()

        val mappedPort = container.getMappedPort(embeddedMongoProperties.containerExposedPort)
        try {
            Socket(container.containerIpAddress, mappedPort)
        } catch (e: IOException) {
            throw ContainerLaunchException("The expected port $mappedPort is not available from the container '${container.containerName}'!")
        }

        return container
    }

    @Bean
    @DependsOn("mongoDBContainer")
    fun mongo(
        builderCustomizers: ObjectProvider<MongoClientSettingsBuilderCustomizer>,
        settings: MongoClientSettings
    ): MongoClient? {
        return MongoClientFactory(builderCustomizers.orderedStream().collect(Collectors.toList()))
            .createMongoClient(settings)
    }

}