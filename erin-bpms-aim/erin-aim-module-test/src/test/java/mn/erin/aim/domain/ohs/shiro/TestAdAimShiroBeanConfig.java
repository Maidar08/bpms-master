package mn.erin.aim.domain.ohs.shiro;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import mn.erin.aim.rest.shiro.config.AdRealmShiroBeanConfig;

/**
 * @author EBazarragchaa
 */
@Configuration
@Import(AdRealmShiroBeanConfig.class)
@PropertySource("classpath:shiro-test.properties")
public class TestAdAimShiroBeanConfig
{

}
