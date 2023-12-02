package mn.erin.bpm.repository.jdbc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Tamir & Sukhbaatar
 */
@Table("ERIN_ORG_LEASING")
public class JdbcOrganizationLeasingExcel
{
  @Id
  String registernumber;
  String contractid;
  String contractbranch;
  String name;
  LocalDateTime contractdt;
  String cyear;
  LocalDateTime expiredt;
  String fee;
  String lastcontractno;
  String custtype;
  String exposurecategoryCode;
  String exposurecategoryDescription;
  String cif;
  String countryregnumber;
  LocalDateTime birthdt;
  String address;
  String phone;
  String mail;
  String productcat;
  String productdesc;
  String contactname;
  String contactphone;
  String contactemail;
  String contactdesc;
  String chargetype;
  String chargeamount;
  String loanamount;
  String settlementdate;
  String settlementpercent;
  String settlementaccount;
  String condition;
  String rate;
  String discharge;
  String leasing;
  String bnpl;
  String terminalid;
  String cextendyear;
  String cextended;
  LocalDateTime cextendedDate;
  String recordStat;
  String createdUserid;
  LocalDateTime createdAt;
  String makerId;
  LocalDateTime makerDtStamp;
  String checkerId;
  LocalDateTime checkerDtStamp;
  String lastUpdatedBy;
  LocalDateTime updatedAt;
  String modNo;

  public String getRegisternumber()
  {
    return registernumber;
  }

