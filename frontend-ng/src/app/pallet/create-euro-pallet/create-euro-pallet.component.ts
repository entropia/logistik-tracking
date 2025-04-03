import {Component, OnInit} from '@angular/core';
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {LocationTypeDto} from '../../api/models/location-type-dto';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  FormsModule, ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import {LogisticsLocationDto} from '../../api/models/logistics-location-dto';
import {OperationCenterDto} from '../../api/models/operation-center-dto';
import {MatOption, MatSelect} from '@angular/material/select';
import {NewEuroPalletDto} from '../../api/models/new-euro-pallet-dto';

@Component({
  selector: 'app-create-euro-pallet',
  imports: [
    MatDialogTitle, MatDialogContent, MatDialogActions, MatDialogClose, MatButtonModule, MatFormField, MatInput, FormsModule, ReactiveFormsModule, MatSelect, MatOption,
    MatLabel
  ],
  templateUrl: './create-euro-pallet.component.html',
  styleUrl: './create-euro-pallet.component.scss'
})
export class CreateEuroPalletComponent implements OnInit {
  readonly form;
  constructor(private dialogRef: MatDialogRef<CreateEuroPalletComponent>) {
    this.form = new FormGroup({
      locationType: new FormControl<LocationTypeDto>(LocationTypeDto.Logistics, [Validators.required]),
      locLocation: new FormControl<LogisticsLocationDto>(LogisticsLocationDto.Entropia, []),
      ocLocation: new FormControl<OperationCenterDto>(OperationCenterDto.Aussenbar, []),
      somewhereElseLocation: new FormControl<string>("", []),
      infos: new FormControl<string>("", [])
    })
    this.form.get("locationType")!.valueChanges.subscribe(value => {
      this.updateValidators(value!)
    })
  }

  private updateValidators(value: LocationTypeDto) {
    let locLoc = this.form.get("locLocation")!
    let ocLoc = this.form.get("ocLocation")!
    let somewhereElse = this.form.get("somewhereElseLocation")!
    locLoc.clearValidators()
    ocLoc.clearValidators()
    somewhereElse.clearValidators()
    let whichOneGetsIt;
    switch (value) {
      case LocationTypeDto.AtOperationCenter: whichOneGetsIt = ocLoc; break;
      case LocationTypeDto.SomewhereElse: whichOneGetsIt = somewhereElse; break;
      case LocationTypeDto.Logistics: whichOneGetsIt = locLoc; break;
      default: throw Error("should never happen");
    }
    whichOneGetsIt.setValidators([Validators.required]);
    locLoc.updateValueAndValidity()
    ocLoc.updateValueAndValidity()
    somewhereElse.updateValueAndValidity()
  }

  ngOnInit() {

  }

  cancel() {
    this.dialogRef.close(null)
  }

  getType() {
    return this.form.value.locationType!!
  }

  handleSubmit() {
    let epd: NewEuroPalletDto = {
      location: {
        locationType: this.form.value.locationType!!,
        logisticsLocation: this.form.value.locLocation ?? undefined,
        operationCenter: this.form.value.ocLocation ?? undefined,
        somewhereElse: this.form.value.somewhereElseLocation ?? undefined
      },
      information: this.form.value.infos ?? undefined
    }
    this.dialogRef.close(epd)
  }

  locationValues() {
    return Object.values(LocationTypeDto)
  }

  locLocationValues() {
    return Object.values(LogisticsLocationDto)
  }

  ocLocationValues() {
    return Object.values(OperationCenterDto)
  }

  protected readonly LocationTypeDto = LocationTypeDto;
}
