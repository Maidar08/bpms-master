package mn.erin.bpm.repository.jdbc.model;

import org.springframework.data.relational.core.mapping.Table;

@Table("BI_PATH")
public class JdbcBiPath
{
  private String processTypeId;
  private String productCode;
  private String folder;
  private String path;
  
  public String getProcessTypeId() {
    return processTypeId;
  }
  public void setProcessTypeId(String processTypeId) {
    this.processTypeId = processTypeId;
  }
  public String getProductCode() {
    return productCode;
  }
  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }
  public String getFolder() {
    return folder;
  }
  public void setFolder(String folder) {
    this.folder = folder;
  }
  public String getPath() {
    return path;
  }
  public void setPath(String path) {
    this.path = path;
  }
}
