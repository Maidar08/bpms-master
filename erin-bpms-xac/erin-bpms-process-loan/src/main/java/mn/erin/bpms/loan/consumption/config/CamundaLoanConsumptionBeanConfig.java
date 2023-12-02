/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import mn.erin.bpm.domain.ohs.xac.XacNewCoreBeanConfig;
import mn.erin.bpms.loan.consumption.case_listener.CustomerRejectedListener;
import mn.erin.bpms.loan.consumption.case_listener.DisableCoBorrowerProcessesListener;
import mn.erin.bpms.loan.consumption.case_listener.DisableElementaryCriteriaListener;
import mn.erin.bpms.loan.consumption.case_listener.DisableEnabledExecutionsListener;
import mn.erin.bpms.loan.consumption.case_listener.DisableLoanSendProcessListener;
import mn.erin.bpms.loan.consumption.case_listener.DownloadCollateralInfoByCifListener;
import mn.erin.bpms.loan.consumption.case_listener.LogVariableCaseListener;
import mn.erin.bpms.loan.consumption.case_listener.SetCollAccountStageStartedListener;
import mn.erin.bpms.loan.consumption.case_listener.SetCollateralRelatedVariablesListener;
import mn.erin.bpms.loan.consumption.case_listener.TerminateCollateralListListener;
import mn.erin.bpms.loan.consumption.case_listener.TerminateCreateCollateralListener;
import mn.erin.bpms.loan.consumption.case_listener.TriggerCollateralListAfterSalaryListener;
import mn.erin.bpms.loan.consumption.case_listener.TriggerCollateralListListener;
import mn.erin.bpms.loan.consumption.case_listener.micro.CheckIfAmountCalculationIsActiveListener;
import mn.erin.bpms.loan.consumption.case_listener.micro.DisableCollateralExecutionsListener;
import mn.erin.bpms.loan.consumption.case_listener.micro.TerminateAndDisableTasksListener;
import mn.erin.bpms.loan.consumption.service_task.CalculateInterestRateTask;
import mn.erin.bpms.loan.consumption.service_task.CleanFieldsTask;
import mn.erin.bpms.loan.consumption.service_task.ClearRelatedUserTaskIdVariableTask;
import mn.erin.bpms.loan.consumption.service_task.CreateCollateralLoanAccountTaskMicro;
import mn.erin.bpms.loan.consumption.service_task.DateToNumberTask;
import mn.erin.bpms.loan.consumption.service_task.DownloadOrganizationLevelTask;
import mn.erin.bpms.loan.consumption.service_task.GetBranchTask;
import mn.erin.bpms.loan.consumption.service_task.GetBranchTaskMicro;
import mn.erin.bpms.loan.consumption.service_task.GetLoanPeriodFromXacTask;
import mn.erin.bpms.loan.consumption.service_task.GetResourceFromXacTask;
import mn.erin.bpms.loan.consumption.service_task.GetResourceToLoanAmountRatioTask;
import mn.erin.bpms.loan.consumption.service_task.SendEmailTaskForLoanDecision;
import mn.erin.bpms.loan.consumption.service_task.SendEmailTaskForSendLoanDecision;
import mn.erin.bpms.loan.consumption.service_task.SetAccountCreationFieldsTask;
import mn.erin.bpms.loan.consumption.service_task.SetAssetToLoanRatioTask;
import mn.erin.bpms.loan.consumption.service_task.SetCreateCollateralRelatedUserTaskIdTask;
import mn.erin.bpms.loan.consumption.service_task.SetScoringLevelTask;
import mn.erin.bpms.loan.consumption.service_task.SetVariablesBeforeMicroAccountCreationTask;
import mn.erin.bpms.loan.consumption.service_task.SignUpCollateralTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.CreateCollateralLoanAccountTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.CreateLoanAccountTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.DisableDownloadEnquireListener;
import mn.erin.bpms.loan.consumption.service_task.bpms.DisableTasksAfterAccountCreationTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.LoanDecisionTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.ManuallyStartRelatedTaskListener;
import mn.erin.bpms.loan.consumption.service_task.bpms.MicroLoanDecisionTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.MortgageLoanDecisionTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.SendDirectorListener;
import mn.erin.bpms.loan.consumption.service_task.bpms.SetBankRejectedStateTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.SetCaseInstanceIdListener;
import mn.erin.bpms.loan.consumption.service_task.bpms.SetCustomerRejectedStateTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.SetLoanAmountCalculationData;
import mn.erin.bpms.loan.consumption.service_task.bpms.SetRequestConfirmedStateTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.SetSentUserTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.VariableLoggerTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CleanCoBorrowerFieldListener;
import mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CleanCoBorrowerRegNumTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CleanMicroCoBorrowerMongolBankFieldsTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.SetCoBorrowerIndexListener;
import mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.TerminateCoBoActiveEnquireTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.VerifyCoBoRemovedServiceTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.collateral.CreateImmovableCollateralServiceTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.collateral.CreateMachineryCollateralTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.collateral.CreateOtherCollateralServiceTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.collateral.CreateVehicleCollateralServiceTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.collateral.GenerateCollateralIdServiceTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.collateral.LinkCollateralsServiceTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.collateral.SaveCollateralTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.collateral.UpdateCollateralListTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.contract.CreateLoanContractTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.contract.CreateLoanPaymentScheduleTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.contract.CreateOnlineSalaryLoanContractTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.contract.LogContractPrepCompletedListener;
import mn.erin.bpms.loan.consumption.service_task.bpms.contract.SetLoanPreparationBoolean;
import mn.erin.bpms.loan.consumption.service_task.bpms.elementary_criteria.CleanCriteriaPassedFieldListener;
import mn.erin.bpms.loan.consumption.service_task.bpms.elementary_criteria.EnableElementaryCriteriaListener;
import mn.erin.bpms.loan.consumption.service_task.bpms.elementary_criteria.ManuallyStartElementaryCriteriaListener;
import mn.erin.bpms.loan.consumption.service_task.bpms.elementary_criteria.ManuallyStartSalaryCalculationListener;
import mn.erin.bpms.loan.consumption.service_task.bpms.loan_decision.GenerateLoanDecisionDocumentTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.loan_decision.GenerateMicroLoanDecisionTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.loan_decision.GenerateMortgageLoanDecisionTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountNewCoreTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.new_core.SetCreatedUserBeforeLoanAccountTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.report.CreateLoanReportTask;
import mn.erin.bpms.loan.consumption.service_task.bpms.scoring.DisableScoringExecutionListener;
import mn.erin.bpms.loan.consumption.service_task.bpms.scoring.SetVariablesBeforeMicroScoring;
import mn.erin.bpms.loan.consumption.service_task.calculation.CalculateLoanAmountMicroTask;
import mn.erin.bpms.loan.consumption.service_task.calculation.CalculateLoanAmountTask;
import mn.erin.bpms.loan.consumption.service_task.calculation.DeactivateSalaryCalculation;
import mn.erin.bpms.loan.consumption.service_task.calculation.GenerateCalculationInfoTask;
import mn.erin.bpms.loan.consumption.service_task.calculation.LoanAmountValidationTask;
import mn.erin.bpms.loan.consumption.service_task.calculation.ManuallyStartCalculateLoanAmountStage;
import mn.erin.bpms.loan.consumption.service_task.calculation.ManuallyStartCalculationStageListener;
import mn.erin.bpms.loan.consumption.service_task.calculation.MicroLoanAmountValidationTask;
import mn.erin.bpms.loan.consumption.service_task.calculation.SetFieldsAfterLoanAmountCalculationTask;
import mn.erin.bpms.loan.consumption.service_task.calculation.micro.ManuallyStartMicroCalculationStageListener;
import mn.erin.bpms.loan.consumption.service_task.calculation.micro.ManuallyStartMortgageCalculationStageListener;
import mn.erin.bpms.loan.consumption.service_task.calculation.micro.MicroBalanceCalculationTask;
import mn.erin.bpms.loan.consumption.service_task.calculation.micro.MicroSimpleCalculationTask;
import mn.erin.bpms.loan.consumption.service_task.calculation.micro.SetAccountCreationFieldsTaskMicro;
import mn.erin.bpms.loan.consumption.service_task.calculation.micro.SetAccountFieldsAfterAmountCalcTask;
import mn.erin.bpms.loan.consumption.service_task.calculation.mortgage.BusinessCalculationMortgageTask;
import mn.erin.bpms.loan.consumption.service_task.calculation.mortgage.CalculateMortgageLoanAmountTask;
import mn.erin.bpms.loan.consumption.service_task.calculation.mortgage.MortgageLoanAmountValidation;
import mn.erin.bpms.loan.consumption.service_task.calculation.mortgage.SetLastCalculationTypeBusinessTask;
import mn.erin.bpms.loan.consumption.service_task.calculation.mortgage.SetLastCalculationTypeSalaryTask;
import mn.erin.bpms.loan.consumption.service_task.co_borrower.DeleteCoBorrowerVariablesTask;
import mn.erin.bpms.loan.consumption.service_task.co_borrower.UpdateCoBorrowerVariablesTask;
import mn.erin.bpms.loan.consumption.service_task.core_bank.DownloadCoBorrowerInfoTask;
import mn.erin.bpms.loan.consumption.service_task.core_bank.DownloadCustomerInfoByCifNoTask;
import mn.erin.bpms.loan.consumption.service_task.core_bank.DownloadCustomerInfoByRegNoTask;
import mn.erin.bpms.loan.consumption.service_task.mongol_bank.ConfirmLoanEnquireFromMongolBankTask;
import mn.erin.bpms.loan.consumption.service_task.mongol_bank.DownloadCoBorrowerInfoFromMongolBankTask;
import mn.erin.bpms.loan.consumption.service_task.mongol_bank.DownloadInfoFromMongolBankNewTask;
import mn.erin.bpms.loan.consumption.service_task.mongol_bank.DownloadLoanInfoFromMongolBankTask;
import mn.erin.bpms.loan.consumption.service_task.mongol_bank.DownloadLoanInfoFromMongolBankTaskExtended;
import mn.erin.bpms.loan.consumption.service_task.mongol_bank.GetCustomerCIDFromMongolBankTask;
import mn.erin.bpms.loan.consumption.service_task.mongol_bank.GetEnquireByCoBorrowerFromMongolBankTask;
import mn.erin.bpms.loan.consumption.service_task.mongol_bank.GetLoanEnquireFromMongolBankTask;
import mn.erin.bpms.loan.consumption.service_task.mongol_bank.GetLoanEnquirePdfFromMongolBankTask;
import mn.erin.bpms.loan.consumption.service_task.mongol_bank.GetLoanInfoFromMongolBankTask;
import mn.erin.bpms.loan.consumption.service_task.mortgage.SetMaxLoanAmountMortgageTask;
import mn.erin.bpms.loan.consumption.service_task.risky_customer.CheckRiskyCoBorrowerTask;
import mn.erin.bpms.loan.consumption.service_task.risky_customer.CheckRiskyCustomerTask;
import mn.erin.bpms.loan.consumption.service_task.sequence.CompleteCalculationStageListener;
import mn.erin.bpms.loan.consumption.service_task.sequence.CreateLoanPaymentScheduleListener;
import mn.erin.bpms.loan.consumption.service_task.sequence.DisableXypMongolBankTaskListener;
import mn.erin.bpms.loan.consumption.service_task.sequence.SetInitialVariablesTask;
import mn.erin.bpms.loan.consumption.service_task.xyp_system.DownloadAddressInfoCoBorrowerTask;
import mn.erin.bpms.loan.consumption.service_task.xyp_system.DownloadAddressInfoFromXypTask;
import mn.erin.bpms.loan.consumption.service_task.xyp_system.DownloadIDCardInfoCoBorrowerTask;
import mn.erin.bpms.loan.consumption.service_task.xyp_system.DownloadIDCardInfoFromXypTask;
import mn.erin.bpms.loan.consumption.service_task.xyp_system.DownloadPropertyCoBorrowerFromXypTask;
import mn.erin.bpms.loan.consumption.service_task.xyp_system.DownloadPropertyInfoFromXypTask;
import mn.erin.bpms.loan.consumption.service_task.xyp_system.DownloadSalaryInfoCoBorrowerTask;
import mn.erin.bpms.loan.consumption.service_task.xyp_system.DownloadSalaryInfoFromXypTask;
import mn.erin.bpms.loan.consumption.service_task.xyp_system.DownloadVehicleCoBorrowerFromXypTask;
import mn.erin.bpms.loan.consumption.service_task.xyp_system.DownloadVehicleInfoFromXypTask;
import mn.erin.bpms.loan.consumption.service_task.xyp_system.SetDownloadSalaryInfoConditionListener;
import mn.erin.bpms.loan.consumption.task_listener.DisableElementaryCriteriaTaskListener;
import mn.erin.bpms.loan.consumption.task_listener.SaveDescriptionTaskListener;
import mn.erin.bpms.loan.consumption.task_listener.SaveReasonTaskListener;
import mn.erin.bpms.loan.consumption.task_listener.SetParentTaskIdTaskListener;
import mn.erin.bpms.loan.consumption.task_listener.SetRootParentIdTaskListener;
import mn.erin.bpms.loan.consumption.task_listener.TerminateLoanCalculationStageListener;
import mn.erin.bpms.loan.consumption.task_listener.UpdateContractParamsListener;
import mn.erin.bpms.loan.consumption.task_listener.UpdateLoanAmountTaskListener;
import mn.erin.bpms.loan.consumption.task_listener.UserNameTaskListener;
import mn.erin.bpms.loan.consumption.task_listener.mortgage.SetProductNameTaskListener;
import mn.erin.bpms.loan.consumption.task_listener.mortgage.UpdateMortgageLoanAmountTaskListener;
import mn.erin.bpms.process.base.CamundaBeanConfig;
import mn.erin.common.mail.EmailService;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.bpm.provider.FingerPrintProvider;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.CollateralProductRepository;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.repository.ErrorMessageRepository;
import mn.erin.domain.bpm.repository.OrganizationLeasingRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.CoreBankingService;
import mn.erin.domain.bpm.service.CustomerService;
import mn.erin.domain.bpm.service.LoanService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.service.OrganizationService;
import mn.erin.domain.bpm.service.TaskFormService;

