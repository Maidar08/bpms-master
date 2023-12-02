/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package consumption.config;

import javax.inject.Inject;

import consumption.case_listener.MbSessionStateListener;
import consumption.case_listener.OnlineSalaryAccountNumberListener;
import consumption.case_listener.OnlineSalaryAmountRejectedListener;
import consumption.case_listener.OnlineSalaryCompleteListener;
import consumption.case_listener.OnlineSalaryDisburseErrorListener;
import consumption.case_listener.OnlineSalaryElementaryRejectedListener;
import consumption.case_listener.OnlineSalaryScoringLogListener;
import consumption.case_listener.OnlineSalaryScoringRejectedListener;
import consumption.case_listener.OnlineSalarySetCalculationStateListener;
import consumption.case_listener.OnlineSalaryUpdateDisburseFailedStateListener;
import consumption.case_listener.SendCalculationCompleteSmsListener;
import consumption.process_listener.SaveCreatedLoanAccountListener;
import consumption.service_task.direct_online_salary.AddTransactionTask;
import consumption.service_task.direct_online_salary.CalculateMonthlyLoanPayment;
import consumption.service_task.direct_online_salary.CreateNdshEnquireTask;
import consumption.service_task.direct_online_salary.DirectOnlineCalculateLoanAmountTask;
import consumption.service_task.direct_online_salary.DirectOnlineDownloadCustomerInfoByCifNoTask;
import consumption.service_task.direct_online_salary.DirectOnlineGetLoanInfoTask;
import consumption.service_task.direct_online_salary.DirectOnlineLoanClosureTask;
import consumption.service_task.direct_online_salary.DirectOnlineLoanDisbursementTask;
import consumption.service_task.direct_online_salary.DirectOnlineSendDisbursementDetailSms;
import consumption.service_task.direct_online_salary.DirectOnlineSendEmailTask;
import consumption.service_task.direct_online_salary.DirectOnlineSetAccountDataTask;
import consumption.service_task.direct_online_salary.DisburseToDefaultAccountTask;
import consumption.service_task.direct_online_salary.DownloadGrantLoanAccountInfoTask;
import consumption.service_task.direct_online_salary.DownloadLoanPaymentScheduleTask;
import consumption.service_task.direct_online_salary.ElementaryCriteriaTask;
import consumption.service_task.direct_online_salary.GenerateLoanApplicationTask;
import consumption.service_task.direct_online_salary.GetMaxInterestRateTask;
import consumption.service_task.direct_online_salary.OnlineSalaryCreateLoanPaymentScheduleTask;
import consumption.service_task.direct_online_salary.OnlineSalaryDownloadLoanPaymentScheduleTask;
import consumption.service_task.direct_online_salary.OnlineSalaryGenerateLoanDecisionDocumentTask;
import consumption.service_task.direct_online_salary.PrepareLoanContractTask;
import consumption.service_task.direct_online_salary.SalaryCalculationTask;
import consumption.service_task.direct_online_salary.StartOnlineSalaryProcessTask;
import consumption.service_task.direct_online_salary.TransactRestAmountTask;
import consumption.service_task.direct_online_salary.error.OnlineSalaryDisburseErrorHandlerTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import mn.erin.bpm.domain.ohs.xac.XacNewCoreBankingService;
import mn.erin.common.mail.EmailService;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.DocumentInfoRepository;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.service.DocumentService;
import mn.erin.domain.bpm.service.LoanService;
import mn.erin.domain.bpm.service.MessagingService;
import mn.erin.domain.bpm.service.NewCoreBankingService;

/**
 * @author Lkhagvadorj.A
 */
@Configuration
//@Import({CoreBankingService.class, DirectOnlineCoreBankingService.class })
public class CamundaDirectOnlineBeanConfig
{
  // Direct Online
  @Bean
  public StartOnlineSalaryProcessTask testStartProcess(DefaultParameterRepository defaultParameterRepository, AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRepository processRepository)
  {
    return new StartOnlineSalaryProcessTask(defaultParameterRepository, authenticationService, authorizationService, processRepository);
  }

