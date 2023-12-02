package mn.erin.bpms.organization.registration.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import mn.erin.bpm.domain.ohs.xac.XacNewCoreBeanConfig;
import mn.erin.bpms.process.base.CamundaBeanConfig;

@Configuration
@Import({ CamundaBeanConfig.class,  XacNewCoreBeanConfig.class })
public class CamundaOrganizationRegistrationBeanConfig
{
}
