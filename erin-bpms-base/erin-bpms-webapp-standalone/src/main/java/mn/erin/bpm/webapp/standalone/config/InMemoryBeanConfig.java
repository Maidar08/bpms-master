package mn.erin.bpm.webapp.standalone.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "mn.erin.bpm.webapp.standalone.repository", "mn.erin.bpm.webapp.standalone.service" })
public class InMemoryBeanConfig
{

}
