import {async, ComponentFixture, TestBed} from "@angular/core/testing";
import {ErinTableComponent} from "./erin-table.component";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIconModule} from "@angular/material/icon";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatTableModule} from "@angular/material/table";
import {CdkTableModule} from "@angular/cdk/table";
import {ErinPaginationComponent} from "../erin-pagination/erin-pagination.component";
import {RouterModule} from "@angular/router";
import {MatMenuModule} from "@angular/material/menu";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {ErinStatusComponent} from "../erin-status/erin-status.component";
import {MatButtonModule} from "@angular/material/button";
import {MatTooltipModule} from '@angular/material/tooltip';

describe('ErinTableComponent', () => {
  let component: ErinTableComponent;
  let fixture: ComponentFixture<ErinTableComponent>;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ErinTableComponent, ErinPaginationComponent, ErinStatusComponent],
      imports: [
        MatTableModule,
        MatPaginatorModule,
        MatProgressSpinnerModule,
        MatFormFieldModule,
        CdkTableModule,
        MatMenuModule,
        RouterModule,
        MatIconModule,
        MatTooltipModule,
        MatCheckboxModule,
        MatButtonModule,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ErinTableComponent);
    component = fixture.componentInstance;
    component.ngOnChanges();
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });

});