/**
 * @author EBazarragchaa
 */
@Configuration
@Import({ CamundaBeanConfig.class, XacNewCoreBeanConfig.class })
public class CamundaLoanConsumptionBeanConfig {
  @Bean
  public DisableTasksAfterAccountCreationTask disableTasksAfterAccountCreationTask() {
    return new DisableTasksAfterAccountCreationTask();
  }

  @Bean
  public LinkCollateralsServiceTask linkCollateralsServiceTask(Environment environment,
      NewCoreBankingService newCoreBankingService) {
    return new LinkCollateralsServiceTask(environment, newCoreBankingService);
  }

  @Bean
  public SetRootParentIdTaskListener setRootParentIdTaskListener() {
    return new SetRootParentIdTaskListener();
  }

  @Bean
  @Inject
  public CreateImmovableCollateralServiceTask createImmovableCollateralServiceTask(
      AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRepository processRepository,
      NewCoreBankingService newCoreBankingService) {
    return new CreateImmovableCollateralServiceTask(authenticationService, authorizationService, processRepository,
        newCoreBankingService);
  }

  @Bean
  public CreateMachineryCollateralTask createMachineryCollateralTask(AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRepository processRepository,
      NewCoreBankingService newCoreBankingService) {
    return new CreateMachineryCollateralTask(authenticationService, authorizationService, processRepository,
        newCoreBankingService);
  }

