package mn.erin.domain.bpm.usecase.file;

import java.util.List;

import mn.erin.domain.bpm.model.file.ExcelHeader;

/**
 * @author Bilguunbor
 **/
public class ReadFromExcelInput
{
  private String contentAsBase64;
  private List<ExcelHeader> headerValues;

  public ReadFromExcelInput(String contentAsBase64, List<ExcelHeader> headerValues)
  {
    this.contentAsBase64 = contentAsBase64;
    this.headerValues = headerValues;
  }

  public String getContentAsBase64()
  {
    return contentAsBase64;
  }

  public void setContentAsBase64(String contentAsBase64)
  {
    this.contentAsBase64 = contentAsBase64;
  }

  public List<ExcelHeader> getHeaderValues()
  {
    return headerValues;
  }

  public void setHeaderValues(List<ExcelHeader> headerValues)
  {
    this.headerValues = headerValues;
  }
}
