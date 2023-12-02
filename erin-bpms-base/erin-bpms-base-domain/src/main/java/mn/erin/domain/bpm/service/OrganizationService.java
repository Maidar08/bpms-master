package mn.erin.domain.bpm.service;

import java.util.Map;

/**
 * Represents organization specific services.
 *
 * @author Zorig
 */
public interface OrganizationService
{
  /**
   * Gets Organization information
   *
   * @param cifNumber CIF number of organization
   * @param branchNumber branch number of user requesting the information
   * @return String organization level
   * @throws BpmServiceException when this service is not reachable or usable.
   */
  Map<String, String> getOrganizationLevel(String cifNumber, String branchNumber)
      throws BpmServiceException;
}
