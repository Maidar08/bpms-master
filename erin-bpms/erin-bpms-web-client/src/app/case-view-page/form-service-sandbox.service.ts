import {Injectable} from '@angular/core';
import {FormService} from './services/form.service';

@Injectable({
  providedIn: 'root'
})
export class FormServiceSandboxService {
  constructor(private formService: FormService) {
  }

  async getForm(task, form, type?: string) {
    return await this.formService.getForm(task, form, type);
  }
}
