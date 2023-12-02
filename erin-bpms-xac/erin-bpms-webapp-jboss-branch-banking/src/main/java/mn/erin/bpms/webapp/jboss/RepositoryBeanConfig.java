package mn.erin.bpms.webapp.jboss;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import mn.erin.aim.repository.jdbc.AimJdbcRepositoryBeanConfig;
import mn.erin.bpm.repository.jdbc.BpmJdbcRepositoryBeanConfig;
import mn.erin.repository.jdbc.base.JdbcBaseBeanConfig;

/**
 * @author EBazarragchaa
 */
@Configuration
@EnableJdbcRepositories(basePackages = { "mn.erin.aim.repository.jdbc.interfaces", "mn.erin.bpm.repository.jdbc.interfaces" })
@EnableTransactionManagement
@Import({ BpmJdbcRepositoryBeanConfig.class, AimJdbcRepositoryBeanConfig.class })
public class RepositoryBeanConfig extends JdbcBaseBeanConfig
{
  @Override
  @Bean
  @Inject
  protected PlatformTransactionManager transactionManager(Environment environment)
  {
    return new JtaTransactionManager();
  }
}
