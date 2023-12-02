package mn.erin.bpms.webapp.jdbc.branch.banking;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.Validate;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import mn.erin.aim.repository.jdbc.AimJdbcRepositoryBeanConfig;
import mn.erin.bpm.repository.jdbc.BpmJdbcRepositoryBeanConfig;
import mn.erin.repository.jdbc.base.JdbcBaseBeanConfig;

/**
 * @author EBazarragchaa
 */
@Configuration
@EnableJdbcRepositories(basePackages = { "mn.erin.aim.repository.jdbc.interfaces", "mn.erin.bpm.repository.jdbc.interfaces" })
@Import({ BpmJdbcRepositoryBeanConfig.class, AimJdbcRepositoryBeanConfig.class
})
public class BpmModuleJdbcBeanConfig extends JdbcBaseBeanConfig
{
  @Override
  protected DataSource dataSource(Environment environment)
  {
    BasicDataSource basicDataSource  = new BasicDataSource();

    String driverClass = Validate.notBlank(environment.getProperty("datasource.driver"), "'datasource.driver' is missing in datasource.properties");
    String url = Validate.notBlank(environment.getProperty("datasource.url"), "'datasource.url' is missing in datasource.properties");
    String username = Validate.notBlank(environment.getProperty("datasource.username"), "'datasource.username' is missing in datasource.properties");
    String password = Validate.notBlank(environment.getProperty("datasource.password"), "'datasource.password' is missing in datasource.properties");

    basicDataSource.setDriverClassName(driverClass);
    basicDataSource.setUrl(url);
    basicDataSource.setUsername(username);
    basicDataSource.setPassword(password);
    basicDataSource.setMinIdle(5);
    basicDataSource.setMaxIdle(10);
    basicDataSource.setMaxOpenPreparedStatements(100);

    return basicDataSource;
  }
}
