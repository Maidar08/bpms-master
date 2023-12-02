package mn.erin.aim.webapp.jdbc;

import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import mn.erin.aim.repository.jdbc.AimJdbcRepositoryBeanConfig;
import mn.erin.repository.jdbc.base.JdbcBaseBeanConfig;

/**
 * @author EBazarragchaa
 */
@EnableJdbcRepositories("mn.erin.aim.repository.jdbc.interfaces")
@Import(AimJdbcRepositoryBeanConfig.class)
public class AimJdbcBeanConfig extends JdbcBaseBeanConfig
{
}
