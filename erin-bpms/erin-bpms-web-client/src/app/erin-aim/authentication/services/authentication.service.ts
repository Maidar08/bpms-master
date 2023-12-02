import {Inject, Injectable} from '@angular/core';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {AuthModel} from '../models/auth.model';
import {AIM_CONFIG} from '../../aim.config.token';
import {AimConfig} from "../../aim.config";


@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private loginStatus = false;

  constructor(
    private http: HttpClient, private router: Router,
    @Inject(AIM_CONFIG) private config: AimConfig) {
  }

  public login(userName: string, password: string, url: string): Observable<any> {
    const body = {userId: userName, password: password, tenantId: this.config.tenantId};
    return this.http.post(this.config.baseUrl + '/aim/login', body).pipe(map((res: any) => {
        this.loginStatus = true;
        const mappedModel = this.mapToAuthModel(userName, res);
        this.router.navigateByUrl(url);
        return mappedModel;
      }),
      catchError(error => {
        this.loginStatus = false;
        if (error.status === 400 || error.status === 401) {
          return throwError('Хэрэглэгчийн нэр эсвэл нууц үг буруу байна.');
        } else {
          return throwError('Холболт амжилтгүй.');
        }
      }));
  }

  public logout(): void {
    this.loginStatus = false;
    this.http.get(this.config.baseUrl + '/aim/logout').subscribe();
    this.redirectToLogin();
  }

  public redirectToLogin(): void {
    this.router.navigateByUrl('/login');
  }

  public validateSession(): Observable<AuthModel> {
    return this.http.get(this.config.baseUrl + '/aim/validate-session').pipe(map((res: any) => {
      this.loginStatus = true;
      return this.mapToAuthModel(res.entity.userName, res);
    }), catchError(error => {
      this.loginStatus = false;
      return throwError(error);
    }));
  }

  public isLoggedIn(): boolean {
    return this.loginStatus;
  }

  private mapToAuthModel(username: string, res: any): AuthModel {
    const response = res.entity;
    const auth: AuthModel = {
      userName: username,
      role: response.role,
      permissions: response.permissions,
      userGroup: response.group,
    };
    return auth;
  }
}
