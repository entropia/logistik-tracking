import {Component, EventEmitter, Output} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {LocationDto, LocationTypeDto, LogisticsLocationDto, OperationCenterDto} from '../../api/models';

@Component({
  selector: 'app-create-location',
	imports: [
		ReactiveFormsModule,
		MatFormField,
		MatInput,
		MatSelect,
		MatOption,
		MatLabel
	],
  templateUrl: './create-location.component.html',
  styleUrl: './create-location.component.scss'
})
export class CreateLocationComponent {
	readonly form: FormGroup;
	@Output() locationChange = new EventEmitter<LocationDto>();

	constructor() {
		this.form = new FormGroup({
			locationType: new FormControl<LocationTypeDto>(LocationTypeDto.Logistics, [Validators.required]),
			locLocation: new FormControl<LogisticsLocationDto | null>(null),
			ocLocation: new FormControl<OperationCenterDto | null>(null),
			somewhereElseLocation: new FormControl<string>('', [])
		});

		this.form.get('locationType')!.valueChanges.subscribe(value => {
			this.updateValidators(value);
			this.emitLocation();
		});

		this.form.valueChanges.subscribe(() => this.emitLocation());
	}

	private updateValidators(value: LocationTypeDto) {
		const controls = {
			locLocation: this.form.get('locLocation')!,
			ocLocation: this.form.get('ocLocation')!,
			somewhereElseLocation: this.form.get('somewhereElseLocation')!
		};

		Object.values(controls).forEach(c => {
			c.clearValidators();
			c.updateValueAndValidity();
		});

		const activeControl = this.getActiveControl(value);
		activeControl?.setValidators([Validators.required]);
		activeControl?.updateValueAndValidity();
	}

	private getActiveControl(value: LocationTypeDto) {
		switch (value) {
			case LocationTypeDto.AtOperationCenter:
				return this.form.get('ocLocation');
			case LocationTypeDto.SomewhereElse:
				return this.form.get('somewhereElseLocation');
			case LocationTypeDto.Logistics:
				return this.form.get('locLocation');
			default:
				return null;
		}
	}

	private emitLocation() {
		if (this.form.invalid) return;

		this.locationChange.emit({
			locationType: this.form.value.locationType,
			logisticsLocation: this.form.value.locLocation,
			operationCenter: this.form.value.ocLocation,
			somewhereElse: this.form.value.somewhereElseLocation
		});
	}

	getType() {
		return this.form.value.locationType;
	}

	locationValues() {
		return Object.values(LocationTypeDto);
	}

	locLocationValues() {
		return Object.values(LogisticsLocationDto);
	}

	ocLocationValues() {
		return Object.values(OperationCenterDto);
	}

	protected readonly LocationTypeDto = LocationTypeDto;
}
