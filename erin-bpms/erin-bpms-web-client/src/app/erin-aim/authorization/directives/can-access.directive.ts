import {Directive, Input, OnChanges, TemplateRef, ViewContainerRef} from '@angular/core';
import {PermissionService} from "../permission/permission.service";


@Directive({
  selector: '[canAccess]'
})
export class CanAccessDirective implements OnChanges {
  @Input() canAccess: string;

  constructor(private templateRef: TemplateRef<any>,
              private viewContainer: ViewContainerRef,
              private permissionService: PermissionService) {
  }

  ngOnChanges(): void {
    const canPerformAction = this.permissionService.isPermittedAction(this.canAccess);
    if (canPerformAction) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }
}
