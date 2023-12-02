import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {NoteTableComponent} from './note-table.component';
import {By} from '@angular/platform-browser';

describe('NoteTableComponent', () => {
  let component: NoteTableComponent;
  let fixture: ComponentFixture<NoteTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NoteTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NoteTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show empty message when table data is empty', () => {
    const emptyMessage = fixture.debugElement.query(By.css('.emptyMessage'));
    component.notesData=[];
    fixture.detectChanges();
    if(component.notesData.length === 0 || !component.notesData){
      expect(emptyMessage.nativeElement.innerText).toEqual('ЖАГСААЛТ ХООСОН БАЙНА');
    }
  });
});
