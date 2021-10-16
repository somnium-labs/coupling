package com.roy.coupling.common.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.transaction.ReactiveTransactionManager

@Configuration
class R2dbcConfiguration {
    @Bean
    fun transactionManager(connectionFactory: ConnectionFactory): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }

    @Bean
    fun initializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        return ConnectionFactoryInitializer().apply {
            setConnectionFactory(connectionFactory)
            val populator = CompositeDatabasePopulator().apply {
                val resource = ClassPathResource("coupling-init.sql") // create outbox_event table
                addPopulators(ResourceDatabasePopulator(resource))
                val resources = PathMatchingResourcePatternResolver().getResources("init.d/*.sql") // app's sql
                resources.forEach {
                    addPopulators(ResourceDatabasePopulator(it))
                }
            }
            setDatabasePopulator(populator)
        }
    }
}
