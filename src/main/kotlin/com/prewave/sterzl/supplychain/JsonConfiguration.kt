package com.prewave.sterzl.supplychain

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JsonConfiguration {
    @Bean
    fun jsonFactory() = JsonFactory()

    @Bean
    fun objectMapper() = ObjectMapper()
}
