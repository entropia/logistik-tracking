import {Component, Input, OnInit} from '@angular/core';
import {EuroPalletDto} from '../../api/models/euro-pallet-dto';
import {ApiService} from '../../api/services/api.service';
import {LocationEditorComponent} from '../../util/location-editor/location-editor.component';
import {FormsModule, NgForm} from '@angular/forms';
import {ValidateLocationDirective} from '../../util/location-editor/location-validator';
import {LocationDto} from '../../api/models/location-dto';
import {MatError} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {MatSnackBar} from '@angular/material/snack-bar';

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
	set id(id: number) {
		this.apiService.getEuroPallet({
			euroPalletId: id
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
	}

	saveIt(_t8: NgForm) {
		const outer = this;
		this.apiService.updateLastLocationOfEuroPallet({
			euroPalletId: this.pallet!.euroPalletId,
			body: this.editingLocation!
		}).subscribe({
			next(a) {
				outer.pallet = a;
				_t8.resetForm()
				outer.editingLocation = {...a.location};
				outer.snackbar.open("Saved!", undefined, {
					duration: 3000
				})
			},
			error(e) {
				alert("Failed to save! check logs for details")
				console.error(e)
			}
		})
	}
}
