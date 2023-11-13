package com.example.milkchat2.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.util.MimeTypeUtils
import reactor.util.retry.Retry
import java.time.Duration

@Configuration
class RSocketConfig {

    @Bean
    fun getRSocketRequester(strategies: RSocketStrategies): RSocketRequester {
        return RSocketRequester.builder()
                .rsocketConnector{ connector -> connector.reconnect(Retry.backoff(10, Duration.ofMillis(500))) }
                .rsocketStrategies(strategies)
                .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
                .tcp("localhost", 8080)
    }
}