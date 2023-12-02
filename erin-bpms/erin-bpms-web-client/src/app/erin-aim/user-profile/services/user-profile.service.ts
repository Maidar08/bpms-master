import {Inject, Injectable} from '@angular/core';
import {Observable, throwError} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {AimConfig} from "../../aim.config";
import {AIM_CONFIG} from "../../aim.config.token";
import {catchError, map} from "rxjs/operators";
import {UserProfileModel} from "../models/user-profile.model";
import {MatSnackBar} from "@angular/material/snack-bar";

@Injectable({
  providedIn: 'root'
})
export class UserProfileService {

  UPDATE_PROFILE_SUCCESS_MSG = 'Өөрийн мэдээлэл амжилттай засварлалаа.';
  UPDATE_PROFILE_FAILURE_MSG = 'Өөрийн мэдээлэл засварлахад адлаа гарлаа.';

  SNACKBAR_ACTION_CLOSE = 'ХААХ';
  SNACKBAR_CLOSE_DURATION = {duration: 3297};

  constructor(private http: HttpClient, private snackbar: MatSnackBar, @Inject(AIM_CONFIG) private config: AimConfig) {
  }

  public getUserProfile(): Observable<any> {
    return this.http.get(this.config.baseUrl + `/aim/users/profile`)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError((error: any) => {
        return throwError(error);
      }));
  }

  public updateUserProfile(newProfile: UserProfileModel): Observable<any> {
    return this.http.put(this.config.baseUrl + `/aim/users/profile`, newProfile)
      .pipe(map((res: any) => {
        this.snackbar.open(this.UPDATE_PROFILE_SUCCESS_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return res.entity;
      }), catchError((error: any) => {
        this.snackbar.open(this.UPDATE_PROFILE_FAILURE_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return throwError(error);
      }));
  }
}
