package org.themarioga.engine.cah;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

@Configuration
@EntityScan(basePackages = {"org.themarioga.engine"})
@ComponentScan(basePackages = {"org.themarioga.engine"})
@EnableJpaRepositories(basePackages = "org.themarioga.engine.cah.dao")
public class TestConfiguration {

    @Value("${dbunit.schema}")
    private String schema;

    @Autowired
    private DataSource dataSource;

    @Bean
    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
        DatabaseConfigBean bean = new DatabaseConfigBean();
        bean.setDatatypeFactory(new H2DataTypeFactory());

        DatabaseDataSourceConnectionFactoryBean dbConnectionFactory = new DatabaseDataSourceConnectionFactoryBean(dataSource);
        dbConnectionFactory.setDatabaseConfig(bean);
        dbConnectionFactory.setSchema(schema);
        return dbConnectionFactory;
    }

}
