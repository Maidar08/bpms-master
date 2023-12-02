package mn.erin.domain.bpm.usecase.loan.create_account;

import java.util.List;
import java.util.Map;

/**
 * @author Tamir
 */
public class AddLoanAccountInput
{
  private final Map<String, Object> genericInfo;
  private final Map<String, Object> additionalInfos;

  private final List<Map<String, Object>> coBorrowers;

  public AddLoanAccountInput(Map<String, Object> genericInfo,
      Map<String, Object> additionalInfos,
      List<Map<String, Object>> coBorrowers)
  {
    this.genericInfo = genericInfo;
    this.additionalInfos = additionalInfos;

    this.coBorrowers = coBorrowers;
  }

  public Map<String, Object> getGenericInfo()
  {
    return genericInfo;
  }

  public List<Map<String, Object>> getCoBorrowers()
  {
    return coBorrowers;
  }

  public Map<String, Object> getAdditionalInfos()
  {
    return additionalInfos;
  }
}
