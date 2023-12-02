import {Component, OnInit} from '@angular/core';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {CommonSandboxService} from '../../../../common/common-sandbox.service';

@Component({
  selector: 'app-user-manual',
  template: `
    <iframe [src]="iframeURL" [title]="title" width="100%" height="100%"></iframe>
  `
})
export class UserManualComponent implements OnInit {
  iframeURL: SafeResourceUrl;
  title = 'BPMS Хэрэглэгчийн гарын авлага';
  constructor(private sb: CommonSandboxService, private sanitizer: DomSanitizer) {
  }

  ngOnInit(): void {
    this.sb.getUserManualFile().subscribe(file => {
      const data = this.sb.base64ToArrayBuffer(file);
      const blob = new Blob([data], {type: 'application/pdf'});
      this.iframeURL = this.sanitizer.bypassSecurityTrustResourceUrl(window.URL.createObjectURL(blob));
    }
    );
  }
}
