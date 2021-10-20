package com.roy.coupling.common.config

import com.roy.coupling.common.producer.R2dbcMessageProducer
import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EnableR2dbcAuditing
@EnableR2dbcRepositories(basePackages = ["com.roy.coupling.common.domain"])
@Import(value = [R2dbcMessageProducer::class])
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
                val resource = ClassPathResource("init.d/coupling-init.sql") // create outbox_event table
                addPopulators(ResourceDatabasePopulator(resource))
                val resources = PathMatchingResourcePatternResolver().getResources("sql/*.sql") // app's sql
                resources.forEach {
                    addPopulators(ResourceDatabasePopulator(it))
                }
            }
            setDatabasePopulator(populator)
        }
    }
}
