package mn.erin.domain.bpm.usecase.document;

import java.util.ArrayList;
import java.util.List;

import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Tamir
 */
public final class UploadDocumentUtil
{
  public static final String UNSUPPORTED_EXTENSIONS = "File extension is unsupported!, supported extensions :";

  private static final String DOC = "doc";
  private static final String DOCX = "docx";

  private static final String XLS = "xls";
  private static final String XLSX = "xlsx";

  private static final String PPT = "ppt";
  private static final String PPTX = "pptx";
  private static final String PDF = "pdf";

  private static final String JPG = "jpg";
  private static final String JPEG = "jpeg";

  private static final String MSG = "msg";
  private static final String MP_3 = "mp3";

  private static final String HTML = "html";
  private static final String HTM = "htm";
  public static final String DOT = ".";

  private UploadDocumentUtil()
  {

  }

  public static void validateFileExtension(String fileName) throws UseCaseException
  {
    if (null == fileName)
    {
      String errorCode = "DMS027";
      throw new UseCaseException(errorCode, "File name cannot be null!");
    }

    if (fileName.contains(DOT))
    {
      String extension = fileName.substring(fileName.lastIndexOf(DOT) + 1);
      List<String> fileExtensions = getSupportedExtensions();

      if (!fileExtensions.contains(extension))
      {
        throw new UseCaseException(UNSUPPORTED_EXTENSIONS + fileExtensions.toString());
      }
    }
  }

  public static List<String> getSupportedExtensions()
  {
    List<String> fileExtensions = new ArrayList<>();

    fileExtensions.add(DOC);
    fileExtensions.add(DOCX);

    fileExtensions.add(XLS);
    fileExtensions.add(XLSX);

    fileExtensions.add(PPT);
    fileExtensions.add(PPTX);
    fileExtensions.add(PDF);

    fileExtensions.add(JPG);
    fileExtensions.add(JPEG);

    fileExtensions.add(MSG);
    fileExtensions.add(MP_3);

    fileExtensions.add(HTML);
    fileExtensions.add(HTM);

    return fileExtensions;
  }
}
