package mn.erin.domain.bpm.usecase.organization;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.organization.FormDataOrganizationLeasing;
import mn.erin.domain.bpm.model.organization.FormDataOrganizationSalary;
import mn.erin.domain.bpm.repository.OrganizationLeasingRepository;
import mn.erin.domain.bpm.repository.OrganizationSalaryRepository;
import mn.erin.domain.bpm.util.process.BpmUtils;

import static mn.erin.domain.bpm.BpmModuleConstants.ISO_DATE_FORMAT;

public class SaveCreateOrganizationFormData extends AbstractUseCase<SaveCreateOrganizationFormDataInput, SaveCreateOrganizationFormDataOutput>
{
  private final OrganizationSalaryRepository organizationSalaryRepository;
  private final OrganizationLeasingRepository organizationLeasingRepository;

  public SaveCreateOrganizationFormData(OrganizationSalaryRepository organizationSalaryRepository, OrganizationLeasingRepository organizationLeasingRepository)
  {
    this.organizationSalaryRepository = organizationSalaryRepository;
    this.organizationLeasingRepository = organizationLeasingRepository;
  }

  @Override
  public SaveCreateOrganizationFormDataOutput execute(SaveCreateOrganizationFormDataInput input) throws UseCaseException
  {
    String processType = input.getProcessType();
    String requestId = input.getRequestId();
    Map<String, Object> formProperties = input.getProperties();
    int numberOfColumnsUpdated = 0;

    if (processType.equals("leasingOrganization"))
    {
      try
      {
        FormDataOrganizationLeasing formDataOrganizationLeasing = mapToLeasingJdbc(requestId, formProperties);
        numberOfColumnsUpdated = organizationLeasingRepository.updateLeasingOrganizationRequest(formDataOrganizationLeasing);
      }
      catch (ParseException e)
      {
        throw new UseCaseException(e.getMessage());
      }
    }
    else if (processType.equals("salaryOrganization"))
    {
      try
      {
        FormDataOrganizationSalary formDataOrganizationSalary = mapToSalaryJdbc(requestId, formProperties);
        numberOfColumnsUpdated = organizationSalaryRepository.updateSalaryOrganizationRequest(formDataOrganizationSalary);
      }
      catch (ParseException e)
      {
        throw new UseCaseException(e.getMessage());
      }
    }

    return new SaveCreateOrganizationFormDataOutput(numberOfColumnsUpdated);
  }

