package mn.erin.bpm.repository.jdbc.model;

import java.sql.NClob;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Tamir
 */
@Table("ERIN_ORG_SALARY")
public class JdbcOrganizationSalaryExcel
{
  @Id
  String registernumber;
  String contractid;
  String contractnumber;
  String cif;
  String contractbranch;
  String cname;
  String fcname;
  String lovnumber;
  String caccountid;
  String exposurecategoryCode;
  String exposurecategoryDescription;
  LocalDateTime ccreatedt;
  String hrcnt;
  String empname;
  String empphone;
  LocalDateTime form4001;
  LocalDateTime contractdt;
  LocalDateTime expiredt;
  LocalDateTime mstartsalary;
  LocalDateTime mendsalary;
  String arate;
  String erate;
  String countryregnumber;
  LocalDateTime extensionDt;
  String leakage;
  String corporateType;
  String lastcontractno;
  String salarytranfee;
  String chargeglaccount;
  String isSalaryLoan;
  String releaseempname;
  NClob additionInfo;
  String corporaterank;
  String recordStat;
  String authStat;
  String onceAuth;
  String intcond;
  String erateMax;
  String sday1;
  String sday2;
  String stime;
  String cyear;
  String cextended;
  LocalDateTime cextendedDate;
  String cextendyear;
  LocalDateTime ccreatedDate;
  String additionalInfo;
  String danregnumber;
  String district;
  String onlinesal;
  String createdUserid;
  LocalDateTime createdAt;
  String makerId;
  LocalDateTime makerDtStamp;
  String checkerId;
  LocalDateTime checkerDtStamp;
  String lastUpdatedBy;
  LocalDateTime updatedAt;
  String modNo;

  public String getContractid()
  {
    return contractid;
  }

  public void setContractid(String contractid)
  {
    this.contractid = contractid;
  }

  public String getContractnumber()
  {
    return contractnumber;
  }

  public void setContractnumber(String contractnumber)
  {
    this.contractnumber = contractnumber;
  }

  public String getCif()
  {
    return cif;
  }

