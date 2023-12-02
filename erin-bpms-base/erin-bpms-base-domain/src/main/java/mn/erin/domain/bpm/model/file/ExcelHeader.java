package mn.erin.domain.bpm.model.file;

/**
 * @author Bilguunbor
 **/

public class ExcelHeader
{
  private String key;
  private String headerValue;

  public ExcelHeader(String key, String headerValue)
  {
    this.key = key;
    this.headerValue = headerValue;
  }

  public String getKey()
  {
    return key;
  }

  public void setKey(String key)
  {
    this.key = key;
  }

  public String getHeaderValue()
  {
    return headerValue;
  }

  public void setHeaderValue(String headerValue)
  {
    this.headerValue = headerValue;
  }
}
