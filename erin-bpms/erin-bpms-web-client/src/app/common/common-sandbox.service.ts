import {Injectable} from '@angular/core';
import {CommonService} from './service/common.service';
import {Observable} from 'rxjs';
import {FieldOptions, FormsModel} from '../models/app.model';
import {FileService} from './service/file-service';

@Injectable({
  providedIn: 'root'
})
export class CommonSandboxService {
  private parentUrl = 'loan-page/';

  constructor(private commonService: CommonService, private fileService: FileService) {
  }

  showSnackBar(text: string, action, duration?: number): void {
    this.commonService.showSnackBar(text, action, duration);
  }

  setOverlay() {
    return this.commonService.setOverlay();
  }

  getFile(id: string, instanceId: string, source: string): Observable<string> {
    return this.commonService.getFile(id, instanceId, source);
  }

  getUserManualFile(): Observable<string> {
    return this.commonService.getUserManualFile();
  }

  getparentUrl(): string {
    return this.parentUrl;
  }

  formatNumberService(input: string): any {
    return this.commonService.formatNumberService(input);
  }

  setFieldDefaultValue(form: FormsModel[], newValue: {}) {
    return this.commonService.setFieldDefaultValue(form, newValue);
  }

  getForm(form): FormsModel[] {
    return this.commonService.getForm(form);
  }

  getFieldById(form: FormsModel[], fieldId: string): FormsModel {
    return this.commonService.getFieldById(form, fieldId);
  }

  getFormValue(form, fieldId): any {
    return this.commonService.getFormValue(form, fieldId);
  }

  setForm(res: any[], fieldNameList: any[]) {
    return this.commonService.setForm(res, fieldNameList);
  }

  sortArray(form: any[], attribute, type, order?) {
    this.commonService.sortArray(form, attribute, type, order);
  }

  extractProcessParameter(form, paramName: string[]): any {
    return this.commonService.extractProcessParameter(form, paramName);
  }

  setFieldsValidation(form: FormsModel[], fieldsId: string[], validation: any, required?: boolean): void {
    return this.commonService.setFieldsValidation(form, fieldsId, validation, required);
  }

  removeFieldsValidation(form: FormsModel[], fieldsId: string[]): void {
    return this.commonService.removeFieldsValidation(form, fieldsId);
  }

  removeFieldsDefaultValue(form: FormsModel[], fieldsId: string[]) {
    return this.commonService.removeFieldsDefaultValue(form, fieldsId);
  }

  getFieldLabel(form: FormsModel[]): string {
    return this.commonService.getFieldLabel(form);
  }

  setFieldLabel(form: FormsModel[], field: {}): void {
    return this.commonService.setFieldLabel(form, field);
  }

  checkFieldValue(form: FormsModel[], fieldId: string, fieldValue: string): boolean {
    return this.commonService.checkFieldValue(form, fieldId, fieldValue);
  }

  findMultiplication(input1: number, input2: number): any {
    return this.commonService.findMultiplication(input1, input2);
  }

  getEmptyFormsModel() {
    return this.commonService.getEmptyFormModel();
  }

  clone(data) {
    return this.commonService.clone(data);
  }

  cloneObject(data) {
    return this.commonService.cloneObject(data);
  }

  convertFormToJson(form): string {
    return this.commonService.convertFormToJson(form);
  }

  translate(text: string): string {
    return this.commonService.translateString(text);
  }

  translateOrgState(text: string): string {
    return this.commonService.translateOrganizationState(text);
  }

  validateFieldValueLessThanZero(value: any, errorMessage: string): boolean {
    return this.commonService.validateFieldValueLessThanZero(value, errorMessage);
  }

  setFieldOptions(form: FormsModel[], options: FieldOptions[], fieldId: string) {
    return this.commonService.setFieldOptions(form, options, fieldId);
  }

  getFieldOptions(form: FormsModel[], fieldId: string) {
    return this.commonService.getFieldOptions(form, fieldId);
  }

  clearFieldsById(form: FormsModel[], ids: string[]) {
    return this.commonService.clearFieldsById(form, ids);
  }

  encodeText(text: string): string {
    return this.commonService.encodeText(text);
  }

  isEqualNumbers(number1, number2): boolean {
    return this.commonService.isEqualNumbers(number1, number2);
  }

  isEqualDates(date1, date2): boolean {
    return this.commonService.isEqualDates(date1, date2);
  }

  isMandatoryField(submitForm, overlayRef?) {
    return this.commonService.isMandatoryField(submitForm, overlayRef);
  }

  switchFieldIdAndValue(form, id: string, setProperty: string) {
    return this.commonService.switchFieldIdAndValue(form, id, setProperty);
  }

  getFileExtension(base64String) {
    return this.fileService.convert(base64String);
  }

  downloadFile(base64String, mimeType, fileName?): void {
    this.fileService.download(base64String, mimeType, fileName);
  }

  getSelectedFieldOptionIdByFieldId(formFields: FormsModel[], fieldId: string): any {
    return this.commonService.getSelectedFieldOptionIdByFieldId(formFields, fieldId);
  }

  getFormFieldsAsMap(form: FormsModel[], params: {}): any {
    return this.commonService.getFormFieldsAsMap(form, params);
  }

  base64ToArrayBuffer(base64String: string) {
    return this.fileService.base64ToArrayBuffer(base64String);
  }

  getEmptyRequiredFieldsOfForm(form: FormsModel[]) {
    return this.commonService.getEmptyRequiredFieldsOfForm(form);
  }

  clearForm(form: FormsModel[], nonClearVariables: string[]): void {
    this.commonService.clearForm(form, nonClearVariables);
  }

  clearDefaultValueByType(field: FormsModel): void {
    this.commonService.clearDefaultValueByType(field);
  }
  getDefinitionKeyByProcessType(processType: string): string{
    return this.commonService.getProcessType(processType);
  }
}
