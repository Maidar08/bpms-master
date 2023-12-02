package mn.erin.bpm.rest.model;

public class RestDocument
{
  private String id;
  private String name;

  private String type;
  private String subType;

  private String source;
  private String reference;

  private boolean isDownloadable;

  public RestDocument()
  {

  }

  public RestDocument(String id, String name, String type, String subType, String source, String reference, boolean isDownloadable)
  {
    this.id = id;
    this.name = name;
    this.type = type;
    this.subType = subType;
    this.source = source;
    this.reference = reference;
    this.isDownloadable = isDownloadable;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public String getSubType()
  {
    return subType;
  }

  public void setSubType(String subType)
  {
    this.subType = subType;
  }

  public String getSource()
  {
    return source;
  }

  public void setSource(String source)
  {
    this.source = source;
  }

  public String getReference()
  {
    return reference;
  }

  public void setReference(String reference)
  {
    this.reference = reference;
  }

  public boolean getIsDownloadable()
  {
    return this.isDownloadable;
  }

  public void setIsDownloadable(boolean isDownloadable)
  {
    this.isDownloadable = isDownloadable;
  }
}