  @Bean
  public StartOnlineSalaryProcessTask startOnlineSalaryProcess(DefaultParameterRepository defaultParameterRepository,
      AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRepository processRepository)
  {
    return new StartOnlineSalaryProcessTask(defaultParameterRepository, authenticationService, authorizationService, processRepository);
  }

  @Bean
  @Inject
  public DirectOnlineDownloadCustomerInfoByCifNoTask directOnlineDownloadCustomerInfoByCifNo(
      XacNewCoreBankingService xacNewCoreBankingService, AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRequestRepository processRequestRepository)
  {
    return new DirectOnlineDownloadCustomerInfoByCifNoTask(xacNewCoreBankingService, authenticationService, authorizationService, processRequestRepository);
  }

  @Bean
  @Inject
  public CreateNdshEnquireTask createNdshEnquireTask(BpmsServiceRegistry bpmsServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry,
      AimServiceRegistry aimServiceRegistry, GroupRepository groupRepository)
  {
    return new CreateNdshEnquireTask(bpmsServiceRegistry, bpmsRepositoryRegistry, aimServiceRegistry, groupRepository);
  }

  @Bean
  @Inject
  public DirectOnlineCalculateLoanAmountTask directOnlineCalculateLoanAmount(BpmsServiceRegistry bpmsServiceRegistry,ProductRepository productRepository,
      DefaultParameterRepository defaultParameterRepository)
  {
    return new DirectOnlineCalculateLoanAmountTask(bpmsServiceRegistry, productRepository, defaultParameterRepository);
  }

  @Bean
  @Inject
  public DirectOnlineGetLoanInfoTask directOnlineGetLoanInfo(DirectOnlineCoreBankingService directOnlineCoreBankingService,
      NewCoreBankingService newCoreBankingService, ProductRepository productRepository, Environment environment)
  {
    return new DirectOnlineGetLoanInfoTask(directOnlineCoreBankingService, newCoreBankingService, productRepository, environment);
  }

  @Bean
  @Inject
  public DownloadGrantLoanAccountInfoTask downloadGrantLoanAccountInfo(DirectOnlineCoreBankingService directOnlineCoreBankingService,
      NewCoreBankingService newCoreBankingService, Environment environment)
  {
    return new DownloadGrantLoanAccountInfoTask(directOnlineCoreBankingService, newCoreBankingService, environment);
  }

  @Bean
  public SalaryCalculationTask salaryCalculationTask(DefaultParameterRepository defaultParameterRepository, AuthorizationService authorizationService,
      AuthenticationService authenticationService, ProcessRepository processRepository)
  {
    return new SalaryCalculationTask(defaultParameterRepository, authorizationService, authenticationService, processRepository);
  }

  @Bean
  @Inject
  public ElementaryCriteriaTask elementaryCriteriaTask(ProcessRequestRepository processRequestRepository)
  {
    return new ElementaryCriteriaTask(processRequestRepository);
  }

  @Bean
  @Inject
  public CalculateMonthlyLoanPayment calculateMonthlyLoanRepaymentAmount(LoanService loanService, Environment environment, DirectOnlineCoreBankingService directOnlineCoreBankingService, ProductRepository productRepository)
  {
    return new CalculateMonthlyLoanPayment(loanService,  environment, directOnlineCoreBankingService, productRepository);
  }

  @Bean
  @Inject
  public GenerateLoanApplicationTask generateLoanApplication(DocumentRepository documentRepository, AuthenticationService authenticationService,
      AuthorizationService authorizationService, DocumentService documentService, MembershipRepository membershipRepository,
      DocumentInfoRepository documentInfoRepository, AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    return new GenerateLoanApplicationTask(documentRepository, authenticationService, authorizationService, documentService, membershipRepository,
        documentInfoRepository, aimServiceRegistry, bpmsRepositoryRegistry);
  }

  @Bean
  public DirectOnlineSetAccountDataTask onlineSalarySetAccountData(BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    return new DirectOnlineSetAccountDataTask(bpmsRepositoryRegistry);
  }

