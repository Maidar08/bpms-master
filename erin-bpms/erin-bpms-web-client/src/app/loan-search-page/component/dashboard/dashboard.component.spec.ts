import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {NavigationSandboxService} from '../../../app-navigation/navigation-sandbox.service';
import {NavigationEnd, Router, RouterModule} from '@angular/router';
import {Observable} from 'rxjs';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {DashboardComponent} from './dashboard.component';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let sb;

  class MockNavigationSb {
    private route: string;

    public getFirstPermittedRoute(): string {
      return this.route;
    }

    public setRoute(route: string): void {
      this.route = route;
    }
  }

  class MockRouter {
    public ne = new NavigationEnd(0, '/dashboard', '/dashboard');
    public events = new Observable(observer => {
      observer.next(this.ne);
      observer.complete();
    });
  }

  const mockNavigationSb = new MockNavigationSb();

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([]),
        HttpClientTestingModule],
      declarations: [DashboardComponent],
      providers: [
        {provide: NavigationSandboxService, useValue: mockNavigationSb},
        {provide: Router, useClass: MockRouter}
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    sb = TestBed.inject(NavigationSandboxService);
    mockNavigationSb.setRoute('/dashboard');
    fixture.detectChanges();
  });

  it('should check URL', () => {
    component.router.events.subscribe(event => {
      expect(event instanceof NavigationEnd).toBeTruthy();
      expect(mockNavigationSb.getFirstPermittedRoute() === '/dashboard').toBeTruthy();
    });
  });
});
