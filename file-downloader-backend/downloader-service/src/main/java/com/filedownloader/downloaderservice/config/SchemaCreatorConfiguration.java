package com.filedownloader.downloaderservice.config;

import com.filedownloader.downloaderservice.service.SchemaCreatorService;
import liquibase.change.DatabaseChange;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@ConditionalOnClass({ SpringLiquibase.class, DatabaseChange.class, SchemaCreatorService.class })
@ConditionalOnProperty(prefix = "spring.liquibase", name = "enabled", matchIfMissing = true)
@AutoConfigureAfter(name = {
        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
        "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
public class SchemaCreatorConfiguration extends AbstractDependsOnBeanFactoryPostProcessor {

    public SchemaCreatorConfiguration() {
        super(SpringLiquibase.class, SchemaCreatorService.class);
        log.debug("Dependency order for liquibase library changed.");
    }
}