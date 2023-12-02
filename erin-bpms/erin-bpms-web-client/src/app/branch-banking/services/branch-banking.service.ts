import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { NavigationSandboxService } from '../../app-navigation/navigation-sandbox.service';
import { CommonSandboxService } from '../../common/common-sandbox.service';
import { FormsModel } from '../../models/app.model';
import { BRANCH_BANKING } from '../model/branch-banking-constant';

@Injectable({
  providedIn: 'root'
})

export class BranchBankingService {
  constructor(private httpService: HttpClient, private commonService: CommonSandboxService,
              private snack: MatSnackBar, private  navSb: NavigationSandboxService) {
  }

  jsonURL = 'assets/branch-banking-form-data.json';
  subMenuJsonURL = 'assets/branch-banking-sub-menu-constants.json';

  private getJSON(): Observable<any> {
    return this.httpService.get(this.jsonURL);
  }

  private getSubMenuJSON(): Observable<any> {
    return this.httpService.get(this.subMenuJsonURL);
  }


  getData(processId: string) {
    return new Promise(resolve => {
      this.getJSON().subscribe(data => {
        if (null != data[processId]) {
          const processData = data[processId];
          resolve(processData);
        } else {
          resolve(null);
        }
      });
    });
  }

  getSubMenu(processId: string) {
    return new Promise(resolve => {
      this.getSubMenuJSON().subscribe(data => {
        if (null != data[processId]) {
          const processData = data[processId];
          resolve(processData);
        } else {
          resolve(null);
        }
      });
    });
  }

  showErrorMessage(error, errorText?) {
    if (this.checkErrorMessage(error)) {
      const errorCode = error.error.code != null ? error.error.code : '';
      this.snack.open(errorCode + ' - ' + error.error.message, 'ХААХ', {duration: 5000});
    } else if (errorText) {
      this.snack.open(errorText, 'ХААХ', {duration: 5000});
    }
  }

  checkErrorMessage(error) {
    return error && error.error.code && error.error.code !== '';
  }

  checkResponseIsEmpty(res) {
    if (res != null) {
      if ((Array.isArray(res) && res.length === 0) || (res instanceof Object &&  Object.keys(res).length === 0)) {
        this.commonService.showSnackBar('Мэдээлэл олдсонгүй', 'ХААХ', 3000);
      }
    }
  }

  getTransactionList(): Observable<any> {
    return this.httpService.get(environment.baseServerUrl + 'branch-banking/branch-banking/transactions/123/123').pipe(map((res: any) => {
      return res.entity;
    }), catchError(err => {
      this.showErrorMessage(err, 'Мэдээлэл татахад алдаа гарлаа!');
      return throwError(err.errorText);
    }));
  }

  getTransactionDocument(): Observable<any> {
    return this.httpService.get(environment.baseServerUrl + 'branch-banking/branch-banking/transactions/document/123').pipe(map((res: any) => {
      this.commonService.downloadFile(res.entity.documentAsBase64, 'application/pdf', 'Гүйлгээний баримт');
    }), catchError(err => {
      this.showErrorMessage(err, 'Мэдээлэл татахад алдаа гарлаа!');
      return throwError(err.errorText);
    }));
  }

  getTaxInvoiceList(form: FormsModel[], instanceId: string): Observable<any> {
    return this.httpService.post(environment.baseServerUrl + 'branch-banking/tax-info/instanceId/' + instanceId, form).pipe(map((res: any) => {
      return res.entity;
    }), catchError(err => {
      this.showErrorMessage(err, 'Мэдээлэл татахад алдаа гарлаа!');
      return throwError(err.errorText);
    }));
  }


  getTtdOptions(searchValue, instanceId: string): Observable<any> {
    return this.httpService.post(environment.baseServerUrl + 'branch-banking/tax-invoice/instanceId/' + instanceId, searchValue)
      .pipe(map((res: any) => {
        this.checkResponseIsEmpty(res.entity);
        return res.entity;
    }), catchError(err => {
      this.showErrorMessage(err, 'Мэдээлэл татахад алдаа гарлаа!');
      return throwError(err.errorText);
    }));
  }

