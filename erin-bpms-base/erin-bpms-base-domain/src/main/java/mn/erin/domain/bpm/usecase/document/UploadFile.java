package mn.erin.domain.bpm.usecase.document;

import java.util.Objects;

/**
 * @author Tamir
 */
public class UploadFile
{
  private String name;
  private String contentAsBase64;

  public UploadFile(String name, String contentAsBase64)
  {
    this.name = Objects.requireNonNull(name, "File name is required!");
    this.contentAsBase64 = Objects.requireNonNull(contentAsBase64, "File content is required!");
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getContentAsBase64()
  {
    return contentAsBase64;
  }

  public void setContentAsBase64(String contentAsBase64)
  {
    this.contentAsBase64 = contentAsBase64;
  }
}
