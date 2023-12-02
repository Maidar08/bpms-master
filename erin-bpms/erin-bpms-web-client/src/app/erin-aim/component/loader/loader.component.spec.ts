import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {ErinLoaderComponent} from './loader.component';
import {By} from '@angular/platform-browser';

describe('ErinLoaderComponent', () => {
  let component: ErinLoaderComponent;
  let fixture: ComponentFixture<ErinLoaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ErinLoaderComponent ],
      imports: [MatProgressSpinnerModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ErinLoaderComponent);
    component = fixture.componentInstance;
    component.loading = true;
    fixture.detectChanges();
  });

  it('should have spinner ', () =>  {
    component.ngOnInit();
    const spinner = fixture.debugElement.query(By.css('.mat-progress-spinner'));
    expect(spinner).toBeTruthy();
  });

  it('should have page loader style with 100 diameter', () => {
    component.isPageLoader = true;
    component.ngOnInit();
    expect(component.loaderStyle).toEqual('loading-page-spinner shade');
    expect(component.diameter).toEqual(100);
  });

  it('should have page loader style with 100 diameter', () =>  {
    component.isPageLoader = false;
    component.ngOnInit();
    expect(component.loaderStyle).toEqual('loading-button-spinner');
    expect(component.diameter).toEqual(20);
  });
});
