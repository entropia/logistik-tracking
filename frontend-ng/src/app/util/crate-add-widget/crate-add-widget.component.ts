import {Component, EventEmitter, Output} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {EuroCrateDto} from '../../api/models/euro-crate-dto';
import {FormsModule, NgForm} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {OperationCenterDto} from '../../api/models/operation-center-dto';
import {MatInput} from '@angular/material/input';
import {MatFabButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatSnackBar} from '@angular/material/snack-bar';
import {parseCrateId} from '../qr-id-parser';
import {QrScannerService, QrScanRef} from '../../qr-scanner.service';

interface InputModel {
	oc?: OperationCenterDto;
	name: string;
}

@Component({
  selector: 'app-crate-add-widget',
	imports: [
		FormsModule,
		MatFormFieldModule,
		MatSelectModule,
		MatInput,
		MatIcon,
		MatFabButton

	],
  templateUrl: './crate-add-widget.component.html',
  styleUrl: './crate-add-widget.component.scss'
})
export class CrateAddWidgetComponent {

	@Output() crateSubmitted = new EventEmitter<EuroCrateDto>();

	inputModel: InputModel = {
		oc: undefined,
		name: ""
	}

	err = false;

	constructor(
		private api: ApiService,
		private snack: MatSnackBar,
		private qrCode: QrScannerService
	) {
	}

	saveIt(form: NgForm) {
		this.api.getEuroCrate({
			euroCrateName: this.inputModel.name,
			operationCenter: this.inputModel.oc!
		}).subscribe({
			next: t => {
				this.err = false;
				form.resetForm()
				this.crateSubmitted.next(t)
			},
			error: _ => {
				this.err = true;
			}
		})
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
			let crateId = parseCrateId($event);
			this.inputModel.oc = crateId.oc
			this.inputModel.name = crateId.name
			qsc.close()
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
