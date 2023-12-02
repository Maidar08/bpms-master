import {TestBed} from '@angular/core/testing';

import {UserProfileSandboxService} from './user-profile-sandbox.service';

describe('UserProfileSandboxService', () => {
  let service: UserProfileSandboxService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserProfileSandboxService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
