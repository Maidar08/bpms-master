package mn.erin.domain.bpm.usecase.document;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Bilguunbor
 */
public class UploadFileTest
{
  private static final String NAME = "name";
  private static final String CONTENT_AS_BASE64 = "base64";

  private UploadFile execute;

  @Before
  public void setUp()
  {
    execute = new UploadFile(NAME, CONTENT_AS_BASE64);
  }

  @Test(expected = NullPointerException.class)
  public void when_name_is_null()
  {
    new UploadFile(null, CONTENT_AS_BASE64);
  }

  @Test(expected = NullPointerException.class)
  public void when_content_as_base64_is_null()
  {
    new UploadFile(NAME, null);
  }

  @Test
  public void when_set_name_is_correct()
  {
    execute.setName("eman");
    Assert.assertEquals("eman", execute.getName());
  }

  @Test
  public void when_set_content_as_base64_is_correct()
  {
    execute.setContentAsBase64("contentAsBase64");
    Assert.assertEquals("contentAsBase64", execute.getContentAsBase64());
  }
}
