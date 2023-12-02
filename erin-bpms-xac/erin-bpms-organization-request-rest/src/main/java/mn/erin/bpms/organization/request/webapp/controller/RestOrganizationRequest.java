package mn.erin.bpms.organization.request.webapp.controller;

import java.util.Map;

/**
 * @author Odgavaa
 */

public class RestOrganizationRequest
{
  private String id;
  private String state;
  private String instanceId;
  private String countryRegNumber;
  private String channel;
  private String userId;

  private String contractDt;
  private String registrationNumber;

  private String cif;
  private String phoneNumber;

  private String email;
  private String branchNumber;
  private String processType;

  private String confirmedUser;
  private String oldContractNumber;
  private Map<String, Object> parameters;

  public RestOrganizationRequest()
  {

  }

  public RestOrganizationRequest(String id, String processType,
      String registrationNumber, String countryRegNumber,
      String contractDt, String userId,
      String branchNumber, String state, String confirmedUser, String oldContractNumber, Map<String, Object> parameters)
  {
    this.id = id;
    this.processType = processType;

    this.registrationNumber = registrationNumber;
    this.countryRegNumber = countryRegNumber;

    this.contractDt = contractDt;
    this.userId = userId;

    this.branchNumber = branchNumber;
    this.state = state;

    this.confirmedUser = confirmedUser;
    this.oldContractNumber = oldContractNumber;
    this.parameters = parameters;
  }

  public RestOrganizationRequest(String id, String state, String instanceId, String countryRegNumber, String channel, String userId, String contractDt,
      String registrationNumber, String cifNumber, String phoneNumber, String branchNumber, String processType, String confirmedUser
  )
  {
    this.id = id;
    this.state = state;
    this.instanceId = instanceId;
    this.countryRegNumber = countryRegNumber;
    this.channel = channel;
    this.userId = userId;
    this.contractDt = contractDt;
    this.registrationNumber = registrationNumber;
    this.cif = cifNumber;
    this.phoneNumber = phoneNumber;
    this.branchNumber = branchNumber;
    this.processType = processType;
    this.confirmedUser = confirmedUser;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getState()
  {
    return state;
  }

  public void setState(String state)
  {
    this.state = state;
  }

  public String getInstanceId()
  {
    return instanceId;
  }

  public void setInstanceId(String instanceId)
  {
    this.instanceId = instanceId;
  }

  public String getCountryRegNumber()
  {
    return countryRegNumber;
  }

  public void setCountryRegNumber(String countryRegNumber)
  {
    this.countryRegNumber = countryRegNumber;
  }

  public String getChannel()
  {
    return channel;
  }

  public void setChannel(String channel)
  {
    this.channel = channel;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public String getCreatedDate()
  {
    return contractDt;
  }

  public void setCreatedDate(String createdDate)
  {
    this.contractDt = createdDate;
  }

  public String getRegistrationNumber()
  {
    return registrationNumber;
  }

  public void setRegistrationNumber(String registrationNumber)
  {
    this.registrationNumber = registrationNumber;
  }

  public String getCif()
  {
    return cif;
  }

  public void setCif(String cif)
  {
    this.cif = cif;
  }

  public String getPhoneNumber()
  {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber)
  {
    this.phoneNumber = phoneNumber;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public String getBranchNumber()
  {
    return branchNumber;
  }

  public void setBranchNumber(String branchNumber)
  {
    this.branchNumber = branchNumber;
  }

  public String getProcessType()
  {
    return processType;
  }

  public void setProcessType(String processType)
  {
    this.processType = processType;
  }

  public String getConfirmedUser()
  {
    return confirmedUser;
  }

  public void setConfirmedUser(String confirmedUser)
  {
    this.confirmedUser = confirmedUser;
  }

  public String getOldContractNumber()
  {
    return oldContractNumber;
  }

  public void setOldContractNumber(String oldContractNumber)
  {
    this.oldContractNumber = oldContractNumber;
  }

  public Map<String, Object> getParameters()
  {
    return parameters;
  }

  public void setParameters(Map<String, Object> parameters)
  {
    this.parameters = parameters;
  }
}
