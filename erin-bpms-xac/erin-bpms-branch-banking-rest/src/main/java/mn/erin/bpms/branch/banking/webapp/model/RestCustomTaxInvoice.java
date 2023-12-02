package mn.erin.bpms.branch.banking.webapp.model;

import java.util.List;

import mn.erin.domain.bpm.model.branch_banking.PaymentInfo;

/**
 * @author Balbabahadir
 */
public class RestCustomTaxInvoice {

    private String branchName;
    private String invoiceNumber;
    private String registerId;
    private String paymentAmount;
    private String charge;
    private String type;
    private String declarationDate;
    private String taxPayerName;
    private List<PaymentInfo> paymentList;


    public RestCustomTaxInvoice(String branchName, String invoiceNumber, String registerId, String paymentAmount, String charge,
        String declarationDate, String taxPayerName, List<PaymentInfo> paymentList) {
        this.branchName = branchName;
        this.invoiceNumber = invoiceNumber;
        this.registerId = registerId;
        this.paymentAmount = paymentAmount;
        this.charge = charge;
        this.declarationDate = declarationDate;
        this.taxPayerName = taxPayerName;
        this.paymentList = paymentList;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeclarationDate()
    {
        return declarationDate;
    }

    public void setDeclarationDate(String declarationDate)
    {
        this.declarationDate = declarationDate;
    }

    public String getTaxPayerName()
    {
        return taxPayerName;
    }

    public void setTaxPayerName(String taxPayerName)
    {
        this.taxPayerName = taxPayerName;
    }

    public List<PaymentInfo> getPaymentList()
    {
        return paymentList;
    }

    public void setPaymentList(List<PaymentInfo> paymentList)
    {
        this.paymentList = paymentList;
    }

}
