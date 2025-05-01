import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatSelectModule} from '@angular/material/select';
import {DeliveryStateEnumDto} from '../../api/models/delivery-state-enum-dto';
import {OperationCenterDto} from '../../api/models/operation-center-dto';
import {LocationDto} from '../../api/models/location-dto';
import {NewEuroCrateDto} from '../../api/models/new-euro-crate-dto';
import {DatePipe, NgForOf} from '@angular/common';
import {LocationEditorComponent} from '../../location/location-editor/location-editor.component';
import {ValidateLocationDirective} from '../../location/location-editor/location-validator';
import {LocationTypeDto} from '../../api/models/location-type-dto';
import {LogisticsLocationDto} from '../../api/models/logistics-location-dto';

@Component({
  selector: 'app-create-euro-crate',
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatButtonModule,
		MatFormField,
		MatInput,
		FormsModule,
		ReactiveFormsModule,
		MatLabel,
		MatSelectModule,
		DatePipe,
		NgForOf,
		LocationEditorComponent,
		ValidateLocationDirective
	],
  templateUrl: './create-euro-crate.component.html',
  styleUrl: './create-euro-crate.component.scss'
})
export class CreateEuroCrateComponent {
	locationFormData: LocationDto = {
		locationType: LocationTypeDto.Logistics,
		logisticsLocation: LogisticsLocationDto.Entropia,
		operationCenter: OperationCenterDto.Aussenbar,
		somewhereElse: ''
	};
	readonly form;
	operationCenters = Object.values(OperationCenterDto);
	deliveryStates = Object.values(DeliveryStateEnumDto);
	readonly returnDates = this.generateDateRange('2025-06-18', '2025-06-23');

	constructor(private dialogRef: MatDialogRef<CreateEuroCrateComponent>) {
		this.form = new FormGroup({
			name: new FormControl<string>('', { nonNullable: true }),
			operationCenter: new FormControl<OperationCenterDto>(this.operationCenters[0], { nonNullable: true }),
			deliveryState: new FormControl<DeliveryStateEnumDto>(this.deliveryStates[0], { nonNullable: true }),
			returnBy: new FormControl<string>(this.returnDates[0], { nonNullable: true }),
			infos: new FormControl<string>('')
		});
	}

	private generateDateRange(start: string, end: string): string[] {
		const dates: string[] = [];
		let current = new Date(start);
		const endDate = new Date(end);

		while (current <= endDate) {
			dates.push(current.toISOString().split('T')[0]);
			current.setDate(current.getDate() + 1);
		}

		return dates;
	}

	cancel() {
		this.dialogRef.close(null);
	}

	handleSubmit() {
		if (!this.locationFormData) {
			return;
		}

		let crate: NewEuroCrateDto = {
			name: this.form.value.name!,
			operationCenter: this.form.value.operationCenter!,
			deliveryState: this.form.value.deliveryState!,
			returnBy: this.form.value.returnBy!,
			information: this.form.value.infos ?? undefined,
			location: this.locationFormData
		};
		this.dialogRef.close(crate);
	}
}