  @Bean
  @Inject
  public CreateVehicleCollateralServiceTask createVehicleCollateralServiceTask(
      NewCoreBankingService newCoreBankingService) {
    return new CreateVehicleCollateralServiceTask(newCoreBankingService);
  }

  @Bean
  @Inject
  public CreateOtherCollateralServiceTask createOtherCollateralServiceTask(
      NewCoreBankingService newCoreBankingService) {
    return new CreateOtherCollateralServiceTask(newCoreBankingService);
  }

  @Bean
  public SetDownloadSalaryInfoConditionListener setDownloadSalaryInfoConditionListener() {
    return new SetDownloadSalaryInfoConditionListener();
  }

  @Bean
  @Inject
  public DownloadVehicleCoBorrowerFromXypTask downloadVehicleCoBorrowerFromXypTask(
      AuthenticationService authenticationService,
      CustomerService customerService,
      GroupRepository groupRepository,
      MembershipRepository membershipRepository,
      DocumentRepository documentRepository) {
    return new DownloadVehicleCoBorrowerFromXypTask(authenticationService,
        customerService,
        groupRepository,
        membershipRepository,
        documentRepository);
  }

  @Bean
  @Inject
  public DownloadVehicleInfoFromXypTask downloadVehicleInfoFromXypTask(AuthenticationService authenticationService,
      CustomerService customerService,
      GroupRepository groupRepository,
      MembershipRepository membershipRepository,
      DocumentRepository documentRepository) {
    return new DownloadVehicleInfoFromXypTask(authenticationService,
        customerService,
        groupRepository,
        membershipRepository,
        documentRepository);
  }

  @Bean
  public CleanMicroCoBorrowerMongolBankFieldsTask cleanMicroCoBorrowerMongolBankFieldsTask() {
    return new CleanMicroCoBorrowerMongolBankFieldsTask();
  }

  @Bean
  @Inject
  public SetAccountFieldsAfterAmountCalcTask setAccountFieldsAfterAmountCalcTask(
      NewCoreBankingService newCoreBankingService,
      AuthenticationService authenticationService) {
    return new SetAccountFieldsAfterAmountCalcTask(newCoreBankingService, authenticationService);
  }

  @Bean
  @Inject
  public LoanAmountValidationTask loanAmountValidationTask(ProductRepository productRepository) {
    return new LoanAmountValidationTask(productRepository);
  }

  @Bean
  @Inject
  public GenerateMicroLoanDecisionTask generateMicroLoanDecisionTask(AuthenticationService authenticationService,
      MembershipRepository membershipRepository,
      GroupRepository groupRepository, DocumentRepository documentRepository,
      RoleRepository roleRepository, UserRepository userRepository, ProcessRepository processRepository,
      ProductRepository productRepository) {
    return new GenerateMicroLoanDecisionTask(authenticationService, membershipRepository, groupRepository,
        documentRepository, roleRepository, userRepository, processRepository, productRepository);
  }

  @Bean
  public MicroLoanDecisionTask microLoanDecisionTask(AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      MembershipRepository membershipRepository, BpmsRepositoryRegistry bpmsRepositoryRegistry) {
    return new MicroLoanDecisionTask(authenticationService, authorizationService, membershipRepository,
        bpmsRepositoryRegistry);
  }

  @Bean
  @Inject
  public DownloadCollateralInfoByCifListener downloadCollateralInfoByCifListener(CoreBankingService coreBankingService,
      NewCoreBankingService newCoreBankingService) {
    return new DownloadCollateralInfoByCifListener(coreBankingService, newCoreBankingService);
  }

  @Bean
  public SetCollateralRelatedVariablesListener setCollateralRelatedVariablesListener() {
    return new SetCollateralRelatedVariablesListener();
  }

