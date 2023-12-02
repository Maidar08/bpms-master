import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Subject, throwError} from 'rxjs';
import {CreateRequestDialogComponent} from '../loan-search-page/container/create-request-dialog/create-request-dialog.component';
import {catchError} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class DialogService {
  processDialogSbj = new Subject<string>();

  constructor(private dialog: MatDialog) {
  }

  openProcessDialog() {
    const dialogRef = this.dialog.open(CreateRequestDialogComponent, {width: '550px', disableClose: true});
    dialogRef.afterClosed().subscribe((res) => {
      if (res) {
        this.processDialogSbj.next(res);
      }
    }, catchError(err => {
      if (err.status === 401) {
        dialogRef.close();
        return throwError(err);
      }
    }));
  }

  openCustomDialog(classRef, config) {
    return new Promise(resolve => {
        const dialogRef = this.dialog.open(classRef, config);
        dialogRef.afterClosed().subscribe((res) => {
          resolve(res);
        });
      }
    );
  }
}
