package com.filedownloader.downloaderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "spring.liquibase", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class SchemaCreatorService implements InitializingBean {

    private final LiquibaseProperties liquibaseProperties;
    private final DataSource dataSource;

    @Override
    public void afterPropertiesSet() {
        log.debug("Creation schema process started with configs: {}", liquibaseProperties);
        if (liquibaseProperties.isEnabled() && StringUtils.isNotBlank(liquibaseProperties.getDefaultSchema())) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + liquibaseProperties.getDefaultSchema());
        }
        log.debug("Creation schema process finished with configs: {}", liquibaseProperties);
    }
}
