package com.weedow.searchy.sample.mongodb.config

import com.weedow.searchy.mongodb.converter.MongoConverters
import com.weedow.searchy.sample.mongodb.converter.DocumentToTaskConverter
import com.weedow.searchy.sample.mongodb.converter.TaskToDocumentConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableMongoAuditing


@Configuration
@EnableMongoAuditing
class SampleAppMongoConfiguration {

    /*
    @Configuration
    @ConditionalOnClass(Logger::class)
    internal class RuntimeConfigConfiguration {
        @Bean
        fun embeddedMongoRuntimeConfig(): IRuntimeConfig {
            val logger = LoggerFactory.getLogger(javaClass.getPackage().name + ".EmbeddedMongo")
            val processOutput = ProcessOutput(
                Processors.logTo(logger, Slf4jLevel.INFO),
                Processors.logTo(logger, Slf4jLevel.ERROR),
                Processors.named("[console>]", Processors.logTo(logger, Slf4jLevel.DEBUG))
            )
            return RuntimeConfigBuilder()
                .defaultsWithLogger(Command.MongoD, logger)
                .processOutput(processOutput)
                .artifactStore(getArtifactStore(logger))
                .daemonProcess(false)
                .build()
        }

        private fun getArtifactStore(logger: Logger): ArtifactStoreBuilder {
            val downloadConfigBuilder = DownloadConfigBuilder()
                .defaults()
                .progressListener(Slf4jProgressListener(logger))
                .downloadPath("https://fastdl.mongodb.org/")
                .packageResolver(CustomPaths(Command.MongoD))
            val downloadConfig = downloadConfigBuilder.build()
            return ExtractedArtifactStoreBuilder().defaults(Command.MongoD).download(downloadConfig)
        }
    }
    */

    /*
    @Bean
    fun customDownloadConfigBuilder(): DownloadConfigBuilderCustomizer {
        return DownloadConfigBuilderCustomizer { downloadConfigBuilder ->
            downloadConfigBuilder
                .downloadPath("https://fastdl.mongodb.org/")
                .packageResolver(CustomPaths(Command.MongoD))
        }
    }
    */

    /*
    internal class CustomPaths(command: Command) : Paths(command) {
        override fun getPath(distribution: Distribution): String {
            var path = super.getPath(distribution)

            var version = getVersionPart(distribution.version)

            if (version.contains("4.4.")) {
                val archiveType = Paths::class.declaredMemberFunctions.find { it.name == "getArchiveString" }?.let {
                    it.isAccessible = true
                    it.call(this, getArchiveType(distribution))
                }

                val bitSize = Paths::class.declaredMemberFunctions.find { it.name == "getBitSize" }?.let {
                    it.isAccessible = true
                    it.call(this, distribution)
                }

                if (distribution.platform == Platform.Linux) {
                    version = "ubuntu1804-$version"
                }

                val platform = when (distribution.platform) {
                    Platform.Windows -> "windows"
                    Platform.OS_X -> "osx"
                    else -> "linux"
                }

                var distrib = when (distribution.platform) {
                    Platform.Windows -> "windows"
                    Platform.OS_X -> "macos"
                    else -> "linux"
                }

                path = "$platform/mongodb-$distrib-$bitSize-$version.$archiveType"
            }


            return path
        }
    }
    */

    @Bean
    fun customConversions(): MongoConverters {
        return MongoConverters.of(
            TaskToDocumentConverter,
            DocumentToTaskConverter
        )
    }

}