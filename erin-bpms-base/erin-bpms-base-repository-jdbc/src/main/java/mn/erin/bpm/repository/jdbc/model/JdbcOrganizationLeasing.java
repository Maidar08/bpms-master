package mn.erin.bpm.repository.jdbc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Tamir & Sukhbaatar
 */
@Table("ERIN_ORG_LEASING")
public class JdbcOrganizationLeasing
{
  @Id
  String contractid;
  String contractbranch;

  String name;
  String registernumber;

  String cif;
  String recordStat;

  LocalDateTime contractdt;
  String createdUserid;

  String checkerId;

  String processInstanceId;

  String makerId;

  public String getMakerId()
  {
    return makerId;
  }

  public void setMakerId(String makerId)
  {
    this.makerId = makerId;
  }

  public String getContractnumber()
  {
    return contractid;
  }

  public void setContractnumber(String contractnumber)
  {
    this.contractid = contractnumber;
  }

  public String getContractbranch()
  {
    return contractbranch;
  }

  public void setContractbranch(String contractbranch)
  {
    this.contractbranch = contractbranch;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getOrganizationNumber()
  {
    return registernumber;
  }

  public void setOrganizationNumber(String registernumber)
  {
    this.registernumber = registernumber;
  }

  public String getCif()
  {
    return cif;
  }

  public void setCif(String cif)
  {
    this.cif = cif;
  }

  public String getRecordStat()
  {
    return recordStat;
  }

  public void setRecordStat(String recordStat)
  {
    this.recordStat = recordStat;
  }

  public LocalDateTime getContractdt()
  {
    return contractdt;
  }

  public void setContractdt(LocalDateTime contractdt)
  {
    this.contractdt = contractdt;
  }

  public String getCreatedUserid()
  {
    return createdUserid;
  }

  public void setCreatedUserid(String createdUserid)
  {
    this.createdUserid = createdUserid;
  }

  public String getCheckerId()
  {
    return checkerId;
  }

  public void setCheckerId(String checkerId)
  {
    this.checkerId = checkerId;
  }

  public String getProcessInstanceId() { return processInstanceId; }

  public void setProcessInstanceId(String processInstanceId) { this.processInstanceId = processInstanceId; }
}