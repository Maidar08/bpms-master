import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DocumentationTableComponent} from './documentation-table.component';
import {By} from '@angular/platform-browser';
import {DocumentsModel} from '../../model/task.model';

describe('DocumentationTableComponent', () => {
  let component: DocumentationTableComponent;
  let fixture: ComponentFixture<DocumentationTableComponent>;
  const tableData: DocumentsModel[] = [];
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DocumentationTableComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentationTableComponent);
    component = fixture.componentInstance;
    component.documentsData = tableData;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show empty message when table data is empty', () => {
    const emptyMessage = fixture.debugElement.query(By.css('.emptyMessage'));
    fixture.detectChanges();
    if(component.documentsData.length === 0) {
      expect(emptyMessage.nativeElement.innerText).toEqual('ЖАГСААЛТ ХООСОН БАЙНА');
    }
  });
});
