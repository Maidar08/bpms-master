package mn.erin.bpms.branch.banking.config;

import javax.inject.Inject;

import listener.ClearDepositContractVariables;
import listener.ClearFutureMillionareVariables;
import listener.ClearLoanRepaymentFormVariablesListener;
import listener.ClearPreviousVariablesListener;
import listener.ClearUssdFormVariablesListener;
import listener.SetParentTaskIdTaskListener;
import listener.account_reference.ClearAccountReferenceVariablesListener;
import listener.account_reference.ClearTransactionAmountListener;
import listener.billing.ClearVariablesToBackListener;
import listener.billing.custom.ClearCustomVariablesListener;
import listener.billing.custom.SetCustomPayAmountListener;
import listener.billing.tax.ClearTaxVariablesListener;
import listener.billing.tax.SetInvoiceAmountToPreviousListener;
import listener.salary_package_transaction.ClearSalaryPackageVariablesListener;
import listener.transactionDocument.ClearCustomerTranVariablesListener;
import listener.transactionDocument.ClearETransactionVariablesListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import service_task.account_reference.account_reference.MakeAccountFeeTransactionTask;
import service_task.account_reference.account_reference.MakeNoCashAccountFeeTransactionTask;
import service_task.billing.custom.MakeCustomTransactionTask;
import service_task.billing.tax.MakeTaxTransactionTask;
import service_task.loan_repayment.AddUnschLoanRepaymentTask;
import service_task.loan_repayment.ScheduledLoanPaymentTask;
import service_task.loan_repayment.SetPayOffProcTask;
import service_task.salary_package.MakeSalaryPackageTransactionTask;

import mn.erin.bpm.domain.ohs.xac.branch.banking.config.XacBranchBankingBeanConfig;
import mn.erin.bpms.process.base.CamundaBeanConfig;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.service.BranchBankingService;

/**
 * @author Tamir
 */
@EnableAsync
@Configuration
@Import({ CamundaBeanConfig.class, XacBranchBankingBeanConfig.class })
public class CamundaBranchBankingBeanConfig
{
  @Bean()
  @Inject
  public MakeTaxTransactionTask makeTaxTransactionTask(BranchBankingService branchBankingService)
  {
    return new MakeTaxTransactionTask(branchBankingService);
  }

  @Bean()
  @Inject
  public MakeCustomTransactionTask makeCustomTransactionTask(BranchBankingService branchBankingService)
  {
    return new MakeCustomTransactionTask(branchBankingService);
  }

  @Bean()
  @Inject
  public MakeAccountFeeTransactionTask makeAccountFeeTransactionTask(BranchBankingService branchBankingService)
  {
    return new MakeAccountFeeTransactionTask(branchBankingService);
  }

  @Bean()
  @Inject
  public MakeNoCashAccountFeeTransactionTask makeNoCashAccountFeeTransactionTask(BranchBankingService branchBankingService)
  {
    return new MakeNoCashAccountFeeTransactionTask(branchBankingService);
  }

  @Bean
  @Inject
  public ScheduledLoanPaymentTask scheduledLoanPayment(BranchBankingService branchBankingService)
  {
    return new ScheduledLoanPaymentTask(branchBankingService);
  }

  // Salary package
  @Bean
  MakeSalaryPackageTransactionTask makeSalaryPackageTransaction(BranchBankingService branchBankingService, AuthenticationService authenticationService)
  {
    return new MakeSalaryPackageTransactionTask(branchBankingService, authenticationService);
  }

  @Bean()
  @Inject
  public AddUnschLoanRepaymentTask addUnschLoanRepaymentTask(BranchBankingService branchBankingService)
  {
    return new AddUnschLoanRepaymentTask(branchBankingService);
  }

  @Bean()
  public SetPayOffProcTask setPayOffProcTask(BranchBankingService branchBankingService)
  {
    return new SetPayOffProcTask(branchBankingService);
  }

  @Bean()
  public SetInvoiceAmountToPreviousListener setInvoiceAmountToPreviousListener()
  {
    return new SetInvoiceAmountToPreviousListener();
  }

  @Bean()
  public ClearTaxVariablesListener clearTaxVariablesListener()
  {
    return new ClearTaxVariablesListener();
  }

  @Bean()
  public ClearCustomVariablesListener clearCustomVariablesListener()
  {
    return new ClearCustomVariablesListener();
  }

  @Bean()
  public SetParentTaskIdTaskListener setParentTaskIdTaskListener()
  {
    return new SetParentTaskIdTaskListener();
  }

  @Bean()
  public SetCustomPayAmountListener setCustomPayAmountListener()
  {
    return new SetCustomPayAmountListener();
  }

  @Bean()
  public ClearVariablesToBackListener clearVariablesToBackListener()
  {
    return new ClearVariablesToBackListener();
  }

  @Bean()
  public ClearCustomerTranVariablesListener clearCustomerTranVariablesListener()
  {
    return new ClearCustomerTranVariablesListener();
  }

  @Bean()
  public ClearETransactionVariablesListener clearETransactionVariablesListener()
  {
    return new ClearETransactionVariablesListener();
  }

  @Bean()
  public ClearAccountReferenceVariablesListener clearAccountReferenceVariablesListener()
  {
    return new ClearAccountReferenceVariablesListener();
  }

  @Bean()
  public ClearTransactionAmountListener clearTransactionAmountListener()
  {
    return new ClearTransactionAmountListener();
  }

  @Bean()
  public ClearSalaryPackageVariablesListener clearSalaryPackageVariablesListener()
  {
    return new ClearSalaryPackageVariablesListener();
  }

  @Bean()
  public ClearUssdFormVariablesListener clearUssdFormVariablesListener()
  {
    return new ClearUssdFormVariablesListener();
  }

  @Bean()
  public ClearLoanRepaymentFormVariablesListener clearLoanRepaymentFormVariablesListener()
  {
    return new ClearLoanRepaymentFormVariablesListener();
  }

  @Bean()
  public ClearDepositContractVariables clearDepositContractVariables()
  {
    return new ClearDepositContractVariables();
  }

  @Bean()
  public ClearFutureMillionareVariables clearFutureMillionareVariables()
  {
    return new ClearFutureMillionareVariables();
  }

  @Bean()
  public ClearPreviousVariablesListener clearPreviousVariablesListener()
  {
    return new ClearPreviousVariablesListener();
  }
}
