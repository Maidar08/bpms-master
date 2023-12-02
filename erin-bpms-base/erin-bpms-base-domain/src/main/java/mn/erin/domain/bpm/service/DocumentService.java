/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.service;

import java.util.Map;

import mn.erin.domain.bpm.repository.BpmRepositoryException;

/**
 * @author Tamir
 */
public interface DocumentService
{
  /**
   * @param caseInstanceId   Case instance id.
   * @param documentId       Unique document id.
   * @param mainType         document main type.
   * @param subType          document sub type.
   * @param groupId          group id.
   * @param fileName         file name.
   * @param documentAsBase64 document as base64 value.
   * @return reference of uploaded file.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  String uploadDocument(String caseInstanceId, String documentId, String mainType, String subType, String groupId, String fileName, String documentAsBase64, Map<String, String> parameters)
      throws BpmServiceException;

  /**
   * Gets document reference it can be link or id.
   *
   * @param documentId Unique document id
   * @param userId     user id.
   * @return document reference.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  String getDocumentReference(String documentId, String userId) throws BpmServiceException;

  /**
   * Downloads loan contract as base64 by given parameters.
   *
   * @param accountNumber given unique account number.
   * @param paymentType   loan payment type.
   * @return loan contract as base64.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  String downloadContractAsBase64(String accountNumber, String paymentType, String productId) throws BpmServiceException;
  /**
   * Downloads Online Salary loan contract as base64 by given parameters.
   *
   * @param accountNumber given unique account number.
   * @param requestId     loan request id.
   * @return loan contract as base64.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  String downloadOnlineSalaryContractAsBase64(String accountNumber, String requestId) throws BpmServiceException;

  /**
   * Downloads report using BI Publisher.
   *
   * @param accountNumber Account number of customer
   * @return report in Base64
   * @throws BpmServiceException when this service is not reachable or usable
   */
  String downloadLoanReportAsBase64(String accountNumber) throws BpmServiceException;

  /**
   * Downloads loan payment schedule as base64 by given parameters.
   *
   * @param accountNumber       given unique account number.
   * @param repaymentType       repayment type used to determine what kind of payment schedule will be downloaded.
   * @param paymentScheduleInfo map of info that will be sent in soap request for downloading payment schedule.
   * @return loan payment schedule as base64.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  String downloadPaymentScheduleAsBase64(String accountNumber, String repaymentType, Map<String, String> paymentScheduleInfo, String processType) throws BpmServiceException;

  /**
   * Downloads loan contract documents as base64 by type.
   *
   * @param documentParam parameters required in order to call publisher service
   * @param documentType type is required to acquire correct path
   * @param instanceId instance id
   * @return string base64
   * @throws BpmServiceException when this service is not reachable or usable
   */
  String downloadDocumentByType(Map<String, String> documentParam, String documentType, String instanceId) throws BpmServiceException;
  /**
   * Downloads purchase trade contract documents as base64 by type.
   * @param documentParameter parameters required in order to call publisher service
   * @param documentType type is required to acquire correct path
   * @param instanceId instance id
   * @return string base64
   * @throws BpmServiceException when this service is not reachable or usable
   */
  String downloadPurchaseSaleContractAsBase64(Map<String, String> documentParameter, String documentType, String instanceId) throws BpmServiceException;
}
