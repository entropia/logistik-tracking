import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ZXingScannerComponent, ZXingScannerModule} from '@zxing/ngx-scanner';
import {BarcodeFormat, Exception, NotFoundException} from '@zxing/library';
import { MatSnackBar } from '@angular/material/snack-bar';
import {MatButtonModule} from '@angular/material/button';
import {QrScanRef} from '../../qr-scanner.service';

@Component({
  selector: 'app-qr-scanner-actual',
	imports: [
		ZXingScannerModule,
		MatButtonModule
	],
  templateUrl: './qr-scanner-actual.component.html',
  styleUrl: './qr-scanner-actual.component.scss'
})
export class QrScannerActualComponent implements OnInit, OnDestroy {

	@ViewChild("scanner", {static: true})
	theScanner?: ZXingScannerComponent;

	scanIsActive = false;

	err = false;

	allowedFormats: BarcodeFormat[] = [BarcodeFormat.QR_CODE];
	canScan: boolean = true;

	constructor(private snack: MatSnackBar, private ref: QrScanRef) {
	}

	async ngOnInit() {
		await this.start()
	}

	ngOnDestroy() {
		this.stopScanning()
	}

	stopScanning() {
		this.scanIsActive = false;
		this.theScanner!.enable = false;
		this.ref.close()
	}

	handlePerm($event: boolean | null) {
		this.canScan = !!$event;
		if (!$event) {
			this.stopScanning()
			alert("Berechtigung wurde verweigert.\nOhne die Kameraberechtigung kann kein Scan durchgeführt werden.")
		}
	}

	camerasNotFound() {
		this.stopScanning()
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

	scanSuccess($event: string) {
		this.ref.onScanned.next($event)
	}

	public async start() {

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

}
