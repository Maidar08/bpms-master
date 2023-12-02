package consumption.service_task.direct_online_salary;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.direct_online.GetLoanInfo;
import mn.erin.domain.bpm.usecase.direct_online.GetLoanInfoOutput;

import static mn.erin.domain.bpm.BpmModuleConstants.ACTIVE_LOAN_ACCOUNT_LIST;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.HAS_ACTIVE_LOAN_ACCOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_DUE;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PENALTY_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.TOTAL_BALANCE;
import static mn.erin.domain.bpm.BpmModuleConstants.TOTAL_CLOSING_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.XAC_CLOSING_LOAN_AMOUNT;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.BpmUtils.toBigDecimal;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.getLogPrefix;

/**
 * @author Temuulen Naranbold
 */
public class DirectOnlineGetLoanInfoTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DirectOnlineGetLoanInfoTask.class);

  private final DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private final NewCoreBankingService newCoreBankingService;
  private final ProductRepository productRepository;
  private final Environment environment;

  public DirectOnlineGetLoanInfoTask(DirectOnlineCoreBankingService directOnlineCoreBankingService,
      NewCoreBankingService newCoreBankingService, ProductRepository productRepository,Environment environment)
  {
    this.directOnlineCoreBankingService = directOnlineCoreBankingService;
    this.newCoreBankingService = newCoreBankingService;
    this.productRepository = productRepository;
    this.environment = environment;
  }

  @Override
  public void execute(DelegateExecution execution) throws UseCaseException, BpmServiceException
  {
    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    String logPrefix = getLogPrefix(processTypeId);
    String productCategory = String.valueOf(execution.getVariable(PRODUCT_CATEGORY));
    String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));

    Map<String, Object> variables = execution.getVariables();
    execution.setVariable(HAS_ACTIVE_LOAN_ACCOUNT, false);
    final String cif = String.valueOf(variables.get(CIF_NUMBER));

    GetLoanInfo getLoanInfo = new GetLoanInfo(newCoreBankingService, directOnlineCoreBankingService, productRepository);
    Map<String, String> input = new HashMap<>();
    input.put(REGISTER_NUMBER, String.valueOf(variables.get(REGISTER_NUMBER)));
    input.put(CIF_NUMBER, cif);
    input.put(PRODUCT_CODE, processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID) ? productCategory : String.valueOf(variables.get(PROCESS_TYPE_ID)));
    input.put(PROCESS_TYPE_ID, processTypeId);
    input.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));
    input.put(PRODUCT_CATEGORY, String.valueOf(execution.getVariable(PRODUCT_CATEGORY)));
    final GetLoanInfoOutput output = getLoanInfo.execute(input);
    if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
    {
      if (output == null)
      {
        throw new UseCaseException("Failed to download loan account info for cif = " + cif + "trackNumber = " + trackNumber);
      }
    }
    else
    {
      if (output == null)
      {
        throw new UseCaseException("Failed to download loan account info for cif = " + cif);
      }
    }

    if (output.isHasActiveLoanAccount())
    {
      execution.setVariable(HAS_ACTIVE_LOAN_ACCOUNT, true);
    }

    final Map<String, Map<String, Object>> mappedAccount = output.getMappedAccount();
    final BigDecimal totalClosingAmount = output.getTotalClosingAmount();
    final BigDecimal totalBalance = output.getTotalBalance();
    if (getValidString(execution.getVariable(PROCESS_TYPE_ID)).equals(INSTANT_LOAN_PROCESS_TYPE_ID) && !mappedAccount.isEmpty())
    {
      Map<String, Object> account = mappedAccount.entrySet().iterator().next().getValue();
      String clearBalance = (String) account.get("ClearBalance");
      execution.setVariable("clearBalance", clearBalance);
      BigDecimal closingLoanAmount = (BigDecimal) account.get(XAC_CLOSING_LOAN_AMOUNT);
      execution.setVariable(XAC_CLOSING_LOAN_AMOUNT, closingLoanAmount);

      BigDecimal interestAmount = toBigDecimal(account.get(INTEREST_DUE)).add(toBigDecimal(account.get(PENALTY_AMOUNT)));
      execution.setVariable(INTEREST_AMOUNT, interestAmount);
      boolean hasInterestAmount = false;
      if (interestAmount.compareTo(BigDecimal.ZERO) > 0)
      {
        hasInterestAmount = true;
      }
      execution.setVariable("hasInterestAmount", hasInterestAmount);
      String ISDmd = String.valueOf(account.get(BpmModuleConstants.ISDmd));
      execution.setVariable(BpmModuleConstants.ISDmd, ISDmd);
      boolean hasISDmd = false;
      if(ISDmd.equals(environment.getProperty("isdmd.yes")))
      {
        hasISDmd = true;
      }
      execution.setVariable("hasPrincipleDue", hasISDmd);
    }
    execution.setVariable(ACTIVE_LOAN_ACCOUNT_LIST, mappedAccount);
    Object requestId = execution.getVariable(PROCESS_REQUEST_ID);
    boolean hasActiveLoan = (boolean) execution.getVariable(HAS_ACTIVE_LOAN_ACCOUNT);
    if (hasActiveLoan)
    {
      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("{} [{}] ACTIVE LOAN ACCOUNTS HAS FOUND FOR REQUEST ID = [{}] WITH TRACKNUMBER = [{}].", logPrefix, mappedAccount, requestId, trackNumber);
      }
      else {
        LOGGER.info("{} [{}] ACTIVE LOAN ACCOUNTS HAS FOUND FOR REQUEST ID = [{}].", logPrefix, mappedAccount, requestId);
      }
    }
    else
    {
      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("{} NO ACTIVE LOAN ACCOUNT HAS FOUND FOR CIF = [{}], WITH TRACKNUMBER = [{}]", logPrefix, execution.getVariable(CIF_NUMBER), trackNumber);
      }
      else
      {
        LOGGER.info("{} NO ACTIVE LOAN ACCOUNT HAS FOUND FOR CIF = [{}]", logPrefix, execution.getVariable(CIF_NUMBER));
      }
    }
    execution.setVariable(TOTAL_CLOSING_AMOUNT, totalClosingAmount);
    execution.setVariable(TOTAL_BALANCE, totalBalance);
    if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
    {
      LOGGER.info("{} TOTAL CLOSING AMOUNT = [{}] FOR CIF = [{}], WITH TRACKNUMBER = [{}]", logPrefix, totalClosingAmount, execution.getVariable(CIF_NUMBER), trackNumber);
    }
    else
    {
      LOGGER.info("{} TOTAL CLOSING AMOUNT = [{}] FOR CIF = [{}]", logPrefix, totalClosingAmount, execution.getVariable(CIF_NUMBER));
    }
  }
}
