package com.weedow.searchy.mongodb.autoconfigure

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.testcontainers.utility.TestcontainersConfiguration

/**
 * [Properties][ConfigurationProperties] for embedded MongoDB.
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "weedow.mongodb.embedded.container")
data class EmbeddedMongoProperties(
    /** MongoDB Docker image name. Use standard Docker format, e.g. name:tag, some.registry/path/name:tag, some.registry/path/name@sha256:abcdef..., etc. Default is 'mongo:latest' */
    val mongoImage: String = "mongo:latest",
    /** Published port of the Docker host mapped to [the container port][containerExposedPort]. Default is 27017 */
    val hostPort: Int = 27017,
    /** Container exposed port mapped to [the host port][hostPort] */
    val containerExposedPort: Int = hostPort,

    /** Container's name. Default is 'weedow-mongo' */
    val containerName: String = "weedow-mongo",
    /** Whether the container should be reused if it already exists. Default is false */
    val containerReusable: Boolean = false,

    /** Attach standard streams to a TTY, including stdin if it is not closed. Default is false */
    val tty: Boolean = false,
    /** Keep stdin open even if not attached. Default is false */
    val stdinOpen: Boolean = false,
    /**
     * Whether the container runtime should close the stdin channel after it has been opened by a single attach.
     * When [stdinOpen] is true the stdin stream will remain open across multiple attach sessions.
     * If stdinOnce is set to `true`, stdin is opened on container start, is empty until the first client attaches to stdin,
     * and then remains open and accepts data until the client disconnects, at which time stdin is closed and remains closed until the container is restarted.
     * If this flag is `false`, a container processes that reads from stdin will never receive an EOF.
     * Default is false
     */
    val stdinOnce: Boolean = false,
    /** Whether to attach to stdin. Default is false */
    val attachStdin: Boolean = false,
    /** Whether to attach to stderr. Default is false */
    val attachStderr: Boolean = false,
    /** Whether to attach to stdout. Default is false */
    val attachStdout: Boolean = false,

    /** Run the container in privileged mode. Default is false */
    val privilegedMode: Boolean = false,

    @NestedConfigurationProperty
    val ryuk: Ryuk = Ryuk(),
) {

    @ConstructorBinding
    class Ryuk(
        /** Whether the Ryuk container should be turned off. Default is false. See https://www.testcontainers.org/features/configuration/#disabling-ryuk */
        val disabled: Boolean = false,
        /** Run the container in privileged mode. Default is false */
        val privilegedMode: Boolean = false
    )

    fun updateTestcontainersConfig(configuration: TestcontainersConfiguration) {
        configuration.updateUserConfig("testcontainers.reuse.enable", containerReusable.toString())
        configuration.updateUserConfig("ryuk.container.privileged", ryuk.privilegedMode.toString())

        EnvironmentVariables().set("TESTCONTAINERS_RYUK_DISABLED", ryuk.disabled.toString())
    }
}