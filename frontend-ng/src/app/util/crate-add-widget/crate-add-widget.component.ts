import {Component, EventEmitter, Output, ViewChild} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {EuroCrateDto} from '../../api/models/euro-crate-dto';
import {FormsModule, NgForm} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {OperationCenterDto} from '../../api/models/operation-center-dto';
import {MatInput} from '@angular/material/input';
import {MatButton, MatFabButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {ZXingScannerComponent, ZXingScannerModule} from '@zxing/ngx-scanner';
import {BarcodeFormat, Exception, IllegalArgumentException, NotFoundException} from '@zxing/library';
import {MatSnackBar} from '@angular/material/snack-bar';

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
		MatButton,
		MatIcon,
		ZXingScannerModule,
		MatFabButton

	],
  templateUrl: './crate-add-widget.component.html',
  styleUrl: './crate-add-widget.component.scss'
})
export class CrateAddWidgetComponent {

	@Output() crateSubmitted = new EventEmitter<EuroCrateDto>();

	@ViewChild("scanner", {static: true})
	theScanner?: ZXingScannerComponent;

	inputModel: InputModel = {
		oc: undefined,
		name: ""
	}

	scanIsActive = false;

	err = false;

	constructor(
		private api: ApiService,
		private snack: MatSnackBar
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
		let doWeHavePerm = await this.theScanner!.askForPermission()
		if (doWeHavePerm) {
			let devices = await this.theScanner!.updateVideoInputDevices()

			// select the rear camera by default, otherwise take the last camera.
			const device = devices.find(({label}) => /back|trás|rear|traseira|environment|ambiente/gi.test(label)) || devices.pop();

			if (!device) {
				this.camerasNotFound()
				return;
			}

			this.theScanner!.device = device

			this.scanIsActive = true;
			this.theScanner!.enable = true;
		}
	}

	protected readonly Object = Object;
	protected readonly OperationCenterDto = OperationCenterDto;

	scanSuccess($event: string) {
		try {
			let data = atob($event)
			let a = data.charCodeAt(0)
			let name = data.substring(1)
			let availValues = Object.values(OperationCenterDto)
			if (a < 0 || a >= availValues.length) throw new IllegalArgumentException("OC index invalid")
			if (name.length < 1) throw new IllegalArgumentException("name length invalid")
			this.inputModel.oc = availValues[a]
			this.inputModel.name = name
			this.close()
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
	allowedFormats: BarcodeFormat[] = [BarcodeFormat.QR_CODE];
	canScan: boolean = true;

	camerasNotFound() {
		this.close()
		alert("Keine Kameras gefunden!\nÜberprüfe die Berechtigungen für die Website, oder versuche einen anderen Browser.")
	}

	scanFailed(eve: Exception | undefined) {
		if (eve) {
			if (eve instanceof NotFoundException) return; // wird gespammt wenn nix gefunden wurde
			let message = eve.message
			let kind = eve.getKind()
			this.snack.open(`Konnte nicht scannen: ${kind}: ${message}`, "OK", {
				duration: 7000
			})
		} else {
			this.snack.open(`Konnte nicht scannen: Unbekannter fehler.`, "OK", {
				duration: 5000
			})
		}
	}

	scanError(eve: Error) {
		let message = eve.message
		this.snack.open(`Fehler beim Scannen (siehe Konsole): ${message}`, "OK", {
			duration: 7000
		})

		console.error(eve)
	}

	close() {
		this.scanIsActive = false;
		this.theScanner!.enable = false;
	}

	handlePerm($event: boolean | null) {
		this.canScan = !!$event;
		if (!$event) {
			this.close()
			alert("Berechtigung wurde verweigert.\nOhne die Kameraberechtigung kann kein Scan durchgeführt werden.")
		}
	}
}
