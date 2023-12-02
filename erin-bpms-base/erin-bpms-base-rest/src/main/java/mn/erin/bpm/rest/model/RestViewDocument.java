package mn.erin.bpm.rest.model;

/**
 * @author Tamir
 */
public class RestViewDocument
{
  private String documentAsBase64;

  public RestViewDocument(String documentAsBase64)
  {
    this.documentAsBase64 = documentAsBase64;
  }

  public String getDocumentAsBase64()
  {
    return documentAsBase64;
  }

  public void setDocumentAsBase64(String documentAsBase64)
  {
    this.documentAsBase64 = documentAsBase64;
  }
}