  @Bean
  @Inject
  public OnlineSalaryGenerateLoanDecisionDocumentTask onlineSalaryGenerateLoanDecisionDocumentTask(AimServiceRegistry aimServiceRegistry,
      DocumentService documentService, BpmsRepositoryRegistry bpmsRepositoryRegistry, MembershipRepository membershipRepository, UserRepository userRepository,
      RoleRepository roleRepository, GroupRepository groupRepository)
  {
    return new OnlineSalaryGenerateLoanDecisionDocumentTask(aimServiceRegistry, documentService, bpmsRepositoryRegistry,
        membershipRepository, userRepository, roleRepository, groupRepository);
  }

  @Bean
  @Inject
  public OnlineSalaryCreateLoanPaymentScheduleTask onlineSalaryCreateLoanPaymentScheduleTask(AuthenticationService authenticationService,
      AuthorizationService authorizationService , CaseService caseService, DocumentRepository documentRepository)
  {
    return new OnlineSalaryCreateLoanPaymentScheduleTask(authenticationService, authorizationService, caseService, documentRepository);
  }

  @Bean
  @Inject
  public OnlineSalaryDownloadLoanPaymentScheduleTask onlineSalaryDownloadLoanPaymentScheduleTask(BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, AimServiceRegistry aimServiceRegistry)
  {
    return new OnlineSalaryDownloadLoanPaymentScheduleTask(bpmsServiceRegistry, bpmsRepositoryRegistry, aimServiceRegistry);
  }

  @Bean
  @Inject
  public DownloadLoanPaymentScheduleTask downloadLoanPaymentSchedule(BpmsRepositoryRegistry bpmsRepositoryRegistry, BpmsServiceRegistry bpmsServiceRegistry,
      AimServiceRegistry aimServiceRegistry)
  {
    return new DownloadLoanPaymentScheduleTask(bpmsServiceRegistry, bpmsRepositoryRegistry,
        aimServiceRegistry);
  }

  @Bean
  @Inject
  public PrepareLoanContractTask prepareLoanContract(BpmsRepositoryRegistry bpmsRepositoryRegistry, BpmsServiceRegistry bpmsServiceRegistry,
      AimServiceRegistry aimServiceRegistry)
  {
    return new PrepareLoanContractTask(bpmsServiceRegistry, bpmsRepositoryRegistry, aimServiceRegistry);
  }

  @Bean
  GetMaxInterestRateTask getMaxInterestRateTask(ProductRepository productRepository)
  {
    return new GetMaxInterestRateTask(productRepository);
  }

  @Bean
  public DirectOnlineLoanClosureTask directOnlineLoanClosure(DirectOnlineCoreBankingService directOnlineCoreBankingService, ProcessRequestRepository processRequestRepository)
  {
    return new DirectOnlineLoanClosureTask(directOnlineCoreBankingService, processRequestRepository);
  }

  @Bean
  @Inject
  public DirectOnlineLoanDisbursementTask directOnlineLoanDisbursement(DirectOnlineCoreBankingService directOnlineCoreBankingService, BpmsRepositoryRegistry bpmsRepositoryRegistry, AimServiceRegistry aimServiceRegistry,
     Environment environment)
  {
    return new DirectOnlineLoanDisbursementTask(directOnlineCoreBankingService, bpmsRepositoryRegistry, aimServiceRegistry, environment);
  }

  @Bean
  public DirectOnlineSendEmailTask directOnlineSendEmailTask(EmailService emailService, BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    return new DirectOnlineSendEmailTask(emailService, bpmsRepositoryRegistry);
  }

  @Bean
  public DirectOnlineSendDisbursementDetailSms directOnlineSendDisbursementDetailSms(MessagingService messagingService)
  {
    return new DirectOnlineSendDisbursementDetailSms(messagingService);
  }

  @Bean
  @Inject
  public DisburseToDefaultAccountTask disburseToDefaultAccountTask(DirectOnlineCoreBankingService directOnlineCoreBankingService, Environment environment)
  {
    return new DisburseToDefaultAccountTask(directOnlineCoreBankingService, environment);
  }

  @Bean
  @Inject
  public AddTransactionTask addTransactionFcTask(DirectOnlineCoreBankingService directOnlineCoreBankingService,
      ProcessRequestRepository processRequestRepository, Environment environment)
  {
    return new AddTransactionTask(directOnlineCoreBankingService, processRequestRepository, environment);
  }

