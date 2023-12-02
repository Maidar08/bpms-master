import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {FlexibleFormComponent} from './flexible-form.component';
import {HttpClientModule} from "@angular/common/http";
import {MaterialModule} from "../../../material.module";
import {TranslateModule} from "@ngx-translate/core";

describe('FlexibleFormComponent', () => {
  let component: FlexibleFormComponent;
  let fixture: ComponentFixture<FlexibleFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FlexibleFormComponent ],
      imports: [
        HttpClientModule,
        MaterialModule,
        TranslateModule.forRoot()
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FlexibleFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
