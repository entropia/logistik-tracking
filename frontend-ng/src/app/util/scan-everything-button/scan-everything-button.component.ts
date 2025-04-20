import {Component} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {QrScannerService} from '../../qr-scanner.service';
import {IdKind, parseId} from '../qr-id-parser';
import {Router} from '@angular/router';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-scan-everything-button',
	imports: [
		MatButtonModule,
		MatIconModule
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
					let id = parseId(v)
					scanner.close()
					console.log(id)
					switch (id.kind) {
						case IdKind.Crate:
							this.router.navigateByUrl(`/euroCrate/${id.oc}/${id.name}`)
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