  @Bean
  @Inject
  public TransactRestAmountTask transactRestAmountTask(DirectOnlineCoreBankingService directOnlineCoreBankingService,
      ProcessRequestRepository processRequestRepository)
  {
    return new TransactRestAmountTask(directOnlineCoreBankingService, processRequestRepository);
  }

  @Bean
  public OnlineSalaryDisburseErrorHandlerTask disburseErrorHandlerTask(AimServiceRegistry aimServiceRegistry, ProcessRequestRepository processRequestRepository,
      DefaultParameterRepository defaultParameterRepository, BpmsRepositoryRegistry bpmsRepositoryRegistry, Environment environment)
  {
    return new OnlineSalaryDisburseErrorHandlerTask(processRequestRepository, aimServiceRegistry, defaultParameterRepository, bpmsRepositoryRegistry,
        environment);
  }

  // Case Listener
  @Bean
  public OnlineSalaryScoringLogListener onlineSalaryScoringLogListener()
  {
    return new OnlineSalaryScoringLogListener();
  }

  @Bean
  public OnlineSalaryScoringRejectedListener onlineSalaryScoringRejected(AimServiceRegistry aimServiceRegistry, BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, GroupRepository groupRepository, TenantIdProvider tenantIdProvider)
  {
    return new OnlineSalaryScoringRejectedListener(aimServiceRegistry,
        bpmsServiceRegistry, bpmsRepositoryRegistry, groupRepository, tenantIdProvider);
  }

  @Bean
  public SendCalculationCompleteSmsListener sendCalculationCompleteSms(MessagingService messagingService)
  {
    return new SendCalculationCompleteSmsListener(messagingService);
  }

  @Bean
  public OnlineSalarySetCalculationStateListener onlineSalarySetCalculationState(AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRequestRepository processRequestRepository, ProcessRepository processRepository)
  {
    return new OnlineSalarySetCalculationStateListener(authenticationService, authorizationService, processRequestRepository, processRepository);
  }

  @Bean
  public OnlineSalaryElementaryRejectedListener onlineSalaryElementaryRejectedListener(AimServiceRegistry aimServiceRegistry,
      BpmsServiceRegistry bpmsServiceRegistry, TenantIdProvider tenantIdProvider, BpmsRepositoryRegistry bpmsRepositoryRegistry,
      GroupRepository groupRepository)
  {
    return new OnlineSalaryElementaryRejectedListener(aimServiceRegistry, bpmsServiceRegistry, bpmsRepositoryRegistry, tenantIdProvider, groupRepository);
  }

  @Bean
  public OnlineSalaryAmountRejectedListener onlineSalaryAmountRejectedListener(AimServiceRegistry aimServiceRegistry, BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, GroupRepository groupRepository, TenantIdProvider tenantIdProvider)
  {
    return new OnlineSalaryAmountRejectedListener(aimServiceRegistry, bpmsServiceRegistry, bpmsRepositoryRegistry, groupRepository, tenantIdProvider);
  }

  @Bean
  public OnlineSalaryAccountNumberListener onlineSalaryAccountNumberListener(AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      ProcessRepository processRepository)
  {
    return new OnlineSalaryAccountNumberListener(authenticationService, authorizationService, processRepository);
  }

  @Bean
  public MbSessionStateListener mbSessionStateListener(ProcessRequestRepository processRequestRepository)
  {
    return new MbSessionStateListener(processRequestRepository);
  }

  @Bean
  public OnlineSalaryDisburseErrorListener onlineSalaryDisburseErrorListener()
  {
    return new OnlineSalaryDisburseErrorListener();
  }

  @Bean
  @Inject
  public OnlineSalaryUpdateDisburseFailedStateListener updateDisburseFailedStateListener(ProcessRequestRepository processRequestRepository)
  {
    return new OnlineSalaryUpdateDisburseFailedStateListener(processRequestRepository);
  }

  @Bean
  public OnlineSalaryCompleteListener onlineSalaryCompleteListener(ProcessRequestRepository processRequestRepository)
  {
    return new OnlineSalaryCompleteListener(processRequestRepository);
  }

  @Bean
  public SaveCreatedLoanAccountListener saveCreatedLoanAccountListener(AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    return new SaveCreatedLoanAccountListener(aimServiceRegistry, bpmsRepositoryRegistry);
  }
}
