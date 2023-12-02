package mn.erin.bpms.loan.consumption.service_task.calculation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.alfresco.connector.model.RestTextValueField;
import mn.erin.alfresco.connector.service.transform.AlfrescoRemoteTransformerService;
import mn.erin.alfresco.connector.service.transform.RestTransformDocumentInput;
import mn.erin.alfresco.connector.service.transform.RestTransformDocumentOutput;
import mn.erin.alfresco.connector.service.transform.TransformServiceException;
import mn.erin.alfresco.connector.service.transform.TransformerService;
import mn.erin.bpms.loan.consumption.utils.StringUtils;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;

import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.HAS_MORTGAGE;
import static mn.erin.bpms.loan.consumption.utils.NumberUtils.getThousandSeparatedString;
import static mn.erin.domain.bpm.BpmModuleConstants.ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.ALFRESCO;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CALCULATION_INFO_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_CALCULATION_INFO_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.COST_OF_GOODS;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ASSETS;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT_TO_INCOME_RATIO_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT_TO_SOLVENCY_RATIO_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.DOCUMENT_INFO_ID_CALCULATION_INFO_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ASSETS;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_TERM;
import static mn.erin.domain.bpm.BpmModuleConstants.LONG_TERM_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.NET_BENEFIT;
import static mn.erin.domain.bpm.BpmModuleConstants.NET_PROFIT;
import static mn.erin.domain.bpm.BpmModuleConstants.NULL_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.OPERATING_EXPENSES;
import static mn.erin.domain.bpm.BpmModuleConstants.OTHER_EXPENSES;
import static mn.erin.domain.bpm.BpmModuleConstants.OTHER_INCOME;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.RENTAL_EXPENSES;
import static mn.erin.domain.bpm.BpmModuleConstants.REPORT_PERIOD;
import static mn.erin.domain.bpm.BpmModuleConstants.SALES_INCOME;
import static mn.erin.domain.bpm.BpmModuleConstants.SHORT_TERM_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.SLASH;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_CALCULATION_INFO_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.SUPPLIER_PAY;
import static mn.erin.domain.bpm.BpmModuleConstants.TAX_COSTS;
import static mn.erin.domain.bpm.BpmModuleConstants.TEMPLATE_PATH_CALCULATION_INFO;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertJsonStringToMap;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class GenerateCalculationInfoTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GenerateCalculationInfoTask.class);
  private final DocumentRepository documentRepository;
  private final ProcessRepository processRepository;

  public GenerateCalculationInfoTask(DocumentRepository documentRepository, ProcessRepository processRepository)
  {
    this.documentRepository = documentRepository;
    this.processRepository = processRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registerNumber = (String) execution.getVariable(REGISTER_NUMBER);
    String processRequestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    String documentPath = getDocumentPath(registerNumber, processRequestId);

    List<RestTextValueField> textFields = getTextFields(execution);

    LOGGER.info("########## Generates MICRO loan decision document with REQUEST ID = [{}]", processRequestId);

    List<String> documentIdList = transformDocument(TEMPLATE_PATH_CALCULATION_INFO, documentPath, textFields);

    if (!documentIdList.isEmpty())
    {
      removePreviousLoanDecisionDocument(caseInstanceId, processRequestId);
    }
    for (String documentId : documentIdList)
    {
      persistLoanDecisionDocument(documentId, caseInstanceId, processRequestId);
    }
  }

  private List<String> transformDocument(String templatePath, String documentPath, List<RestTextValueField> restTextValueFields)
      throws TransformServiceException
  {
    TransformerService transformerService = new AlfrescoRemoteTransformerService();

    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("deleteBaseDocument", "false");
    RestTransformDocumentInput input = new RestTransformDocumentInput(templatePath, documentPath);
    input.setTextValueField(restTextValueFields);
    input.setDynamicTables(new ArrayList<>());

    RestTransformDocumentOutput output = transformerService.transform(input, queryParams);

    return Arrays.asList(output.getBaseDocumentId(), output.getDocumentId());
  }

  private void removePreviousLoanDecisionDocument(String caseInstanceId, String processRequestId)
  {
    LOGGER.info("############ Removes previous MICRO loan calculation info with REQUEST ID = [{}]", processRequestId);

    documentRepository.removeBy(caseInstanceId, CATEGORY_CALCULATION_INFO_NAME, SUB_CATEGORY_CALCULATION_INFO_NAME, CALCULATION_INFO_NAME);
  }

  private void persistLoanDecisionDocument(String documentId, String caseInstanceId, String processRequestId) throws BpmRepositoryException
  {
    if (null != documentId)
    {
      LOGGER.info("############ Persists MICRO loan decision document with REQUEST ID = [{}]", processRequestId);
      documentRepository.create(documentId, DOCUMENT_INFO_ID_CALCULATION_INFO_NAME, caseInstanceId, CALCULATION_INFO_NAME,
          CATEGORY_CALCULATION_INFO_NAME, SUB_CATEGORY_CALCULATION_INFO_NAME, documentId, ALFRESCO);
    }

    LOGGER.info("########## Successful generated MICRO loan decision with REQUEST ID = [{}]", processRequestId);
  }

  private String getDocumentPath(String registerNumber, String processRequestId)
  {
    long timeStamp = new Date().getTime();
    String timeStampStr = Long.toString(timeStamp);

    return BpmModuleConstants.CUSTOMERS_FOLDER + SLASH + registerNumber + SLASH + processRequestId + SLASH + CALCULATION_INFO_NAME + "_" + timeStampStr;
  }

  private List<RestTextValueField> getTextFields(DelegateExecution execution)
  {
    List<RestTextValueField> textFields = new ArrayList<>();

    Map<String, Object> variables = execution.getVariables();

    textFields.add(new RestTextValueField("loanProductType", getSpaceString(variables.get("loanProductDescription"))));
    textFields.add(new RestTextValueField(BRANCH_NUMBER, getSpaceString(variables.get(BRANCH_NUMBER))));
    textFields.add(new RestTextValueField(FULL_NAME, getSpaceString(variables.get(FULL_NAME))));
    textFields.add(new RestTextValueField(REGISTER_NUMBER, getSpaceString(variables.get(REGISTER_NUMBER))));
    textFields.add(new RestTextValueField("fullNameCoBorrower", getSpaceString(variables.get("fullNameCoBorrower-1"))));
    textFields.add(new RestTextValueField("registerNumberCoBorrower", getSpaceString(variables.get("registerNumberCoBorrower-1"))));
    textFields.add(new RestTextValueField("amount", getThousandSeparatedString(getSpaceString(variables.get("amount")))));
    textFields.add(new RestTextValueField(INTEREST_RATE, getSpaceString(variables.get(INTEREST_RATE))));
    textFields.add(new RestTextValueField(LOAN_TERM, getSpaceString(variables.get(LOAN_TERM))));
    textFields.add(new RestTextValueField(HAS_MORTGAGE, getSpaceString(variables.get(HAS_MORTGAGE))));
    textFields.add(new RestTextValueField("areasOfActivity", getSpaceString(variables.get("areasOfActivity"))));
    textFields.add(new RestTextValueField("reportingPeriodCash", getThousandSeparatedString(getSpaceString(variables.get("reportingPeriodCash")))));
    textFields.add(new RestTextValueField(CURRENT_ASSETS, getThousandSeparatedString(getSpaceString(variables.get(CURRENT_ASSETS)))));
    textFields.add(new RestTextValueField(FIXED_ASSETS, getThousandSeparatedString(getSpaceString(variables.get(FIXED_ASSETS)))));
    textFields.add(new RestTextValueField(SUPPLIER_PAY, getThousandSeparatedString(getSpaceString(variables.get(SUPPLIER_PAY)))));
    textFields.add(new RestTextValueField(SHORT_TERM_PAYMENT, getThousandSeparatedString(getSpaceString(variables.get(SHORT_TERM_PAYMENT)))));
    textFields.add(new RestTextValueField(LONG_TERM_PAYMENT, getThousandSeparatedString(getSpaceString(variables.get(LONG_TERM_PAYMENT)))));
    textFields.add(new RestTextValueField("consumerLoanRepayment", getThousandSeparatedString(getSpaceString(variables.get("consumerLoanRepayment")))));
    textFields.add(new RestTextValueField("businessLoanRepayment", getThousandSeparatedString(getSpaceString(variables.get("businessLoanRepayment")))));
    textFields.add(new RestTextValueField(REPORT_PERIOD, getSpaceString(variables.get(REPORT_PERIOD))));
    textFields.add(new RestTextValueField(SALES_INCOME, getThousandSeparatedString(getSpaceString(variables.get(SALES_INCOME)))));
    textFields.add(new RestTextValueField(OTHER_INCOME, getThousandSeparatedString(getSpaceString(variables.get(OTHER_INCOME)))));
    textFields.add(new RestTextValueField(COST_OF_GOODS, getThousandSeparatedString(getSpaceString(variables.get(COST_OF_GOODS)))));
    textFields.add(new RestTextValueField(OPERATING_EXPENSES, getThousandSeparatedString(getSpaceString(variables.get(OPERATING_EXPENSES)))));
    textFields.add(new RestTextValueField(TAX_COSTS, getThousandSeparatedString(getSpaceString(variables.get(TAX_COSTS)))));
    textFields.add(new RestTextValueField(RENTAL_EXPENSES, getThousandSeparatedString(getSpaceString(variables.get(RENTAL_EXPENSES)))));
    textFields.add(new RestTextValueField(OTHER_EXPENSES, getSpaceString(getOwnAssets(String.valueOf(variables.get(CASE_INSTANCE_ID)), "operation14", "amount1"))));
    textFields.add(new RestTextValueField(NET_PROFIT, getThousandSeparatedString(getSpaceString(variables.get(NET_BENEFIT)))));
    textFields.add(new RestTextValueField("householdExpenses", getThousandSeparatedString(getSpaceString(variables.get("householdExpenses")))));
    textFields.add(new RestTextValueField(DEBT_TO_SOLVENCY_RATIO_STRING, getSpaceString(variables.get(DEBT_TO_SOLVENCY_RATIO_STRING))));
    textFields.add(new RestTextValueField(DEBT_TO_INCOME_RATIO_STRING, getSpaceString(variables.get(DEBT_TO_INCOME_RATIO_STRING))));
    textFields.add(new RestTextValueField(ACCEPTED_LOAN_AMOUNT, getThousandSeparatedString(getSpaceString(variables.get(ACCEPTED_LOAN_AMOUNT)))));
    textFields.add(new RestTextValueField("purposeOfLoan", getSpaceString(variables.get("purposeOfLoan"))));
    textFields.add(new RestTextValueField("collateralProvidedAmountString", getSpaceString(variables.get("collateralProvidedAmountString"))));
    textFields.add(new RestTextValueField("totalCollateralAmountUDF", getThousandSeparatedString((getSpaceString(variables.get("collateralAmount"))))));
    textFields.add(new RestTextValueField("sentUser", getSpaceString(variables.get("sentUser"))));
    textFields.add(new RestTextValueField("receivedUser", getSpaceString(variables.get("confirmedUserId"))));
    textFields.add(new RestTextValueField("ownAssest", getSpaceString(getOwnAssets(String.valueOf(variables.get(CASE_INSTANCE_ID)), "debt0", "amount2"))));
    textFields.add(new RestTextValueField("date", getSpaceString(LocalDate.now())));
    return textFields;
  }

  private static String getSpaceString(Object value)
  {
    if (null == value || NULL_STRING.equalsIgnoreCase(String.valueOf(value)) || value.toString().equals(NULL_STRING) || org.springframework.util.StringUtils.isEmpty(value))
    {
      return " ";
    }
    return String.valueOf(value);
  }
  private String getOwnAssets(String instanceId,String parameterName, String mapKey){
    Map<String, Object> parameters = processRepository.getProcessParametersByInstanceId(instanceId);
    if(null != parameters) {
      String parameter = StringUtils.getStringValue(parameters.get(parameterName));
      if (null != parameter){
        Map jsonParamater = convertJsonStringToMap(parameter);
        String data = getValidString(jsonParamater.get(mapKey));
        if (null != data) {
          return data;
        } else return null;
      } else return null;
    } else return null;
  }
}