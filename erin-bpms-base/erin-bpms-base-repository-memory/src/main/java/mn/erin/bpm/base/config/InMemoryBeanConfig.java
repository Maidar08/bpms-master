package mn.erin.bpm.base.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "mn.erin.bpm.base.repository.memory", "mn.erin.bpm.base.service.memory" })
public class InMemoryBeanConfig
{

}
