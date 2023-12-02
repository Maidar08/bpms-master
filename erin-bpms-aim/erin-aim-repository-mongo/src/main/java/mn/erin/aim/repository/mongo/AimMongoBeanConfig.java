package mn.erin.aim.repository.mongo;

import javax.inject.Inject;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.mongodb.MongoConnectorBeanConfig;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ MongoConnectorBeanConfig.class })
public class AimMongoBeanConfig
{
  private static final String AIM_DATABASE = "AIM";

  @Inject
  private MongoClient mongoClient;

  @Bean
  public GroupRepository groupRepository()
  {
    return new MongoGroupRepository(mongoClient.getDatabase(AIM_DATABASE).getCollection("Groups"));
  }

  @Bean
  public MembershipRepository membershipRepository()
  {
    return new MongoMembershipRepository(mongoClient.getDatabase(AIM_DATABASE).getCollection("Memberships"));
  }

  @Bean
  public RoleRepository roleRepository()
  {
    return new MongoRoleRepository(mongoClient.getDatabase(AIM_DATABASE).getCollection("Roles"));
  }

  @Bean
  public UserRepository userRepository()
  {
    return new MongoUserRepository(mongoClient.getDatabase(AIM_DATABASE).getCollection("Users"));
  }
}