  @Bean
  public DisableCollateralExecutionsListener disableCollateralExecutionsListener() {
    return new DisableCollateralExecutionsListener();
  }

  @Bean
  public SetCollAccountStageStartedListener setCollAccountStageStartedListener() {
    return new SetCollAccountStageStartedListener();
  }

  @Bean
  @Inject
  public UserNameTaskListener userNameTaskListener(AuthenticationService authenticationService,
      UserRepository userRepository) {
    return new UserNameTaskListener(authenticationService, userRepository);
  }

  @Bean
  public TerminateCollateralListListener terminateCollateralListListener() {
    return new TerminateCollateralListListener();
  }

  @Bean
  public TriggerCollateralListListener triggerCollateralListStageListener() {
    return new TriggerCollateralListListener();
  }

  @Bean
  public LogVariableCaseListener logVariableCaseListener() {
    return new LogVariableCaseListener();
  }

  @Bean
  @Inject
  public TriggerCollateralListAfterSalaryListener triggerCollateralListAfterSalaryListener(
      ProductRepository productRepository) {
    return new TriggerCollateralListAfterSalaryListener(productRepository);
  }

  @Bean
  public TerminateCreateCollateralListener terminateCreateCollateralListener() {
    return new TerminateCreateCollateralListener();
  }

  @Bean
  public ManuallyStartCalculateLoanAmountStage manuallyStartCalculateLoanAmountStage() {
    return new ManuallyStartCalculateLoanAmountStage();
  }

  @Bean
  public TerminateLoanCalculationStageListener terminateLoanCalculationStageListener() {
    return new TerminateLoanCalculationStageListener();
  }

  @Bean
  public VerifyCoBoRemovedServiceTask verifyCoBoRemovedServiceTask() {
    return new VerifyCoBoRemovedServiceTask();
  }

  @Bean
  public TerminateCoBoActiveEnquireTask terminateCoBoActiveEnquireTask() {
    return new TerminateCoBoActiveEnquireTask();
  }

  @Bean
  public DisableCoBorrowerProcessesListener disableCoBorrowerProcessesListener() {
    return new DisableCoBorrowerProcessesListener();
  }

  @Bean
  @Inject
  public SaveDescriptionTaskListener saveDescriptionTaskListener(AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      UserRepository userRepository, ProcessRepository processRepository) {
    return new SaveDescriptionTaskListener(authenticationService, authorizationService, userRepository,
        processRepository);
  }

  @Bean
  @Inject
  public SaveReasonTaskListener saveReasonTaskListener(AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      UserRepository userRepository, ProcessRepository processRepository) {
    return new SaveReasonTaskListener(authorizationService, authenticationService, userRepository, processRepository);
  }

  @Bean
  public SetParentTaskIdTaskListener setParentTaskIdTaskListener() {
    return new SetParentTaskIdTaskListener();
  }

  @Bean
  @Inject
  public UpdateLoanAmountTaskListener updateLoanAmountTaskListener(AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      ProcessRequestRepository processRequestRepository) {
    return new UpdateLoanAmountTaskListener(authenticationService, authorizationService, processRequestRepository);
  }

  @Bean
  @Inject
  public UpdateMortgageLoanAmountTaskListener updateMortgageLoanAmountTaskListener(
      AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      ProcessRequestRepository processRequestRepository) {
    return new UpdateMortgageLoanAmountTaskListener(authenticationService, authorizationService,
        processRequestRepository);
  }

  @Bean
  public CheckIfAmountCalculationIsActiveListener checkIfAmountCalculationIsActiveListener() {
    return new CheckIfAmountCalculationIsActiveListener();
  }

  @Bean
  public DisableLoanSendProcessListener disableLoanSendProcessListener() {
    return new DisableLoanSendProcessListener();
  }

  @Bean
  public DisableElementaryCriteriaTaskListener disableElementaryCriteriaTaskListener() {
    return new DisableElementaryCriteriaTaskListener();
  }

  @Bean
  public DisableElementaryCriteriaListener disableElementaryCriteriaListener() {
    return new DisableElementaryCriteriaListener();
  }

  @Bean
  public DisableEnabledExecutionsListener disableEnabledExecutionsListener() {
    return new DisableEnabledExecutionsListener();
  }

  @Bean
  public DisableScoringExecutionListener disableScoringExecutionListener() {
    return new DisableScoringExecutionListener();
  }

  @Bean
  public EnableElementaryCriteriaListener enableElementaryCriteriaListener() {
    return new EnableElementaryCriteriaListener();
  }

  @Bean
  public LogContractPrepCompletedListener logContractPrepCompletedListener() {
    return new LogContractPrepCompletedListener();
  }

  @Bean
  @Inject
  public UpdateContractParamsListener updateContractParamsListener(AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      ProcessRepository processRepository, TaskFormService taskFormService) {
    return new UpdateContractParamsListener(authenticationService, authorizationService, processRepository,
        taskFormService);
  }

  @Bean
  @Inject
  public CreateLoanContractTask createLoanContractTask(AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      CaseService caseService, DocumentRepository documentRepository) {
    return new CreateLoanContractTask(authenticationService, authorizationService, caseService, documentRepository);
  }

  @Bean
  @Inject
  public CreateOnlineSalaryLoanContractTask createOnlineSalaryLoanContractTask(
      AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      CaseService caseService, DocumentRepository documentRepository) {
    return new CreateOnlineSalaryLoanContractTask(authenticationService, authorizationService, caseService,
        documentRepository);
  }

  @Bean
  public CustomerRejectedListener customerRejectedListener() {
    return new CustomerRejectedListener();
  }

  @Bean
  public SetInitialVariablesTask setInitialVariablesTask() {
    return new SetInitialVariablesTask();
  }

  @Bean
  public DisableXypMongolBankTaskListener disableXypMongolBankTaskListener() {
    return new DisableXypMongolBankTaskListener();
  }

  @Bean
  public CompleteCalculationStageListener completeCalculationStageListener() {
    return new CompleteCalculationStageListener();
  }

  @Bean
  @Inject
  public GenerateLoanDecisionDocumentTask generateLoanDecisionDocumentTask(AuthenticationService authenticationService,
      MembershipRepository membershipRepository,
      GroupRepository groupRepository, DocumentRepository documentRepository,
      RoleRepository roleRepository, UserRepository userRepository, ProcessRepository processRepository,
      NewCoreBankingService newCoreBankingService) {
    return new GenerateLoanDecisionDocumentTask(authenticationService, membershipRepository, groupRepository,
        documentRepository, roleRepository, userRepository, processRepository, newCoreBankingService);
  }

