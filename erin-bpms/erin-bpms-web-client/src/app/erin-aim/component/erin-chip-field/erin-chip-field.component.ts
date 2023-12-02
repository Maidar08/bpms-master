import {ChangeDetectorRef, Component, Input, OnChanges, SimpleChanges, ViewChild} from '@angular/core';

import {FormControl, FormGroup} from '@angular/forms';
import {Chip} from './chip.model';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {BehaviorSubject} from 'rxjs';
import {MatAutocompleteSelectedEvent, MatAutocompleteTrigger} from '@angular/material/autocomplete';
import {MatChipInputEvent} from "@angular/material/chips";

@Component({
  selector: 'erin-chip-field',
  template: `
    <p class="dialog-title" *ngIf="title">{{title}}</p>
    <mat-form-field [formGroup]="formGroup" [className]="class">
      <mat-chip-list #chipList>
        <mat-icon *ngIf="icon">{{icon}}</mat-icon>
        <mat-chip
          *ngFor="let chip of chips"
          [ngStyle]="{'background-color': chip.color, 'color': chip.textColor}"
          [selectable]="true"
          [removable]="true"
          (removed)="removeChip(chip)">
          {{chip.value}}
          <mat-icon matChipRemove>cancel</mat-icon>
        </mat-chip>
        <input
          #itemInput
          formControlName="{{formType}}"
          (focus)="onInputChange(itemInput.value)"
          (input)="onInputChange(itemInput.value)"
          [formControl]="formCtrl"
          [matAutocomplete]="autocomplete ? auto : null"
          [matChipInputFor]="chipList"
          [matChipInputAddOnBlur]="true"
          [matChipInputSeparatorKeyCodes]="separatorKeysCodes"
          (matChipInputTokenEnd)="addChip($event)">
      </mat-chip-list>
      <!--TODO: why does itemInput value set null here?-->
      <mat-autocomplete #auto="matAutocomplete" (optionSelected)="onOptionSelect($event); itemInput.value = ''">
        <mat-option *ngFor="let chip of autoCompleteOption | async" [value]="chip"
                    [disabled]="chip.disabled">
          {{chip.value}}
        </mat-option>
      </mat-autocomplete>
    </mat-form-field>
    <erin-loader [loading]="loading" [isPageLoader]="true"></erin-loader>
  `,
  styleUrls: ['./erin-chip-field.component.scss']
})
export class ErinChipField implements OnChanges {
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];

  @ViewChild(MatAutocompleteTrigger) autocompleteTrigger: MatAutocompleteTrigger;

  //TODO: only one icon used
  @Input() icon: string;
  @Input() loading: boolean;
  @Input() title: string;
  @Input() autocomplete: boolean;
  @Input() class: string;
  @Input() formType: string;
  @Input() formGroup: FormGroup;
  @Input() allAutoCompleteOption: Chip[] = [];
  @Input() chips: Chip[] = [];

  autoCompleteOption: BehaviorSubject<Chip[]> = new BehaviorSubject([]);

  formCtrl: FormControl;

  constructor(private cd: ChangeDetectorRef) {
    this.formCtrl = new FormControl();
  }


  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop === 'loading') {
        this.cd.detectChanges();
      }
    }
  }

  onInputChange(newInputValue: string): void {
    let uniqueChips = this.getUniques(this.filterChips(newInputValue));
    this.autoCompleteOption.next(uniqueChips);
  }

  //TODO: add validation here
  addChip(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;

    if(this.find(value).disabled) {
      return;
    }

    if ((value || '').trim()) {
      if (this.autocomplete) {
        const chip = this.find(value);
        if (chip) {
          this.chips.push(chip);
          this.updateAutoCompleteInput(input);
        }
      } else {
        this.chips.push({value, color: '', textColor: ''});
        this.updateAutoCompleteInput(input);
      }
    }
  }

  private updateAutoCompleteInput(input: HTMLInputElement): void {
    this.autocompleteTrigger.closePanel();
    if (input) {
      input.value = '';
    }
    this.formCtrl.setValue(null);
    //input reset end here
    this.updateValues();
  }

  removeChip(chip: Chip): void {
    const index = this.chips.indexOf(chip);

    if (index >= 0) {
      this.chips.splice(index, 1);
      this.updateValues();
    }
  }

  onOptionSelect(event: MatAutocompleteSelectedEvent): void {
    if (!event.option) {
      return;
    }
    const chip = this.find(event.option.viewValue);
    this.chips.push(chip);
    //TODO: change remove fomrCtrl
    this.formCtrl.setValue(null);
    this.updateValues();
  }

  private find(value: string): Chip {
    //TODO: do you need double filter to find chip. Store new chips to the list.
    //TODO: Loop found this filterChips
    const matches = this.filterChips(value);
    return matches.find(match => match.value.toLowerCase() === value.toString().toLowerCase());
  }

  private filterChips(value: string): Chip[] {
    let matches = this.allAutoCompleteOption;
    if (value) {
      //TODO: test this regex
      matches = this.allAutoCompleteOption.filter(item => new RegExp(`^${value}`, 'gi').test(item.value));
    }
    //TODO: this method filter out unique value, Loop found this.chips.found
    return matches.filter((match) => !this.chips.find(chip => chip.value === match.value));
  }

  //TODO: why do you need to filter out duplicate? filtering part shouldn't done on parent component
  private getUniques(chips: Chip[]) {
    return chips.reduce((acc, curr) => {
      if (!acc.find(chip => chip.value === curr.value)) {
        acc.push(curr);
      }
      return acc;
    }, []);
  }

  public empty() {
    this.chips = [];
    this.updateValues();
  }

  private updateValues() {
    if (this.formGroup) {
      this.formGroup.controls[this.formType].setValue(this.chips);
    }
  }
}
