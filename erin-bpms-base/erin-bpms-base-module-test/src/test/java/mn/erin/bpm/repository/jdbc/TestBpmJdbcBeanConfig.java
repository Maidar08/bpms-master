package mn.erin.bpm.repository.jdbc;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import mn.erin.repository.jdbc.base.JdbcBaseBeanConfig;

/**
 * @author Zorig
 */
@Configuration
@EnableJdbcRepositories("mn.erin.bpm.repository.jdbc.interfaces")
@Import(BpmJdbcRepositoryBeanConfig.class)
@PropertySource("classpath:jdbc-datasource-test.properties")
public class TestBpmJdbcBeanConfig extends JdbcBaseBeanConfig
{
}