  @Bean
  public ManuallyStartSalaryCalculationListener manuallyStartSalaryCalculationListener() {
    return new ManuallyStartSalaryCalculationListener();
  }

  @Bean
  public SetCaseInstanceIdListener setCaseInstanceIdListener() {
    return new SetCaseInstanceIdListener();
  }

  @Bean
  public CleanCriteriaPassedFieldListener cleanCriteriaPassedFieldListener() {
    return new CleanCriteriaPassedFieldListener();
  }

  @Bean
  public ManuallyStartElementaryCriteriaListener manuallyStartElementaryCriteriaListener() {
    return new ManuallyStartElementaryCriteriaListener();
  }

  @Bean
  public DisableDownloadEnquireListener disableDownloadEnquireListener() {
    return new DisableDownloadEnquireListener();
  }

  @Bean
  public DeactivateSalaryCalculation deactivateSalaryCalculation() {
    return new DeactivateSalaryCalculation();
  }

  @Bean
  public ManuallyStartCalculationStageListener manuallyStartCalculationStageListener() {
    return new ManuallyStartCalculationStageListener();
  }

  @Bean
  public CleanCoBorrowerRegNumTask cleanCoBorrowerRegNumTask() {
    return new CleanCoBorrowerRegNumTask();
  }

  // Co-borrower service tasks.
  @Bean
  public DownloadCoBorrowerInfoTask downloadCoBorrowerInfoTask(CoreBankingService coreBankingService,
      NewCoreBankingService newCoreBankingService,
      AuthenticationService authenticationService) {
    return new DownloadCoBorrowerInfoTask(coreBankingService, newCoreBankingService, authenticationService);
  }

  @Bean
  public CheckRiskyCoBorrowerTask checkRiskyCoBorrowerTask(NewCoreBankingService newCoreBankingService,
      AuthenticationService authenticationService) {
    return new CheckRiskyCoBorrowerTask(newCoreBankingService, authenticationService);
  }

  @Bean
  @Inject
  public DownloadAddressInfoCoBorrowerTask downloadAddressInfoCoBorrowerTask(CustomerService customerService,
      ErrorMessageRepository errorMessageRepository,
      FingerPrintProvider fingerPrintProvider, AuthenticationService authenticationService) {
    return new DownloadAddressInfoCoBorrowerTask(customerService, errorMessageRepository, authenticationService);
  }

  @Bean
  @Inject
  public DownloadIDCardInfoCoBorrowerTask downloadIDCardInfoCoBorrowerTask(CustomerService customerService,
      AuthenticationService authenticationService, GroupRepository groupRepository,
      MembershipRepository membershipRepository,
      DocumentRepository documentRepository) {
    return new DownloadIDCardInfoCoBorrowerTask(customerService, authenticationService, groupRepository,
        membershipRepository,
        documentRepository);
  }

  @Bean
  @Inject
  public DownloadSalaryInfoCoBorrowerTask downloadSalaryInfoCoBorrowerTask(CustomerService customerService,
      AuthenticationService authenticationService, GroupRepository groupRepository,
      MembershipRepository membershipRepository,
      DocumentRepository documentRepository) {
    return new DownloadSalaryInfoCoBorrowerTask(customerService, authenticationService, groupRepository,
        membershipRepository,
        documentRepository);
  }

  @Bean
  @Inject
  public DownloadCoBorrowerInfoFromMongolBankTask downloadCoBorrowerInfoFromMongolBankTask(LoanService loanService,
      AuthenticationService authenticationService,
      AuthorizationService authorizationService, MembershipRepository membershipRepository, Environment environment) {
    return new DownloadCoBorrowerInfoFromMongolBankTask(loanService, authenticationService, authorizationService,
        membershipRepository, environment);
  }

  @Bean
  public SetCoBorrowerIndexListener setCoBorrowerIndexListener() {
    return new SetCoBorrowerIndexListener();
  }

  @Bean
  public CleanCoBorrowerFieldListener cleanCoBorrowerFieldListener(AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      ProcessRepository repositoryRegistry) {
    return new CleanCoBorrowerFieldListener(authenticationService, authorizationService, repositoryRegistry);
  }

  // Loan calculation
  @Bean
  public CalculateLoanAmountTask calculateLoanAmountTask(AuthenticationService authenticationService,
      ProductRepository productRepository,
      DefaultParameterRepository defaultParameterRepository) {
    return new CalculateLoanAmountTask(authenticationService, productRepository, defaultParameterRepository);
  }

  // Micro Simple Calculation
  @Bean
  public MicroSimpleCalculationTask microSimpleCalculationTask() {
    return new MicroSimpleCalculationTask();
  }

  @Bean
  public ManuallyStartMicroCalculationStageListener manuallyStartMicroCalculationStageListener() {
    return new ManuallyStartMicroCalculationStageListener();
  }

  @Bean
  public ManuallyStartMortgageCalculationStageListener manuallyStartMortgageCalculationStageListener() {
    return new ManuallyStartMortgageCalculationStageListener();
  }

  @Bean
  @Inject
  public CalculateLoanAmountMicroTask calculateLoanAmountMicroTask(Environment environment,
      AuthenticationService authenticationService,
      ProductRepository productRepository) {
    return new CalculateLoanAmountMicroTask(environment, authenticationService, productRepository);
  }

  @Bean
  public MicroLoanAmountValidationTask microLoanAmountValidationTask() {
    return new MicroLoanAmountValidationTask();
  }

  @Bean
  public TerminateAndDisableTasksListener terminateAndDisableTasksListener() {
    return new TerminateAndDisableTasksListener();
  }

  // Mortgage
  @Bean
  public BusinessCalculationMortgageTask businessCalculationMortgageTask() {
    return new BusinessCalculationMortgageTask();
  }

  @Bean
  public GenerateMortgageLoanDecisionTask generateMortgageLoanDecisionTask(AuthenticationService authenticationService,
      MembershipRepository membershipRepository,
      GroupRepository groupRepository, DocumentRepository documentRepository,
      RoleRepository roleRepository, UserRepository userRepository) {
    return new GenerateMortgageLoanDecisionTask(authenticationService, membershipRepository, groupRepository,
        documentRepository, roleRepository, userRepository);
  }

