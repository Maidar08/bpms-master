package mn.erin.bpm.repository.jdbc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Tamir
 */
@Table("ERIN_ORG_SALARY")
public class JdbcOrganizationSalary
{
  @Id
  String contractid;
  String contractbranch;

  String cname;
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

  public String getContractid()
  {
    return contractid;
  }

  public void setContractid(String contractid)
  {
    this.contractid = contractid;
  }

  public String getContractbranch()
  {
    return contractbranch;
  }

  public void setContractbranch(String contractbranch)
  {
    this.contractbranch = contractbranch;
  }

  public String getCname()
  {
    return cname;
  }

  public void setCname(String cname)
  {
    this.cname = cname;
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
