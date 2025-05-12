import {Component, Input, OnInit} from '@angular/core';
import {ApiService} from '../../api/services';
import {EuroCrateDto} from '../../api/models/euro-crate-dto';
import {FormsModule, NgForm, NgModel} from '@angular/forms';
import {LocationEditorComponent} from '../../location/location-editor/location-editor.component';
import {ValidateLocationDirective} from '../../location/location-editor/location-validator';
import {DeliveryStateEnumDto, EuroCratePatchDto, LogisticsLocationDto} from '../../api/models';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {MatButton} from '@angular/material/button';
import {handleDefaultError} from '../../util/auth';
import {MatProgressSpinner} from '@angular/material/progress-spinner';

@Component({
	selector: 'app-selected-euro-crate',
	imports: [
		FormsModule,
		LocationEditorComponent,
		ValidateLocationDirective,
		MatError,
		MatFormField,
		MatLabel,
		MatSelect,
		MatOption,
		MatInput,
		MatButton,
		MatProgressSpinner
	],
	templateUrl: './selected-euro-crate.component.html',
	styleUrl: './selected-euro-crate.component.scss'
})
export class SelectedEuroCrateComponent implements OnInit {
    crate?: EuroCrateDto;

	constructor(
		private apiService: ApiService
	) {
	}

	@Input()
	id!: number;

	ngOnInit() {
		this.apiService.getEuroCrate({
			id: this.id
		}).subscribe(value => {
			this.crate = value;
		})
	}

	saveIt(form: NgForm, locationMod: NgModel, deliStatusMod: NgModel, ftInfoMod: NgModel) {

		let patch: EuroCratePatchDto = {}
		if (locationMod.dirty) {
			patch.location = this.crate!.location
		}
		if (deliStatusMod.dirty) {
			patch.deliveryState = this.crate!.deliveryState
		}
		if (ftInfoMod.dirty) {
			patch.information = this.crate!.information
		}
		this.apiService.modifyEuroCrate({
			id: this.id,
			body: patch
		}).subscribe({
			next: _ => {
				form.control.markAsPristine()
			},
			error: handleDefaultError
		})
	}

	protected readonly LogisticsLocationDto = LogisticsLocationDto;
	protected readonly Object = Object;
	protected readonly DeliveryStateEnumDto = DeliveryStateEnumDto;
}
