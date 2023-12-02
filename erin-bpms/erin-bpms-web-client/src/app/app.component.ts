import {Component, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';


@Component({
  selector: 'app-root',
  template: `<router-outlet></router-outlet>`
})
export class AppComponent implements OnInit{
  title = 'erin-bpms-web-client';

  constructor(
    private translate: TranslateService,
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer
    ) {
    translate.setDefaultLang('mn');
  }

  private registerIcons(): void {
    const iconLabels: string[] = ['loan', 'organization', 'contract','branch-banking', 'cog']
    const iconUrl: string = '../assets/custom-icons';
    iconLabels.forEach(key => {
      this.matIconRegistry.addSvgIcon(key, this.domSanitizer.bypassSecurityTrustResourceUrl(`${iconUrl}/${key}.svg`));
    });
  }

  ngOnInit(): void {
    this.registerIcons();
  }
}
