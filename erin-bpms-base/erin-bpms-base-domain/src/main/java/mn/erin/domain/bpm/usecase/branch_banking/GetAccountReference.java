package mn.erin.domain.bpm.usecase.branch_banking;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ACCOUNT_NUMBER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ACCOUNT_NUMBER_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_BRANCH_ID_IS_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_BRANCH_ID_IS_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

/**
 * @author Odgavaa
 **/

public class GetAccountReference extends AbstractUseCase<GetAccountInfoInput, Map<String, Object>>

{
  private final BranchBankingService branchBankingService;
  private final AuthenticationService authenticationService;
  private final MembershipRepository membershipRepository;

  public GetAccountReference(BranchBankingService branchBankingService, AuthenticationService authenticationService, MembershipRepository membershipRepository)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService, "BranchBankingService cannot be null!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "AuthenticationService cannot be null!");
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "MembershipRepository cannot be null!");
  }

  @Override
  public Map<String, Object> execute(GetAccountInfoInput input) throws UseCaseException
  {
    validateInput(input);

    try
    {
      String userBranchNumber = getCurrentUserBranch(authenticationService, membershipRepository);

      if (null == userBranchNumber || StringUtils.isBlank(userBranchNumber))
      {
        throw new UseCaseException(BB_BRANCH_ID_IS_NULL_CODE, BB_BRANCH_ID_IS_NULL_MESSAGE);
      }
      return branchBankingService.getAccountReference(input.getAccountId(), userBranchNumber, input.getInstanceId());
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException("Error occurs to get current user branch id");
    }
  }

  private void validateInput(GetAccountInfoInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(input.getInstanceId()))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }
    if (StringUtils.isBlank(input.getAccountId()))
    {
      throw new UseCaseException(BB_ACCOUNT_NUMBER_NULL_CODE, BB_ACCOUNT_NUMBER_NULL_MESSAGE);
    }
  }

  private String getCurrentUserBranch(AuthenticationService authenticationService, MembershipRepository membershipRepository) throws AimRepositoryException
  {
    UserId userId = new UserId(authenticationService.getCurrentUserId());
    return membershipRepository.findByUserId(userId).getGroupId().getId();
  }
}
