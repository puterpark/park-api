package us.puter.park.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
@Slf4j
public class DataSourceConfig {

    private static final String PRIMARY = "primary";
    private static final String REPLICA = "replica";

    @Bean
    @Qualifier(PRIMARY)
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource primary() {
        log.debug("primary register");
        return DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @Qualifier(REPLICA)
    @ConfigurationProperties("spring.datasource.replica")
    public DataSource replica() {
        log.debug("replica register");
        return DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    public DataSource routingDataSource(
            @Qualifier(PRIMARY) DataSource primary
            , @Qualifier(REPLICA) DataSource replica
    ) {
        CustomRoutingDataSource routingDataSource = new CustomRoutingDataSource();

        HashMap<Object, Object> dataSourceMap = new HashMap<>() {{
            put(PRIMARY, primary);
            put(REPLICA, replica);
        }};

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(primary);

        return routingDataSource;
    }

    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}
