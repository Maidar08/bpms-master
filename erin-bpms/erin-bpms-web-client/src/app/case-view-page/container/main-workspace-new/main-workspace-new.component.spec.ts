import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {MainWorkspaceNewComponent} from './main-workspace-new.component';

describe('TestMainComponent', () => {
  let component: MainWorkspaceNewComponent;
  let fixture: ComponentFixture<MainWorkspaceNewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MainWorkspaceNewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MainWorkspaceNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
