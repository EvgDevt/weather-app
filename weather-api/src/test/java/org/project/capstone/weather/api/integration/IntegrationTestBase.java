package org.project.capstone.weather.api.integration;

import org.project.capstone.weather.api.integration.annotation.IT;
import org.springframework.test.context.jdbc.Sql;

@IT
@Sql({"classpath:sql/data.sql"})
public abstract class IntegrationTestBase {
}
