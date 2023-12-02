package mn.erin.domain.bpm.usecase.collateral;

import java.util.List;
import java.util.Map;

/**
 * @author Tamir
 */
public class GetCollReferenceCodesOutput
{
  private final Map<String, List<String>> referenceCodes;

  public GetCollReferenceCodesOutput(Map<String, List<String>> referenceCodes)
  {
    this.referenceCodes = referenceCodes;
  }

  public Map<String, List<String>> getReferenceCodes()
  {
    return referenceCodes;
  }
}
