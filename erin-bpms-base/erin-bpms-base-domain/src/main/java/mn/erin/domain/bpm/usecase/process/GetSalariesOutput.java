package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Zorig
 */
public class GetSalariesOutput
{
  private final Map<String, Map<String, Serializable>> salariesInfo;
  private final Serializable averageBeforeTax;
  private final Serializable averageAfterTax;
  private final Serializable hasMortgage;
  private final Serializable ndsh;
  private final Serializable emd;

  public GetSalariesOutput(Map<String, Map<String, Serializable>> salariesInfo, Serializable averageBeforeTax, Serializable averageAfterTax,
      Serializable hasMortgage, Serializable ndsh, Serializable emd)
  {
    this.salariesInfo = salariesInfo;
    this.averageBeforeTax = averageBeforeTax;
    this.averageAfterTax = averageAfterTax;
    this.hasMortgage = hasMortgage;
    this.ndsh = ndsh;
    this.emd = emd;
  }

  public Map<String, Map<String, Serializable>> getSalariesInfo()
  {
    return salariesInfo;
  }

  public Serializable getAverageBeforeTax()
  {
    return averageBeforeTax;
  }

  public Serializable getAverageAfterTax()
  {
    return averageAfterTax;
  }

  public Serializable getHasMortgage() { return  hasMortgage; }

  public Serializable getNdsh() { return  ndsh; }

  public Serializable getEmd() { return  emd; }
}
