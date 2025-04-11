import {Component, OnInit} from '@angular/core';
import { ApiService } from '../../api/services';
import {OperationCenterDto} from '../../api/models/operation-center-dto';
import {EuroCrateDto} from '../../api/models/euro-crate-dto';
import {ActivatedRoute} from '@angular/router';
import {FormsModule, NgModel} from '@angular/forms';
import {LocationEditorComponent} from '../../util/location-editor/location-editor.component';
import {ValidateLocationDirective} from '../../util/location-editor/location-validator';
import {DeliveryStateEnumDto, LogisticsLocationDto} from '../../api/models';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {MatButton} from '@angular/material/button';
import {forkJoin} from 'rxjs';

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
		MatButton
	],
	templateUrl: './selected-euro-crate.component.html',
	styleUrl: './selected-euro-crate.component.scss'
})
export class SelectedEuroCrateComponent implements OnInit {
    crate?: EuroCrateDto;

	constructor(
		private apiService: ApiService,
		private route: ActivatedRoute
	) {
	}

	ngOnInit() {
		// hacky aber geht
		let name: string, oc: OperationCenterDto;
		let seen = false;
		this.route.params.forEach(it => {
			if (seen) return;
			seen = true;

			name = it["name"] as string
			oc = it["oc"] as OperationCenterDto

			this.apiService.getEuroCrate({
				euroCrateName: name!,
				operationCenter: oc!
			}).subscribe({
				next: ec => {
					this.crate = ec;
					this.crate.information = this.crate.information ?? "";
				},
				error: err => {
					alert("Failed to load crate. See console for error")
					console.error(err)
				}
			})
		})
	}

	saveIt(locationMod: NgModel, deliStatusMod: NgModel, ftInfoMod: NgModel) {
		let promises = []
		if (locationMod.dirty) {
			promises.push(this.apiService.modifyLocationOfCrate({
				euroCrateName: this.crate!.name,
				operationCenter: this.crate!.operationCenter,
				body: this.crate!.location
			}))
		}
		if (deliStatusMod.dirty) {
			promises.push(this.apiService.modifyDeliveryStateOfCrate({
				euroCrateName: this.crate!.name,
				operationCenter: this.crate!.operationCenter,
				body: this.crate!
			}))
		}
		if (ftInfoMod.dirty) {
			promises.push(this.apiService.modifyInformationOfCrate({
				euroCrateName: this.crate!.name,
				operationCenter: this.crate!.operationCenter,
				body: this.crate!
			}))
		}
		console.dir(promises)
		forkJoin(promises).subscribe({
			next: v => {
				console.log(v)
			},
			error: e => {
				alert("failed to save! check console")
				console.error(e)
			}
		})
	}

	protected readonly LogisticsLocationDto = LogisticsLocationDto;
	protected readonly Object = Object;
	protected readonly DeliveryStateEnumDto = DeliveryStateEnumDto;
}
