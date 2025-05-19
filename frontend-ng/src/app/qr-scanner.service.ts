import {Injectable, Injector} from '@angular/core';
import {
	Overlay, OverlayPositionBuilder,
	OverlayRef, ScrollStrategyOptions
} from '@angular/cdk/overlay';
import {ComponentPortal} from '@angular/cdk/portal';
import {QrScannerActualComponent} from './util/qr-scanner/qr-scanner-actual.component';
import {Subject} from 'rxjs';

export class QrScanRef {
	public onScanned = new Subject<string>()
	public onClosed = new Subject<void>()
	constructor(private ovRef: OverlayRef, public doneLabel: string = "Abbrechen") {
	}

	public close() {
		this.ovRef.dispose()
		this.onClosed.next()
	}
}

@Injectable({
  providedIn: 'root'
})
export class QrScannerService {

	constructor(
		private overlay: Overlay,
		private inj: Injector,
		private sso: ScrollStrategyOptions,
		private ps: OverlayPositionBuilder
	) {
	}
	public startScanning(closeLabel: string = "Abbrechen") {
		let ovRef = this.overlay.create({
			disposeOnNavigation: true,
			hasBackdrop: false,
			scrollStrategy: this.sso.close(),
			positionStrategy: this.ps.global(),
			width: "100vw",
			height: "100vh"
		})

		let ref = new QrScanRef(ovRef, closeLabel)

		let inj = Injector.create({
			parent: this.inj,
			providers: [
				{provide: QrScanRef, useValue: ref}
			]
		})


		let componentPortal = new ComponentPortal(QrScannerActualComponent, undefined, inj)

		ovRef.attach(componentPortal);

		return ref;
	}
}