  // Mongol bank services
  @Bean
  @Inject
  public DownloadLoanInfoFromMongolBankTask downloadLoanInfoFromMongolBankTask(LoanService loanService,
      AuthenticationService authenticationService,
      AuthorizationService authorizationService, MembershipRepository membershipRepository, Environment environment) {
    return new DownloadLoanInfoFromMongolBankTask(loanService, authenticationService, authorizationService,
        membershipRepository, environment);
  }

  @Bean
  @Inject
  public DownloadLoanInfoFromMongolBankTaskExtended downloadLoanInfoFromMongolBankTaskExtended(LoanService loanService,
      AuthenticationService authenticationService, AuthorizationService authorizationService,
      MembershipRepository membershipRepository,
      DocumentRepository documentRepository, BpmsRepositoryRegistry bpmsRepositoryRegistry) {
    return new DownloadLoanInfoFromMongolBankTaskExtended(loanService, authenticationService, authorizationService,
        membershipRepository, documentRepository,
        bpmsRepositoryRegistry);
  }

  @Bean
  @Inject
  public GetEnquireByCoBorrowerFromMongolBankTask getEnquireByCoBorrowerFromMongolBank(LoanService loanService) {
    return new GetEnquireByCoBorrowerFromMongolBankTask(loanService);
  }

  @Bean
  @Inject
  public GetLoanEnquirePdfFromMongolBankTask getLoanEnquirePdfFromMongolBank(LoanService loanService) {
    return new GetLoanEnquirePdfFromMongolBankTask(loanService);
  }

  @Bean
  @Inject
  public GetCustomerCIDFromMongolBankTask getCustomerCIDFromMongolBank(LoanService loanService,
      AuthenticationService authenticationService,
      AuthorizationService authorizationService, MembershipRepository membershipRepository) {
    return new GetCustomerCIDFromMongolBankTask(loanService, authenticationService, authorizationService,
        membershipRepository);
  }

  @Bean
  @Inject
  public GetLoanEnquireFromMongolBankTask getLoanEnquireFromMongolBank(LoanService loanService) {
    return new GetLoanEnquireFromMongolBankTask(loanService);
  }

  @Bean
  public GetResourceToLoanAmountRatioTask getResourceToLoanAmountRatioTask(
      AuthenticationService authenticationService) {
    return new GetResourceToLoanAmountRatioTask(authenticationService);
  }

  @Bean
  @Inject
  public GetResourceFromXacTask getResourceFromXacTask(CoreBankingService coreBankingService,
      AuthenticationService authenticationService) {
    return new GetResourceFromXacTask(authenticationService, coreBankingService);
  }

  @Bean
  @Inject
  public GetLoanPeriodFromXacTask getLoanPeriodFromXacTask(CoreBankingService coreBankingService,
      AuthenticationService authenticationService) {
    return new GetLoanPeriodFromXacTask(coreBankingService, authenticationService);
  }

  @Bean
  @Inject
  public ConfirmLoanEnquireFromMongolBankTask confirmLoanEnquireFromMongolBank(LoanService loanService) {
    return new ConfirmLoanEnquireFromMongolBankTask(loanService);
  }

  @Bean
  @Inject
  public GetBranchTask getBranchTask(AuthenticationService authenticationService,
      MembershipRepository membershipRepository) {
    return new GetBranchTask(authenticationService, membershipRepository);
  }

  @Bean
  @Inject
  public GetBranchTaskMicro getBranchTaskMicro(AuthenticationService authenticationService,
      MembershipRepository membershipRepository) {
    return new GetBranchTaskMicro(authenticationService, membershipRepository);
  }

  @Bean
  public SetAssetToLoanRatioTask setAssetToLoanRatioTask() {
    return new SetAssetToLoanRatioTask();
  }

  @Bean
  @Inject
  public GetLoanInfoFromMongolBankTask getLoanInfoFromMongolBank(LoanService loanService) {
    return new GetLoanInfoFromMongolBankTask(loanService);
  }

  @Bean
  @Inject
  public SetBankRejectedStateTask setBankRejectedState(ProcessRequestRepository processRequestRepository,
      AuthenticationService authenticationService) {
    return new SetBankRejectedStateTask(processRequestRepository, authenticationService);
  }

  @Bean
  @Inject
  public SendEmailTaskForSendLoanDecision sendEmailTaskForSendLoanDecision(EmailService emailService,
      AuthenticationService authenticationService,
      UserRepository userRepository, ProcessRequestRepository processRequestRepository,
      ProcessTypeRepository processTypeRepository) {
    return new SendEmailTaskForSendLoanDecision(emailService, authenticationService, userRepository,
        processRequestRepository, processTypeRepository);
  }

  @Bean
  @Inject
  public SendEmailTaskForLoanDecision sendEmailTaskForLoanDecision(EmailService emailService,
      AuthenticationService authenticationService,
      UserRepository userRepository, ProcessRequestRepository processRequestRepository,
      ProcessTypeRepository processTypeRepository) {
    return new SendEmailTaskForLoanDecision(emailService, authenticationService, userRepository,
        processRequestRepository, processTypeRepository);
  }

  @Bean
  @Inject
  public SendDirectorListener sendDirectorListener(TaskFormService taskFormService) {
    return new SendDirectorListener(taskFormService);
  }

  @Bean
  @Inject
  public SetCustomerRejectedStateTask setCustomerRejectedStateTask(ProcessRequestRepository processRequestRepository) {
    return new SetCustomerRejectedStateTask(processRequestRepository);
  }

  @Bean
  @Inject
  public SetSentUserTask setSentUserTask(AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      ProcessRequestRepository processRequestRepository, MembershipRepository membershipRepository,
      Environment environment) {
    return new SetSentUserTask(authenticationService, authorizationService, processRequestRepository,
        membershipRepository, environment);
  }

  @Bean
  public SetLoanAmountCalculationData setLoanAmountCalculationData(AuthenticationService authenticationService) {
    return new SetLoanAmountCalculationData(authenticationService);
  }

  @Bean
  public DateToNumberTask dateToNumber(AuthenticationService authenticationService) {
    return new DateToNumberTask(authenticationService);
  }

  @Bean
  public SetVariablesBeforeMicroScoring setVariablesBeforeMicroScoring() {
    return new SetVariablesBeforeMicroScoring();
  }

  @Bean
  public SetVariablesBeforeMicroAccountCreationTask setVariablesBeforeMicroAccountCreationTask() {
    return new SetVariablesBeforeMicroAccountCreationTask();
  }