  public void setCif(String cif)
  {
    this.cif = cif;
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

  public String getFcname()
  {
    return fcname;
  }

  public void setFcname(String fcname)
  {
    this.fcname = fcname;
  }

  public String getLovnumber()
  {
    return lovnumber;
  }

  public void setLovnumber(String lovnumber)
  {
    this.lovnumber = lovnumber;
  }

  public String getCaccountid()
  {
    return caccountid;
  }

  public void setCaccountid(String caccountid)
  {
    this.caccountid = caccountid;
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

  public LocalDateTime getCcreatedt()
  {
    return ccreatedt;
  }

  public void setCcreatedt(LocalDateTime ccreatedt)
  {
    this.ccreatedt = ccreatedt;
  }

  public String getHrcnt()
  {
    return hrcnt;
  }

  public void setHrcnt(String hrcnt)
  {
    this.hrcnt = hrcnt;
  }

  public String getEmpname()
  {
    return empname;
  }

  public void setEmpname(String empname)
  {
    this.empname = empname;
  }

  public String getEmpphone()
  {
    return empphone;
  }

  public void setEmpphone(String empphone)
  {
    this.empphone = empphone;
  }

  public LocalDateTime getForm4001()
  {
    return form4001;
  }

  public void setForm4001(LocalDateTime form4001)
  {
    this.form4001 = form4001;
  }

  public LocalDateTime getContractdt()
  {
    return contractdt;
  }

  public void setContractdt(LocalDateTime contractdt)
  {
    this.contractdt = contractdt;
  }

  public LocalDateTime getExpiredt()
  {
    return expiredt;
  }

  public void setExpiredt(LocalDateTime expiredt)
  {
    this.expiredt = expiredt;
  }

  public LocalDateTime getMstartsalary()
  {
    return mstartsalary;
  }

  public void setMstartsalary(LocalDateTime mstartsalary)
  {
    this.mstartsalary = mstartsalary;
  }

  public LocalDateTime getMendsalary()
  {
    return mendsalary;
  }

  public void setMendsalary(LocalDateTime mendsalary)
  {
    this.mendsalary = mendsalary;
  }

  public String getArate()
  {
    return arate;
  }

  public void setArate(String arate)
  {
    this.arate = arate;
  }

  public String getErate()
  {
    return erate;
  }

  public void setErate(String erate)
  {
    this.erate = erate;
  }

  public String getCountryregnumber()
  {
    return countryregnumber;
  }

  public void setCountryregnumber(String countryregnumber)
  {
    this.countryregnumber = countryregnumber;
  }

  public String getRegisternumber()
  {
    return registernumber;
  }

  public void setRegisternumber(String registernumber)
  {
    this.registernumber = registernumber;
  }

  public LocalDateTime getExtensionDt()
  {
    return extensionDt;
  }

  public void setExtensionDt(LocalDateTime extensionDt)
  {
    this.extensionDt = extensionDt;
  }

  public String getLeakage()
  {
    return leakage;
  }

  public void setLeakage(String leakage)
  {
    this.leakage = leakage;
  }

  public String getCorporateType()
  {
    return corporateType;
  }

  public void setCorporateType(String corporateType)
  {
    this.corporateType = corporateType;
  }

  public String getLastcontractno()
  {
    return lastcontractno;
  }

  public void setLastcontractno(String lastcontractno)
  {
    this.lastcontractno = lastcontractno;
  }

  public String getSalarytranfee()
  {
    return salarytranfee;
  }

  public void setSalarytranfee(String salarytranfee)
  {
    this.salarytranfee = salarytranfee;
  }

  public String getChargeglaccount()
  {
    return chargeglaccount;
  }

  public void setChargeglaccount(String chargeglaccount)
  {
    this.chargeglaccount = chargeglaccount;
  }

  public String getIsSalaryLoan()
  {
    return isSalaryLoan;
  }

  public void setIsSalaryLoan(String isSalaryLoan)
  {
    this.isSalaryLoan = isSalaryLoan;
  }

  public String getReleaseempname()
  {
    return releaseempname;
  }

  public void setReleaseempname(String releaseempname)
  {
    this.releaseempname = releaseempname;
  }

  public NClob getAdditionInfo()
  {
    return additionInfo;
  }

  public void setAdditionInfo(NClob additionInfo)
  {
    this.additionInfo = additionInfo;
  }

  public String getCorporaterank()
  {
    return corporaterank;
  }

  public void setCorporaterank(String corporaterank)
  {
    this.corporaterank = corporaterank;
  }

  public String getRecordStat()
  {
    return recordStat;
  }

  public void setRecordStat(String recordStat)
  {
    this.recordStat = recordStat;
  }

  public String getAuthStat()
  {
    return authStat;
  }

  public void setAuthStat(String authStat)
  {
    this.authStat = authStat;
  }

  public String getOnceAuth()
  {
    return onceAuth;
  }

  public void setOnceAuth(String onceAuth)
  {
    this.onceAuth = onceAuth;
  }

  public String getIntcond()
  {
    return intcond;
  }

  public void setIntcond(String intcond)
  {
    this.intcond = intcond;
  }

  public String getErateMax()
  {
    return erateMax;
  }

  public void setErateMax(String erateMax)
  {
    this.erateMax = erateMax;
  }

  public String getSday1()
  {
    return sday1;
  }

  public void setSday1(String sday1)
  {
    this.sday1 = sday1;
  }

  public String getSday2()
  {
    return sday2;
  }

  public void setSday2(String sday2)
  {
    this.sday2 = sday2;
  }

  public String getStime()
  {
    return stime;
  }

  public void setStime(String stime)
  {
    this.stime = stime;
  }

  public String getCyear()
  {
    return cyear;
  }

  public void setCyear(String cyear)
  {
    this.cyear = cyear;
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

  public String getCextendyear()
  {
    return cextendyear;
  }

  public void setCextendyear(String cextendyear)
  {
    this.cextendyear = cextendyear;
  }

  public LocalDateTime getCcreatedDate()
  {
    return ccreatedDate;
  }

  public void setCcreatedDate(LocalDateTime ccreatedDate)
  {
    this.ccreatedDate = ccreatedDate;
  }

  public String getAdditionalInfo()
  {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo)
  {
    this.additionalInfo = additionalInfo;
  }

  public String getDanregnumber()
  {
    return danregnumber;
  }

  public void setDanregnumber(String danregnumber)
  {
    this.danregnumber = danregnumber;
  }

  public String getDistrict()
  {
    return district;
  }

  public void setDistrict(String district)
  {
    this.district = district;
  }

  public String getOnlinesal()
  {
    return onlinesal;
  }

  public void setOnlinesal(String onlinesal)
  {
    this.onlinesal = onlinesal;
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