  private FormDataOrganizationSalary mapToSalaryJdbc(String contractId, Map<String, Object> formProperties) throws ParseException
  {
    FormDataOrganizationSalary formDataOrganizationSalary = new FormDataOrganizationSalary(contractId);

    String contractEndDate = (String) formProperties.get("contractEndDate");
    if (!StringUtils.isBlank(contractEndDate))
    {
      if (contractEndDate.contains("/"))
      {
        contractEndDate = contractEndDate.replace("/", "-");
      }
      formDataOrganizationSalary.setContractEndDate(BpmUtils.convertStringToDate("MM-dd-yyyy", contractEndDate));
    }

    String partnerType = BpmUtils.getValidString(formProperties.get("partnerType"));
    if (StringUtils.equalsIgnoreCase("төрийн", partnerType))
    {
      formDataOrganizationSalary.setPartnerType(2);
    }
    else if (StringUtils.equalsIgnoreCase("хувийн", partnerType))
    {
      formDataOrganizationSalary.setPartnerType(1);
    }

    String contractOldNumber = BpmUtils.getValidString(formProperties.get("contractOldNumber"));
    formDataOrganizationSalary.setContractOldNumber(contractOldNumber);

    String partnerDirection = BpmUtils.getValidString(formProperties.get("partnerDirection"));
    formDataOrganizationSalary.setPartnerDirection(partnerDirection);

    String partnerRegistryId = BpmUtils.getValidString(formProperties.get("partnerRegistryId"));
    formDataOrganizationSalary.setPartnerRegistryId(partnerRegistryId);

    String partnerCodeND = BpmUtils.getValidString(formProperties.get("partnerCodeND"));
    formDataOrganizationSalary.setPartnerCodeND(partnerCodeND);

    String partnerNDSubordinate = BpmUtils.getValidString(formProperties.get("partnerNDSubordinate"));
    formDataOrganizationSalary.setPartnerNDSubordinate(partnerNDSubordinate);

    String partnerAccountId = BpmUtils.getValidString(formProperties.get("partnerAccountId"));
    formDataOrganizationSalary.setPartnerAccountId(partnerAccountId);

    String partnerContactEmployee = BpmUtils.getValidString(formProperties.get("partnerContactEmployee"));
    formDataOrganizationSalary.setPartnerContactEmployee(partnerContactEmployee);

    String partnerPhoneNumber = BpmUtils.getValidString(formProperties.get("partnerPhoneNumber"));
    formDataOrganizationSalary.setPartnerPhoneNumber(partnerPhoneNumber);

    String feeHasLoan = (String) formProperties.get("feeHasLoan");

    if (!StringUtils.isBlank(feeHasLoan))
    {
      int feeHasLoanInteger = 0;
      if (StringUtils.equals(feeHasLoan, "Тийм"))
      {
        feeHasLoanInteger = 1;
      }

      formDataOrganizationSalary.setFeeHasLoan(feeHasLoanInteger);
    }

    String feeHasOnline = BpmUtils.getValidString(formProperties.get("feeHasOnline"));
    formDataOrganizationSalary.setFeeHasOnline(getBooleanYorN(feeHasOnline));

    String feeType = BpmUtils.getValidString(formProperties.get("feeType"));
    if (StringUtils.equalsIgnoreCase(feeType, "Стандарт"))
    {
      formDataOrganizationSalary.setFeeType("ST");
    }
    else if (StringUtils.equalsIgnoreCase(feeType, "Тусгай"))
    {
      formDataOrganizationSalary.setFeeType("SP");
    }

    String feeOrganizationRating = BpmUtils.getValidString(formProperties.get("feeOrganizationRating"));
    formDataOrganizationSalary.setFeeOrganizationRating(feeOrganizationRating);

    String feeAmountPercent = BpmUtils.getValidString(formProperties.get("feeAmountPercent"));

    if (!StringUtils.isBlank(feeAmountPercent))
    {
      formDataOrganizationSalary.setFeeAmountPercent(Double.parseDouble(feeAmountPercent));
    }

    String feeKeyWorker = BpmUtils.getValidString(formProperties.get("feeKeyWorker"));
    if (!StringUtils.isBlank(feeKeyWorker))
    {
      formDataOrganizationSalary.setFeeKeyWorker(Double.parseDouble(feeKeyWorker));
    }

    String feeBankFraud = BpmUtils.getValidString(formProperties.get("feeBankFraud"));
    if (!StringUtils.isBlank(feeBankFraud))
    {
      formDataOrganizationSalary.setFeeBankFraud(feeBankFraud);
    }

    String contractExtensionYear = BpmUtils.getValidString(formProperties.get("contractExtensionYear"));
    formDataOrganizationSalary.setContractExtensionYear(contractExtensionYear.split(" ")[0]);

    String contractHasExtension = BpmUtils.getValidString(formProperties.get("contractHasExtension"));
    formDataOrganizationSalary.setContractHasExtension(getBooleanYorN(contractHasExtension));

    String contractExtensionNewDate = (String) formProperties.get("contractExtensionNewDate");
    if (contractExtensionNewDate != null)
    {
      formDataOrganizationSalary.setContractExtensionNewDate(BpmUtils.convertStringToDate(ISO_DATE_FORMAT, contractExtensionNewDate.substring(0,10)));
    }

    String feeSalaryTransaction = BpmUtils.getValidString(formProperties.get("feeSalaryTransaction"));
    if (!StringUtils.isBlank(feeSalaryTransaction))
    {
      formDataOrganizationSalary.setFeeSalaryTransaction(Long.parseLong(feeSalaryTransaction));
    }

    String feeSalaryDays = BpmUtils.getValidString(formProperties.get("feeSalaryDays"));
    formDataOrganizationSalary.setFeeSalaryDays(feeSalaryDays.split(" ")[0]);

    String feeSalaryDaysFirst = BpmUtils.getValidString(formProperties.get("feeSalaryDaysFirst"));
    formDataOrganizationSalary.setFeeSalaryDaysFirst(feeSalaryDaysFirst);

    String feeSalaryDaysSecond = BpmUtils.getValidString(formProperties.get("feeSalaryDaysSecond"));
    formDataOrganizationSalary.setFeeSalaryDaysSecond(feeSalaryDaysSecond);

    String contractSpecialRemark = BpmUtils.getValidString(formProperties.get("contractSpecialRemark"));
    formDataOrganizationSalary.setContractSpecialRemark(contractSpecialRemark);

    String contractPeriod = BpmUtils.getValidString(formProperties.get("contractPeriod"));
    formDataOrganizationSalary.setContractPeriod(contractPeriod.split(" ")[0]);
    Integer hrCount = (Integer) formProperties.get("partnerTotalEmployee");
    if (hrCount != null)
    {
      formDataOrganizationSalary.setHrCount(hrCount);
    }

    String contractDate = (String) formProperties.get("contractDate");
    if (contractDate != null)
    {
      Date date = BpmUtils.convertStringToDate(ISO_DATE_FORMAT, contractDate.substring(0,10));
      formDataOrganizationSalary.setContractDate(date);
    }

    String partnerEstablishedDate = (String) formProperties.get("partnerEstablishedDate");
    if (!StringUtils.isBlank(partnerEstablishedDate))
    {
      Date establishedDate = BpmUtils.convertStringToDate(ISO_DATE_FORMAT, partnerEstablishedDate.substring(0,10));
      formDataOrganizationSalary.setPartnerEstablishedDate(establishedDate);
    }

    String partnerCif = BpmUtils.getValidString(formProperties.get("partnerCif"));
    formDataOrganizationSalary.setPartnerCif(partnerCif);
    formDataOrganizationSalary.setCreatedDate(new Date());

    return formDataOrganizationSalary;
  }

