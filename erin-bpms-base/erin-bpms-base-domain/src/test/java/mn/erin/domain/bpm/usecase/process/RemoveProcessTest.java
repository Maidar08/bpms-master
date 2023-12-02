package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.document.Document;
import mn.erin.domain.bpm.model.document.DocumentId;
import mn.erin.domain.bpm.model.document.DocumentInfoId;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.dms.model.folder.FolderId;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.domain.dms.repository.FolderRepository;

public class RemoveProcessTest
{
  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;

  private BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private ProcessRequestRepository processRequestRepository;
  private ProcessRepository processRepository;

  private DocumentRepository documentRepository;

  private FolderRepository folderRepository;
  private ProcessRequest processRequest;

  private RemoveProcess useCase;

  private static final String CURRENT_USER = "admin";
  private static final String PERMISSION_STR = "bpms.bpm.DeleteProcess";

  @Before
  public void setUp()
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("1", "1");

    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);

    bpmsRepositoryRegistry = Mockito.mock(BpmsRepositoryRegistry.class);
    processRequestRepository = Mockito.mock(ProcessRequestRepository.class);
    processRepository = Mockito.mock(ProcessRepository.class);

    documentRepository = Mockito.mock(DocumentRepository.class);
    folderRepository = Mockito.mock(FolderRepository.class);

    processRequest = new ProcessRequest(ProcessRequestId.valueOf("123"), ProcessTypeId.valueOf("123"), GroupId.valueOf("123"), "123", LocalDateTime.now(),
        ProcessRequestState.DELETED, parameters);

    useCase = new RemoveProcess(bpmsRepositoryRegistry, authenticationService, authorizationService);

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(CURRENT_USER);
    Mockito.when(authorizationService.hasPermission(CURRENT_USER, PERMISSION_STR)).thenReturn(true);

    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository()).thenReturn(processRequestRepository);
    Mockito.when(bpmsRepositoryRegistry.getDocumentRepository()).thenReturn(documentRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_null()
  {
    new RemoveProcess(bpmsRepositoryRegistry, null, authorizationService);
  }

  @Test(expected = NullPointerException.class)
  public void when_authorization_service_null()
  {
    new RemoveProcess(bpmsRepositoryRegistry, authenticationService, null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Ignore
  @Test
  public void when_request_state_is_not_new_and_started() throws DMSRepositoryException, UseCaseException, BpmRepositoryException
  {
    RemoveProcessInput input = new RemoveProcessInput("", "", BpmModuleConstants.REJECTED);
    RemoveProcessOutput output = useCase.execute(input);

    Assert.assertFalse(output.isRemoved());

    Mockito.verify(documentRepository, Mockito.times(0)).findByProcessInstanceId("123");
    Mockito.verify(folderRepository, Mockito.times(0)).getParentFolderId(FolderId.valueOf("123"));
    Mockito.verify(folderRepository, Mockito.times(0)).delete(FolderId.valueOf("123"));

    Mockito.verify(documentRepository, Mockito.times(0)).delete("123");

    Mockito.verify(processRequestRepository, Mockito.times(0)).updateState("123", ProcessRequestState.DELETED);
  }

  @Test
  public void when_request_state_is_new() throws UseCaseException
  {
    RemoveProcessInput input = new RemoveProcessInput("123", "123", BpmModuleConstants.NEW);

    Mockito.when(processRequestRepository.findById(ProcessRequestId.valueOf("123"))).thenReturn(processRequest);
    RemoveProcessOutput output = useCase.execute(input);

    Assert.assertTrue(output.isRemoved());
  }

  @Test
  public void when_request_state_is_started() throws UseCaseException
  {
    RemoveProcessInput input = new RemoveProcessInput("123", "123", BpmModuleConstants.STARTED);

    Mockito.when(processRequestRepository.findById(ProcessRequestId.valueOf("123"))).thenReturn(processRequest);
    RemoveProcessOutput output = useCase.execute(input);

    Assert.assertTrue(output.isRemoved());
  }

  @Test
  public void when_document_id_empty() throws UseCaseException, DMSRepositoryException
  {
    RemoveProcessInput input = new RemoveProcessInput("123", "123", BpmModuleConstants.STARTED);
    Mockito.when(processRequestRepository.findById(ProcessRequestId.valueOf("123"))).thenReturn(processRequest);

    Mockito.when(documentRepository.findByProcessInstanceId("123")).thenReturn(null);
    useCase.execute(input);
    Mockito.verify(folderRepository, Mockito.times(0)).getParentFolderId(FolderId.valueOf("123"));

    Mockito.when(documentRepository.findByProcessInstanceId("123")).thenReturn(new ArrayList<>());
    useCase.execute(input);
    Mockito.verify(folderRepository, Mockito.times(0)).getParentFolderId(FolderId.valueOf("123"));
  }

  @Test
  public void when_process_instance_id_empty() throws UseCaseException
  {
    RemoveProcessInput input = new RemoveProcessInput("123", "", BpmModuleConstants.STARTED);
    Mockito.when(processRequestRepository.findById(ProcessRequestId.valueOf("123"))).thenReturn(processRequest);

    useCase.execute(input);
    Mockito.verify(processRepository, Mockito.times(0)).deleteProcess(new ProcessInstanceId("123"));
    Mockito.verify(documentRepository, Mockito.times(0)).delete("123");
  }

  @Test
  public void when_process_instance_id__not_empty() throws UseCaseException
  {
    RemoveProcessInput input = new RemoveProcessInput("123", "123", BpmModuleConstants.STARTED);
    Mockito.when(processRequestRepository.findById(ProcessRequestId.valueOf("123"))).thenReturn(processRequest);

    useCase.execute(input);
    Mockito.verify(documentRepository, Mockito.atLeast(1)).delete("123");
  }

  @Test
  public void when_process_request_id_empty() throws UseCaseException
  {
    RemoveProcessInput input = new RemoveProcessInput("", "123", BpmModuleConstants.STARTED);
    Mockito.when(processRequestRepository.findById(ProcessRequestId.valueOf("123"))).thenReturn(processRequest);

    useCase.execute(input);
    Mockito.verify(processRequestRepository, Mockito.times(0)).deleteProcessRequest(new ProcessRequestId("123"));
  }

  @Test
  public void when_process_request_id__not_empty() throws UseCaseException, BpmRepositoryException
  {
    RemoveProcessInput input = new RemoveProcessInput("123", "123", BpmModuleConstants.STARTED);
    Mockito.when(processRequestRepository.findById(ProcessRequestId.valueOf("123"))).thenReturn(processRequest);

    useCase.execute(input);
    Mockito.verify(processRequestRepository, Mockito.atLeast(1)).updateState("123", ProcessRequestState.DELETED);
  }

  @Test
  public void when_parent_folder_id_null() throws DMSRepositoryException, UseCaseException
  {
    RemoveProcessInput input = new RemoveProcessInput("123", "123", BpmModuleConstants.STARTED);
    Collection<Document> documents = new ArrayList<>(Collections.singletonList(
        new Document(DocumentId.valueOf("123"), DocumentInfoId.valueOf("123"), ProcessInstanceId.valueOf("123"), "123",
            "", "", "", "")));

    Mockito.when(processRequestRepository.findById(ProcessRequestId.valueOf("123"))).thenReturn(processRequest);

    Mockito.when(documentRepository.findByProcessInstanceId("123")).thenReturn(documents);
    Mockito.when(folderRepository.getParentFolderId(FolderId.valueOf("123"))).thenReturn(null);

    useCase.execute(input);

    Mockito.verify(folderRepository, Mockito.times(0)).delete(FolderId.valueOf("123"));
  }
}
