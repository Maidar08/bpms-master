package consumption.config;

import consumption.process_listener.SetCalculationStateListener;
import consumption.service_task_online_leasing.RefundToOrganizationAccountTask;
import consumption.service_task_online_leasing.StartOnlineLeasingProcessTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;

@Configuration
public class XacOnlineLeasingBeanConfig
{
  @Bean
  public StartOnlineLeasingProcessTask startOnlineLeasingProcessTask(AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    return new StartOnlineLeasingProcessTask(aimServiceRegistry, bpmsRepositoryRegistry);
  }

  @Bean
  public SetCalculationStateListener setCalculationStateListener(AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry){
    return new SetCalculationStateListener(aimServiceRegistry, bpmsRepositoryRegistry);
  }

   @Bean
  public  RefundToOrganizationAccountTask refundToOrganizationAccountTask(BpmsRepositoryRegistry bpmsRepositoryRegistry, BpmsServiceRegistry bpmsServiceRegistry, Environment environment){
    return new RefundToOrganizationAccountTask(bpmsRepositoryRegistry, bpmsServiceRegistry, environment);
   }
}
