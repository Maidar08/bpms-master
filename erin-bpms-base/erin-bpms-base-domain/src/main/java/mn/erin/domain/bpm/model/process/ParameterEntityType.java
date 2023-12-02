package mn.erin.domain.bpm.model.process;

import java.io.Serializable;

/**
 * @author Zorig
 */
public enum ParameterEntityType implements Serializable
{
  CUSTOMER, SALARY, XYP_SALARY, LOAN, ACCOUNT, FORM,
  LOAN_CONTRACT, LOAN_ATTACHMENT_CONTRACT, CO_OWNER_CONTRACT, COLLATERAL_REAL_ESTATE_CONTRACT, FIDUCIARY_CONTRACT,
  LOAN_REPORT, COMPLETED_FORM, NOTE, COLLATERAL, COLLATERAL_LIST, UD_FIELD_COLLATERAL, BALANCE, ONLINE_SALARY, BNPL, INSTANT_LOAN, ONLINE_LEASING;


  ParameterEntityType()
  {

  }

  public static ParameterEntityType fromStringToEnum(String parameterEntityTypeStr)
  {
    switch (parameterEntityTypeStr)
    {
    case "CUSTOMER":
      return ParameterEntityType.CUSTOMER;
    case "LOAN_REPORT":
      return ParameterEntityType.LOAN_REPORT;
    case "SALARY":
      return ParameterEntityType.SALARY;
    case "LOAN":
      return ParameterEntityType.LOAN;
    case "ACCOUNT":
      return ParameterEntityType.ACCOUNT;
    case "FORM":
      return ParameterEntityType.FORM;
    case "LOAN_CONTRACT":
      return ParameterEntityType.LOAN_CONTRACT;
    case "LOAN_ATTACHMENT_CONTRACT" :
      return ParameterEntityType.LOAN_ATTACHMENT_CONTRACT;
    case "CO_OWNER_CONTRACT" :
      return ParameterEntityType.CO_OWNER_CONTRACT;
    case "COLLATERAL_REAL_ESTATE_CONTRACT" :
      return ParameterEntityType.COLLATERAL_REAL_ESTATE_CONTRACT;
    case "FIDUCIARY_CONTRACT" :
      return ParameterEntityType.FIDUCIARY_CONTRACT;
    case "NOTE":
      return ParameterEntityType.NOTE;
    case "COMPLETED_FORM":
      return ParameterEntityType.COMPLETED_FORM;
    case "XYP_SALARY":
      return ParameterEntityType.XYP_SALARY;
    case "COLLATERAL":
      return ParameterEntityType.COLLATERAL;
    case "UD_FIELD_COLLATERAL":
      return ParameterEntityType.UD_FIELD_COLLATERAL;
    case "COLLATERAL_LIST":
      return ParameterEntityType.COLLATERAL_LIST;
    case "BALANCE":
      return ParameterEntityType.BALANCE;
    case "ONLINE_SALARY":
      return ParameterEntityType.ONLINE_SALARY;
    case "BNPL":
      return ParameterEntityType.BNPL;
    case "INSTANT_LOAN":
      return ParameterEntityType.INSTANT_LOAN;
    case "ONLINE_LEASING":
      return ParameterEntityType.ONLINE_LEASING;
    default:
      throw new IllegalArgumentException("Incompatible Entity Type!");
    }
  }

  public static String enumToString(ParameterEntityType entityType)
  {
    switch (entityType)
    {
    case CUSTOMER:
      return "CUSTOMER";
    case SALARY:
      return "SALARY";
    case LOAN:
      return "LOAN";
    case ACCOUNT:
      return "ACCOUNT";
    case FORM:
      return "FORM";
    case LOAN_CONTRACT:
      return "LOAN_CONTRACT";
    case LOAN_ATTACHMENT_CONTRACT:
      return "LOAN_ATTACHMENT_CONTRACT";
    case CO_OWNER_CONTRACT:
      return "CO_OWNER_CONTRACT";
    case COLLATERAL_REAL_ESTATE_CONTRACT:
      return "COLLATERAL_REAL_ESTATE_CONTRACT";
    case FIDUCIARY_CONTRACT:
      return "FIDUCIARY_CONTRACT";
    case NOTE:
      return "NOTE";
    case LOAN_REPORT:
      return "LOAN_REPORT";
    case COMPLETED_FORM:
      return "COMPLETED_FORM";
    case COLLATERAL:
      return "COLLATERAL";
    case UD_FIELD_COLLATERAL:
      return "UD_FIELD_COLLATERAL";
    case  COLLATERAL_LIST:
      return "COLLATERAL_LIST";
    case BALANCE:
      return "BALANCE";
    case ONLINE_SALARY:
      return "ONLINE_SALARY";
    case BNPL:
      return "BNPL";
    case INSTANT_LOAN:
      return "INSTANT_LOAN";
    case ONLINE_LEASING:
      return "ONLINE_LEASING";
    default:
      throw new IllegalArgumentException("Incompatible value!");
    }
  }
  public static ParameterEntityType getTypeByProcessType(String processType)
  {
    switch (processType)
    {
    case "onlineSalary":
      return ParameterEntityType.ONLINE_SALARY;
    case "bnplLoan":
      return ParameterEntityType.BNPL;
    case "instantLoan":
      return ParameterEntityType.INSTANT_LOAN;
    default:
      return ParameterEntityType.ONLINE_LEASING;
    }
  }
}
