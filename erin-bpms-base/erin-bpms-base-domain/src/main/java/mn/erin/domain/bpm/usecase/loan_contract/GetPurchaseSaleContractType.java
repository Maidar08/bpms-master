package mn.erin.domain.bpm.usecase.loan_contract;

import java.util.List;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.model.document.Document;
import mn.erin.domain.bpm.model.document.DocumentId;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.DocumentService;
import static mn.erin.domain.bpm.BpmDocumentConstant.GENERAL_TRADE_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.REPURCHASE_TRADE_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_ACC;

public class GetPurchaseSaleContractType extends AbstractUseCase<GetPurchaseSaleContractTypeInput, List<Document>>
{
  private static final Logger LOG = LoggerFactory.getLogger(GetPurchaseSaleContractType.class);
  private final DocumentService documentService;
  private final CaseService caseService;
  private final AuthenticationService authenticationService;

  public GetPurchaseSaleContractType(DocumentService documentService, CaseService caseService,
      AuthenticationService authenticationService)
  {
    this.documentService = Objects.requireNonNull(documentService, "Document service is required!");
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
  }

  @Override
  public List<Document> execute(GetPurchaseSaleContractTypeInput input) throws UseCaseException
  {
    try
    {
      String instanceId = input.getInstanceId();
      String documentType = input.getDocumentType();
      String accountNumber = input.getAccountNumber();
      DocumentId documentId = getDocumentIdByType(documentType, accountNumber);
      String productCode;
      if(documentType.equals(GENERAL_TRADE_CONTRACT))
        productCode = "EW10";
      else 
        productCode = "EW20";
     
      Map<String, String> documentParameter =  new HashMap<>();
      documentParameter.put(P_ACC, accountNumber);
      documentParameter.put(PRODUCT_CODE, productCode);

      String requestId = documentParameter.get(P_ACC);
      LOG.info("PURCHASE SALE CONTRACT BIP document parameter= [{}] with request id = {}", documentParameter, requestId);
      String base64File = documentService.downloadPurchaseSaleContractAsBase64(documentParameter, documentType,instanceId);
      LOG.info("#### DOWNLOADED BASE64 CONTRACT DOCUMENT");
      List<Document> documents = new ArrayList<>();
      
      documents.add(new Document(documentId, null, null, getDocumentNameByType(documentType, documentId), null, null, null, base64File));

      return documents;
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }

  private DocumentId getDocumentIdByType(String documentType, String accountNumber) throws BpmServiceException
  {
    switch (documentType)
    {
    case GENERAL_TRADE_CONTRACT:
      return new DocumentId(accountNumber);
    case REPURCHASE_TRADE_CONTRACT:
      return new DocumentId(accountNumber);
    default:
      return null;
    }
  }

  private String getDocumentNameByType(String documentType, DocumentId documentId)
  {
    switch (documentType)
    {
      case GENERAL_TRADE_CONTRACT: 
        return GENERAL_TRADE_CONTRACT +"_"+documentId.getId();
      case REPURCHASE_TRADE_CONTRACT:
        return REPURCHASE_TRADE_CONTRACT +"_"+ documentId.getId();
      default:
        return EMPTY_VALUE;
    }
  }
}
