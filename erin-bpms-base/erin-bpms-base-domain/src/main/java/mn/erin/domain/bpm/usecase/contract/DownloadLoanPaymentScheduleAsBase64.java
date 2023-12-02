/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.contract;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.DocumentService;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.EQUAL_PRINCIPLE_PAYMENT_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.EQUATED_MONTHLY_INSTALLMENT_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_EQUAL_PRINCIPLE_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_EQUATED_MONTHLY_INSTALLMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_TYPE_ID;

/**
 * @author Zorig
 */
public class DownloadLoanPaymentScheduleAsBase64 extends AbstractUseCase<Map<String, String>, String>
{
  private static final Logger LOG = LoggerFactory.getLogger(DownloadLoanPaymentScheduleAsBase64.class);

  private final DocumentService documentService;
  private final CaseService caseService;
  private final AuthenticationService authenticationService;
  private final MembershipRepository membershipRepository;

  public DownloadLoanPaymentScheduleAsBase64(DocumentService documentService, CaseService caseService,
      AuthenticationService authenticationService, MembershipRepository membershipRepository)
  {
    this.documentService = Objects.requireNonNull(documentService, "Document service is required!");
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "Membership Repository is required!");
  }

  @Override
  public String execute(Map<String, String> input) throws UseCaseException
  {
    String accountNumber = null;
    String repaymentType = null;
    Map<String, String> paymentScheduleInfo = null;
    String processType = null;
    List<Variable> allVariables = null;
    String instanceId = null;

    try
    {
      if (input.containsKey(PROCESS_TYPE_ID))
      {
        processType = input.get(PROCESS_TYPE_ID);
        instanceId = input.get(INSTANCE_ID);
        accountNumber = input.get(LOAN_ACCOUNT_NUMBER);
        String repaymentTypeId = input.get(REPAYMENT_TYPE_ID);

        if (repaymentTypeId.equals(REPAYMENT_EQUAL_PRINCIPLE_PAYMENT))
        {
          repaymentType = EQUAL_PRINCIPLE_PAYMENT_VALUE;
        }
        else if (repaymentTypeId.equals(REPAYMENT_EQUATED_MONTHLY_INSTALLMENT))
        {
          repaymentType = EQUATED_MONTHLY_INSTALLMENT_VALUE;
        }
      }
      else
      {
        String caseInstanceId = input.get(CASE_INSTANCE_ID);
        if (StringUtils.isBlank(caseInstanceId))
        {
          throw new UseCaseException("Instance Id is blank or null!");
        }
        processType = (String) caseService.getVariableById(caseInstanceId, PROCESS_TYPE_ID);
        allVariables = caseService.getVariables(caseInstanceId);

        instanceId = getProcessRequestId(allVariables);

        for (Variable variable : allVariables)
        {
          String variableId = variable.getId().getId();

          if (variableId.equals(LOAN_ACCOUNT_NUMBER))
          {
            accountNumber = getNotNullValue(variable);
          }
          else if (variableId.equals(REPAYMENT_TYPE_ID))
          {
            repaymentType = getRepaymentTypeValue(variable);
          }
        }
      }

      if (null == accountNumber)
      {
        String errorCode = "DMS020";
        LOG.info("########## LOAN ACCOUNT NUMBER is null!, REQUEST_ID = [{}]", instanceId);
        throw new UseCaseException(errorCode, "########## LOAN ACCOUNT NUMBER is null!, REQUEST_ID =" + instanceId);
      }
      if (null == repaymentType)
      {
        String errorCode = "DMS021";
        LOG.info("########## REPAYMENT TYPE is null, REQUEST_ID = [{}]", instanceId);
        throw new UseCaseException(errorCode, "########## REPAYMENT TYPE is null!, REQUEST_ID =" + instanceId);
      }

      if (repaymentType.equals(EQUAL_PRINCIPLE_PAYMENT_VALUE) && !processType.equals(ONLINE_SALARY_PROCESS_TYPE))
      {
        paymentScheduleInfo = getPaymentScheduleInfoEqualPrinciplePayment(allVariables);
      }
      else if (repaymentType.equals(EQUATED_MONTHLY_INSTALLMENT_VALUE))
      {
        paymentScheduleInfo = getPaymentScheduleInfoEqualMonthlyInstallment(allVariables);
      }
      else if (processType.equals(ONLINE_SALARY_PROCESS_TYPE))
      {
        Map<String, String> map = new HashMap<>();
        map.put("P_GRACE", "N");
        paymentScheduleInfo = map;
      }
      else
      {
        String errorCode = "DMS021";
        LOG.info("########## REPAYMENT TYPE is null, REQUEST_ID = [{}]", instanceId);
        throw new UseCaseException(errorCode, "########## REPAYMENT TYPE is null!, REQUEST_ID =" + instanceId);
      }

      LOG.info("########## Downloads customer LOAN PAYMENT SCHEDULE by ACCOUNT NUMBER = [{}], PAYMENT_TYPE = [{}] with REQUEST_ID = [{}]", accountNumber,
          repaymentType,
          instanceId);
      return documentService.downloadPaymentScheduleAsBase64(accountNumber, repaymentType, paymentScheduleInfo, processType);
    }
    catch (BpmServiceException e)
    {
      LOG.error("######## BPM service exception occurred during download loan payment schedule : ", e);
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }

  private String getRepaymentTypeValue(Variable variable)
  {
    Serializable value = variable.getValue();

    if (null != value)
    {
      String repaymentTypeId = (String) value;

      if (repaymentTypeId.equals(REPAYMENT_EQUAL_PRINCIPLE_PAYMENT))
      {
        return EQUAL_PRINCIPLE_PAYMENT_VALUE;
      }
      else if (repaymentTypeId.equals(REPAYMENT_EQUATED_MONTHLY_INSTALLMENT))
      {
        return EQUATED_MONTHLY_INSTALLMENT_VALUE;
      }
    }
    return null;
  }

  private String getProcessRequestId(List<Variable> allVariables)
  {
    for (Variable variable : allVariables)
    {
      String id = variable.getId().getId();

      if (id.equals(PROCESS_REQUEST_ID))
      {
        return (String) variable.getValue();
      }
    }
    return null;
  }

  private String getNotNullValue(Variable variable) throws UseCaseException
  {
    Serializable value = variable.getValue();

    if (null != value)
    {
      return (String) value;
    }

    String errorCode = "DMS019";
    throw new UseCaseException(errorCode, "######### LOAN ACCOUNT NUMBER variable is null during download loan payment schedule.");
  }

  private Map<String, String> getPaymentScheduleInfoEqualPrinciplePayment(List<Variable> allVariables)
  {
    Map<String, String> paymentScheduleInfo = new HashMap<>();

    //in the future add logic to change P_GRACE

    paymentScheduleInfo.put("P_GRACE", "Y");

    return paymentScheduleInfo;
  }

  private Map<String, String> getPaymentScheduleInfoEqualMonthlyInstallment(List<Variable> allVariables) throws UseCaseException
  {

    try
    {
      Map<String, String> paymentScheduleInfo = new HashMap<>();
      String userId = authenticationService.getCurrentUserId();
      List<Membership> memberships = membershipRepository.listAllByUserId(TenantId.valueOf("xac"), UserId.valueOf(userId));

      if (memberships.isEmpty())
      {
        throw new UseCaseException("User does not have membership!");
      }

      Membership membership = memberships.get(0);

      if (membership == null)
      {
        throw new UseCaseException("User does not have membership!");
      }

      GroupId branch = membership.getGroupId();

      if (branch == null)
      {
        throw new UseCaseException("User does not have a group");
      }

      //in the future add logic to change HAV
      paymentScheduleInfo.put("HAV", "1");
      paymentScheduleInfo.put("P_BRANCH", branch.getId());
      paymentScheduleInfo.put("P_USERID", userId);

      return paymentScheduleInfo;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}
