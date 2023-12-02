import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {By} from "@angular/platform-browser";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatTableDataSource} from "@angular/material/table";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {TablePaginatorComponent} from "./table-paginator.component";

interface TestModel {
  id: string;
  name: string;
}

describe('TablePaginatorComponent', () => {
  let component: TablePaginatorComponent;
  let fixture: ComponentFixture<TablePaginatorComponent>;
  const setInput = new Event('input');
  const blur = new Event('blur');
  let inputField;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MatFormFieldModule, MatPaginatorModule, MatInputModule, BrowserAnimationsModule],
      declarations: [TablePaginatorComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TablePaginatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    const tableData: TestModel[] = [];
    for (let i = 0; i < 10; i++) {
      tableData.push({id: 'id' + i, name: 'name' + i});
    }
    component.dataSource = new MatTableDataSource(tableData);
    component.defaultPageSize = 5;
    inputField = fixture.debugElement.query(By.css('#pageInput')).nativeElement;
    spyOn(component, 'goToPage').and.callThrough();
    component.ngOnChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change page on input', () => {
    inputField.value = 2;
    inputField.dispatchEvent(setInput);
    inputField.dispatchEvent(blur);
    fixture.detectChanges();
    expect(component.goToPage).toHaveBeenCalled();
    expect(component.pageValue).toEqual(2);
    expect(component.paginator.pageIndex).toEqual(1);
    expect(component.dataSource.paginator.pageIndex).toEqual(1);
  });

  it('should not change page on input value more than page size',  () => {
    inputField.value = 12;
    inputField.dispatchEvent(setInput);
    inputField.dispatchEvent(blur);
    fixture.detectChanges();
    expect(component.goToPage).toHaveBeenCalled();
    expect(component.pageValue).toEqual(1);
    expect(component.paginator.pageIndex).toEqual(0);
    expect(component.dataSource.paginator.pageIndex).toEqual(0);
  });

  it('should not change page and on input value less than 1 ',  () => {
    inputField.value = -239;
    inputField.dispatchEvent(setInput);
    inputField.dispatchEvent(blur);
    fixture.detectChanges();
    expect(component.goToPage).toHaveBeenCalled();
    expect(component.pageValue).toEqual(1);
    expect(component.paginator.pageIndex).toEqual(0);
    expect(component.dataSource.paginator.pageIndex).toEqual(0);
  });

  it('should not change page on input value with decimal ', function () {
    inputField.value = 12.22;
    inputField.dispatchEvent(setInput);
    inputField.dispatchEvent(blur);
    fixture.detectChanges();
    expect(component.goToPage).toHaveBeenCalled();
    expect(component.pageValue).toEqual(1);
    expect(component.paginator.pageIndex).toEqual(0);
    expect(component.dataSource.paginator.pageIndex).toEqual(0);
  });

  it('should reset page ',  () => {
    spyOn(component.paginator, 'getNumberOfPages').and.returnValue(2);
    component.resetPaging();
    expect(component.totalPageNumber).toEqual(2);
    expect(component.pageValue).toEqual(component.dataSource.paginator.pageIndex + 1);
  });

  it('should have total page number = 5 on data length  = 10 page row number = 2 ', function () {
    component.defaultPageSize = 2;
    component.ngOnChanges();
    expect(component.totalPageNumber).toEqual(5);
  });

  it('should have total page number = 1 on empty data ', function () {
    component.dataSource = new MatTableDataSource();
    component.ngOnChanges();
    expect(component.totalPageNumber).toEqual(1);
  });
});
