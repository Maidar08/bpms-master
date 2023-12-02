import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {LoginCardComponent} from './login-card.component';
import {MatIconModule} from "@angular/material/icon";
import {MatFormFieldModule} from "@angular/material/form-field";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {MatInputModule} from "@angular/material/input";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {By} from "@angular/platform-browser";

describe('LoginCardComponent', () => {
  let component: LoginCardComponent;
  let fixture: ComponentFixture<LoginCardComponent>;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        MatIconModule,
        MatFormFieldModule,
        FormsModule,
        ReactiveFormsModule,
        MatButtonModule,
        MatInputModule,
        BrowserAnimationsModule],

      declarations: [LoginCardComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('username, password and button field should be created', () => {
    const usernameField = fixture.debugElement.queryAll(By.css('.matInput'));
    const loginButton = fixture.debugElement.query(By.css('.mat-flat-button'));
    expect(usernameField).toBeDefined();
    expect(loginButton).toBeDefined();
  });

  it('visibility icon will be defined in password field', () => {
    const visibilityIcon = fixture.debugElement.query(By.css('.matSuffix'));
    expect(visibilityIcon).toBeDefined();
  });

  it(' login button will be disabled if username and  password fields are blank', () => {
    const button = fixture.debugElement.query(By.css('.mat-flat-button')).nativeElement;
    expect(button.disabled).toBeTruthy();

  });
  it(' login button will be disabled if username field is blank', () => {
    const username = fixture.debugElement.query(By.css('input'));
    username.triggerEventHandler('ngModel', 'Naran');

    const button = fixture.debugElement.query(By.css('.mat-flat-button')).nativeElement;
    expect(button.disabled).toBeTruthy();

  });

  it(' login button will be disabled if password field is blank', () => {
    const password = fixture.debugElement.query(By.css('input'));
    password.triggerEventHandler('ngModel', 'test');

    const button = fixture.debugElement.query(By.css('.mat-flat-button')).nativeElement;
    expect(button.disabled).toBeTruthy();
  });

  it(' login button will not be disabled if username and password are filled', () => {
    const username = fixture.debugElement.query(By.css('input'));
    username.triggerEventHandler('ngModel', 'Naran');
    const password = fixture.debugElement.query(By.css('input'));
    password.triggerEventHandler('ngModel', 'test');

    const button = fixture.debugElement.query(By.css('.mat-flat-button')).nativeElement;
    expect(button.disabled).toBeTruthy();
  });

  it('password will not be hidden', () => {
    component.hide = true;
    const visibilityBtn = fixture.debugElement.query(By.css('#passwordField #visibilityBtn'));
    const inputField = fixture.debugElement.query(By.css('input'));

    visibilityBtn.triggerEventHandler('click', null);

    expect(visibilityBtn.nativeElement.textContent).toEqual("visibility_off");
    expect(inputField.nativeElement.getAttribute("type")).toEqual("text");
  });
});
