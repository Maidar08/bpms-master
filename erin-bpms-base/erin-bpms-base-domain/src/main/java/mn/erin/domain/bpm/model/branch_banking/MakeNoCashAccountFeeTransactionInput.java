package mn.erin.domain.bpm.model.branch_banking;

import java.util.List;
import java.util.Map;
public class MakeNoCashAccountFeeTransactionInput
{
    private List<Map<String, Object>> transactionParameters;
    private String transactionType;
    private String transactionSubType;
    private String userTransactionCode;
    private String remarks;
    private String transactionParticulars;
    private String valueDate;
    private String dtlsOnResponse;
    private String transactionRefNumber;
    private String instanceId;

    public  MakeNoCashAccountFeeTransactionInput(List<Map<String, Object>> transactionParameters, String transactionType, String transactionSubType, String userTransactionCode, String remarks, String transactionParticulars, String valueDate, String dtlsOnResponse, String transactionRefNumber,  String instanceId){
        this.transactionParameters = transactionParameters;
        this.transactionType = transactionType;
        this.transactionSubType = transactionSubType;
        this.userTransactionCode = userTransactionCode;
        this.remarks = remarks;
        this.transactionParticulars= transactionParticulars;
        this.valueDate = valueDate;
        this.dtlsOnResponse = dtlsOnResponse;
        this.transactionRefNumber = transactionRefNumber;
        this.instanceId = instanceId;
    }

    public List<Map<String, Object>> getTransactionParameters() {
        return transactionParameters;
    }

    public void setTransactionParameters(List<Map<String, Object>> transactionParameters) {
        this.transactionParameters = transactionParameters;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String trnType) {
        this.transactionType = trnType;
    }

    public String getTransactionSubType() {
        return transactionSubType;
    }

    public void setTransactionSubType(String trnSubType) {
        this.transactionSubType = trnSubType;
    }

    public String getUserTransactionCode() {
        return userTransactionCode;
    }

    public void setUserTransactionCode(String userTrnCode) {
        this.userTransactionCode = userTrnCode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTransactionParticulars() {
        return transactionParticulars;
    }

    public void setTransactionParticulars(String trnParticulars) {
        this.transactionParticulars = trnParticulars;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDt) {
        this.valueDate = valueDt;
    }

    public String getDtlsOnResponse() {
        return dtlsOnResponse;
    }

    public void setDtlsOnResponse(String dtlsOnResponse) {
        this.dtlsOnResponse = dtlsOnResponse;
    }

    public String getTransactionRefNumber() {
        return transactionRefNumber;
    }

    public void setTransactionRefNumber(String trnRefNum) {
        this.transactionRefNumber = trnRefNum;
    }

    public String getInstanceId(String caseInstanceId) {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
