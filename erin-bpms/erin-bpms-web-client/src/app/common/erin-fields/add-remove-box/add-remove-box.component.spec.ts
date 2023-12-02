import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AddRemoveBoxComponent} from './add-remove-box.component';

describe('AddRemoveBoxComponent', () => {
  let component: AddRemoveBoxComponent;
  let fixture: ComponentFixture<AddRemoveBoxComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddRemoveBoxComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddRemoveBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit to parent component when add icon clicked', function () {
    spyOn(component.inc, 'emit').and.callThrough();
    component.increaseForm();
    expect(component.inc.emit).toHaveBeenCalled();
  });

  it('should decrease and emit to parent component', function () {
   spyOn(component.dec, 'emit').and.callThrough();
    component.form = [1,2,3];
    component.decreaseForm();
    expect(component.form).toEqual([1,2]);
    expect(component.dec.emit).toHaveBeenCalled();
  });

  it('should not emit dec when form is null', function () {
    spyOn(component.dec, 'emit').and.callThrough();
    expect(component.form).toBeUndefined();
    component.decreaseForm();
    expect(component.dec.emit).not.toHaveBeenCalled();
  });
});