  @Bean
  @Inject
  public DownloadCustomerInfoByRegNoTask downloadCustomerInfoByRegNo(NewCoreBankingService coreBankingService,
      ProcessRequestRepository processRequestRepository,
      AuthenticationService authenticationService,
      AuthorizationService authorizationService) {
    return new DownloadCustomerInfoByRegNoTask(coreBankingService, processRequestRepository, authenticationService,
        authorizationService);
  }

  @Bean
  @Inject
  public LoanDecisionTask loanDecisionTask(AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      ProcessRequestRepository processRequestRepository, MembershipRepository membershipRepository) {
    return new LoanDecisionTask(authenticationService, authorizationService, processRequestRepository,
        membershipRepository);
  }

  @Bean
  @Inject
  public MortgageLoanDecisionTask mortgageLoanDecisionTask(AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      ProcessRequestRepository processRequestRepository, MembershipRepository membershipRepository) {
    return new MortgageLoanDecisionTask(authenticationService, authorizationService, processRequestRepository,
        membershipRepository);
  }

  @Bean
  @Inject
  public DownloadCustomerInfoByCifNoTask downloadCustomerInfoByCifNo(NewCoreBankingService coreBankingService,
      AuthenticationService authenticationService) {
    return new DownloadCustomerInfoByCifNoTask(coreBankingService, authenticationService);
  }

  @Bean
  @Inject
  public DownloadOrganizationLevelTask downloadOrganizationLevelTask(OrganizationService organizationService,
      AuthenticationService authenticationService,
      MembershipRepository membershipRepository) {
    return new DownloadOrganizationLevelTask(organizationService, authenticationService, membershipRepository);
  }

  @Bean
  public CalculateInterestRateTask calculateInterestRateTask(AuthenticationService authenticationService) {
    return new CalculateInterestRateTask(authenticationService);
  }

  @Bean
  public SetAccountCreationFieldsTask setAccountCreationFieldsTask(AuthenticationService authenticationService,
      NewCoreBankingService newCoreBankingService) {
    return new SetAccountCreationFieldsTask(authenticationService, newCoreBankingService);
  }

  @Bean
  public SetFieldsAfterLoanAmountCalculationTask setFieldsAfterLoanAmountCalculationTask(
      AuthenticationService authenticationService) {
    return new SetFieldsAfterLoanAmountCalculationTask(authenticationService);
  }

  @Bean
  public SetAccountCreationFieldsTaskMicro setAccountCreationFieldsTaskMicro(
      NewCoreBankingService newCoreBankingService,
      AuthenticationService authenticationService) {
    return new SetAccountCreationFieldsTaskMicro(newCoreBankingService, authenticationService);
  }

  @Bean
  @Inject
  public CheckRiskyCustomerTask checkRiskyCustomerTask(NewCoreBankingService coreBankingService,
      AuthenticationService authenticationService) {
    return new CheckRiskyCustomerTask(coreBankingService, authenticationService);
  }

  @Bean
  @Inject
  public SetRequestConfirmedStateTask setRequestConfirmedState(ProcessRequestRepository processRequestRepository,
      AuthenticationService authenticationService) {
    return new SetRequestConfirmedStateTask(processRequestRepository, authenticationService);
  }

  @Bean
  public CreateLoanAccountTask createLoanAccountTask(NewCoreBankingService newCoreBankingService,
      CoreBankingService coreBankingService,
      AuthenticationService authenticationService,
      MembershipRepository membershipRepository) {
    return new CreateLoanAccountTask(newCoreBankingService, coreBankingService, authenticationService,
        membershipRepository);
  }

  @Bean
  public CreateLoanAccountNewCoreTask createLoanAccountNewCoreTask(NewCoreBankingService newCoreBankingService,
      AuthenticationService authenticationService,
      MembershipRepository membershipRepository, Environment environment,
      OrganizationLeasingRepository organizationLeasingRepository,
      AimServiceRegistry aimServiceRegistry, ProcessRepository processRepository) {
    return new CreateLoanAccountNewCoreTask(newCoreBankingService, authenticationService,
        membershipRepository, environment, organizationLeasingRepository, aimServiceRegistry, processRepository);
  }

  @Bean
  public SetCreatedUserBeforeLoanAccountTask setCreatedUserBeforeLoanAccountTask(
      NewCoreBankingService newCoreBankingService,
      ProcessRequestRepository processRequestRepository) {
    return new SetCreatedUserBeforeLoanAccountTask(newCoreBankingService, processRequestRepository);
  }

  @Bean
  public CreateCollateralLoanAccountTask createCollateralLoanAccountTask(NewCoreBankingService newCoreBankingService,
      CoreBankingService coreBankingService,
      AuthenticationService authenticationService,
      MembershipRepository membershipRepository, ProcessRepository processRepository) {
    return new CreateCollateralLoanAccountTask(newCoreBankingService, coreBankingService, authenticationService,
        membershipRepository, processRepository);
  }

  @Bean
  public CreateCollateralLoanAccountTaskMicro createCollateralLoanAccountTaskMicro(
      NewCoreBankingService newCoreBankingService,
      CoreBankingService coreBankingService,
      AuthenticationService authenticationService,
      MembershipRepository membershipRepository, ProcessRepository processRepository) {
    return new CreateCollateralLoanAccountTaskMicro(newCoreBankingService, coreBankingService, authenticationService,
        membershipRepository, processRepository);
  }

  @Bean
  @Inject
  public SignUpCollateralTask signUpCollateralTask(AuthenticationService authenticationService,
      ProcessRepository processRepository, CollateralProductRepository collateralProductRepository,
      CoreBankingService coreBankingService,
      MembershipRepository membershipRepository) {
    return new SignUpCollateralTask(authenticationService, processRepository, collateralProductRepository,
        coreBankingService,
        membershipRepository);
  }

  // Xyp service bean configs.
  @Bean
  @Inject
  public DownloadSalaryInfoFromXypTask downloadSalaryInfoFromXypTask(CustomerService customerService,
      AuthenticationService authenticationService, AuthorizationService authorizationService,
      GroupRepository groupRepository,
      MembershipRepository membershipRepository,
      DocumentRepository documentRepository, ProcessRepository processRepository) {
    return new DownloadSalaryInfoFromXypTask(customerService, authenticationService, authorizationService,
        groupRepository,
        membershipRepository,
        documentRepository, processRepository);
  }

  @Bean
  @Inject
  public DownloadPropertyInfoFromXypTask downloadPropertyInfoFromXypTask(CustomerService customerService,
      AuthenticationService authenticationService, GroupRepository groupRepository,
      MembershipRepository membershipRepository,
      DocumentRepository documentRepository) {
    return new DownloadPropertyInfoFromXypTask(customerService, authenticationService, groupRepository,
        membershipRepository,
        documentRepository);
  }

