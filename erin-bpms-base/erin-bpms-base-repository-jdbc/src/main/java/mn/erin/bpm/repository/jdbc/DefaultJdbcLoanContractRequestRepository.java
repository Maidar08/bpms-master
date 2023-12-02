package mn.erin.bpm.repository.jdbc;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.springframework.stereotype.Repository;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcLoanContractRequestRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcLoanContractRequest;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.contract.LoanContractRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;

/**
 * @author Temuulen Naranbold
 */
@Repository
public class DefaultJdbcLoanContractRequestRepository implements LoanContractRequestRepository
{
  private final JdbcLoanContractRequestRepository jdbcLoanContractRequestRepository;

  @Inject
  public DefaultJdbcLoanContractRequestRepository(JdbcLoanContractRequestRepository jdbcLoanContractRequestRepository)
  {
    this.jdbcLoanContractRequestRepository = Objects.requireNonNull(jdbcLoanContractRequestRepository, "Jdbc loan contract request repository is required!");
  }

  @Override
  public void create(ProcessRequestId id, String processInstanceId, String type, String account,
      BigDecimal amount, Date createdDate, String userId, String groupId, String tenantId, String state, String cif, String product)
  {
    jdbcLoanContractRequestRepository.insert(processInstanceId, id.getId(), type, account, amount, createdDate, userId, groupId, tenantId, state, cif, product);
  }

  @Override
  public LoanContractRequest findById(EntityId entityId)
  {
    return jdbcLoanContractRequestRepository.findById(entityId.getId()).map(this::mapToLoanContractRequest).orElse(null);
  }

  @Override
  public Collection<LoanContractRequest> findAll()
  {
    return Collections.emptyList();
  }

  @Override
  public List<LoanContractRequest> findByAccountId(String loanAccount)
  {
    List<JdbcLoanContractRequest> loanContractRequestList = jdbcLoanContractRequestRepository.findByAccountId(loanAccount + "%");
    return loanContractRequestList.stream().map(this::mapToLoanContractRequest).collect(Collectors.toList());

  }

  @Override
  public List<LoanContractRequest> findAllByGivenDate(Date startDate, Date endDate)
  {
    List<JdbcLoanContractRequest> loanContractRequestList = jdbcLoanContractRequestRepository.findAllByGivenDate(startDate, endDate);
    return loanContractRequestList.stream().map(this::mapToLoanContractRequest).collect(Collectors.toList());
  }

  @Override
  public LoanContractRequest findByInstanceId(String instanceId)
  {
    return jdbcLoanContractRequestRepository.findByProcessInstanceId(instanceId).map(this::mapToLoanContractRequest).orElse(null);
  }

  @Override
  public List<LoanContractRequest> findByGroupId(String groupId, Date startDate, Date endDate)
  {
    List<JdbcLoanContractRequest> loanContractRequestList = jdbcLoanContractRequestRepository.findByGroupId(groupId, startDate, endDate);
    return loanContractRequestList.stream().map(this::mapToLoanContractRequest).collect(Collectors.toList());
  }

  @Override
  public boolean update(String processInstanceId, String processRequestId, String modifiedUser)
  {
    return 1 == jdbcLoanContractRequestRepository.update(processInstanceId, processRequestId, modifiedUser);
  }

  @Override
  public boolean update(String processInstanceId, String modifiedUser)
  {
    return 1 == jdbcLoanContractRequestRepository.update(processInstanceId, modifiedUser);
  }

  private LoanContractRequest mapToLoanContractRequest(JdbcLoanContractRequest jdbcLoanContractRequest)
  {
    return new LoanContractRequest(
        ProcessRequestId.valueOf(jdbcLoanContractRequest.getLoanContractId()),
        jdbcLoanContractRequest.getProcessInstanceId(),
        jdbcLoanContractRequest.getLoanContractType(),
        jdbcLoanContractRequest.getLoanAccount(),
        jdbcLoanContractRequest.getLoanAmount(),
        jdbcLoanContractRequest.getCreatedDate(),
        jdbcLoanContractRequest.getAssignedUserId(),
        jdbcLoanContractRequest.getGroupId(),
        TenantId.valueOf(jdbcLoanContractRequest.getTenantId()),
        jdbcLoanContractRequest.getState(),
        jdbcLoanContractRequest.getCifNumber(),
        jdbcLoanContractRequest.getProductDescription()
    );
  }
}
