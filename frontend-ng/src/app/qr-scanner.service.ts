import {Injectable, Injector} from '@angular/core';
import {
	Overlay, OverlayPositionBuilder,
	OverlayRef, ScrollStrategyOptions
} from '@angular/cdk/overlay';
import {ComponentPortal} from '@angular/cdk/portal';
import {QrScannerActualComponent} from './util/qr-scanner/qr-scanner-actual.component';
import {Subject} from 'rxjs';
import QrScanner from 'qr-scanner';

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
		private sso: ScrollStrategyOptions,
		private ps: OverlayPositionBuilder
	) {
	}

	public async hasCamera() {
		return await QrScanner.hasCamera()
	}

	public startScanning(fullscreen: boolean = true, ...customClasses: string[]) {
		if (fullscreen) {
			customClasses.push("spl-panel-fullscreen_yes_i_know_this_is_global_cope_about_it")
		}
		let ovRef = this.overlay.create({
			disposeOnNavigation: true,
			hasBackdrop: false,
			scrollStrategy: this.sso.close(),
			panelClass: customClasses,
			positionStrategy: this.ps.global(),
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