  @Bean
  @Inject
  public DownloadPropertyCoBorrowerFromXypTask downloadPropertyCoBorrowerFromXypTask(CustomerService customerService,
      AuthenticationService authenticationService, GroupRepository groupRepository,
      MembershipRepository membershipRepository,
      DocumentRepository documentRepository) {
    return new DownloadPropertyCoBorrowerFromXypTask(customerService, authenticationService, groupRepository,
        membershipRepository,
        documentRepository);
  }

  @Bean
  @Inject
  public DownloadAddressInfoFromXypTask downloadAddressInfoFromXypTask(CustomerService customerService,
      ErrorMessageRepository errorMessageRepository, AuthenticationService authenticationService) {
    return new DownloadAddressInfoFromXypTask(customerService, errorMessageRepository, authenticationService);
  }

  @Bean
  public SetScoringLevelTask setScoringLevelTask(AuthenticationService authenticationService) {
    return new SetScoringLevelTask(authenticationService);
  }

  @Bean
  @Inject
  public DownloadIDCardInfoFromXypTask downloadIDCardInfoFromXypTask(CustomerService customerService,
      AuthenticationService authenticationService, GroupRepository groupRepository,
      MembershipRepository membershipRepository,
      DocumentRepository documentRepository) {
    return new DownloadIDCardInfoFromXypTask(customerService, authenticationService, groupRepository,
        membershipRepository,
        documentRepository);
  }

  @Bean
  @Inject
  public DeleteCoBorrowerVariablesTask deleteCoBorrowerVariablesTask(AuthenticationService authenticationService,
      AuthorizationService authorizationService, CaseService caseService) {
    return new DeleteCoBorrowerVariablesTask(authenticationService, authorizationService, caseService);
  }

  @Bean
  public UpdateCoBorrowerVariablesTask updateCoBorrowerVariablesTask() {
    return new UpdateCoBorrowerVariablesTask();
  }

  @Bean
  public VariableLoggerTask variableLoggerTask(AuthenticationService authenticationService) {
    return new VariableLoggerTask(authenticationService);
  }

  @Bean
  public CreateLoanPaymentScheduleTask createLoanPaymentScheduleTask(AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      CaseService caseService, DocumentRepository documentRepository) {
    return new CreateLoanPaymentScheduleTask(authenticationService, authorizationService, caseService,
        documentRepository);
  }

  @Bean
  public CreateLoanPaymentScheduleListener createLoanPaymentScheduleListener(
      AuthenticationService authenticationService,
      AuthorizationService authorizationService, CaseService caseService, DocumentRepository documentRepository) {
    return new CreateLoanPaymentScheduleListener(authenticationService, authorizationService, caseService,
        documentRepository);
  }

  @Bean
  @Inject
  public CreateLoanReportTask createLoanReportTask(AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      CaseService caseService, DocumentRepository documentRepository) {
    return new CreateLoanReportTask(authenticationService, authorizationService, caseService, documentRepository);
  }

  @Bean
  public SetLoanPreparationBoolean setLoanPreparationBoolean() {
    return new SetLoanPreparationBoolean();
  }

  @Bean
  public GenerateCollateralIdServiceTask generateCollateralId(ProcessRepository processRepository) {
    return new GenerateCollateralIdServiceTask(processRepository);
  }

  @Bean
  public SaveCollateralTask saveCollateral(ProcessRepository processRepository,
      AuthenticationService authenticationService,
      AuthorizationService authorizationService, CollateralProductRepository collateralProductRepository) {
    return new SaveCollateralTask(processRepository, authenticationService, authorizationService,
        collateralProductRepository);
  }

  @Bean
  public UpdateCollateralListTask updateCollateralList(ProcessRepository processRepository,
      AuthenticationService authenticationService,
      AuthorizationService authorizationService) {
    return new UpdateCollateralListTask(processRepository, authenticationService, authorizationService);
  }

  @Bean
  public MicroBalanceCalculationTask microBalanceCalculationTask() {
    return new MicroBalanceCalculationTask();
  }

  @Bean
  public SetMaxLoanAmountMortgageTask setMaxLoanAmountMortgageTask() {
    return new SetMaxLoanAmountMortgageTask();
  }

  @Bean
  public SetProductNameTaskListener setProductNameTaskListener() {
    return new SetProductNameTaskListener();
  }

  @Bean
  @Inject
  public CalculateMortgageLoanAmountTask calculateMortgageLoanAmountTask(Environment environment,
      AuthenticationService authenticationService) {
    return new CalculateMortgageLoanAmountTask(environment, authenticationService);
  }

  @Bean
  @Inject
  public DownloadInfoFromMongolBankNewTask downloadInfoFromMongolBankNew(AimServiceRegistry aimServiceRegistry,
      BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry) {
    return new DownloadInfoFromMongolBankNewTask(aimServiceRegistry, bpmsServiceRegistry, bpmsRepositoryRegistry);
  }

  @Bean
  public MortgageLoanAmountValidation mortgageLoanAmountValidation() {
    return new MortgageLoanAmountValidation();
  }

  @Bean
  public SetLastCalculationTypeBusinessTask setLastCalculationTypeBusinessTask() {
    return new SetLastCalculationTypeBusinessTask();
  }

  @Bean
  public SetLastCalculationTypeSalaryTask setLastCalculationTypeSalaryTask() {
    return new SetLastCalculationTypeSalaryTask();
  }

  @Bean
  public SetCreateCollateralRelatedUserTaskIdTask setCreateCollateralRelatedUserTaskIdTask() {
    return new SetCreateCollateralRelatedUserTaskIdTask();
  }

  @Bean
  public ClearRelatedUserTaskIdVariableTask clearRelatedUserTaskIdVariableTask() {
    return new ClearRelatedUserTaskIdVariableTask();
  }

  @Bean
  public ManuallyStartRelatedTaskListener manuallyStartRelatedTaskListener() {
    return new ManuallyStartRelatedTaskListener();
  }

  @Bean
  public CleanFieldsTask cleanFieldsTask() {
    return new CleanFieldsTask();
  }

  @Bean
  GenerateCalculationInfoTask generateCalculationInfoTask(DocumentRepository documentRepository,
      ProcessRepository processRepository) {
    return new GenerateCalculationInfoTask(documentRepository, processRepository);
  }
}
