import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {DeliveryStateEnumDto} from '../../api/models/delivery-state-enum-dto';
import {OperationCenterDto} from '../../api/models/operation-center-dto';
import {LocationDto} from '../../api/models/location-dto';
import {NewEuroCrateDto} from '../../api/models/new-euro-crate-dto';
import {DatePipe, NgForOf} from '@angular/common';
import {LocationEditorComponent} from '../../location/location-editor/location-editor.component';
import {ValidateLocationDirective} from '../../location/location-editor/location-validator';
import {LocationTypeDto} from '../../api/models/location-type-dto';
import {MatOption, MatSelect} from '@angular/material/select';
import {MatButton} from '@angular/material/button';
import {LogisticsLocationDto} from '../../api/models/logistics-location-dto';
import {EuroCrateDto} from '../../api/models/euro-crate-dto';

interface ReturnDate {
	date: string;
	offsetGpnDay0: number;
}


@Component({
  selector: 'app-create-euro-crate',
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatFormField,
		MatInput,
		FormsModule,
		ReactiveFormsModule,
		MatLabel,
		DatePipe,
		NgForOf,
		LocationEditorComponent,
		ValidateLocationDirective,
		MatSelect,
		MatOption,
		MatButton
	],
  templateUrl: './create-euro-crate.component.html',
  styleUrl: './create-euro-crate.component.scss'
})
export class CreateEuroCrateComponent {
	rdOwithSign(e: ReturnDate) {
		let s = Math.sign(e.offsetGpnDay0)
		if (s > 0 || s == 0) return "+"+e.offsetGpnDay0;
		return ""+e.offsetGpnDay0;
	}

	readonly form;
	operationCenters = Object.values(OperationCenterDto).sort();
	deliveryStates = Object.values(DeliveryStateEnumDto);
	readonly returnDates = this.generateDateRange('2025-06-16', '2025-06-23', "2025-06-18");

	constructor(private dialogRef: MatDialogRef<CreateEuroCrateComponent>, @Inject(MAT_DIALOG_DATA) protected data: EuroCrateDto | undefined) {
		console.log(data)
		this.form = new FormGroup({
			name: new FormControl<string>(data?.name || "", { nonNullable: true }),
			location: new FormControl<LocationDto>(data?.location || {locationType: LocationTypeDto.Logistics, logisticsLocation: LogisticsLocationDto.Entropia}, {nonNullable: true}),
			operationCenter: new FormControl<OperationCenterDto>(data?.operationCenter || this.operationCenters[0], { nonNullable: true }),
			deliveryState: new FormControl<DeliveryStateEnumDto>(data?.deliveryState || this.deliveryStates[0], { nonNullable: true }),
			returnBy: new FormControl<string>(data?.returnBy || this.returnDates[this.returnDates.length-1].date, { nonNullable: true }),
			infos: new FormControl<string>(data?.information || "")
		});
	}

	private generateDateRange(start: string, end: string, gpn: string): ReturnDate[] {
		const dates: ReturnDate[] = [];
		let current = new Date(start);
		const endDate = new Date(end);
		const gpnDate = new Date(gpn);

		while (current <= endDate) {
			let isoDay = current.toISOString().split('T')[0]
			let diffMs = current.getTime() - gpnDate.getTime()
			let diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
			dates.push({
				date: isoDay,
				offsetGpnDay0: diffDays
			});
			current.setDate(current.getDate() + 1);
		}

		return dates;
	}

	cancel() {
		this.dialogRef.close(null);
	}

	handleSubmit() {
		let crate: NewEuroCrateDto = {
			name: this.form.value.name!,
			operationCenter: this.form.value.operationCenter!,
			deliveryState: this.form.value.deliveryState!,
			returnBy: this.form.value.returnBy!,
			information: this.form.value.infos ?? undefined,
			location: this.form.value.location!
		};
		this.dialogRef.close(crate);
	}
}
