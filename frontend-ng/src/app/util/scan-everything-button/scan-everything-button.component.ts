import {Component} from '@angular/core';
import {MatMiniFabButton} from '@angular/material/button';
import {QrScannerService} from '../../qr-scanner.service';
import {extractIdFromUrl, IdKind, parseId} from '../qr-id-parser';
import {Router} from '@angular/router';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-scan-everything-button',
	imports: [
		MatMiniFabButton,
		MatIcon

	],
  templateUrl: './scan-everything-button.component.html',
  styleUrl: './scan-everything-button.component.scss'
})
export class ScanEverythingButtonComponent {
	constructor(
		private qrScanner: QrScannerService,
		private router: Router
	) {
	}
	doTheScan() {
		let scanner = this.qrScanner.startScanning();
		scanner.onScanned.subscribe({
			next: v => {
				try {
					let id = parseId(extractIdFromUrl(v))
					scanner.close()
					switch (id.kind) {
						case IdKind.Crate:
							this.router.navigateByUrl(`/euroCrate/${id.id}`)
							break;
						case IdKind.List:
							this.router.navigateByUrl(`/packingList/${id.id}`)
							break;
						case IdKind.Pallet:
							this.router.navigateByUrl(`/euroPallet/${id.id}`)
							break;
					}
				} catch (e) {
					console.error(e)
				}
			}
		})
	}
}
