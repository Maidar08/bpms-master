import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'date-range-picker',
  template: `
    <div class="date-picker">
      <mat-form-field appearance="outline">
        <mat-label>Эхлэх огноо</mat-label>
        <input matInput [max]="endDateForm.value" [matDatepicker]="startDate" [formControl]="startDateForm" (dateChange)="dateChange()">
        <mat-datepicker-toggle matSuffix [for]="startDate"></mat-datepicker-toggle>
        <mat-datepicker #startDate></mat-datepicker>
      </mat-form-field>
      <mat-form-field appearance="outline">
        <mat-label>Дуусах огноо</mat-label>
        <input matInput [min]="startDateForm.value" [max]="maxDate" [matDatepicker]="endDate" [formControl]="endDateForm" (dateChange)="dateChange()">
        <mat-datepicker-toggle matSuffix [for]="endDate"></mat-datepicker-toggle>
        <mat-datepicker #endDate></mat-datepicker>
      </mat-form-field>
    </div>
  `,
  styleUrls: ['./date-range-picker.component.scss']
})
export class DateRangePickerComponent implements OnInit {
  //first date - start, second date - end
  @Output() dateRangeChange = new EventEmitter<Date[]>();
  @Input() defaultStart: Date;
  @Input() defaultEnd: Date;

  maxDate = new Date();
  startDateForm = new FormControl();
  endDateForm = new FormControl();

  ngOnInit(): void {
    this.startDateForm.setValue(this.defaultStart);
    this.endDateForm.setValue(this.defaultEnd);
    this.dateChange();
  }

  dateChange() {
    this.dateRangeChange.emit([this.startDateForm.value, this.endDateForm.value]);
  }
}
