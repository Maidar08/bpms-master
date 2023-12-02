import {Injectable} from '@angular/core';
import {BranchBankingService} from './branch-banking.service';
import {FormsModel} from '../../models/app.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class BranchBankingSandbox {
  constructor(private service: BranchBankingService) {
  }

  async getData(processId) {
    return await this.service.getData(processId);
  }

  async getSubMenu(processId) {
    return await this.service.getSubMenu(processId);
  }

  getTransactionList() {
    return this.service.getTransactionList();
  }

  getTransactionDocument() {
    return this.service.getTransactionDocument();
  }

  getTaxInvoiceList(form: FormsModel[], instanceId: string) {
    return this.service.getTaxInvoiceList(form, instanceId);
  }

  getTtdOptions(searchValue, instanceId: string) {
    return this.service.getTtdOptions(searchValue, instanceId);
  }

  getCustomInvoiceList(form: FormsModel[], instanceId: string) {
    return this.service.getCustomInvoiceList(form, instanceId);
  }

  getAccountInfo(accountNumber: string, instanceId: string): Observable<string> {
    return this.service.getAccountInfo(accountNumber, instanceId);
  }

  downloadDocumentByType(instanceId: string, documentType: string, documentParams: any) {
    return this.service.downloadDocumentByType(instanceId, documentType, documentParams);
  }

  getCustomerTransactions(transactionDate: string, instanceId: string) {
    return this.service.getCustomerTransactions(transactionDate, instanceId);
  }

  getETransactionList(form: FormsModel[], instanceId: string) {
    return this.service.getETransactionList(form, instanceId);
  }

  getAccountReference(accountId: any, instanceId: string) {
    return this.service.getAccountReference(accountId, instanceId);
  }

  checkAccountsInfo(instanceId: string, tableData) {
    return this.service.checkAccountsInfo(instanceId, tableData);
  }

  uploadFile(contentAsBase64: string) {
    return this.service.uploadFile(contentAsBase64);
  }

  getUssdInfo(form: FormsModel[], instanceId: string): Observable<string> {
    return this.service.getUssdInfo(form, instanceId);
  }

  changeUssdStateService(actionId: string, phone: any, instanceId: string): Observable<any> {
    return this.service.changeUssdStateService(actionId, phone, instanceId);
  }

  saveUssdInfo(form: FormsModel[], instanceId: string, mainAccounts: [], allAccounts: []) {
    return this.service.saveUssdInfo(form, instanceId, mainAccounts, allAccounts);
  }

  sendOtpCode(instanceId: string, destination: string) {
    return this.service.sendOtpCode(instanceId, destination);
  }

  verifyOtpCode(actionId: any, instanceId: string, verificationCode: string) {
    return this.service.verifyOtpCode(actionId, instanceId, verificationCode);
  }

  calculateLoanRepayment(form: FormsModel[], instanceId: string): Observable<string> {
    return this.service.calculateLoanRepayment(form, instanceId);
  }
  confirmUSSD(form: FormsModel[], instanceId: string): Observable<boolean> {
    return this.service.confirmUSSD(form, instanceId);
  }
  cancelUSSD(form: FormsModel[], instanceId: string): Observable<boolean> {
    return this.service.cancelUSSD(form, instanceId);
  }
}
