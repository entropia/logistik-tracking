import {Injectable, Injector} from '@angular/core';
import {
	GlobalPositionStrategy,
	Overlay,
	OverlayRef, ScrollStrategyOptions
} from '@angular/cdk/overlay';
import {ComponentPortal} from '@angular/cdk/portal';
import {QrScannerActualComponent} from './util/qr-scanner/qr-scanner-actual.component';
import {Subject} from 'rxjs';

export class QrScanRef {
	public onScanned = new Subject<string>()
	public onClosed = new Subject<void>()
	constructor(private ovRef: OverlayRef) {
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
		private sso: ScrollStrategyOptions
	) {
	}

	public startScanning() {
		let ovRef = this.overlay.create({
			disposeOnNavigation: true,
			hasBackdrop: false,
			height: "100vh",
			width: "100vw",
			scrollStrategy: this.sso.close(),
			positionStrategy: new GlobalPositionStrategy().top("0").left("0").bottom("0").right("0"),
		})

		let ref = new QrScanRef(ovRef)

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
