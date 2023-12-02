package mn.erin.bpm.base.service.memory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import mn.erin.domain.bpm.model.loan.BorrowerId;
import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.model.loan.LoanEnquire;
import mn.erin.domain.bpm.model.loan.LoanEnquireId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.LoanService;

@Service
public class InMemoryLoanService implements LoanService
{
  @Override
  public String getCustomerCID(String searchValueType, String searchValue, String searchType, boolean isSearchByCoBorrower, String branchNumber, String userId,
    String userName) throws BpmServiceException
  {
    return null;
  }

  @Override
  public LoanEnquire getLoanEnquire(String customerCID) throws BpmServiceException
  {
    return null;
  }

  @Override
  public boolean confirmLoanEnquire(LoanEnquireId loanEnquireId, BorrowerId borrowerId, String customerCID) throws BpmServiceException
  {
    return false;
  }

  @Override
  public List<Loan> getLoanList(String customerCID, BorrowerId borrowerId) throws BpmServiceException
  {
    return Collections.emptyList();
  }

  @Override
  public LoanEnquire getLoanEnquireWithFile(LoanEnquireId loanEnquireId, BorrowerId borrowerId, String customerCID) throws BpmServiceException
  {
    return null;
  }

  @Override
  public LoanEnquire getLoanEnquireByCoBorrower(String customerCID) throws BpmServiceException
  {
    return null;
  }

  @Override
  public Map<String, String> getCustomerRelatedInfo(String customerCID, String relation) throws BpmServiceException
  {
    return null;
  }

  @Override
  public Map<String, String> getCustomerDetail(String customerCID) throws BpmServiceException
  {
    return null;
  }

  @Override
  public Map<String, Object> getCIBInfo(Map<String, String> requestParameters) throws BpmServiceException
  {
    return Collections.emptyMap();
  }
}