  getCustomInvoiceList(form: FormsModel[], instanceId: string) {
    return this.httpService.post(environment.baseServerUrl + 'branch-banking/custom-info/instanceId/' + instanceId, form)
      .pipe(map((res: any) => {
        this.checkResponseIsEmpty(res.entity);
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err, 'Мэдээлэл татахад алдаа гарлаа!');
        return throwError(err.errorText);
      })
    );
  }

  getAccountInfo(accountNumber: string, instanceId: string): Observable<string> {
    return this.httpService.get(environment.baseServerUrl + 'branch-banking/account-info/' + accountNumber + '/instanceId/' + instanceId)
      .pipe(map((res: any) => {
        this.checkResponseIsEmpty(res.entity);
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err, 'Дансны мэдээлэл татахад алдаа гарлаа!');
        return throwError(err.errorText);
      }));
  }

  downloadDocumentByType(instanceId: string, documentType: string, documentParams: any) {
    const userId = this.navSb.getUserName();
    const userGroup = this.navSb.getUserGroup();
    documentParams.userName = userId;
    documentParams.branchNumber = userGroup;
    return this.httpService.post(environment.baseServerUrl + 'branch-banking/document/' + instanceId + '/' + documentType, documentParams)
      .pipe(map((res: any) => {
      return res.entity;
    }), catchError(err => {
      this.showErrorMessage(err, 'Баримт татахад алдаа гарлаа!');
      return throwError(err.errorText);
    }));
  }

  getCustomerTransactions(transactionDate: string, instanceId: string) {
    const userId = this.navSb.getUserName();
    return  this.httpService.get(environment.baseServerUrl + 'branch-banking/transaction/customer-transactions/'
      + userId + '/' + transactionDate + '/instanceId/' + instanceId)
      .pipe(map((res: any) => {
          this.checkResponseIsEmpty(res.entity);
          return res.entity;
        }),
        catchError(err => {
          this.showErrorMessage(err, 'Гүйлгээний жагсаалт татахад алдаа гарлаа!');
          return throwError(err.errorText);
        }));
  }

  getETransactionList(form: FormsModel[], instanceId: string) {
    return this.httpService.post(environment.baseServerUrl + 'branch-banking/transaction/e-transaction/instanceId/' + instanceId, form)
      .pipe(map((res: any) => {
          this.checkResponseIsEmpty(res.entity);
          return res.entity;
      }),
      catchError(err => {
        this.showErrorMessage(err, 'Цахим гүйлгээний жагсаалт татахад алдаа гарлаа!');
        return throwError(err.errorText);
      }));
  }

  getAccountReference(accountId: any, instanceId: string) {
    return this.httpService.get(environment.baseServerUrl + BRANCH_BANKING + '/account-reference/' + accountId + '/' + instanceId)
      .pipe(map((res: any) => {
        this.checkResponseIsEmpty(res.entity);
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err, 'Mэдээлэл татахад алдаа гарлаа!');
        return throwError(err.errorText);
      }));
  }

  getUssdInfo(form: FormsModel[], instanceId: string): Observable<string> {
    const customerId = this.commonService.getFormValue(form, 'customerId');
    const phoneNumber = this.commonService.getFormValue(form, 'ussdSearchPhoneNumber');
    return this.httpService.get(environment.baseServerUrl + `branch-banking/USSD/instanceId/${instanceId}/searchUser`, {
      params: {
        customerId,
        phoneNumber
      }
    })
      .pipe(map((res: any) => {
        this.checkResponseIsEmpty(res.entity);
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err, 'Харилцагчийн мэдээлэл татахад алдаа гарлаа!');
        return throwError(err.errorText);
      }));
  }

  checkAccountsInfo(instanceId: string, tableData) {
    return this.httpService.post(environment.baseServerUrl + BRANCH_BANKING + '/checkAccountsInfo/' + instanceId, tableData).pipe(map((res: any) => {
      if (null == res || null == res.entity) {
        this.showErrorMessage(null, 'Нэр Данс шалгахад алдаа гарлаа!');
      }
      return res.entity;
    }), catchError(err => {
      this.showErrorMessage(err, 'Нэр Данс шалгахад алдаа гарлаа!');
      return throwError(err.errorText);
    }));
  }

  uploadFile(contentAsBase64: string) {
    return this.httpService.post(environment.baseServerUrl + BRANCH_BANKING + '/excel/read', contentAsBase64).pipe(map((res: any) => {
      return res.entity;
    }), catchError(err => {
      this.showErrorMessage(err, 'Гүйлгээний жагсаалт татахад алдаа гарлаа!');
      return throwError(err.errorText);
    }));
  }

  saveUssdInfo(form: any, instanceId: string, mainAccounts: [], allAccounts: []) {
    const formMap = this.commonService.getFormFieldsAsMap(form, {});
    formMap['mainAccounts'] = mainAccounts;
    formMap['allAccounts'] = allAccounts;
    return this.httpService.post(environment.baseServerUrl + `branch-banking/USSD/instanceId/${instanceId}/updateOrCreate`, formMap)
      .pipe(map(
        (res: any) => {
          this.checkResponseIsEmpty(res);
          return res.entity;
        }
      ), catchError(err => {
        this.showErrorMessage(err, 'Харилцагчийн мэдээлэл хадгалахад алдаа гарлаа!');
        return throwError(err.errorText);
      }));
  }

  changeUssdStateService(action: any, phone: any, instanceId: string): Observable<any> {
    return this.httpService.get(environment.baseServerUrl + `branch-banking/USSD/instanceId/${instanceId}/userStatusChange`, {
      params: {
        mobile: phone,
        type: this.setUssdUserStatus(action.actionId)
      }
    })
      .pipe(map((res: any) => {
        this.checkResponseIsEmpty(res.entity);
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err,  action.actionName + ' хүсэлт илгээхэд алдаа гарлаа!');
        return throwError(err.errorText);
      }));
  }

  sendOtpCode(instanceId: string, destination: string) {
    return this.httpService.get(environment.baseServerUrl + 'branch-banking/USSD/instanceId/' +  instanceId + '/userOtpSend/' + destination)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err, 'Баталгаажуулах код илгээхэд алдаа гарлаа!');
        return throwError(err.errorText);
      }));

  }

  verifyOtpCode(action: any, instanceId: string, verificationCode: string) {
    return this.httpService.post(environment.baseServerUrl + 'branch-banking/USSD/instanceId/' + instanceId + '/verifyOtpCode', verificationCode)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        switch (action.actionId) {
          case 'recoveryRights':
            this.showErrorMessage(err, 'Харилцагчийн эрх сэргээхэд алдаа гарлаа!');
            break;
          case 'save':
            this.showErrorMessage(err, 'Харилцагчийн мэдээлэл хадгалахад алдаа гарлаа!');
        }
        return  throwError(err.errorText);
      }));
  }

  setUssdUserStatus(actionId: string) {
    let ussdStatus;
    switch (actionId) {
      case 'forgetPassword':
        ussdStatus = 'P';
        break;
      case 'removeRights':
        ussdStatus = 'C';
        break;
      case 'recoveryRights':
        ussdStatus = 'O';
        break;
      case 'unblock':
        ussdStatus = 'B';
        break;
      default:
        ussdStatus = null;
        break;
    }
    return ussdStatus;
  }

  calculateLoanRepayment(form: FormsModel[], instanceId: string): Observable<string> {
    const accountId = this.commonService.getFormValue(form, 'accountId');
    return this.httpService.get(environment.baseServerUrl + 'branch-banking/loanPaymentInfo/' + instanceId + '/' + accountId)
      .pipe(map((res: any) => {
        this.checkResponseIsEmpty(res.entity);
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err, 'Дансны мэдээлэл татахад алдаа гарлаа!');
        this.commonService.clearFieldsById(form, ['loanRepaymentType', 'loanBalance', 'currencyValue', 'customerFullName', 'basicPayment', 'interestPayment', 'penaltyAmount', 'feePayment', 'totalAmount', 'accountNumber', 'payLoanAmount', 'transactionDescription', 'fAccountCurrencyValue']);
        return throwError(err.errorText);
      }));
  }

  confirmUSSD(form: FormsModel[], instanceId: string): Observable<boolean>{
    const id = this.commonService.getFormValue(form, "id");
    const branchNumber = this.commonService.getFormValue(form, "registeredBranch");
    return this.httpService.post(environment.baseServerUrl + `branch-banking/USSD/instanceId/${instanceId}/ussdUserEndorse`,{
      id,
      branchNumber
    })
      .pipe(map((res: any) => {
        this.checkResponseIsEmpty(res.entity);
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err, 'USSD харилцагч батлахад алдаа гарлаа!');
        return throwError(err.errorText);
      }));
  }
  cancelUSSD(form: FormsModel[], instanceId: string): Observable<boolean>{
    const id = this.commonService.getFormValue(form, "id");
    const branchNumber = this.commonService.getFormValue(form, "registeredBranch");
    return this.httpService.post(environment.baseServerUrl + `branch-banking/USSD/instanceId/${instanceId}/ussdUserCancel`,{
      id,
      branchNumber
    })
      .pipe(map((res: any) => {
        this.checkResponseIsEmpty(res.entity);
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err, 'USSD харилцагч цуцлахад алдаа гарлаа!');
        return throwError(err.errorText);
      }));
  }
}
