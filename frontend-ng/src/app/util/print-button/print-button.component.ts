import {Component, Input} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {MatButton} from '@angular/material/button';
import {handleDefaultError} from '../auth';
import {MatIcon} from '@angular/material/icon';

export type PrintableResourceType = "crate" | "pallet" | "list";

export interface PrintableThing {
	type: PrintableResourceType;
	id: number;
}

@Component({
	selector: 'app-print-button',
	imports: [
		MatButton,
		MatIcon
	],
	templateUrl: './print-button.component.html',
	styleUrl: './print-button.component.scss'
})
export class PrintButtonComponent {
	@Input() thing!: PrintableThing;

	constructor(private api: ApiService) {}

	print() {
		let the;
		switch (this.thing.type) {
			case "crate":
				the = this.api.printEuroCrate({
					id: this.thing.id
				})
				break;
			case "list":
				the = this.api.printPackingList({
					packingListId: this.thing.id
				})
				break;
			case "pallet":
				the = this.api.printEuroPallet({
					euroPalletId: this.thing.id
				})
				break;
			default:
				throw Error("what the hell oh my god no wayayayay")
		}
		the.subscribe({
			next: v => {
				let ou = URL.createObjectURL(v)
				let opened = window.open(ou, "_blank")
				if (!opened) {
					alert("Konnte kein Fenster öffnen! Bitte erlaube für diese Webseite popups.")
				}
				URL.revokeObjectURL(ou)
			},
			error: handleDefaultError
		})
	}
}
