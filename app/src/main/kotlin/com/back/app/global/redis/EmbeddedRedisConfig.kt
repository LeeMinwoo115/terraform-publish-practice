package com.back.app.global.redis

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import redis.embedded.RedisServer

@Profile("!prod")
@Configuration
class EmbeddedRedisConfig {
    @Value("\${spring.data.redis.port}")
    private val redisPort: Int = 0

    private var redisServer: RedisServer? = null

    @PostConstruct
    fun startRedis() {
        val port = if (isRedisRunning(redisPort)) findAvailablePort() else redisPort
        redisServer = RedisServer(port)
        redisServer?.start()
    }

    @PreDestroy
    fun stopRedis() {
        redisServer?.stop()
    }

    fun findAvailablePort(): Int {
        for (port in 10_000..65_535) {
            if (!isRedisRunning(port)) {
                return port
            }
        }

        throw IllegalStateException("Could not find available port $redisPort")
    }

    /**
     * Embedded Redis가 현재 실행중인지 확인
     */
    private fun isRedisRunning(port: Int): Boolean {
        return isRunning(executeGrepProcessCommand(port))
    }

    /**
     * OS에 따라 적절한 명령어 실행 (윈도우/리눅스 구분)
     */
    private fun executeGrepProcessCommand(port: Int): Process {
        val os = System.getProperty("os.name").lowercase()
        val command = if (os.contains("win")) {
            "netstat -ano | findstr :$port"
        } else {
            "netstat -nat | grep LISTEN | grep $port"
        }

        val shell = if (os.contains("win")) {
            arrayOf("cmd", "/c", command)
        } else {
            arrayOf("/bin/sh", "-c", command)
        }

        return Runtime.getRuntime().exec(shell)
    }

    private fun isRunning(process: Process): Boolean {
        return try {
            val output = process.inputStream.bufferedReader().use { it.readText() }
            output.isNotBlank()
        } catch (e: Exception) {
            throw IllegalStateException("Error occurred while checking redis port $redisPort", e)
        }
    }
}
