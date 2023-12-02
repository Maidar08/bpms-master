package mn.erin.bpms.branch.banking.webapp.controller;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.bpms.branch.banking.webapp.model.RestCompletedFormField;
import mn.erin.bpms.branch.banking.webapp.util.BranchBankingRestUtils;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.model.branch_banking.transaction.CustomerTransaction;
import mn.erin.domain.bpm.model.branch_banking.transaction.ETransaction;
import mn.erin.domain.bpm.service.BranchBankingService;
import mn.erin.domain.bpm.usecase.branch_banking.transaction.GetCustomerTransactions;
import mn.erin.domain.bpm.usecase.branch_banking.transaction.GetCustomerTransactionsInput;
import mn.erin.domain.bpm.usecase.branch_banking.transaction.GetCustomerTransactionsOutput;
import mn.erin.domain.bpm.usecase.branch_banking.transaction.GetTransactionEbankList;
import mn.erin.domain.bpm.usecase.branch_banking.transaction.GetTransactionEbankListInput;
import mn.erin.domain.bpm.util.process.BpmUtils;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.bpms.branch.banking.webapp.util.BranchBankingRestUtils.getFormFieldById;
import static mn.erin.bpms.branch.banking.webapp.util.BranchBankingRestUtils.getSelectedFieldOptionIdByFieldID;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ISO_DATE_FORMAT;
import static mn.erin.domain.bpm.BpmModuleConstants.ISO_SIMPLE_DATE_FORMATTER;
import static mn.erin.domain.bpm.util.process.BpmUtils.getFormattedDateString;

/**
 * @author Bilguunbor
 **/

@RestController
@RequestMapping(value = "/transaction", name = "Provides branch banking transactions API.")
public class BranchBankingTransactionRestApi
{
  private final BranchBankingService branchBankingService;

  public BranchBankingTransactionRestApi(BranchBankingService branchBankingService)
  {
    this.branchBankingService = branchBankingService;
  }

  @GetMapping(value = "/customer-transactions/{userId}/{transactionDate}/instanceId/{instanceId}")
  public ResponseEntity<RestResult> getTransactions(@PathVariable String userId, @PathVariable Date transactionDate, @PathVariable String instanceId)
      throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }
    String formattedTransactionDate = BpmUtils.dateFormatter(transactionDate, ISO_SIMPLE_DATE_FORMATTER);

    GetCustomerTransactionsInput input = new GetCustomerTransactionsInput(instanceId, userId, formattedTransactionDate);
    GetCustomerTransactions getCustomerTransactions = new GetCustomerTransactions(branchBankingService);

    GetCustomerTransactionsOutput output = getCustomerTransactions.execute(input);

    Collection<CustomerTransaction> transactions = output.getTransactions();

    return RestResponse.success(BranchBankingRestUtils.toRestTransactions(transactions));
  }

  @ApiOperation("Gets a list of eBank transaction")
  @PostMapping(value = "/e-transaction/instanceId/{instanceId}")
  public ResponseEntity<RestResult> getTransactionEBankList(@PathVariable String instanceId, @RequestBody List<RestCompletedFormField> formFields)
      throws UseCaseException, BpmInvalidArgumentException, ParseException
  {
    if (null == formFields)
    {
      throw new BpmInvalidArgumentException(PARAMETER_NULL_CODE, PARAMETER_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    GetTransactionEbankList useCase = new GetTransactionEbankList(branchBankingService);
    GetTransactionEbankListInput input = getETransactionReqParameters(formFields, instanceId);

    List<ETransaction> output = useCase.execute(input);
    return RestResponse.success(output);
  }

  private GetTransactionEbankListInput getETransactionReqParameters(List<RestCompletedFormField> formFields, String instanceId) throws ParseException
  {

    String channelId = getSelectedFieldOptionIdByFieldID(formFields, "transactionChannel");
    RestCompletedFormField accountIdField = getFormFieldById(formFields, "accountId");
    RestCompletedFormField startDateField = getFormFieldById(formFields, "transactionStartDate");
    RestCompletedFormField endDateField = getFormFieldById(formFields, "transactionEndDate");
    RestCompletedFormField channelField = getFormFieldById(formFields,"transactionChannel");


    String accountId = String.valueOf(Objects.requireNonNull(accountIdField).getFormFieldValue().getDefaultValue());
    String startDate = String.valueOf(Objects.requireNonNull(startDateField).getFormFieldValue().getDefaultValue());
    String endDate = String.valueOf(Objects.requireNonNull(endDateField).getFormFieldValue().getDefaultValue());
    String channel =  String.valueOf(Objects.requireNonNull(channelField).getFormFieldValue().getDefaultValue());

    String formattedStartDate = getFormattedDateString(startDate, ISO_DATE_FORMAT);
    String formattedEndDate = getFormattedDateString(endDate, ISO_DATE_FORMAT);

    return new GetTransactionEbankListInput(accountId, channelId, channel, formattedStartDate, formattedEndDate, instanceId);
  }
}