  private FormDataOrganizationLeasing mapToLeasingJdbc(String contractId, Map<String, Object> formProperties) throws ParseException
  {
    FormDataOrganizationLeasing formDataOrganizationLeasing = new FormDataOrganizationLeasing(contractId);

    String contractDate = (String) formProperties.get("contractDate");

    if (!StringUtils.isBlank(contractDate))
    {
      Date date = BpmUtils.convertStringToDate(ISO_DATE_FORMAT, contractDate);
      formDataOrganizationLeasing.setContractDate(date);
    }

    String contractEndDate = (String) formProperties.get("contractEndDate");
    if (!StringUtils.isBlank(contractEndDate))
    {
      if (contractEndDate.contains("/"))
      {
        contractEndDate = contractEndDate.replace("/", "-");
      }
      formDataOrganizationLeasing.setContractEndDate(BpmUtils.convertStringToDate("MM-dd-yyyy", contractEndDate));
    }

    String contractOldNumber = BpmUtils.getValidString(formProperties.get("contractOldNumber"));
    formDataOrganizationLeasing.setContractOldNumber(contractOldNumber);

    String partnerCif = BpmUtils.getValidString(formProperties.get("partnerCif"));
    formDataOrganizationLeasing.setPartnerCif(partnerCif);

    String partnerRegistryId = BpmUtils.getValidString(formProperties.get("partnerRegistryId"));
    formDataOrganizationLeasing.setPartnerRegistryId(partnerRegistryId);

    String partnerEstablishedDate = (String) formProperties.get("partnerEstablishedDate");

    if (!StringUtils.isBlank(partnerEstablishedDate))
    {
      Date establishedDate = BpmUtils.convertStringToDate(ISO_DATE_FORMAT, partnerEstablishedDate);
      formDataOrganizationLeasing.setPartnerEstablishedDate(establishedDate);
    }

    String partnerAddress = BpmUtils.getValidString(formProperties.get("partnerAddress"));
    formDataOrganizationLeasing.setPartnerAddress(partnerAddress);

    String partnerPhoneNumber = BpmUtils.getValidString(formProperties.get("partnerPhoneNumber"));
    formDataOrganizationLeasing.setPartnerPhoneNumber(partnerPhoneNumber);

    String partnerEmail = BpmUtils.getValidString(formProperties.get("partnerEmail"));
    formDataOrganizationLeasing.setPartnerEmail(partnerEmail);

    String contractPeriod = BpmUtils.getValidString(formProperties.get("contractPeriod"));
    formDataOrganizationLeasing.setContractPeriod(contractPeriod.split(" ")[0]);

    String contractFee = BpmUtils.getValidString(formProperties.get("contractFee"));
    if (!StringUtils.isBlank(contractFee))
    {
      formDataOrganizationLeasing.setContractFee(Long.parseLong(contractFee));
    }

    String partnerType = BpmUtils.getValidString(formProperties.get("partnerType"));
    if (StringUtils.equalsIgnoreCase(partnerType, "ИРГЭН"))
    {
      formDataOrganizationLeasing.setPartnerType("R");
    }
    else if (StringUtils.equalsIgnoreCase(partnerType, "БАЙГУУЛЛАГА"))
    {
      formDataOrganizationLeasing.setPartnerType("C");
    }

    String partnerDirection = BpmUtils.getValidString(formProperties.get("partnerDirection"));
    formDataOrganizationLeasing.setPartnerDirection(partnerDirection);

    String productSuppliedType = BpmUtils.getValidString(formProperties.get("productSuppliedType"));
    formDataOrganizationLeasing.setProductSuppliedType(productSuppliedType);

    String productSuppliedDescription = BpmUtils.getValidString(formProperties.get("productSuppliedDescription"));
    formDataOrganizationLeasing.setProductSuppliedDescription(productSuppliedDescription);

    String contactName = BpmUtils.getValidString(formProperties.get("contactName"));
    formDataOrganizationLeasing.setContactName(contactName);

    String contactPhoneNumber = BpmUtils.getValidString(formProperties.get("contactPhoneNumber"));
    formDataOrganizationLeasing.setContactPhoneNumber(contactPhoneNumber);

    String contactEmail = BpmUtils.getValidString(formProperties.get("contactEmail"));
    formDataOrganizationLeasing.setContactEmail(contactEmail);

    String contactDescription = BpmUtils.getValidString(formProperties.get("contactDescription"));
    formDataOrganizationLeasing.setContactDescription(contactDescription);

    String feeSupplierDueDate = BpmUtils.getValidString(formProperties.get("feeSupplierDue"));
    formDataOrganizationLeasing.setFeeSupplierDueDate(feeSupplierDueDate);

    String feeSupplierAmountPercent = BpmUtils.getValidString(formProperties.get("feeSupplierAmountPercent"));
    formDataOrganizationLeasing.setFeeSupplierAmountPercent(feeSupplierAmountPercent);

    String feeAccountNumber = BpmUtils.getValidString(formProperties.get("feeAccountNumber"));
    formDataOrganizationLeasing.setFeeAccountNumber(feeAccountNumber);

    String feeType = BpmUtils.getValidString(formProperties.get("feeType"));
    if (StringUtils.equalsIgnoreCase(feeType, "СТАНДАРТ"))
    {
      formDataOrganizationLeasing.setFeeType("Standard");
    }
    else if (StringUtils.equalsIgnoreCase(feeType, "ТУСГАЙ"))
    {
      formDataOrganizationLeasing.setFeeType("Special");
    }

    String feeAmountPercent = BpmUtils.getValidString(formProperties.get("feeAmountPercent"));

    if (!StringUtils.isBlank(feeAmountPercent))
    {
      formDataOrganizationLeasing.setFeeAmountPercent(Double.parseDouble(feeAmountPercent));
    }

    String feeLoanOriginationFee = BpmUtils.getValidString(formProperties.get("feeLoanOriginationFee"));

    if (!StringUtils.isBlank(feeLoanOriginationFee))
    {
      formDataOrganizationLeasing.setFeeLoanOriginationFee(Long.parseLong(feeLoanOriginationFee));
    }

    String feeOnlineBnpl = BpmUtils.getValidString(formProperties.get("feeOnlineBnpl"));
    String convertedBnpl;
    if (feeOnlineBnpl.equals("Тийм")) {
      convertedBnpl = "Y";
    }
    else {
      convertedBnpl = "N";
    }
    formDataOrganizationLeasing.setFeeOnlineBnpl(convertedBnpl);

    String feeTerminalId = BpmUtils.getValidString(formProperties.get("feeTerminalId"));
    formDataOrganizationLeasing.setFeeTerminalId(feeTerminalId);

    String contractExtensionYear = BpmUtils.getValidString(formProperties.get("contractExtensionYear"));
    formDataOrganizationLeasing.setContractExtensionYear(contractExtensionYear.split(" ")[0]);

    String contractHasExtension = BpmUtils.getValidString(formProperties.get("contractHasExtension"));
    formDataOrganizationLeasing.setContractHasExtension(contractHasExtension);

    String contractExtensionNewDate = (String) formProperties.get("contractExtensionNewDate");
    if (contractExtensionNewDate != null)
    {
      formDataOrganizationLeasing.setContractExtensionNewDate(BpmUtils.convertStringToDate(ISO_DATE_FORMAT, contractExtensionNewDate));
    }

    String feePaymentType0 = (String) formProperties.get("feePaymentType0");
    String feePaymentType1 = (String) formProperties.get("feePaymentType1");
    String feePaymentType2 = (String) formProperties.get("feePaymentType2");

    formDataOrganizationLeasing.setFeePaymentType(setDynamicFormsToOne(feePaymentType0, feePaymentType1, feePaymentType2));

    String feePercentAmount0 = (String) formProperties.get("feePercentAmount0");
    String feePercentAmount1 = (String) formProperties.get("feePercentAmount1");
    String feePercentAmount2 = (String) formProperties.get("feePercentAmount2");

    formDataOrganizationLeasing.setFeePercentAmount(setDynamicFormsToOne(feePercentAmount0, feePercentAmount1, feePercentAmount2));

    Object feeLoanAmount0 = formProperties.get("feeLoanAmount0");
    Object feeLoanAmount1 = formProperties.get("feeLoanAmount1");
    Object feeLoanAmount2 = formProperties.get("feeLoanAmount2");

    formDataOrganizationLeasing.setFeeLoanAmount(setDynamicFormObjectsToOne(feeLoanAmount0, feeLoanAmount1, feeLoanAmount2));
    formDataOrganizationLeasing.setCreatedDate(new Date());

    return formDataOrganizationLeasing;
  }

