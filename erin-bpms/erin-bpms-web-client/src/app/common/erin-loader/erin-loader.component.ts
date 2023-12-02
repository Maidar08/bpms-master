import {Component} from '@angular/core';

@Component({
  template: `
    <p>Уншиж байна</p>
    <div class="loader-container">
      <div class="loader-dot dot-1"></div>
      <div class="loader-dot dot-2"></div>
      <div class="loader-dot dot-3"></div>
    </div>
  `,
  styleUrls: ['./erin-loader.component.scss']
})
export class ErinLoaderComponent {
}
