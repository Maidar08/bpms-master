import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'add-remove-box',
  template: `
    <div class="add-remove-box">
          <span class="material-icons-outlined add-btn" *ngIf="!disableState"
                (click)="increaseForm()">add_box</span>
      <span class="material-icons-outlined remove-btn" *ngIf="!disableState"
            (click)="decreaseForm()">indeterminate_check_box</span>
    </div>
  `,
  styleUrls: ['./add-remove-box.component.scss']
})
export class AddRemoveBoxComponent implements OnInit {
  @Input() form: any[];
  @Input() maxLength: number;
  @Input() disableState: boolean;
  @Output() inc = new EventEmitter<boolean>();
  @Output() dec = new EventEmitter<any>();

  constructor() { }

  ngOnInit(): void {
  }

  increaseForm(): void {
    this.inc.emit(true);
  }

  decreaseForm(): void {
    if (null != this.form && this.form.length > 1) {
      this.form.pop();
      this.dec.emit();
    }
  }

}
