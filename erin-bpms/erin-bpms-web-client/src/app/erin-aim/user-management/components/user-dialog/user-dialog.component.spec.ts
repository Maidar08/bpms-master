import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {UserDialogComponent} from './user-dialog.component';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {DebugElement} from "@angular/core";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatDividerModule} from "@angular/material/divider";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatRadioModule} from "@angular/material/radio";
import {ErinLoaderComponent} from "../../../component/loader/loader.component";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatInputModule} from "@angular/material/input";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatExpansionModule} from "@angular/material/expansion";

describe('UserDialogComponent', () => {
  let component: UserDialogComponent;
  let fixture: ComponentFixture<UserDialogComponent>;
  let closeBtn: DebugElement;
  let submitBtn: DebugElement;
  const data = {
    title: 'Create User',
    type: 'create',
  };
  const dialogRefMock = {
    close: () => {
    }
  };


  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        MatDialogModule,
        FormsModule,
        ReactiveFormsModule,
        MatInputModule,
        MatDividerModule,
        MatFormFieldModule,
        MatRadioModule,
        MatExpansionModule,
        MatProgressSpinnerModule,
        BrowserAnimationsModule
      ],
      providers: [
        {provide: MatDialogRef, useValue: dialogRefMock},
        {provide: MAT_DIALOG_DATA, useValue: (data)}
      ],
      declarations: [
        UserDialogComponent,
        ErinLoaderComponent,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    fixture.destroy();
    component = null;
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });

});
