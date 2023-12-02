package consumption.config;

import javax.inject.Inject;

import consumption.case_listener.AccountCreationErrorListener;
import consumption.case_listener.BnplAccountCreationCompleteListener;
import consumption.case_listener.BnplDisburseErrorHandler;
import consumption.case_listener.BnplProcessCompleteListener;
import consumption.case_listener.BnplScoringCompleteListener;
import consumption.case_listener.BnplScoringRejectedListener;
import consumption.case_listener.BnplSetCalculationStateListener;
import consumption.case_listener.BnplSetDebtIncomeRatioListener;
import consumption.case_listener.RefundTransactionListener;
import consumption.case_listener.TransactionFailedErrorHandler;
import consumption.case_listener.UploadFileToLdmsErrorHandler;
import consumption.service_task_bnpl.BnplAddTransactionTask;
import consumption.service_task_bnpl.BnplCalculateLoanAmountTask;
import consumption.service_task_bnpl.BnplDisburseDebtIncomeRatioTask;
import consumption.service_task_bnpl.BnplGenerateLoanDecisionDocumentTask;
import consumption.service_task_bnpl.BnplLoanDisbursementTask;
import consumption.service_task_bnpl.BnplSetAccountCreationDataTask;
import consumption.service_task_bnpl.BnplUploadLoanContractAndOtherFiles;
import consumption.service_task_bnpl.PrepareLoanReportTask;
import consumption.service_task_bnpl.RefundTransactionTask;
import consumption.service_task_bnpl.StartBnplProcessTask;
import consumption.service_task_bnpl.UploadFilesToLdmsTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.service.BnplCoreBankingService;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.service.MessagingService;

@Configuration
public class XacBnplBeanConfig
{
  // Direct Online
  @Bean
  public StartBnplProcessTask startBnplProcessTask(DefaultParameterRepository defaultParameterRepository, AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRepository processRepository)
  {
    return new StartBnplProcessTask(defaultParameterRepository, authenticationService, authorizationService, processRepository);
  }

  @Bean
  public BnplCalculateLoanAmountTask bnplCalculateLoanAmountTask(Environment environment)
  {
    return new BnplCalculateLoanAmountTask(environment);
  }

  @Bean
  public BnplSetCalculationStateListener bnplSetCalculationStateListener(AuthenticationService authenticationService, AuthorizationService authorizationService,
      ProcessRequestRepository processRequestRepository, ProcessRepository processRepository)
  {
    return new BnplSetCalculationStateListener(authenticationService, authorizationService, processRequestRepository, processRepository);
  }

  @Bean
  public BnplScoringCompleteListener bnplScoringCompleteListener(AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    return new BnplScoringCompleteListener(aimServiceRegistry, bpmsRepositoryRegistry);
  }

  @Bean
  public BnplScoringRejectedListener bnplScoringRejectedListener(BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    return new BnplScoringRejectedListener(bpmsRepositoryRegistry);
  }

  @Bean
  public BnplDisburseDebtIncomeRatioTask bnplDisburseDebtIncomeRatioTask(Environment environment)
  {
    return new BnplDisburseDebtIncomeRatioTask(environment);
  }

  @Bean
  public BnplSetDebtIncomeRatioListener bnplSetDebtIncomeRatioListener(AuthenticationService authenticationService, AuthorizationService authorizationService,
      ProcessRequestRepository processRequestRepository, ProcessRepository processRepository)
  {
    return new BnplSetDebtIncomeRatioListener(authenticationService, authorizationService, processRequestRepository, processRepository);
  }

  @Bean
  @Inject
  public BnplLoanDisbursementTask bnplLoanDisbursementTask(DirectOnlineCoreBankingService directOnlineCoreBankingService,
      ProcessRequestRepository processRequestRepository, Environment environment, AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    return new BnplLoanDisbursementTask(directOnlineCoreBankingService, processRequestRepository, environment, aimServiceRegistry, bpmsRepositoryRegistry);
  }

