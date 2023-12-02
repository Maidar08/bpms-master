package mn.erin.bpms.branch.banking.webapp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.bpms.branch.banking.webapp.model.RestDocument;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.model.document.Document;
import mn.erin.domain.bpm.service.BranchBankingDocumentService;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.branch_banking.billing.DownloadPublisherDocumentsByType;
import mn.erin.domain.bpm.usecase.branch_banking.billing.DownloadPublisherDocumentsByTypeInput;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.domain.bpm.BpmMessagesConstants.BB_DOCUMENT_TYPE_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_DOCUMENT_TYPE_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_NULL_MESSAGE;

/**
 * @author Lkhagvadorj.A
 **/

@RestController
@RequestMapping(value = "/document", name = "Provides branch banking document request API.")
public class BranchBankingDocumentRestApi
{
  private final BranchBankingDocumentService branchBankingDocumentService;
  private final CaseService caseService;
  private final AuthenticationService authenticationService;

  public BranchBankingDocumentRestApi(BranchBankingDocumentService branchBankingDocumentService, CaseService caseService,
      AuthenticationService authenticationService)
  {
    this.branchBankingDocumentService = Objects.requireNonNull(branchBankingDocumentService, "Branch banking document service required!");
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
  }

  @PostMapping("/{instanceId}/{documentType}")
  public ResponseEntity<RestResult> getDocumentByType(@PathVariable String instanceId, @PathVariable String documentType,
      @RequestBody Map<String, Object> parameter) throws BpmInvalidArgumentException, UseCaseException
  {
    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }
    if (StringUtils.isBlank(documentType))
    {
      throw new BpmInvalidArgumentException(BB_DOCUMENT_TYPE_NULL_CODE, BB_DOCUMENT_TYPE_NULL_MESSAGE);
    }
    if (null == parameter)
    {
      throw new BpmInvalidArgumentException(PARAMETER_NULL_CODE, PARAMETER_NULL_MESSAGE);
    }

    DownloadPublisherDocumentsByTypeInput input = new DownloadPublisherDocumentsByTypeInput(instanceId, documentType, parameter);
    DownloadPublisherDocumentsByType downloadPublisherDocumentsByType = new DownloadPublisherDocumentsByType(caseService, branchBankingDocumentService,
        authenticationService);
    List<Document> documents = downloadPublisherDocumentsByType.execute(input);
    List<RestDocument> restDocuments = new ArrayList<>();

    if (documents.size() > 1)
    {
      for (Document document : documents)
      {
        restDocuments.add(new RestDocument(null, null, null, document.getName(),
            document.getCategory(), document.getSubCategory(), document.getReference(), document.getSource()));
      }
      return RestResponse.success(restDocuments);
    }
    Document document = documents.get(0);
    return RestResponse.success(new RestDocument(null, null, null, document.getName(),
        document.getCategory(), document.getSubCategory(), document.getReference(), document.getSource()));
  }
}
