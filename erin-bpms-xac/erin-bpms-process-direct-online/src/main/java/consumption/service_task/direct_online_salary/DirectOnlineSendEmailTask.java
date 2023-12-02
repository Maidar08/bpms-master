package consumption.service_task.direct_online_salary;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.mail.EmailService;
import mn.erin.domain.base.usecase.MessageUtil;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.GetProcessLargeParameter;
import mn.erin.domain.bpm.usecase.process.GetProcessParameterInput;
import mn.erin.domain.bpm.usecase.process.GetProcessParameterOutput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateOutput;

import static consumption.constant.CamundaVariableConstants.LOCALE;
import static consumption.util.CamundaUtils.updateTaskStatus;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.EMAIL;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_AMOUNT_75;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.OLD_FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_LOAN_CONTRACT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_LOAN_REPAYMENT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.ONLINE_SALARY;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.DISBURSED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.DISBURSE_FAILED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.fromEnumToString;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.getLogPrefix;

public class DirectOnlineSendEmailTask implements JavaDelegate

{
  private final EmailService emailService;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private static final Logger LOGGER = LoggerFactory.getLogger(DirectOnlineSendEmailTask.class);
  public static final String EMAIL_XAC_ONLINE_SALARY_LOAN_CONFIRMED = "email.xac.ONLINE_SALARY_LOAN_CONFIRMED";
  public static final String EMAIL_XAC_BNPL_LOAN_CONFIRMED = "email.subject.xac.BNPL_LOAN_DISBURSED_EMAIL";
  public static final String EMAIL_XAC_INSTANT_LOAN_CONFIRMED = "email.subject.xac.INSTANT_LOAN_DISBURSED_EMAIL";


  public DirectOnlineSendEmailTask(EmailService emailService, BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    this.emailService = emailService;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
  }

  @Override
  public void execute(DelegateExecution execution)
  {
    boolean isInstantLoan = false;
    boolean isOnlineLeasing = false;
    try
    {
      String loanAccountNumber = String.valueOf(execution.getVariable(LOAN_ACCOUNT_NUMBER));
      String customerEmail = String.valueOf(execution.getVariable(EMAIL));
      String locale = String.valueOf(execution.getVariable(LOCALE));
      String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));

      String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
      String processInstanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
      String processType = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
      String instanceId = processType.equals(ONLINE_SALARY_PROCESS_TYPE) ? caseInstanceId : processInstanceId;
      String templateName;
      String emailSubject;
      String logPrefix = getLogPrefix(processType);
      byte[] paymentFileAsByte;
      byte[] contractFileAsByte;
      double loanAmount;

