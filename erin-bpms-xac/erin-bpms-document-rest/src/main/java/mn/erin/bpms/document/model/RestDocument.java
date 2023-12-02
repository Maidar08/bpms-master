package mn.erin.bpms.document.model;

/**
 * @author Bilguunbor
 */

public class RestDocument
{
  private String documentId;
  private String documentInfoId;
  private String processInstanceId;

  private String name;
  private String category;
  private String subCategory;
  private String reference;
  private String source;

  public RestDocument(String documentId, String documentInfoId, String processInstanceId, String name, String category, String subCategory,
      String reference, String source)
  {
    this.documentId = documentId;
    this.documentInfoId = documentInfoId;
    this.processInstanceId = processInstanceId;
    this.name = name;
    this.category = category;
    this.subCategory = subCategory;
    this.reference = reference;
    this.source = source;
  }

  public String getDocumentId()
  {
    return documentId;
  }

  public void setDocumentId(String documentId)
  {
    this.documentId = documentId;
  }

  public String getDocumentInfoId()
  {
    return documentInfoId;
  }

  public void setDocumentInfoId(String documentInfoId)
  {
    this.documentInfoId = documentInfoId;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getCategory()
  {
    return category;
  }

  public void setCategory(String category)
  {
    this.category = category;
  }

  public String getSubCategory()
  {
    return subCategory;
  }

  public void setSubCategory(String subCategory)
  {
    this.subCategory = subCategory;
  }

  public String getReference()
  {
    return reference;
  }

  public void setReference(String reference)
  {
    this.reference = reference;
  }

  public String getSource()
  {
    return source;
  }

  public void setSource(String source)
  {
    this.source = source;
  }
}
