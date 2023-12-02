package mn.erin.domain.bpm.service;

import java.util.Map;

/**
 * @author Lkhagvadorj.A
 **/

public interface BranchBankingDocumentService
{
  /**
   * Download transaction document
   * @param documentParam - document data
   * @return tax transaction document as base 64
   */
  String downloadDocumentByType(Map<String, String> documentParam, String documentType, String instanceId) throws BpmServiceException;
}
