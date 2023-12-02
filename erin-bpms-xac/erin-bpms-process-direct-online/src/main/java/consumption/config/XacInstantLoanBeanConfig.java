package consumption.config;

import consumption.case_listener.InstantLoanProcessCompleteListener;
import consumption.case_listener.CalculationCompleteSmsListener;
import consumption.case_listener.InstantLoanSetCalculationStateListener;
import consumption.service_task_bnpl.InstantSetAccountCreationDataTask;
import consumption.service_task_instant_loan.StartInstantLoanProcessTask;
import consumption.service_task_instant_loan.AddUnschLoanRepaymentTask;
import consumption.service_task_instant_loan.CalculateFeeTask;
import consumption.service_task_instant_loan.ScheduledLoanPaymentTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.service.MessagingService;
import mn.erin.domain.bpm.service.NewCoreBankingService;

@Configuration
public class XacInstantLoanBeanConfig
{
  @Bean
  public StartInstantLoanProcessTask startInstantLoanProcessTask(
      DefaultParameterRepository defaultParameterRepository, AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRepository processRepository)
  {
    return new StartInstantLoanProcessTask(defaultParameterRepository, authenticationService, authorizationService, processRepository);
  }

  @Bean
  public InstantSetAccountCreationDataTask instantSetAccountCreationDataTask(BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    return new InstantSetAccountCreationDataTask(bpmsRepositoryRegistry);
  }

  @Bean
  public CalculationCompleteSmsListener calculationCompleteSmsListener(MessagingService messagingService)
  {
    return new CalculationCompleteSmsListener(messagingService);
  }

  @Bean
  public InstantLoanSetCalculationStateListener instantLoanSetCalculationStateListener(AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRequestRepository processRequestRepository,
      ProcessRepository processRepository)
  {
    return new InstantLoanSetCalculationStateListener(authenticationService, authorizationService, processRequestRepository, processRepository);
  }

  @Bean
  public CalculateFeeTask calculateFeeTask(BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    return new CalculateFeeTask(bpmsRepositoryRegistry);
  }

  @Bean
  public ScheduledLoanPaymentTask scheduledLoanPayment(NewCoreBankingService newCoreBankingService)
  {
    return new ScheduledLoanPaymentTask(newCoreBankingService);
  }

  @Bean
  public AddUnschLoanRepaymentTask addUnschLoanRepaymentTask(NewCoreBankingService newCoreBankingService)
  {
    return new AddUnschLoanRepaymentTask(newCoreBankingService);
  }

  @Bean
  public InstantLoanProcessCompleteListener instantLoanProcessCompleteListener(ProcessRequestRepository processRequestRepository)
  {
    return new InstantLoanProcessCompleteListener(processRequestRepository);
  }
}
