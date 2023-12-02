package mn.erin.bpm.base.service.memory;

import java.util.Map;

import org.springframework.stereotype.Service;

import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DocumentService;

@Service
public class InMemoryDocumentService implements DocumentService
{
  @Override
  public String uploadDocument(String caseInstanceId,String documentId, String mainType, String subType, String groupId, String fileName,
      String documentAsBase64, Map<String, String> parameters) throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getDocumentReference(String documentId, String userId) throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String downloadContractAsBase64(String accountNumber, String paymentType, String productId) throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String downloadOnlineSalaryContractAsBase64(String accountNumber, String requestId) throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String downloadLoanReportAsBase64(String accountNumber) throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String downloadPaymentScheduleAsBase64(String accountNumber, String repaymentType, Map<String, String> paymentScheduleInfo, String type) throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String downloadDocumentByType(Map<String, String> documentParam, String documentType, String instanceId) throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String downloadPurchaseSaleContractAsBase64(Map<String, String> documentParam, String documentType, String instanceId)
      throws BpmServiceException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method ''downloadPurchaseSaleContractAsBase64'");
  }
}
