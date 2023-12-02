package mn.erin.bpms.loan.consumption.utils;

/**
 * @author Tamir
 */
public final class AccountUtils
{

  public static final String EB_50_PRODUCT_CODE = "EB50";
  public static final String EF_50_PRODUCT_CODE = "EF50";
  public static final String EB_51_PRODUCT_CODE = "EB51";

  public static final String EB_50_PRODUCT_DESCRIPTION = "EB50-365-Цалингийн зээл-Иргэн";
  public static final String EF_50_PRODUCT_DESCRIPTION = "EF50-365-Ажиллагсадын хэрэглээний зээл";
  public static final String EB_51_PRODUCT_DESCRIPTION = "EB51-365-Цалингийн зээл-Иргэн-EMI";

  private AccountUtils()
  {

  }

  public static String getProductCode(String loanProduct)
  {
    String productCode = null;
    if (loanProduct.equals(EB_50_PRODUCT_DESCRIPTION) || loanProduct.equals(EB_50_PRODUCT_CODE))
    {
      productCode = EB_50_PRODUCT_CODE;
    }
    else if (loanProduct.equals(EF_50_PRODUCT_DESCRIPTION) || loanProduct.equals(EF_50_PRODUCT_CODE))
    {
      productCode = EF_50_PRODUCT_CODE;
    }
    else if (loanProduct.equals(EB_51_PRODUCT_DESCRIPTION) || loanProduct.equals(EB_51_PRODUCT_CODE))
    {
      productCode = EB_51_PRODUCT_CODE;
    }

    if (null == productCode)
    {
      productCode = loanProduct.substring(0, 4);
    }

    return productCode;
  }
}
