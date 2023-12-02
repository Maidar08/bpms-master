import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {AimConfig} from '../../aim.config';
import {ArchiveUserModel, Gender, UserModel, UserStatus} from '../models/user-management.model';
import {AIM_CONFIG} from '../../aim.config.token';
import {MatSnackBar} from '@angular/material/snack-bar';
import {catchError, map} from 'rxjs/operators';
import {Observable, throwError} from 'rxjs';
import {MatDialog} from "@angular/material/dialog";
import {ConfirmDialogComponent} from "../../component/confirm-dialog/confirm-dialog.component";

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {

  constructor(private http: HttpClient, private snackbar: MatSnackBar, @Inject(AIM_CONFIG) private config: AimConfig, public dialog: MatDialog) {
  }

  CREATE_USER_SUCCESS_MSG = 'Хэрэглэгч амжилттай үүслээ.';
  CREATE_USER_FAILURE_MSG = 'Хэрэглэгч үүсгэхэд алдаа гарлаа.';

  CHECK_USERNAME_FAILURE_MSG = 'Хэрэглэгчийн нэр шалгахад алдаа гарлаа.';

  GET_USERS_FAILURE_MSG = 'Хэрэглэгчид авахад алдаа гарлаа.';

  DELETE_USER_SUCCESS_MSG = 'Хэрэглэгч амжилттай устгалаа.';
  DELETE_USER_FAILURE_MSG = 'Хэрэглэгч устгахад алдаа гарлаа.';

  BULK_DELETE_USER_SUCCESS_MSG = 'Хэрэглэгчдийг амжилттай устгалаа.';
  BULK_DELETE_USER_FAILURE_MSG = 'Хэрэглэгчдийг устгахад алдаа гарлаа.';

  UPDATE_USER_SUCCESS_MSG = 'Хэрэглэгч амжилттай засварлалаа.';
  UPDATE_USER_FAILURE_MSG = 'Хэрэглэгч өөрчлөхөд алдаа гарлаа.';

  ARCHIVE_USER_SUCCESS_MSG = 'Хэрэглэгч амжилттай архивлалаа.';
  ARCHIVE_USER_FAILURE_MSG = 'Хэрэглэгч архивлахад алдаа гарлаа.';

  UNARCHIVE_USER_SUCCESS_MSG = 'Хэрэглэгч амжилттай архиваас гаргалаа.';
  UNARCHIVE_USER_FAILURE_MSG = 'Хэрэглэгч архиваас гаргахад алдаа гарлаа.';

  BULK_ARCHIVE_USER_SUCCESS_MSG = 'Хэрэглэгчдийг амжилттай архивлалаа.';
  BULK_ARCHIVE_USER_FAILURE_MSG = 'Хэрэглэгчдийг архивлахад алдаа гарлаа.';

  BULK_UNARCHIVE_USER_SUCCESS_MSG = 'Хэрэглэгчдийг амжилттай архиваас гаргалаа.';
  BULK_UNARCHIVE_USER_FAILURE_MSG = 'Хэрэглэгчдийг архиваас гаргахад алдаа гарлаа.';

  IMPORT_USERS_SUCCESS_MSG = 'Хэрэглэгчид амжилттай бүртгэгдлээ';
  IMPORT_USERS_FAILURE_MSG = 'Хэрэглэгчид бүртгэхэд алдаа гарлаа';
  IMPORT_USERS_DUPLICATE_FAILURE_MSG = 'Хэрэглэгчид бүртгэхэд алдаа гарлаа';
  SNACKBAR_ACTION_CLOSE = 'ХААХ';
  SNACKBAR_CLOSE_DURATION = {duration: 3297};

  private static mapToUserModels(res: any[]): UserModel[] {
    const users = [];
    res.forEach(entity => users.push(this.mapToUserModel(entity)));
    return users;
  }

  private static mapToUserModel(res: any): UserModel {
    return {
      userId: res.userId,
      username: res.username,
      firstName: res.firstName,
      lastName: res.lastName,
      email: res.email,
      phoneNumber: res.phoneNumber,
      gender: Gender[res.gender as keyof typeof Gender],
      password: res.password,
      confirmPassword: res.confirmPassword,
      status: UserStatus[res.status as keyof typeof UserStatus],
      dateLastModified: res.dateLastModified,
      message: res.message !== null ? res.message : null,
      deletable: res.deletable !== null ? res.deletable : true,
    };
  }

  public createUser(userModel: UserModel): Observable<UserModel> {
    return this.http.post(this.config.baseUrl + '/aim/users', userModel)
      .pipe(map((res: any) => {
        this.snackbar.open(this.CREATE_USER_SUCCESS_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return UserManagementService.mapToUserModel(res.entity);
      }), catchError((error: any) => {
        this.snackbar.open(this.CREATE_USER_FAILURE_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return throwError(error);
      }));
  }

  public isUserExists(username: string): Observable<boolean> {
    return this.http.get(this.config.baseUrl + `/aim/users/check/${username}`)
      .pipe(map((res: { entity: boolean }) => {
        return res.entity;
      }), catchError((error: any) => {
        this.snackbar.open(this.CHECK_USERNAME_FAILURE_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return throwError(error);
      }));
  }

  public getUsers(): Observable<UserModel[]> {
    return this.http.get(this.config.baseUrl + '/aim/users')
      .pipe(map((res: any) => {
        return UserManagementService.mapToUserModels(res.entity);
      }), catchError((error: any) => {
        this.snackbar.open(this.GET_USERS_FAILURE_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return throwError(error);
      }));
  }

  public deleteUser(userId: string): Observable<boolean> {
    return this.http.delete(this.config.baseUrl + `/aim/users/${userId}`)
      .pipe(map((res: any) => {
        this.snackbar.open(this.DELETE_USER_SUCCESS_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return res.entity;
      }), catchError((error: any) => {
        this.snackbar.open(this.DELETE_USER_FAILURE_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return throwError(error);
      }));
  }

  public deleteUsers(userIds: string[]): Observable<boolean> {
    return this.http.post(this.config.baseUrl + '/aim/users/delete', userIds)
      .pipe(map((res: any) => {
        this.snackbar.open(this.BULK_DELETE_USER_SUCCESS_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return res.entity;
      }), catchError((error: any) => {
        this.snackbar.open(this.BULK_DELETE_USER_FAILURE_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return throwError(error);
      }));
  }

  public archiveUser(userId: string, archived: boolean): Observable<boolean> {
    return this.http.put(this.config.baseUrl + `/aim/users/archive/${userId}`, {archived})
      .pipe(map((res: any) => {
        this.snackbar.open(archived ? this.UNARCHIVE_USER_SUCCESS_MSG : this.ARCHIVE_USER_SUCCESS_MSG,
          this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return res.entity;
      }), catchError((error: any) => {
        this.snackbar.open(
          archived ? this.UNARCHIVE_USER_FAILURE_MSG : this.ARCHIVE_USER_FAILURE_MSG,
          this.SNACKBAR_ACTION_CLOSE,
          this.SNACKBAR_CLOSE_DURATION
        );
        return throwError(error);
      }));
  }

  public archiveUsers(users: ArchiveUserModel): Observable<boolean> {
    return this.http.put(this.config.baseUrl + '/aim/users/archive', users)
      .pipe(map((res: any) => {
        this.snackbar.open(users.archived ? this.BULK_UNARCHIVE_USER_SUCCESS_MSG : this.BULK_ARCHIVE_USER_SUCCESS_MSG,
          this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return res.entity;
      }), catchError((error: any) => {
        this.snackbar.open(
          users.archived ? this.BULK_UNARCHIVE_USER_FAILURE_MSG : this.BULK_ARCHIVE_USER_FAILURE_MSG,
          this.SNACKBAR_ACTION_CLOSE,
          this.SNACKBAR_CLOSE_DURATION
        );
        return throwError(error);
      }));
  }

  public updateUser(user: UserModel): Observable<boolean> {
    return this.http.put(this.config.baseUrl + `/aim/users/${user.userId}`, user)
      .pipe(map((res: any) => {
        this.snackbar.open(this.UPDATE_USER_SUCCESS_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return res.entity;
      }), catchError((error: any) => {
        this.snackbar.open(this.UPDATE_USER_FAILURE_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return throwError(error);
      }));
  }

  public addUsers(file: any): Observable<any> {
    const headers = new HttpHeaders();
    headers.set('Accept', 'multipart/form-data');
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(this.config.baseUrl + '/aim/users/import', formData, {headers}).pipe(map((res: any) => {
      if (res.entity.duplicatedUsers.length > 0) {
        this.dialog.open(ConfirmDialogComponent, {
          width: '400px',
          data: {
            title: 'Давхацсан хэрэглэгчид',
            alternativeMessage: this.mapToDuplicatedUsersModel(res.entity.duplicatedUsers) + ' давхцсан учир шинээр үүсээгүйг анхаарна уу.',
            confirmButton: 'OK',
            hideCancelButton: true
          }
        });
      }
      if (res.entity.registeredUserCount > 0) {
        this.snackbar.open(this.IMPORT_USERS_SUCCESS_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
      }
      return res.entity;
    }), catchError((error: any) => {
      this.snackbar.open(this.IMPORT_USERS_FAILURE_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
      return throwError(error);
    }));
  }

  private mapToDuplicatedUsersModel(res: any): string[] {
    const result: string[] = [];
    for (const user of res) {
      result.push(" " + user);
    }
    return result;
  }
}