  public void setRegisternumber(String registernumber)
  {
    this.registernumber = registernumber;
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

  public String getContractid()
  {
    return contractid;
  }

  public void setContractid(String contractid)
  {
    this.contractid = contractid;
  }

  public LocalDateTime getContractdt()
  {
    return contractdt;
  }

  public void setContractdt(LocalDateTime contractdt)
  {
    this.contractdt = contractdt;
  }

  public String getCyear()
  {
    return cyear;
  }

  public void setCyear(String cyear)
  {
    this.cyear = cyear;
  }

  public LocalDateTime getExpiredt()
  {
    return expiredt;
  }

  public void setExpiredt(LocalDateTime expiredt)
  {
    this.expiredt = expiredt;
  }

  public String getFee()
  {
    return fee;
  }

  public void setFee(String fee)
  {
    this.fee = fee;
  }

  public String getLastcontractno()
  {
    return lastcontractno;
  }

  public void setLastcontractno(String lastcontractno)
  {
    this.lastcontractno = lastcontractno;
  }

  public String getCusttype()
  {
    return custtype;
  }

  public void setCusttype(String custtype)
  {
    this.custtype = custtype;
  }

  public String getExposurecategoryCode()
  {
    return exposurecategoryCode;
  }

  public void setExposurecategoryCode(String exposurecategoryCode)
  {
    this.exposurecategoryCode = exposurecategoryCode;
  }

  public String getExposurecategoryDescription()
  {
    return exposurecategoryDescription;
  }

  public void setExposurecategoryDescription(String exposurecategoryDescription)
  {
    this.exposurecategoryDescription = exposurecategoryDescription;
  }

  public String getCif()
  {
    return cif;
  }

  public void setCif(String cif)
  {
    this.cif = cif;
  }

  public String getCountryregnumber()
  {
    return countryregnumber;
  }

  public void setCountryregnumber(String countryregnumber)
  {
    this.countryregnumber = countryregnumber;
  }

  public LocalDateTime getBirthdt()
  {
    return birthdt;
  }

  public void setBirthdt(LocalDateTime birthdt)
  {
    this.birthdt = birthdt;
  }

  public String getAddress()
  {
    return address;
  }

  public void setAddress(String address)
  {
    this.address = address;
  }

  public String getPhone()
  {
    return phone;
  }

  public void setPhone(String phone)
  {
    this.phone = phone;
  }

  public String getMail()
  {
    return mail;
  }

  public void setMail(String mail)
  {
    this.mail = mail;
  }

  public String getProductcat()
  {
    return productcat;
  }

  public void setProductcat(String productcat)
  {
    this.productcat = productcat;
  }

  public String getProductdesc()
  {
    return productdesc;
  }

  public void setProductdesc(String productdesc)
  {
    this.productdesc = productdesc;
  }

  public String getContactname()
  {
    return contactname;
  }

  public void setContactname(String contactname)
  {
    this.contactname = contactname;
  }

  public String getContactphone()
  {
    return contactphone;
  }

  public void setContactphone(String contactphone)
  {
    this.contactphone = contactphone;
  }

  public String getContactemail()
  {
    return contactemail;
  }

  public void setContactemail(String contactemail)
  {
    this.contactemail = contactemail;
  }

  public String getContactdesc()
  {
    return contactdesc;
  }

  public void setContactdesc(String contactdesc)
  {
    this.contactdesc = contactdesc;
  }

  public String getChargetype()
  {
    return chargetype;
  }

  public void setChargetype(String chargetype)
  {
    this.chargetype = chargetype;
  }

  public String getChargeamount()
  {
    return chargeamount;
  }

  public void setChargeamount(String chargeamount)
  {
    this.chargeamount = chargeamount;
  }

  public String getLoanamount()
  {
    return loanamount;
  }

  public void setLoanamount(String loanamount)
  {
    this.loanamount = loanamount;
  }

  public String getSettlementdate()
  {
    return settlementdate;
  }

  public void setSettlementdate(String settlementdate)
  {
    this.settlementdate = settlementdate;
  }

  public String getSettlementpercent()
  {
    return settlementpercent;
  }

  public void setSettlementpercent(String settlementpercent)
  {
    this.settlementpercent = settlementpercent;
  }

  public String getSettlementaccount()
  {
    return settlementaccount;
  }

  public void setSettlementaccount(String settlementaccount)
  {
    this.settlementaccount = settlementaccount;
  }

  public String getCondition()
  {
    return condition;
  }

  public void setCondition(String condition)
  {
    this.condition = condition;
  }

  public String getRate()
  {
    return rate;
  }

  public void setRate(String rate)
  {
    this.rate = rate;
  }

  public String getDischarge()
  {
    return discharge;
  }

  public void setDischarge(String discharge)
  {
    this.discharge = discharge;
  }

  public String getLeasing()
  {
    return leasing;
  }

  public void setLeasing(String leasing)
  {
    this.leasing = leasing;
  }

  public String getBnpl()
  {
    return bnpl;
  }

  public void setBnpl(String bnpl)
  {
    this.bnpl = bnpl;
  }

  public String getTerminalid()
  {
    return terminalid;
  }

  public void setTerminalid(String terminalid)
  {
    this.terminalid = terminalid;
  }

  public String getCextendyear()
  {
    return cextendyear;
  }

  public void setCextendyear(String cextendyear)
  {
    this.cextendyear = cextendyear;
  }

  public String getCextended()
  {
    return cextended;
  }

  public void setCextended(String cextended)
  {
    this.cextended = cextended;
  }

  public LocalDateTime getCextendedDate()
  {
    return cextendedDate;
  }

  public void setCextendedDate(LocalDateTime cextendedDate)
  {
    this.cextendedDate = cextendedDate;
  }

  public String getRecordStat()
  {
    return recordStat;
  }

  public void setRecordStat(String recordStat)
  {
    this.recordStat = recordStat;
  }

  public String getCreatedUserid()
  {
    return createdUserid;
  }

  public void setCreatedUserid(String createdUserid)
  {
    this.createdUserid = createdUserid;
  }

  public LocalDateTime getCreatedAt()
  {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt)
  {
    this.createdAt = createdAt;
  }

  public String getMakerId()
  {
    return makerId;
  }

  public void setMakerId(String makerId)
  {
    this.makerId = makerId;
  }

  public LocalDateTime getMakerDtStamp()
  {
    return makerDtStamp;
  }

  public void setMakerDtStamp(LocalDateTime makerDtStamp)
  {
    this.makerDtStamp = makerDtStamp;
  }

  public String getCheckerId()
  {
    return checkerId;
  }

  public void setCheckerId(String checkerId)
  {
    this.checkerId = checkerId;
  }

  public LocalDateTime getCheckerDtStamp()
  {
    return checkerDtStamp;
  }

  public void setCheckerDtStamp(LocalDateTime checkerDtStamp)
  {
    this.checkerDtStamp = checkerDtStamp;
  }

  public String getLastUpdatedBy()
  {
    return lastUpdatedBy;
  }

  public void setLastUpdatedBy(String lastUpdatedBy)
  {
    this.lastUpdatedBy = lastUpdatedBy;
  }

  public LocalDateTime getUpdatedAt()
  {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt)
  {
    this.updatedAt = updatedAt;
  }

  public String getModNo()
  {
    return modNo;
  }

  public void setModNo(String modNo)
  {
    this.modNo = modNo;
  }
}