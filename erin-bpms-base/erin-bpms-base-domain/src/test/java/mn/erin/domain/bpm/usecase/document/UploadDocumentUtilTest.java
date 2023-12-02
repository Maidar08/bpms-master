package mn.erin.domain.bpm.usecase.document;

import org.junit.Test;

import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Bilguunbor
 */
public class UploadDocumentUtilTest
{
  @Test(expected = UseCaseException.class)
  public void when_file_name_is_null() throws UseCaseException
  {
    UploadDocumentUtil.validateFileExtension(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_file_name_does_not_have_supported_extension() throws UseCaseException
  {
    UploadDocumentUtil.validateFileExtension("file.extension");
  }
}
