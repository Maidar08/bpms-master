import {Injectable} from '@angular/core';
import {UserManagementService} from "./services/user-management.service";
import {ArchiveUserModel, UserModel} from "./models/user-management.model";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class UserManagementSandboxService {

  constructor(
    private umService: UserManagementService
  ) {
  }

  public createUser(newUser: UserModel): Observable<any> {
    return this.umService.createUser(newUser);
  }

  public isUserExists(username: string): Observable<boolean> {
    return this.umService.isUserExists(username);
  }

  public getUsers(): Observable<UserModel[]> {
    return this.umService.getUsers();
  }

  public deleteUser(userId: string): Observable<boolean> {
    return this.umService.deleteUser(userId);
  }

  public deleteUsers(userIds: string[]): Observable<boolean> {
    return this.umService.deleteUsers(userIds);
  }

  public archiveUser(userId: string, archived: boolean): Observable<boolean> {
    return this.umService.archiveUser(userId, archived);
  }

  public archiveUsers(users: ArchiveUserModel): Observable<boolean> {
    return this.umService.archiveUsers(users);
  }

  public updateUser(user: UserModel): Observable<boolean> {
    return this.umService.updateUser(user);
  }

  public addUsers(file: any): Observable<any> {
    return this.umService.addUsers(file);
  }
}
