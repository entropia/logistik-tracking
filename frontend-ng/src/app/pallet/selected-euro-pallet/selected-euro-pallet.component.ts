import {Component, Input, OnInit} from '@angular/core';
import {EuroPalletDto} from '../../api/models/euro-pallet-dto';
import {ApiService} from '../../api/services/api.service';
import {LocationEditorComponent} from '../../location/location-editor/location-editor.component';
import {FormsModule, NgForm} from '@angular/forms';
import {ValidateLocationDirective} from '../../location/location-editor/location-validator';
import {LocationDto} from '../../api/models/location-dto';
import {MatError} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {MatSnackBar} from '@angular/material/snack-bar';
import {handleDefaultError} from '../../util/auth';
import {NewPackingListDto} from '../../api/models/new-packing-list-dto';
import {Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {RequiresAuthorityDirective} from '../../util/requires-permission.directive';
import {AuthorityEnumDto} from '../../api/models/authority-enum-dto';
import {AuthorityStatus, UserService} from '../../util/user.service';
import {PrintButtonComponent} from '../../util/print-button/print-button.component';

@Component({
	selector: 'app-selected-euro-pallet',
	imports: [
		LocationEditorComponent,
		FormsModule,
		ValidateLocationDirective,
		MatError,
		MatButton,

		RequiresAuthorityDirective,
		MatProgressSpinner,
		PrintButtonComponent
	],
	templateUrl: './selected-euro-pallet.component.html',
	styleUrl: './selected-euro-pallet.component.scss'
})
export class SelectedEuroPalletComponent implements OnInit {
	@Input()
	id!: number;

	pallet?: EuroPalletDto;
	editingLocation?: LocationDto;

	canEdit: boolean = false;

	constructor(
		private apiService: ApiService,
		private snackbar: MatSnackBar,
		private router: Router,
		private diag: MatDialog,
		private userService: UserService
	) {
		userService.hasAuthority(AuthorityEnumDto.ManageResources).then(does => {
			this.canEdit = does == AuthorityStatus.HasIt
		});
	}

	getInfos(): string {
		let existing = this.pallet?.information ?? "";
		existing = existing.trim()
		if (existing.length == 0) return "Keine Informationen";
		return existing;
	}

	ngOnInit(): void {
		this.apiService.getEuroPallet({
			euroPalletId: this.id
		}).subscribe({
			next: euroPallet => {
				this.pallet = euroPallet;
				this.editingLocation = {...this.pallet.location};
			},
			error: handleDefaultError
		});
	}

	saveIt(form: NgForm) {
		this.apiService.updateLastLocationOfEuroPallet({
			euroPalletId: this.pallet!.euroPalletId,
			body: this.editingLocation!
		}).subscribe({
			next: _ => {
				this.snackbar.open("Saved!", undefined, {
					duration: 3000
				})
				form.control.markAsPristine()
			},
			error: handleDefaultError
		})
	}

	createPackingList() {
		import("../../packlist/create-packing-list/create-packing-list.component").then(it => {
			this.diag.open<any, any, NewPackingListDto>(it.CreatePackingListComponent, {
				data: {
					packedOnPallet: this.pallet?.euroPalletId
				}
			}).afterClosed()
				.subscribe(value => {
					if (!value) return;
					this.apiService.createPackingList({
						body: value
					}).subscribe({
						next: crate => {
							this.router.navigate(['packingList/' + crate.packingListId])
								.catch(reason => {
									console.log("Failed to redirect to newly created packing list because: " + reason);
								});
						}
					});
				});
		})
	}

	protected readonly AuthorityEnumDto = AuthorityEnumDto;
}
