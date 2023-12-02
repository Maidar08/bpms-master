package mn.erin.bpms.direct.online.webapp.model;

import java.util.List;

import mn.erin.domain.bpm.model.directOnline.DanInfo;

public class OnlineLeasingRequestBody

{
  private String cifNumber;
  private String businessSector;
  private String workspan;
  private String joblessMembers;
  private String address;
  private String locale;
  private String email;
  private Long phoneNumber;
  private String channel;
  private String processType;



  private List<DanInfo> danInfo;
  private UnionField unionFields;

  public String getChannel()
  {
    return channel;
  }

  public void setChannel(String channel)
  {
    this.channel = channel;
  }

  public String getCifNumber()
  {
    return cifNumber;
  }

  public void setCifNumber(String cifNumber)
  {
    this.cifNumber = cifNumber;
  }

  public String getBusinessSector()
  {
    return businessSector;
  }

  public void setBusinessSector(String businessSector)
  {
    this.businessSector = businessSector;
  }

  public void setWorkspan(String workspan)
  {
    this.workspan = workspan;
  }

  public String getWorkspan() { return workspan;}

  public String getJoblessMembers()
  {
    return joblessMembers;
  }

  public void setJoblessMembers(String joblessMembers)
  {
    this.joblessMembers = joblessMembers;
  }

  public String getAddress()
  {
    return address;
  }

  public void setAddress(String address)
  {
    this.address = address;
  }

  public String getLocale()
  {
    return locale;
  }

  public void setLocale(String locale)
  {
    this.locale = locale;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public Long getPhoneNumber()
  {
    return phoneNumber;
  }

  public void setPhoneNumber(Long phoneNumber)
  {
    this.phoneNumber = phoneNumber;
  }
  public String getProcessType() {return processType;}

  public void setProcessType(String processType) {this.processType = processType;}

  public List<DanInfo> getDanInfo()
  {
    return danInfo;
  }

  public void setDanInfo(List<DanInfo> danInfo)
  {
    this.danInfo = danInfo;
  }

  public UnionField getUnionFields()
  {
    return unionFields;
  }

  public void setUnionFields(UnionField unionFields)
  {
    this.unionFields = unionFields;
  }
}
