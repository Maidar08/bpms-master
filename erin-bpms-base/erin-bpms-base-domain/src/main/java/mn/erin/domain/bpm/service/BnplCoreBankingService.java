package mn.erin.domain.bpm.service;

import java.util.Map;

/**
 * @author Bilguunbor
 */

public interface BnplCoreBankingService
{
  /**
   *  Set BNPL state
   *
   * @param invoiceNumber Invoice number
   * @return null since method or service failure will throw an exception.
   * @throws BpmServiceException when this service is not reachable or usable.
   */
  Void setBnplInvoiceState(Map<String, String> invoiceNumber) throws BpmServiceException;

  /**
   * Get BNPL INVOICE INFO
   * @param invoiceNo invoiceNo
   * @return returns Bnpl Invoice Info in a map
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, Object> getBnplInvoiceInfo(String invoiceNo) throws BpmServiceException;
}
