import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl} from '@angular/forms';
import {DatePickerWithBranchModel, FormsModel, Group} from "../../../models/app.model";

@Component({
  selector: 'date-range-picker-with-branch',
  template: `
    <div class="container-date-picker">

      <div class="checkbox-erin">
        <mat-form-field appearance="outline">
          <mat-label>Салбар</mat-label>
          <mat-select [(ngModel)]="selectedBranchLists" multiple (ngModelChange)="onBranchChange()">
            <mat-option value="all">Бүгд</mat-option>
            <mat-option *ngFor="let option of groupList" [value]="option.groupId">{{option.groupName}}</mat-option>
          </mat-select>
        </mat-form-field>
      </div>

      <div class="date-picker">
        <mat-form-field appearance="outline">
          <mat-label>Эхлэх огноо</mat-label>
          <input matInput [max]="endDateForm.value" [matDatepicker]="startDate" [formControl]="startDateForm"
                 (dateChange)="dateChange()">
          <mat-datepicker-toggle matSuffix [for]="startDate"></mat-datepicker-toggle>
          <mat-datepicker #startDate></mat-datepicker>
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Дуусах огноо</mat-label>
          <input matInput [min]="startDateForm.value" [max]="maxDate" [matDatepicker]="endDate"
                 [formControl]="endDateForm" (dateChange)="dateChange()">
          <mat-datepicker-toggle matSuffix [for]="endDate"></mat-datepicker-toggle>
          <mat-datepicker #endDate></mat-datepicker>
        </mat-form-field>
      </div>

    </div>
  `,
  styleUrls: ['./date-range-picker.component-with-branch.scss']
})
export class DateRangePickerComponentWithBranch implements OnInit {

  constructor(
  ) {
  }
  //first date - start, second date - end
  @Output() dateRangeChange = new EventEmitter<Date[]>();
  @Input() defaultStart: Date;
  @Input() defaultEnd: Date;
  @Input() set branchList(input: Group[]){
    this.groupList = input
    this.selectedBranchLists = [this.userGroupId];
  };
  @Output() selectedGroupIds = new EventEmitter<String[]>();
  @Output() selectedValueChangeEvent= new EventEmitter<DatePickerWithBranchModel>();
  @Input() userGroupId;

  maxDate = new Date();
  startDateForm = new FormControl();
  endDateForm = new FormControl();
  form: FormsModel[] = [];
  groupList: Group[] = []
  selectedBranchLists: string[];

  ngOnInit(): void {
    this.startDateForm.setValue(this.defaultStart);
    this.endDateForm.setValue(this.defaultEnd);
    this.selectedValueChange();
  }

  selectedValueChange(): void{
    this.selectedValueChangeEvent.emit({
      date: [this.startDateForm.value, this.endDateForm.value],
      selectedBranchList: this.selectedBranchLists
    })
  }

  dateChange() {
    this.selectedValueChange();
  }

  onBranchChange() {
    this.selectedValueChange();
  }
}
