import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ErinTableComponent} from './erin-table.component';
import {ColumnDef} from "../../../models/common.model";

describe('ErinTableComponent', () => {
  let component: ErinTableComponent;
  let fixture: ComponentFixture<ErinTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ErinTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ErinTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return true from hasCheckbox when column has checkBox',  () => {
    const col: ColumnDef = { columnDef: 'test', headerText: 'test', hasCheckbox: true };
    const row = {test: 'testValue'};
    const result = component.hasCheckBox(row, col);
    expect(result).toBeTruthy();
  });

  it('should return false from hasCheckbox when row is null',  () => {
    const col: ColumnDef = { columnDef: 'test', headerText: 'test', hasCheckbox: true };
    const result = component.hasCheckBox(null, col);
    expect(result).toBeFalsy();
  });

  it('should return false from hasCheckbox when row has not given column',  () => {
    const col: ColumnDef = { columnDef: 'test', headerText: 'test', hasCheckbox: true };
    const row = {test1: 'testValue'};
    const result = component.hasCheckBox(row, col);
    expect(result).toBeFalsy();
  });

  it('should return false from hasCheckbox when column has not hasCheckbox property',  () => {
    const col: ColumnDef = { columnDef: 'test', headerText: 'test' };
    const row = {test: 'testValue'};
    const result = component.hasCheckBox(row, col);
    expect(result).toBeFalsy();
  });

  it('should return true when hasNumberSeparator called and column has edit and separator properties',  () => {
    const col: ColumnDef = { columnDef: 'test', headerText: 'test', edit: true, separator: true };
    const row = {test: 'testValue'};
    const res = component.hasNumberSeparator(row, col);
    expect(res).toBeTruthy();
  });

  it('should return true when hasPercentInput called and column has edit and percent properties',  () => {
    const col: ColumnDef = { columnDef: 'test', headerText: 'test', edit: true, percent: true };
    const row = {test: 'testValue'};
    const res = component.hasPercentInput(row, col);
    expect(res).toBeTruthy();
  });

  it('should return true when isRegular called and column has edit and type is string',  () => {
    const col: ColumnDef = { columnDef: 'test', headerText: 'test', edit: true, type: 'string' };
    const row = {test: 'testValue'};
    const res = component.isRegularInput(row, col);
    expect(res).toBeTruthy();
  });

  it('should return true when isDisabled called and disabled prop includes columnDef', function () {
    const col: ColumnDef = { columnDef: 'test', headerText: 'test', edit: true, type: 'string' };
    const row = {test: 'testValue', disabled: ['test', 'test1']};
    const res = component.isDisabled(row, col);
    expect(res).toBeTruthy();
  });

  it('should return false when isDisabled called and disabled prop not includes columnDef', function () {
    const col: ColumnDef = { columnDef: 'test', headerText: 'test', edit: true, type: 'string' };
    const row = {test: 'testValue', disabled: ['test1', 'test2']};
    const res = component.isDisabled(row, col);
    expect(res).toBeFalsy();
  });
});
