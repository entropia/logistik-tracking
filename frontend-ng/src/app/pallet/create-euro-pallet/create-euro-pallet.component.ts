import {Component} from '@angular/core';
import {MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NewEuroPalletDto} from '../../api/models/new-euro-pallet-dto';
import {LocationDto} from '../../api/models/location-dto';
import {LocationEditorComponent} from '../../location/location-editor/location-editor.component';
import {ValidateLocationDirective} from '../../location/location-editor/location-validator';
import {LocationTypeDto} from '../../api/models/location-type-dto';
import {OperationCenterDto} from '../../api/models/operation-center-dto';
import {LogisticsLocationDto} from '../../api/models/logistics-location-dto';

@Component({
	selector: 'app-create-euro-pallet',
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
		LocationEditorComponent,
		ValidateLocationDirective
	],
	templateUrl: './create-euro-pallet.component.html',
	styleUrl: './create-euro-pallet.component.scss'
})
export class CreateEuroPalletComponent {
	locationFormData: LocationDto = {
		locationType: LocationTypeDto.Logistics,
		logisticsLocation: LogisticsLocationDto.Entropia,
		operationCenter: OperationCenterDto.Aussenbar,
		somewhereElse: ''
	};
	readonly form;

	constructor(private dialogRef: MatDialogRef<CreateEuroPalletComponent>) {
		this.form = new FormGroup({
			infos: new FormControl<string>("", [])
		});
	}

	cancel() {
		this.dialogRef.close(null)
	}

	handleSubmit() {
		if (!this.locationFormData) {
			return;
		}

		let epd: NewEuroPalletDto = {
			location: this.locationFormData,
			information: this.form.value.infos ?? undefined
		}
		this.dialogRef.close(epd)
	}
}
