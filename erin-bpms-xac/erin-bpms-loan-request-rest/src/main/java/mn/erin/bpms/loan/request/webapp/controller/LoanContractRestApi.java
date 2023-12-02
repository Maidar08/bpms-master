package mn.erin.bpms.loan.request.webapp.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.bpms.loan.request.webapp.model.RestLoanContractRequest;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.contract.LoanContractRequest;
import mn.erin.domain.bpm.repository.LoanContractParameterRepository;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.contract.GetAllLoanContractRequests;
import mn.erin.domain.bpm.usecase.contract.GetAllLoanContractRequestsInput;
import mn.erin.domain.bpm.usecase.contract.GetLoanContractProcessRequest;
import mn.erin.domain.bpm.usecase.contract.GetLoanContractProcessRequestInput;
import mn.erin.domain.bpm.usecase.contract.GetSubGroupLoanContractRequest;
import mn.erin.domain.bpm.usecase.contract.UpdateLoanContractRequest;
import mn.erin.domain.bpm.usecase.contract.UpdateLoanContractRequestInput;
import mn.erin.domain.bpm.usecase.loan_contract.GetInquireCollateralDetails;
import mn.erin.domain.bpm.usecase.loan_contract.GetInquireCollateralDetailsInput;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

/**
 * @author Sukhbat
 */

@RestController
@RequestMapping(value = "/loan-contract", name = "Provides BPMS loan contract API.")
public class LoanContractRestApi
{
  private final LoanContractRequestRepository loanContractRequestRepository;
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final LoanContractParameterRepository loanContractParameterRepository;
  private final NewCoreBankingService newCoreBankingService;
  private final GroupRepository groupRepository;

  public LoanContractRestApi(LoanContractRequestRepository loanContractRequestRepository, AuthenticationService authenticationService,
      AuthorizationService authorizationService, LoanContractParameterRepository loanContractParameterRepository,
      NewCoreBankingService newCoreBankingService, GroupRepository groupRepository)
  {
    this.loanContractRequestRepository = loanContractRequestRepository;
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.loanContractParameterRepository = loanContractParameterRepository;
    this.newCoreBankingService = newCoreBankingService;
    this.groupRepository = groupRepository;
  }

  @ApiOperation("Gets the loan contract Request by GroupId")
  @GetMapping()
  public ResponseEntity<RestResult> getLoanContractByGroupId(@RequestParam(required = false) String type, @RequestParam(required = false) String id,
      @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
      throws UseCaseException, ParseException
  {
    /* Convert to date from date string. */
    Date startValidDate = convertDateString(startDate);
    Date endValidDate = convertDateString(endDate);

    switch (type)
    {
    case "group-request":
      GetLoanContractProcessRequestInput input = new GetLoanContractProcessRequestInput(id, startValidDate, endValidDate);
      GetLoanContractProcessRequest getLoanContractRequestByGroupId = new GetLoanContractProcessRequest(authenticationService, authorizationService,
          loanContractRequestRepository);
      return RestResponse.success(toRestLoanContractRequest(getLoanContractRequestByGroupId.execute(input)));
    case "sub-group-request":
      GetLoanContractProcessRequestInput requestInput = new GetLoanContractProcessRequestInput(id, startValidDate, endValidDate);
      GetSubGroupLoanContractRequest getSubGroupLoanContractRequest = new GetSubGroupLoanContractRequest(groupRepository, authenticationService,
          authorizationService, loanContractRequestRepository);
      return RestResponse.success(toRestLoanContractRequest(getSubGroupLoanContractRequest.execute(requestInput)));
    default:
      GetAllLoanContractRequestsInput allRequestInput = new GetAllLoanContractRequestsInput(startValidDate, endValidDate);
      GetAllLoanContractRequests getAllLoanContractRequests = new GetAllLoanContractRequests(loanContractRequestRepository, authenticationService,
          authorizationService);
      return RestResponse.success(toRestLoanContractRequest(getAllLoanContractRequests.execute(allRequestInput)));
    }
  }

  @ApiOperation("Update process request assigned user")
  @PostMapping("/update-user")
  public ResponseEntity<RestResult> updateLoanContractAssignedUser(@RequestBody Map<String, String> requestBody) throws UseCaseException
  {
    String processInstanceId = requestBody.get("processInstanceId");
    String processRequestId = requestBody.get("processRequestId");
    String modifiedUser = requestBody.get("modifiedUser");

    UpdateLoanContractRequestInput input = new UpdateLoanContractRequestInput(processInstanceId, processRequestId, modifiedUser);
    UpdateLoanContractRequest updateLoanContractRequest = new UpdateLoanContractRequest(loanContractRequestRepository);

    return RestResponse.success(updateLoanContractRequest.execute(input));
  }

  @ApiOperation("Get inquire collateral information")
  @PostMapping("/get-inquire-collateral-info")
  public ResponseEntity<RestResult> getInquireCollateralInfo(@RequestBody String accountNumber) throws UseCaseException
  {
    GetInquireCollateralDetailsInput input = new GetInquireCollateralDetailsInput(accountNumber, "ACCNT");
    GetInquireCollateralDetails useCase = new GetInquireCollateralDetails(newCoreBankingService);

    return RestResponse.success(useCase.execute(input));
  }

  private static List<RestLoanContractRequest> toRestLoanContractRequest(List<LoanContractRequest> loanContractRequestList)
  {
    List<RestLoanContractRequest> restLoanContractRequestList = new ArrayList<>();

    for (LoanContractRequest loanContractRequest : loanContractRequestList)
    {
      restLoanContractRequestList.add(
          new RestLoanContractRequest(loanContractRequest.getId().getId(), loanContractRequest.getCifNumber(), loanContractRequest.getAccount(),
              loanContractRequest.getProductDescription(),
              getValidString(loanContractRequest.getAmount()), loanContractRequest.getCreatedDate(),
              loanContractRequest.getUserId(),
              loanContractRequest.getProcessInstanceId()));
    }

    return restLoanContractRequestList;
  }

  private static String getValidString(Object object)
  {
    return object != null ? object.toString() : "";
  }

  private Date convertDateString(String dateString) throws ParseException
  {
    String dateFormat = "E MMM dd yyyy";
    return new SimpleDateFormat(dateFormat).parse(dateString);
  }
}
