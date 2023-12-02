package mn.erin.domain.bpm.usecase.customer;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CoreBankingService;

/**
 * @author Zorig
 */
public class GetCustomerAccountCreationInfo extends AbstractUseCase<String, GetCustomerAccountCreationInfoOutput>
{
  private final CoreBankingService coreBankingService;

  public GetCustomerAccountCreationInfo(CoreBankingService coreBankingService)
  {
    this.coreBankingService = Objects.requireNonNull(coreBankingService, "Core banking service is required!");
  }

  @Override
  public GetCustomerAccountCreationInfoOutput execute(String input) throws UseCaseException
  {
    if (StringUtils.isBlank(input))
    {
      throw new UseCaseException(BpmMessagesConstants.INVALID_INPUT_CODE, BpmMessagesConstants.INVALID_INPUT_MESSAGE);
    }

    try
    {
      Map<String, UDField> udFieldsMap = coreBankingService.getUDFields(input);

      UDField lateReasonAttention = udFieldsMap.get("REASON_OVERDUE_ANHAARAL_TAT");
      UDField firstAccountNumber = udFieldsMap.get("FIRST_ACCOUNT_NUMBER");
      UDField restructuredNumber = udFieldsMap.get("RESTRUCTURED_NUMBER");
      UDField attentiveLoan = udFieldsMap.get("ANHAARAL_TATSAN_ZEEL");
      UDField firstDisbursedLoanDate = udFieldsMap.get("FIRST_DISBURSED_DATE");
      UDField firstSupplier = udFieldsMap.get("SUPPLIERS_FINANCE_LEASING");

      UDField loanPurpose = udFieldsMap.get("LOAN_PURPOSE");
      UDField businessTypeReason = udFieldsMap.get("LOAN_PURPOSE1");
      UDField sanctionedBy = udFieldsMap.get("SANCTIONED_BY");

      String subTypeFieldName = getSubTypeFieldName(udFieldsMap, input);
      UDField subType = udFieldsMap.get(subTypeFieldName);

      UDField insuranceCompanyInfo = udFieldsMap.get("DETAILINFO_INSURANCE_COMPANY");
      UDField secondSupplier = udFieldsMap.get("SUPPLIERS_FINANCE_LEASING1");
      UDField thirdSupplier = udFieldsMap.get("SUPPLIERS_FINANCE_LEASING2");
      UDField lateReason = udFieldsMap.get("REASON_OVERDUE");
      UDField loanCycle = udFieldsMap.get("LOAN_CYCLE");
      UDField worker = udFieldsMap.get("CREDIT_OFFICER");

      return new GetCustomerAccountCreationInfoOutput(lateReasonAttention, firstAccountNumber, restructuredNumber, attentiveLoan, firstDisbursedLoanDate,
          firstSupplier,
          loanPurpose, businessTypeReason, sanctionedBy, subType, insuranceCompanyInfo, secondSupplier, thirdSupplier, lateReason, loanCycle, worker);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }

  private String getSubTypeFieldName(Map<String, UDField> udFieldMap, String productCode) throws UseCaseException
  {
    for (Map.Entry<String, UDField> udFieldEntry : udFieldMap.entrySet())
    {
      String udFieldEntryKey = udFieldEntry.getKey();
      UDField udField = udFieldEntry.getValue();
      if (udFieldEntryKey.contains("SUBTYPE") && udField.getFieldType().equals("T"))
      {
        return udFieldEntryKey;
      }
    }

    throw new UseCaseException("SUBTYPE could not be found for " + productCode + " from UDF Fields.");
  }
}
