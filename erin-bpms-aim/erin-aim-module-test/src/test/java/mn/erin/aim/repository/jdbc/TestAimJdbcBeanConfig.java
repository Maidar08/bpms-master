package mn.erin.aim.repository.jdbc;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import mn.erin.repository.jdbc.base.JdbcBaseBeanConfig;

/**
 *
 * @author EBazarragchaa
 */
@Configuration
@EnableJdbcRepositories("mn.erin.aim.repository.jdbc.interfaces")
@Import(AimJdbcRepositoryBeanConfig.class)
@PropertySource("classpath:jdbc-datasource-test.properties")
public class TestAimJdbcBeanConfig extends JdbcBaseBeanConfig
{
}
