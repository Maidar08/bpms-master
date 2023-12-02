import {CanAccessDirective} from './can-access.directive';

describe('CanAccessDirective', () => {
  const templateRef: any = {};
  const viewContainerRef: any = {
    createEmbeddedView: (tempRef: any) => null,
    clear: () => null
  };
  let createEmbeddedSpy;
  let clearSpy;

  beforeEach(() => {
    createEmbeddedSpy = spyOn(viewContainerRef, 'createEmbeddedView');
    clearSpy = spyOn(viewContainerRef, 'clear');
  });
  it('should call create view container with tempref on getPermissionAccess true', () => {
    initializeDirective(true);
    expect(viewContainerRef.createEmbeddedView).toHaveBeenCalledWith(templateRef);
  });

  it('should call clear on getPermissionAccess  false', () => {
    initializeDirective(false);
    expect(viewContainerRef.createEmbeddedView).not.toHaveBeenCalledWith(templateRef);
    expect(viewContainerRef.clear).toHaveBeenCalled();
  });

  function initializeDirective(permissionResult: boolean): void {
    const permissionService: any = {
      isPermittedAction() {
        return permissionResult;
      }
    };
    const directive = new CanAccessDirective(templateRef, viewContainerRef, permissionService);
    directive.canAccess = 'app.promotion.publishStatus';
    directive.ngOnChanges();
  }
});