  @Bean
  @Inject
  public BnplAddTransactionTask bnplAddTransactionTask(DirectOnlineCoreBankingService directOnlineCoreBankingService, MessagingService messagingService,
      BpmsServiceRegistry bpmsServiceRegistry)
  {
    return new BnplAddTransactionTask(directOnlineCoreBankingService, messagingService, bpmsServiceRegistry);
  }

  @Bean
  public BnplSetAccountCreationDataTask bnplSetAccountCreationDataTask(BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    return new BnplSetAccountCreationDataTask(bpmsRepositoryRegistry);
  }

  @Bean
  public BnplAccountCreationCompleteListener bnplAccountCreationComplete(AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRepository processRepository)
  {
    return new BnplAccountCreationCompleteListener(authenticationService, authorizationService, processRepository);
  }

  @Bean
  public AccountCreationErrorListener accountCreationErrorListener(AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry, Environment environment)
  {
    return new AccountCreationErrorListener(aimServiceRegistry, bpmsRepositoryRegistry, environment);
  }

  @Bean
  public BnplGenerateLoanDecisionDocumentTask bnplGenerateLoanDecisionDocumentTask(AimServiceRegistry aimServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, MembershipRepository membershipRepository, GroupRepository groupRepository)

  {
    return new BnplGenerateLoanDecisionDocumentTask(aimServiceRegistry, bpmsRepositoryRegistry, membershipRepository, groupRepository);
  }

  @Bean
  public RefundTransactionTask refundTransactionTask(DirectOnlineCoreBankingService directOnlineCoreBankingService,
      ProcessRequestRepository processRequestRepository, BnplCoreBankingService bnplCoreBankingService)
  {
    return new RefundTransactionTask(directOnlineCoreBankingService, processRequestRepository, bnplCoreBankingService);
  }

  @Bean
  public RefundTransactionListener refundTransactionListener(AuthenticationService authenticationService, AuthorizationService authorizationService,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, ProcessRepository processRepository, ProcessRequestRepository processRequestRepository, Environment environment)
  {
    return new RefundTransactionListener(authenticationService, authorizationService, bpmsRepositoryRegistry, processRepository, processRequestRepository, environment);
  }

  @Bean
  public UploadFilesToLdmsTask uploadFilesToLdmsTask(AimServiceRegistry aimServiceRegistry, BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    return new UploadFilesToLdmsTask(aimServiceRegistry, bpmsServiceRegistry, bpmsRepositoryRegistry);
  }

  @Bean
  public UploadFileToLdmsErrorHandler uploadFileToLdmsErrorHandler(AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry,
      Environment environment)
  {
    return new UploadFileToLdmsErrorHandler(aimServiceRegistry, bpmsRepositoryRegistry, environment);
  }

  @Bean
  public PrepareLoanReportTask prepareLoanReport(BpmsServiceRegistry bpmsServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry,
      AimServiceRegistry aimServiceRegistry)
  {
    return new PrepareLoanReportTask(bpmsServiceRegistry, bpmsRepositoryRegistry, aimServiceRegistry);
  }
  @Bean
  public BnplUploadLoanContractAndOtherFiles uploadLoanContractAndOtherFiles(BpmsRepositoryRegistry bpmsRepositoryRegistry, AimServiceRegistry aimServiceRegistry,
      BpmsServiceRegistry bpmsServiceRegistry){
    return new BnplUploadLoanContractAndOtherFiles(bpmsRepositoryRegistry, aimServiceRegistry, bpmsServiceRegistry);
  }

  @Bean
  public BnplDisburseErrorHandler bnplDisburseErrorHandler(AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry, Environment environment)
  {
    return new BnplDisburseErrorHandler(aimServiceRegistry, bpmsRepositoryRegistry, environment);
  }

  @Bean
  public BnplProcessCompleteListener bnplProcessCompleteListener(ProcessRequestRepository processRequestRepository)
  {
    return new BnplProcessCompleteListener(processRequestRepository);
  }

  @Bean
  public TransactionFailedErrorHandler transactionFailedErrorHandler(AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry,
      Environment environment)
  {
    return new TransactionFailedErrorHandler(aimServiceRegistry, bpmsRepositoryRegistry, environment);
  }
}
