package mn.erin.bpm.domain.ohs.xac.branch.banking.util;

import java.util.Map;


/**
 * @author Lkhagvadorj.A
 **/

public class DocumentServiceUtil
{
  private DocumentServiceUtil()
  {

  }

  public static String getDocumentRequestBody(Map<String, String> documentParam, String reportAbsolutePathDocType, String reportAbsolutePath, String userId, String password)
  {
    return "<soapenv:Envelope\n"
        + "xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
        + "xmlns:v2=\"http://xmlns.oracle.com/oxp/service/v2\">\n"
        + "<soapenv:Header/>\n"
        + "<soapenv:Body>\n"
        + "\n"
        + getSoapBody(documentParam, reportAbsolutePathDocType, reportAbsolutePath, userId, password) + "\n"
        + "</soapenv:Body>\n"
        + "</soapenv:Envelope>";
  }

  private static String getSoapBody(Map<String, String> documentParam, String reportAbsolutePathDocType, String reportAbsolutePath, String userId, String password)
  {
    return "<v2:runReport>\n"
        + "<v2:reportRequest>\n"
        + getAttributeFormat() + "\n"
        + "<v2:parameterNameValues>\n"
        + "<v2:listOfParamNameValues>\n"
        + getParamItems(documentParam) + "\n"
        + "</v2:listOfParamNameValues>\n"
        + "</v2:parameterNameValues>\n"
        + "<v2:reportAbsolutePath>/"+ reportAbsolutePathDocType +"/" + reportAbsolutePath + ".xdo</v2:reportAbsolutePath>\n"
        + "<v2:sizeOfDataChunkDownload>-1</v2:sizeOfDataChunkDownload>"
        + "</v2:reportRequest>\n"
        + "<v2:userID>" + userId + "</v2:userID>\n"
        + "<v2:password>" + password + "</v2:password>\n"
        + "</v2:runReport>";
  }
  private static String getAttributeFormat(){
    return "<v2:attributeFormat>pdf</v2:attributeFormat>\n"
        + "<v2:attributeLocale>English</v2:attributeLocale>\n"
        + "<v2:attributeTemplate>English</v2:attributeTemplate>\n"
        + "<v2:attributeTemplate>2</v2:attributeTemplate>";
  }
  private static String getParamItems(Map<String, String> documentParam)
  {
    StringBuilder paramItem = new StringBuilder();

    for (Map.Entry<String, String> entry : documentParam.entrySet())
    {
      paramItem.append("<v2:item>\n")
          .append("<v2:name>")
          .append(entry.getKey())
          .append("</v2:name>\n")
          .append("<v2:values>\n")
          .append("<v2:item>")
          .append(entry.getValue())
          .append("</v2:item>")
          .append("</v2:values>")
          .append("</v2:item>");
    }

    return paramItem.toString();
  }
}
