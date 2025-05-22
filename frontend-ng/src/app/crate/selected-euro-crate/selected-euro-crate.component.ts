import {Component, Input, OnInit} from '@angular/core';
import {ApiService} from '../../api/services';
import {EuroCrateDto} from '../../api/models/euro-crate-dto';
import {FormsModule, NgForm, NgModel} from '@angular/forms';
import {LocationEditorComponent} from '../../location/location-editor/location-editor.component';
import {ValidateLocationDirective} from '../../location/location-editor/location-validator';
import {AuthorityEnumDto, DeliveryStateEnumDto, EuroCratePatchDto, LogisticsLocationDto, OperationCenterDto} from '../../api/models';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {MatButton} from '@angular/material/button';
import {handleDefaultError} from '../../util/auth';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {PrintButtonComponent} from '../../util/print-button/print-button.component';
import {RequiresAuthorityDirective} from '../../util/requires-permission.directive';
import {AuthorityStatus, UserService} from '../../util/user.service';

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
		MatProgressSpinner,
		PrintButtonComponent,
		RequiresAuthorityDirective
	],
	templateUrl: './selected-euro-crate.component.html',
	styleUrl: './selected-euro-crate.component.scss'
})
export class SelectedEuroCrateComponent implements OnInit {
    crate?: EuroCrateDto;
	canEdit: boolean = false;

	constructor(
		private apiService: ApiService,
		userService: UserService,

	) {
		userService.hasAuthority(AuthorityEnumDto.ManageResources).then(does => {
			this.canEdit = does == AuthorityStatus.HasIt
		});
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

	saveIt(form: NgForm, oc: NgModel, locationMod: NgModel, deliStatusMod: NgModel, ftInfoMod: NgModel) {
		let patch: EuroCratePatchDto = {}
		if (oc.dirty) {
			patch.operationCenter = this.crate!.operationCenter
		}
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
	protected readonly AuthorityEnumDto = AuthorityEnumDto;
	protected readonly OperationCenterDto = OperationCenterDto;
}
