import {Component, Input, OnInit} from '@angular/core';
import {EuroPalletDto} from '../../api/models/euro-pallet-dto';
import {ApiService} from '../../api/services/api.service';
import {LocationEditorComponent} from '../../util/location-editor/location-editor.component';
import {FormsModule} from '@angular/forms';
import {ValidateLocationDirective} from '../../util/location-editor/location-validator';
import {LocationDto} from '../../api/models/location-dto';
import {MatError} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {MatSnackBar} from '@angular/material/snack-bar';
import {checkErrorAndAlertUser} from '../../util/auth';

@Component({
	selector: 'app-selected-euro-pallet',
	imports: [
		LocationEditorComponent,
		FormsModule,
		ValidateLocationDirective,
		MatError,
		MatButton,
	],
	templateUrl: './selected-euro-pallet.component.html',
	styleUrl: './selected-euro-pallet.component.scss'
})
export class SelectedEuroPalletComponent implements OnInit {
	@Input()
	id!: number;

	pallet?: EuroPalletDto;
	editingLocation?: LocationDto;

	constructor(
		private apiService: ApiService,
		private snackbar: MatSnackBar
	) {
	}

	getInfos(): string {
		let existing = this.pallet?.information ?? "";
		existing = existing.trim()
		if (existing.length == 0) return "Keine Informationen";
		return existing;
	}

	ngOnInit(): void {
		console.log("loading it")
		this.apiService.getEuroPallet({
			euroPalletId: this.id
		}).subscribe({
			next: euroPallet => {
				this.pallet = euroPallet;
				this.editingLocation = {...this.pallet.location};
			},
			error: err => {
				alert("Failed to load pallet. See console for error")
				console.error(err)
			}
		});
	}

	saveIt() {
		this.apiService.updateLastLocationOfEuroPallet({
			euroPalletId: this.pallet!.euroPalletId,
			body: this.editingLocation!
		}).subscribe({
			next: _ => {
				this.snackbar.open("Saved!", undefined, {
					duration: 3000
				})
			},
			error(e) {
				console.error(e)
				if (!checkErrorAndAlertUser(e)) alert(`Failed to save pallet: ${e}`)
			}
		})
	}
}
