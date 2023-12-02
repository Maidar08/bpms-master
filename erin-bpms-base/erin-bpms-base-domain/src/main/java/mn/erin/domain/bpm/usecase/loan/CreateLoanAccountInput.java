package mn.erin.domain.bpm.usecase.loan;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Zorig
 */
public class CreateLoanAccountInput
{
  private final String productCode;
  private final Map<String, Object> accountCreationInformation;
  private final List<Map<String, String>> coBorrowers;

  public CreateLoanAccountInput(String productCode, Map<String, Object> accountCreationInformation,
      List<Map<String, String>> coBorrowers)
  {
    this.productCode = productCode;
    this.accountCreationInformation = accountCreationInformation;
    this.coBorrowers = coBorrowers;
  }

  public Map<String, Object> getAccountCreationInformation()
  {
    return Collections.unmodifiableMap(accountCreationInformation);
  }

  public String getProductCode()
  {
    return productCode;
  }

  public List<Map<String, String>> getCoBorrowers()
  {
    return coBorrowers;
  }
}