      if (processType.equals(BNPL_PROCESS_TYPE_ID))
      {
        paymentFileAsByte = base64toByte(getFileFromProcessLargeParameter(instanceId, "bnplLoanRepaymentFile", ParameterEntityType.BNPL), "repaymentFile");
        contractFileAsByte = base64toByte(getFileFromProcessLargeParameter(instanceId, "bnplLoanContractFile", ParameterEntityType.BNPL), "contractFile");
        loanAmount = new BigDecimal(String.valueOf(execution.getVariable(INVOICE_AMOUNT_75))).doubleValue();
        templateName = locale.equalsIgnoreCase("mn") ? "send-bnpl-email-mn.ftl" : "send-bnpl-email-en.ftl";
        emailSubject = MessageUtil.getMessageByLocale(EMAIL_XAC_BNPL_LOAN_CONFIRMED, locale.toLowerCase()).getText();
      }
      else if (processType.equals(INSTANT_LOAN_PROCESS_TYPE_ID))
      {
        isInstantLoan = true;
        paymentFileAsByte = base64toByte(getFileFromProcessLargeParameter(instanceId, "instantLoanRepaymentFile", ParameterEntityType.INSTANT_LOAN),
            "repaymentFile");
        contractFileAsByte = base64toByte(getFileFromProcessLargeParameter(instanceId, "instantLoanContractFile", ParameterEntityType.INSTANT_LOAN),
            "contractFile");
        loanAmount = Double.parseDouble(String.valueOf(execution.getVariable(OLD_FIXED_ACCEPTED_LOAN_AMOUNT)));
        templateName = locale.equalsIgnoreCase("mn") ? "send-instantLoan-email-mn.ftl" : "send-instantLoan-email-en.ftl";
        emailSubject = MessageUtil.getMessageByLocale(EMAIL_XAC_INSTANT_LOAN_CONFIRMED, locale.toLowerCase()).getText();
      }
      else if (processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        isOnlineLeasing = true;
        paymentFileAsByte = base64toByte(getFileFromProcessLargeParameter(instanceId, "onlineLeasingRepaymentFile", ParameterEntityType.ONLINE_LEASING),
            "repaymentFile");
        contractFileAsByte = base64toByte(getFileFromProcessLargeParameter(instanceId, "onlineLeasingContractFile", ParameterEntityType.ONLINE_LEASING),
            "contractFile");
        loanAmount = Double.parseDouble(String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT)));
        templateName = locale.equalsIgnoreCase("mn")  ? "send-online-leasing-email-mn.ftl" : "send-online-leasing-email-en.ftl";
        emailSubject = MessageUtil.getMessageByLocale(EMAIL_XAC_INSTANT_LOAN_CONFIRMED, locale.toLowerCase()).getText();
      }
      else
      {
        paymentFileAsByte = base64toByte(getFileFromProcessLargeParameter(instanceId, ONLINE_SALARY_LOAN_REPAYMENT_BASE64, ONLINE_SALARY), "repaymentFile");
        contractFileAsByte = base64toByte(getFileFromProcessLargeParameter(instanceId, ONLINE_SALARY_LOAN_CONTRACT_BASE64, ONLINE_SALARY), "contractFile");
        loanAmount = getValidString(execution.getVariable("requestedLoanAmount")).equals(EMPTY_VALUE) ?
            Double.parseDouble(String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT))) :
             Double.parseDouble(getValidString(execution.getVariable("requestedLoanAmount")));
        templateName = locale.equalsIgnoreCase("mn") ? "send-email-for-confirm-mn.ftl" : "send-email-for-confirm-en.ftl";
        emailSubject = MessageUtil.getMessageByLocale(EMAIL_XAC_ONLINE_SALARY_LOAN_CONFIRMED, locale.toLowerCase()).getText();
      }

      List<String> recipients = new ArrayList<>();
      recipients.add(customerEmail);

      Map<String, byte[]> attachmentData = new HashMap<>();
      attachmentData.put("Loan Repayment Schedule", paymentFileAsByte);
      attachmentData.put("Loan Contract", contractFileAsByte);

      Map<String, Object> templateData = new HashMap<>();

      templateData.put(LOAN_ACCOUNT_NUMBER, loanAccountNumber);
      templateData.put("loanAmount", loanAmount);
      templateData.put("templateName", templateName);
      templateData.put("subject", emailSubject);
      templateData.put("isDigitalLoan", true);

      LOGGER.info("{} Send email, requestId [{}]. {}", logPrefix, requestId,
          (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));

      boolean isEmailSent = bulkSendEmailSyncWithAttachment(emailService, recipients, emailSubject, templateName, templateData, attachmentData, requestId,
          logPrefix);
      ProcessRequestState state = isEmailSent ? DISBURSED : DISBURSE_FAILED;
      updateProcessRequestState(requestId, state, logPrefix);
      if (!isEmailSent)
      {
        throw new BpmnError("Send Email", "Failed to send mail!");
      }
      if (isInstantLoan || isOnlineLeasing)
      {
        updateTaskStatus(execution, "Send email", "Success");
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      if (isInstantLoan  || isOnlineLeasing)
        updateTaskStatus(execution, "Send email", "Failed");
      String message = e.getMessage() == null ? "java.lang.NullPointerException" : e.getMessage();
      execution.setVariable(ERROR_CAUSE, message);
      throw new BpmnError("Send Email", message);
    }
  }

  public boolean bulkSendEmailSyncWithAttachment(EmailService emailService, List<String> recipients, String subject, String templateName,
      Map<String, Object> templateData, Map<String, byte[]> attachmentData, String requestId, String logPrefix)
  {
    boolean isEmailSent = emailService.sendEmail(recipients, Collections.emptyList(), subject, templateName, templateData, attachmentData);
    if (isEmailSent)
    {
      LOGGER.info("{} Sent an email with requestId = {} ", logPrefix, requestId);
      return true;
    }
    else
    {
      LOGGER.error("{} Failed to send email with requestId = {} ", logPrefix, requestId);
    }
    return false;
  }

  private byte[] base64toByte(String base64String, String filePathName)
  {
    File file = new File("./" + filePathName + ".pdf");
    byte[] arr = new byte[0];
    try (FileOutputStream fos = new FileOutputStream(file))
    {
      byte[] decoder = Base64.getDecoder().decode(base64String);
      fos.write(decoder);
      arr = decoder;
      fos.close();
      return arr;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return arr;
  }

  private String getFileFromProcessLargeParameter(String caseInstanceId, String parameterName, ParameterEntityType processType) throws UseCaseException
  {
    GetProcessLargeParameter getProcessLargeParameter = new GetProcessLargeParameter(bpmsRepositoryRegistry.getProcessRepository());
    GetProcessParameterInput input1 = new GetProcessParameterInput(caseInstanceId, parameterName, processType);
    final GetProcessParameterOutput execute = getProcessLargeParameter.execute(input1);
    return String.valueOf(execute.getParameterValue());
  }

  private void updateProcessRequestState(String requestId, ProcessRequestState state, String logPrefix) throws UseCaseException
  {
    ProcessRequestRepository processRequestRepository = bpmsRepositoryRegistry.getProcessRequestRepository();
    UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);
    UpdateRequestStateInput input = new UpdateRequestStateInput(requestId, state);
    UpdateRequestStateOutput output = updateRequestState.execute(input);
    if (output.isUpdated())
    {
      LOGGER.info("{} Updated process request state to {}, request id [{}]", logPrefix, fromEnumToString(state), requestId);
    }
  }
}