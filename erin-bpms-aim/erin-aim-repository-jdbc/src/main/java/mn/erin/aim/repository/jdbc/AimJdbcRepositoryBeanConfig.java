/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.repository.jdbc;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mn.erin.aim.repository.jdbc.interfaces.JdbcGroupParentChildRepository;
import mn.erin.aim.repository.jdbc.interfaces.JdbcGroupRepository;
import mn.erin.aim.repository.jdbc.interfaces.JdbcMembershipRepository;
import mn.erin.aim.repository.jdbc.interfaces.JdbcRolePermissionRepository;
import mn.erin.aim.repository.jdbc.interfaces.JdbcRoleRepository;

@Configuration
public class AimJdbcRepositoryBeanConfig
{
  @Bean
  @Inject
  public DefaultJdbcGroupRepository defaultJdbcGroupRepository(JdbcGroupRepository jdbcGroupRepository,
      JdbcGroupParentChildRepository jdbcGroupParentChildRepository)
  {
    return new DefaultJdbcGroupRepository(jdbcGroupRepository, jdbcGroupParentChildRepository);
  }

  @Bean
  @Inject
  public DefaultJdbcMembershipRepository defaultJdbcMembershipRepository(JdbcMembershipRepository jdbcMembershipRepository)
  {
    return new DefaultJdbcMembershipRepository(jdbcMembershipRepository);
  }

  @Bean
  @Inject
  public DefaultJdbcRoleRepository defaultJdbcRoleRepository(JdbcRoleRepository jdbcRoleRepository, JdbcRolePermissionRepository jdbcRolePermissionRepository)
  {
    return new DefaultJdbcRoleRepository(jdbcRoleRepository, jdbcRolePermissionRepository);
  }
}
