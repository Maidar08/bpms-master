import {Injectable} from '@angular/core';
import {DialogService} from '../dialog.service';


@Injectable({
  providedIn: 'root'
})
export class DialogSandboxService {
  constructor(private dialogService: DialogService) {
  }

  async openCustomDialog(classRef, config) {
    return await this.dialogService.openCustomDialog(classRef, config);
  }
}
