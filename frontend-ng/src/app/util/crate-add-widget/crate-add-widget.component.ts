import {Component, EventEmitter, Output} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {EuroCrateDto} from '../../api/models/euro-crate-dto';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatError, MatFormField} from '@angular/material/form-field';
import {MatOption} from '@angular/material/select';
import {OperationCenterDto} from '../../api/models/operation-center-dto';
import {MatInput, MatLabel} from '@angular/material/input';
import {MatFabButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatSnackBar} from '@angular/material/snack-bar';
import {extractIdFromUrl, parseCrateId} from '../qr-id-parser';
import {QrScannerService, QrScanRef} from '../../qr-scanner.service';
import {handleDefaultError} from '../auth';
import {Observable, startWith, Subject} from 'rxjs';
import {map} from 'rxjs/operators';
import {MatAutocomplete, MatAutocompleteTrigger} from '@angular/material/autocomplete';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-crate-add-widget',
	imports: [
		FormsModule,
		MatInput,
		MatIcon,
		MatFabButton,
		ReactiveFormsModule,
		AsyncPipe,
		MatFormField,
		MatAutocompleteTrigger,
		MatAutocomplete,
		MatOption,
		MatError,
		MatLabel
	],
  templateUrl: './crate-add-widget.component.html',
  styleUrl: './crate-add-widget.component.scss'
})
export class CrateAddWidgetComponent {

	@Output() crateSubmitted = new EventEmitter<EuroCrateDto>();
	err = false;
	allCrates: EuroCrateDto[] = [];

	input = new FormControl("")
	fg = new FormGroup({
		search: this.input
	})

	filteredAvailable: Observable<string[]>;

	constructor(
		private api: ApiService,
		private snack: MatSnackBar,
		private qrCode: QrScannerService
	) {
		this.filteredAvailable = new Subject()
		this.api.getAllEuroCrates().subscribe({
			next: v => {
				this.allCrates = v
				this.filteredAvailable = this.input.valueChanges.pipe(
					startWith(""),
					map(v => this._filter(v || ""))
				)
			},
			error: handleDefaultError
		})
	}

	private _filter(value: string): string[] {
		const filterValue = value.toLowerCase().trim();

		return this.allCrates.map(it => it.operationCenter+"/"+it.name).filter(option => option.toLowerCase().includes(filterValue));
	}

	saveIt() {
		let the = this.allCrates.find(it => (it.operationCenter+"/"+it.name) == this.input.value)
		if (the) {
			this.err = false;
			this.input.reset()
			this.crateSubmitted.next(the)
		} else {
			this.err = true
		}
	}

	async scan() {
		let qrScanRef = this.qrCode.startScanning();
		qrScanRef.onScanned.subscribe({
			next: t => this.scanSuccess(qrScanRef, t)
		})
	}

	protected readonly Object = Object;
	protected readonly OperationCenterDto = OperationCenterDto;

	scanSuccess(qsc: QrScanRef, $event: string) {
		try {
			let crateId = parseCrateId(extractIdFromUrl($event));
			let the = this.allCrates.find(it => it.internalId == crateId)
			if (the) {
				this.input.setValue(the.operationCenter+"/"+the.name)
				qsc.close()
			}
		} catch (e) {
			console.error(e)
			if (e instanceof Error) {
				let err = e.message
				this.snack.open(`Wahrscheinlich kein QR Code einer Eurobox: ${err}`, "OK", {
					duration: 7000
				})
			} else {
				this.snack.open(`Wahrscheinlich kein QR Code einer Eurobox: Unbekannter fehler (siehe Konsole)`, "OK", {
					duration: 5000
				})
			}

		}
	}
}
