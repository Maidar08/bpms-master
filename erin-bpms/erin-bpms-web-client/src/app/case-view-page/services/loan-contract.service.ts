import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {MatSnackBar} from "@angular/material/snack-bar";
import {environment} from "../../../environments/environment";
import {catchError, map} from "rxjs/operators";
import {Observable, throwError} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class LoanContractService {
  constructor(private httpService: HttpClient, private snack: MatSnackBar) {
  }

  getLoanAccountInfo(accountNumber: string): Observable<any> {
    return this.httpService.get(environment.baseServerUrl + '/bpms/bpm/products/account-info/' + accountNumber).pipe(map((res: any) => {
      return res.entity;
    }), catchError(err => {
      this.showErrorMessage(err, 'Мэдээлэл татахад алдаа гарлаа!');
      return throwError(err.errorText);
    }));
  }

  getInquireCollateralInfo(accountNumber: string): Observable<any> {
    return this.httpService.post(environment.baseServerUrl + 'bpms/loan-contract/get-inquire-collateral-info', accountNumber).pipe(map((res: any) => {
      return res.entity;
    }), catchError(err => {
      this.showErrorMessage(err, 'Мэдээлэл татахад алдаа гарлаа!');
      return throwError(err.errorText);
    }));
  }

  showErrorMessage(error, errorText?): void {
    if (this.checkErrorMessage(error)) {
      const errorCode = error.error.code != null ? error.error.code : '';
      this.snack.open(errorCode + ' - ' + error.error.message, 'ХААХ', {duration: 5000});
    } else if (error.error != null) {
      this.snack.open(error.error, 'ХААХ', {duration: 5000});
    } else if (errorText) {
      this.snack.open(errorText, 'ХААХ', {duration: 5000});
    }
  }

  checkErrorMessage(error): boolean {
    return error.error.code && error.error.code !== '';
  }
}
