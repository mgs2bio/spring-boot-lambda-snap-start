package com.amrut.prabhu

import com.amazonaws.services.lambda.runtime.events.SQSEvent
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import java.time.Instant
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

@SpringBootApplication
@Slf4j
open class SqsListenerApplication {
    fun toUpperCase(): Function<String, String> {
        return Function { value: String ->
            System.out.printf("invoked UpperCase: $value")
            value.uppercase(Locale.getDefault()) + " " + Instant.now()
        }
    }

    //input type must be SQSEvent in order to consume from sqs
    // delete and recreate Lambda triggers after terraform apply
    @Bean
    open fun processOrder(): Consumer<SQSEvent> {
        return Consumer { orderMsg: SQSEvent ->
            val messages = orderMsg.records
            log.info("received {} order message: {}", messages.size,
                    messages[0].body)
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(SqsListenerApplication::class.java, *args)
        }

        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}