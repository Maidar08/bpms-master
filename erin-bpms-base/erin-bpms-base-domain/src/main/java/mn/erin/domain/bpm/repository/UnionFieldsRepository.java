package mn.erin.domain.bpm.repository;

import java.time.LocalDateTime;

/**
 * @author Sukhbaatar
 */
public interface UnionFieldsRepository
{
  /**
   * Creates Union fields table
   *
   * @param customerNumber   customerNumber
   * @param registerId       registerId
   * @param keyField         keyField
   * @param trackNumber      trackNumber
   * @param productCategory  productCategory
   * @param processRequestId processRequestId
   * @param processTypeId    processTypeId
   * @param requestType      requestType
   * @param dateTime         system dateTime
   * @throws BpmRepositoryException Repository Exception
   */
  void create(String customerNumber, String registerId, String keyField, String trackNumber, String productCategory, String processRequestId, String processTypeId, String requestType, LocalDateTime dateTime)
      throws BpmRepositoryException;
}