  private String setDynamicFormsToOne(String first, String second, String third)
  {
    if (!StringUtils.isBlank(first))
    {
      if (!StringUtils.isBlank(second))
      {
        String concattedString = first.concat(";").concat(second);
        if (!StringUtils.isBlank(third))
        {
          return concattedString.concat(";").concat(third);
        }
        else
        {
          return concattedString;
        }
      }
      else
      {
        return first;
      }
    }
    else
    {
      return "";
    }
  }

  private String setDynamicFormObjectsToOne(Object first, Object second, Object third)
  {
    if (first != null)
    {
      if (first.equals(0))
      {
        return "";
      }

      String firstString = first.toString();
      if (second != null)
      {
        if (second.equals(-1) || second.equals(0))
        {
          return firstString;
        }
        String secondString = second.toString();

        String concatString = firstString.concat(";").concat(secondString);

        if (third != null)
        {
          if (third.equals(-1) || third.equals(0))
          {
            return concatString;
          }
          String thirdString = third.toString();

          return concatString.concat(";").concat(thirdString);
        }
        else
        {
          return concatString;
        }
      }
      else
      {
        return firstString;
      }
    }
    else
    {
      return "";
    }
  }

  private String getBooleanYorN(String str)
  {
    if (!StringUtils.isBlank(str))
    {
      if (StringUtils.equalsIgnoreCase(str, "тийм"))
      {
        return "Y";
      }
      else
      {
        return "N";
      }
    }
    else
      return null;
  }
}