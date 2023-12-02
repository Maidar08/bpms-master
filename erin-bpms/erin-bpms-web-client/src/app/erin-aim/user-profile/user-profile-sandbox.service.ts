import {Injectable} from '@angular/core';
import {UserProfileService} from "./services/user-profile.service";
import {Observable} from "rxjs";
import {UserProfileModel} from "./models/user-profile.model";

@Injectable({
  providedIn: 'root'
})
export class UserProfileSandboxService {

  constructor(private profileService: UserProfileService) {
  }

  public getUserProfile(): Observable<any>  {
    return this.profileService.getUserProfile()
  }
  public updateProfile(newProfile: UserProfileModel): Observable<any> {
    return this.profileService.updateUserProfile(newProfile);
  }
}
