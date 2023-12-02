/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.service;

import java.util.List;
import java.util.Map;

import mn.erin.domain.bpm.model.loan.BorrowerId;
import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.model.loan.LoanEnquire;
import mn.erin.domain.bpm.model.loan.LoanEnquireId;

/**
 * @author Tamir
 */
public interface LoanService
{
  /**
   * @param searchValueType      Should be customer register number, passport number or customer name.
   * @param searchValue          if search value type is register number, it will be "regNumValue".
   * @param searchType           Should be citizen or organization etc...
   * @param isSearchByCoBorrower customer is co-borrower true otherwise false.
   * @param branchNumber         branch number.
   * @param userId               current user id.
   * @param userName             borrower name.
   * @return CID number.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  String getCustomerCID(String searchValueType, String searchValue, String searchType, boolean isSearchByCoBorrower, String branchNumber, String userId,
      String userName) throws BpmServiceException;

  /**
   * Gets loan enquire info by customer CID.
   *
   * @param customerCID Unique customer CID.
   * @return found {@link LoanEnquire}.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  LoanEnquire getLoanEnquire(String customerCID) throws BpmServiceException;

  /**
   * Confirms loan enquire info is valid.
   *
   * @param loanEnquireId Unique id of loan enquire.
   * @param borrowerId    Unique borrower id.
   * @param customerCID   Customer CID.
   * @return true if loan enquire date is valid otherwise return false.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  boolean confirmLoanEnquire(LoanEnquireId loanEnquireId, BorrowerId borrowerId, String customerCID) throws BpmServiceException;

  /**
   * Gets loan list by customer CID.
   *
   * @param customerCID Unique customer CID.
   * @param borrowerId  Unique borrower ID.
   * @return loan list with related customer CID.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  List<Loan> getLoanList(String customerCID, BorrowerId borrowerId) throws BpmServiceException;

  /**
   * Gets loan enquire with PDF file.
   *
   * @param loanEnquireId Unique loan enquire ID.
   * @param customerCID   Unique customer CID.
   * @param borrowerId    Unique borrower ID.
   * @return found {@link LoanEnquire}.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  LoanEnquire getLoanEnquireWithFile(LoanEnquireId loanEnquireId, BorrowerId borrowerId, String customerCID) throws BpmServiceException;

  /**
   * Gets loan enquire by co-borrower customer CID.
   *
   * @param customerCID Unique customer CID.
   * @return found {@link LoanEnquire}.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  LoanEnquire getLoanEnquireByCoBorrower(String customerCID) throws BpmServiceException;

  /**
   * Gets customer related info
   *
   * @param customerCID Unique customer CID.
   * @param relation customer relation type
   * @return
   * @throws BpmServiceException
   */
  Map<String, String> getCustomerRelatedInfo(String customerCID, String relation) throws BpmServiceException;

  /**
   * Gets customer detailed info
   *
   * @param customerCID Unique customer CID.
   * @return
   * @throws BpmServiceException
   */
  Map<String, String> getCustomerDetail(String customerCID) throws BpmServiceException;

  /** Gets Mongol Bank Info
   *
   * @param body  customer info
   * @return customer and customer loan info from Mongol bank
   * @throws BpmServiceException
   */
  Map<String, Object> getCIBInfo(Map<String, String> requestParameters) throws  BpmServiceException;
}
