package mn.erin.domain.bpm.service;

import org.json.JSONArray;

/**
 * @author Bilguunbor
 **/

public interface ExcelService
{
  /**
   * Reads table content from given excel file.
   *
   * @param base64       excel file encoded into base64 string
   * @param headerValues json object consisting of set value and excel file header value.
   * @return returns JSONArray response
   * @throws BpmServiceException when service has a trouble reading excel file.
   */

  JSONArray readFromExcel(String base64, JSONArray headerValues) throws BpmServiceException;
}

