import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatButtonModule} from '@angular/material/button';
import {QrScanRef} from '../../qr-scanner.service';
import QrScanner from 'qr-scanner';
import ScanResult = QrScanner.ScanResult;

@Component({
  selector: 'app-qr-scanner-actual',
	imports: [
		MatButtonModule
	],
  templateUrl: './qr-scanner-actual.component.html',
  styleUrl: './qr-scanner-actual.component.scss'
})
export class QrScannerActualComponent implements OnInit, OnDestroy {

	@ViewChild("theVideo", {static: true})
	theVideo?: ElementRef<HTMLVideoElement>;

	theScanner!: QrScanner;

	constructor(private snack: MatSnackBar, private ref: QrScanRef) {
	}

	async ngOnInit() {
		// js callbacks that happen to for some reason bind this to QrScanner so the refs in those methods get nuked
		// typical js jank solution: bind this beforehand :skull:
		this.theScanner = new QrScanner(
			this.theVideo!.nativeElement, this.scanSuccess.bind(this), {
				highlightCodeOutline: true,
				onDecodeError: this.scanError.bind(this),
				highlightScanRegion: true,
				maxScansPerSecond: 5,
				returnDetailedScanResult: true
			}
		)

		await this.theScanner.start()
	}

	ngOnDestroy() {
		// at this point we're gone, nuke the scanner and close the parent overlay if needed
		this.closeRefIfNotAlready()
		this.theScanner.destroy()
	}

	closeRefIfNotAlready() {
		// called by button, close will call ngOnDestroy if we werent already removed
		this.ref.close()
	}

	scanError(eve: (Error | string)) {
		if (eve == QrScanner.NO_QR_CODE_FOUND) return // boring
		let message = eve.toString()
		console.error(eve)
		this.snack.open(`Fehler beim Scannen (siehe Konsole): ${message}`, "OK", {
			duration: 7000
		})
	}

	scanSuccess($event: ScanResult) {
		this.ref.onScanned.next($event.data)
	}
}
