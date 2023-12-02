package mn.erin.domain.bpm.usecase.collateral;

import java.util.List;

/**
 * @author Tamir
 */
public class GetCollReferenceCodesInput
{
  private final List<String> types;

  public GetCollReferenceCodesInput(List<String> types)
  {
    this.types = types;
  }

  public List<String> getTypes()
  {
    return types;
  }
}
