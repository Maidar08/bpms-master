package mn.erin.bpm.webapp.jdbc;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import mn.erin.aim.repository.jdbc.AimJdbcRepositoryBeanConfig;
import mn.erin.bpm.repository.jdbc.BpmJdbcRepositoryBeanConfig;
import mn.erin.repository.jdbc.base.JdbcBaseBeanConfig;

/**
 * @author EBazarragchaa
 */
@Configuration
@EnableJdbcRepositories(basePackages = { "mn.erin.aim.repository.jdbc.interfaces", "mn.erin.bpm.repository.jdbc.interfaces" })
@Import({ BpmJdbcRepositoryBeanConfig.class, AimJdbcRepositoryBeanConfig.class })
public class BpmJdbcBeanConfig extends JdbcBaseBeanConfig
{
}
