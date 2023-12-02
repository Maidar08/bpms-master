package mn.erin.bpms.document.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.bpms.document.model.RestDocument;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.model.document.Document;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.DocumentService;
import mn.erin.domain.bpm.usecase.loan.GetLoanContractsByType;
import mn.erin.domain.bpm.usecase.loan.GetLoanContractsByTypeInput;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.domain.bpm.BpmMessagesConstants.BB_DOCUMENT_TYPE_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_DOCUMENT_TYPE_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_NULL_MESSAGE;

/**
 * @author Bilguunbor
 **/

@RestController
@RequestMapping(value = "/document", name = "Provides branch banking document request API.")
public class XacDocumentRestApi
{
  private final DocumentService documentService;
  private final CaseService caseService;
  private final AuthenticationService authenticationService;

  public XacDocumentRestApi(DocumentService documentService, CaseService caseService, AuthenticationService authenticationService)
  {
    this.documentService = documentService;
    this.caseService = caseService;
    this.authenticationService = authenticationService;
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

    GetLoanContractsByTypeInput input = new GetLoanContractsByTypeInput(instanceId, documentType, parameter);
    GetLoanContractsByType useCase = new GetLoanContractsByType(documentService, caseService, authenticationService);
    List<Document> documents = useCase.execute(input);

    if (documents.size() > 1)
    {
      List<RestDocument> restDocuments = new ArrayList<>();

      for (Document document : documents)
      {
        restDocuments.add(toRestDocument(document));
      }

      return RestResponse.success(restDocuments);
    }

    return RestResponse.success(toRestDocument(documents.get(0)));
  }

  private RestDocument toRestDocument(Document document)
  {
    return new RestDocument(null, null, null, document.getName(),
        document.getCategory(), document.getSubCategory(), document.getReference(), document.getSource());
  }
}
