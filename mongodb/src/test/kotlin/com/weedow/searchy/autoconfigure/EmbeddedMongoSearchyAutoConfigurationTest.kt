package com.weedow.searchy.autoconfigure

import com.github.dockerjava.api.command.InspectContainerResponse
import com.github.dockerjava.api.model.RestartPolicy
import com.weedow.searchy.mongodb.autoconfigure.EmbeddedMongoSearchyAutoConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import java.util.function.Supplier

internal class EmbeddedMongoSearchyAutoConfigurationTest {

    @Test
    fun initialize_embedded_mongodb_beans() {
        val containerName = "weedow-mongo"
        val stdinOpen = false
        val stdinOnce = false
        val attachStdin = false
        val attachStdout = false
        val attacheStderr = false
        val tty = false
        val cmd = listOf("--replSet", "docker-rs")
        val env = mapOf("MONGO_MAJOR" to "4.4", "MONGO_VERSION" to "4.4.3")
        val hostPort = 27017
        val containerExposedPort = 27017
        val exposedExportStr = "$containerExposedPort/tcp"
        val image = "mongo:latest"
        val imageDone = true
        val platform = "linux"
        val running = true
        val host = "localhost"
        val privilegedMode = false

        ApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    MongoAutoConfiguration::class.java,
                    EmbeddedMongoSearchyAutoConfiguration::class.java
                )
            )
            .withPropertyValues()
            .run { context ->
                assertThat(context).hasBean("mongo")
                assertThat(context).hasBean("mongoDBContainer")
                assertThat(context).hasSingleBean(MongoDBContainer::class.java)

                val mongoDBContainer = context.getBean("mongoDBContainer", MongoDBContainer::class.java)

                //assertThat(mongoDBContainer.containerId).isEqualTo("df17a10c65d365260ba30b207bec6f70e4fb63c38f0444837023e9740c574162")
                assertThat(mongoDBContainer.containerName).isEqualTo("/$containerName")
                val expectedValue = mapOf(
                    "stdinOpen" to stdinOpen,
                    "stdinOnce" to stdinOnce,
                    "attachStdin" to attachStdin,
                    "attachStdout" to attachStdout,
                    "attachStderr" to attacheStderr,
                    "tty" to tty,
                    "cmd" to cmd,
                    "env" to env,
                    "exposedPortStr" to exposedExportStr,
                    "image" to image,
                    "name" to "/$containerName",
                    "platform" to platform,
                    "running" to running
                )
                assertThat(mongoDBContainer.containerInfo) { expectedValue }
                assertThat(mongoDBContainer.currentContainerInfo) { expectedValue }

                //mongoDBContainer.dockerClient
                //mongoDBContainer.binds
                //mongoDBContainer.boundPortNumbers
                assertThat(mongoDBContainer.commandParts).containsExactlyElementsOf(cmd)
                assertThat(mongoDBContainer.containerIpAddress).isEqualTo(host)
                //mongoDBContainer.dependencies
                assertThat(mongoDBContainer.dockerImageName).isEqualTo(image)
                //mongoDBContainer.env
                //mongoDBContainer.envMap
                assertThat(mongoDBContainer.exposedPorts.firstOrNull()).isEqualTo(containerExposedPort)
                //mongoDBContainer.extraHosts
                assertThat(mongoDBContainer.firstMappedPort).isEqualTo(hostPort)
                assertThat(mongoDBContainer.host).isEqualTo(host)
                assertThat(mongoDBContainer.image.isDone).isEqualTo(imageDone)
                assertThat(mongoDBContainer.image.get()).isEqualTo(image)
                //mongoDBContainer.labels
                //mongoDBContainer.copyToFileContainerPathMap

                val consumer = mongoDBContainer.createContainerCmdModifiers.first()
                val createContainerCmd = mongoDBContainer.dockerClient.createContainerCmd(image)
                consumer.accept(createContainerCmd)
                assertThat(createContainerCmd.name).isEqualTo(containerName)
                assertThat(createContainerCmd.isTty).isEqualTo(tty)
                assertThat(createContainerCmd.isStdinOpen).isEqualTo(stdinOpen)
                assertThat(createContainerCmd.isStdInOnce).isEqualTo(stdinOnce)
                assertThat(createContainerCmd.isAttachStdin).isEqualTo(attachStdin)
                assertThat(createContainerCmd.isAttachStdout).isEqualTo(attachStdout)
                assertThat(createContainerCmd.isAttachStderr).isEqualTo(attacheStderr)
                val hostConfig = createContainerCmd.hostConfig!!
                assertThat(hostConfig.privileged).isEqualTo(privilegedMode)
                assertThat(hostConfig.restartPolicy).isEqualTo(RestartPolicy.alwaysRestart())
                val portBinding = hostConfig.portBindings.bindings.entries.first()
                assertThat(portBinding.component1().toString()).isEqualTo(exposedExportStr)
                assertThat(portBinding.component2().first().hostPortSpec).isEqualTo(hostPort.toString())

                assertThat(mongoDBContainer.livenessCheckPortNumbers.firstOrNull()).isEqualTo(hostPort)
                assertThat(mongoDBContainer.logConsumers).hasSize(1)
                assertThat(mongoDBContainer.logConsumers[0]).isInstanceOf(Slf4jLogConsumer::class.java)
                // mongoDBContainer.logs // INTERESTING TO SHOW MongoDB LOGS
                //mongoDBContainer.network
                //mongoDBContainer.networkAliases
                //mongoDBContainer.networkMode
                //mongoDBContainer.shmSize
                //mongoDBContainer.startupAttempts
                //mongoDBContainer.startupCheckStrategy
                //mongoDBContainer.testHostIpAddress
                //mongoDBContainer.tmpFsMapping
                //mongoDBContainer.volumesFroms
                //mongoDBContainer.workingDirectory
                //mongoDBContainer.portBindings
                assertThat(mongoDBContainer.replicaSetUrl).isEqualTo("mongodb://$host:$hostPort/test")
                assertThat(mongoDBContainer.getMappedPort(containerExposedPort)).isEqualTo(hostPort)
                //mongoDBContainer.getReplicaSetUrl("test")
            }
    }

    @Test
    fun initialize_embedded_mongodb_beans_with_custom_properties() {
        val mongoVersion = "4.2.11"
        val image = "mongo:$mongoVersion"
        val containerName = "weedow-mongo-test"
        val stdinOpen = true
        val stdinOnce = true
        val attachStdin = true
        val attachStdout = true
        val attacheStderr = true
        val tty = true
        val cmd = listOf("--replSet", "docker-rs")
        val env = mapOf("MONGO_MAJOR" to "4.2", "MONGO_VERSION" to "$mongoVersion")
        val hostPort = 50017
        val containerExposedPort = 27017
        val exposedExportStr = "$containerExposedPort/tcp"
        val imageDone = true
        val platform = "linux"
        val running = true
        val host = "localhost"
        val privilegedMode = true

        ApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    MongoAutoConfiguration::class.java,
                    EmbeddedMongoSearchyAutoConfiguration::class.java
                )
            )
            .withPropertyValues(
                "weedow.mongodb.embedded.container.mongo-image=mongo:$mongoVersion",
                "weedow.mongodb.embedded.container.host-port=$hostPort",
                "weedow.mongodb.embedded.container.container-exposed-port=$containerExposedPort",
                "weedow.mongodb.embedded.container.container-name=$containerName",
                "weedow.mongodb.embedded.container.container-reusable=true",
                "weedow.mongodb.embedded.container.tty=$tty",
                "weedow.mongodb.embedded.container.stdin-open=$stdinOpen",
                "weedow.mongodb.embedded.container.stdin-once=$stdinOnce",
                "weedow.mongodb.embedded.container.attach-stdin=$attachStdin",
                "weedow.mongodb.embedded.container.attach-stdout=$attachStdout",
                "weedow.mongodb.embedded.container.attach-stderr=$attacheStderr",
                "weedow.mongodb.embedded.container.privileged-mode=$privilegedMode",
                "weedow.mongodb.embedded.container.ryuk.disabled=true",
                "weedow.mongodb.embedded.container.ryuk.privileged-mode=true"
            )
            .run { context ->
                assertThat(context).hasBean("mongo")
                assertThat(context).hasBean("mongoDBContainer")
                assertThat(context).hasSingleBean(MongoDBContainer::class.java)

                val mongoDBContainer = context.getBean("mongoDBContainer", MongoDBContainer::class.java)

                //assertThat(mongoDBContainer.containerId).isEqualTo("df17a10c65d365260ba30b207bec6f70e4fb63c38f0444837023e9740c574162")
                assertThat(mongoDBContainer.containerName).isEqualTo("/$containerName")
                val expectedValue = mapOf(
                    "stdinOpen" to stdinOpen,
                    "stdinOnce" to stdinOnce,
                    "attachStdin" to attachStdin,
                    "attachStdout" to attachStdout,
                    "attachStderr" to attacheStderr,
                    "tty" to tty,
                    "cmd" to cmd,
                    "env" to env,
                    "exposedPortStr" to exposedExportStr,
                    "image" to image,
                    "name" to "/$containerName",
                    "platform" to platform,
                    "running" to running
                )
                assertThat(mongoDBContainer.containerInfo) { expectedValue }
                assertThat(mongoDBContainer.currentContainerInfo) { expectedValue }

                //mongoDBContainer.dockerClient
                //mongoDBContainer.binds
                //mongoDBContainer.boundPortNumbers
                assertThat(mongoDBContainer.commandParts).containsExactlyElementsOf(cmd)
                assertThat(mongoDBContainer.containerIpAddress).isEqualTo(host)
                //mongoDBContainer.dependencies
                assertThat(mongoDBContainer.dockerImageName).isEqualTo(image)
                //mongoDBContainer.env
                //mongoDBContainer.envMap
                assertThat(mongoDBContainer.exposedPorts.firstOrNull()).isEqualTo(containerExposedPort)
                //mongoDBContainer.extraHosts
                assertThat(mongoDBContainer.firstMappedPort).isEqualTo(hostPort)
                assertThat(mongoDBContainer.host).isEqualTo(host)
                assertThat(mongoDBContainer.image.isDone).isEqualTo(imageDone)
                assertThat(mongoDBContainer.image.get()).isEqualTo(image)
                //mongoDBContainer.labels
                //mongoDBContainer.copyToFileContainerPathMap

                val consumer = mongoDBContainer.createContainerCmdModifiers.first()
                val createContainerCmd = mongoDBContainer.dockerClient.createContainerCmd(image)
                consumer.accept(createContainerCmd)
                assertThat(createContainerCmd.name).isEqualTo(containerName)
                assertThat(createContainerCmd.isTty).isEqualTo(tty)
                assertThat(createContainerCmd.isStdinOpen).isEqualTo(stdinOpen)
                assertThat(createContainerCmd.isStdInOnce).isEqualTo(stdinOnce)
                assertThat(createContainerCmd.isAttachStdin).isEqualTo(attachStdin)
                assertThat(createContainerCmd.isAttachStdout).isEqualTo(attachStdout)
                assertThat(createContainerCmd.isAttachStderr).isEqualTo(attacheStderr)
                val hostConfig = createContainerCmd.hostConfig!!
                assertThat(hostConfig.privileged).isEqualTo(privilegedMode)
                assertThat(hostConfig.restartPolicy).isEqualTo(RestartPolicy.alwaysRestart())
                val portBinding = hostConfig.portBindings.bindings.entries.first()
                assertThat(portBinding.component1().toString()).isEqualTo(exposedExportStr)
                assertThat(portBinding.component2().first().hostPortSpec).isEqualTo(hostPort.toString())

                assertThat(mongoDBContainer.livenessCheckPortNumbers.firstOrNull()).isEqualTo(hostPort)
                assertThat(mongoDBContainer.logConsumers).hasSize(1)
                assertThat(mongoDBContainer.logConsumers[0]).isInstanceOf(Slf4jLogConsumer::class.java)
                // mongoDBContainer.logs // INTERESTING TO SHOW MongoDB LOGS
                //mongoDBContainer.network
                //mongoDBContainer.networkAliases
                //mongoDBContainer.networkMode
                //mongoDBContainer.shmSize
                //mongoDBContainer.startupAttempts
                //mongoDBContainer.startupCheckStrategy
                //mongoDBContainer.testHostIpAddress
                //mongoDBContainer.tmpFsMapping
                //mongoDBContainer.volumesFroms
                //mongoDBContainer.workingDirectory
                //mongoDBContainer.portBindings
                assertThat(mongoDBContainer.replicaSetUrl).isEqualTo("mongodb://$host:$hostPort/test")
                assertThat(mongoDBContainer.getMappedPort(containerExposedPort)).isEqualTo(hostPort)
                //mongoDBContainer.getReplicaSetUrl("test")
            }
    }

    @Suppress("UNCHECKED_CAST")
    fun assertThat(containerResponse: InspectContainerResponse, expectedValuesSupplier: Supplier<Map<String, Any>>) {
        val expectedValues = expectedValuesSupplier.get()
        //assertThat(containerResponse.id).isEqualTo("df17a10c65d365260ba30b207bec6f70e4fb63c38f0444837023e9740c574162")
        assertThat(containerResponse.config).isNotNull
        assertThat(containerResponse.config.stdinOpen).isEqualTo(expectedValues["stdinOpen"])
        assertThat(containerResponse.config.stdInOnce).isEqualTo(expectedValues["stdinOnce"])
        assertThat(containerResponse.config.attachStdin).isEqualTo(expectedValues["attachStdin"])
        assertThat(containerResponse.config.attachStdout).isEqualTo(expectedValues["attachStdout"])
        assertThat(containerResponse.config.attachStderr).isEqualTo(expectedValues["attachStderr"])
        assertThat(containerResponse.config.tty).isEqualTo(expectedValues["tty"])
        assertThat(containerResponse.config.cmd).containsExactlyElementsOf(expectedValues["cmd"] as List<String>)
        val env = containerResponse.config.env!!
        (expectedValues["env"] as Map<String, String>).forEach { (key, value) ->
            assertThat(env.first { it.startsWith(key) }.split("=")[1]).isEqualTo(value)
        }
        assertThat(containerResponse.config.exposedPorts.first().toString()).isEqualTo(expectedValues["exposedPortStr"])
        assertThat(containerResponse.config.image).isEqualTo(expectedValues["image"])
        assertThat(containerResponse.name).isEqualTo(expectedValues["name"])
        assertThat(containerResponse.platform).isEqualTo(expectedValues["platform"])
        assertThat(containerResponse.state.running).isEqualTo(expectedValues["running"])
    }
}