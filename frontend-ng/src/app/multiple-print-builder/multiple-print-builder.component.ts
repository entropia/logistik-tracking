import {Component, OnInit} from '@angular/core';
import {CdkDrag, CdkDragDrop, CdkDropList, moveItemInArray} from '@angular/cdk/drag-drop';
import {ApiService} from '../api/services/api.service';
import {MatIcon} from '@angular/material/icon';
import {MatButton, MatMiniFabButton} from '@angular/material/button';
import {FormsModule} from '@angular/forms';
import {MatFormField} from '@angular/material/form-field';
import {MatInput, MatLabel} from '@angular/material/input';
import {PrintMultipleDto} from '../api/models/print-multiple-dto';
import {handleDefaultError} from '../util/auth';
import {MatProgressSpinner} from '@angular/material/progress-spinner';

enum ResType {
	Crate = "Crate",
	Pallet = "Pallet"
}

interface Resource {
	type: ResType;
	icon: string;
	id: number;
	label: string;
}

@Component({
  selector: 'app-multiple-print-builder',
	imports: [
		CdkDropList,
		CdkDrag,
		MatMiniFabButton,
		FormsModule,
		MatButton,
		MatFormField,
		MatInput,
		MatIcon,
		MatProgressSpinner,
		MatLabel
	],
  templateUrl: './multiple-print-builder.component.html',
  styleUrl: './multiple-print-builder.component.scss'
})
export class MultiplePrintBuilderComponent implements OnInit {
	allRes: Resource[] = []
	availableRes: Resource[] = []
	existingRes: Resource[] = []
	filter: string = ""
	constructor(
		private api: ApiService
	) {
	}
	ngOnInit() {
		this.api.getAllEuroCrates().subscribe({
			next: e => {
				let bruh = e.map(it => {
					return {
						type: "Crate",
						id: it.internalId,
						label: it.operationCenter+"/"+it.name,
						icon: "box"
					} as Resource
				})
				this.allRes.push(...bruh)
				this.availableRes.push(...bruh)
			}
		})
		this.api.getAllEuroPallets().subscribe({
			next: e => {
				let bruh = e.map(it => {
					return {
						type: "Pallet",
						id: it.euroPalletId,
						label: it.name,
						icon: "pallet"
					} as Resource
				})
				this.allRes.push(...bruh)
				this.availableRes.push(...bruh)
			}
		})
	}

	drop(event: CdkDragDrop<Resource[]>) {
		moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
	}

	addItem(a: Resource) {
		this.existingRes.push(a)
		this.availableRes.splice(this.availableRes.indexOf(a), 1)
	}

	removeItem(a: Resource) {
		this.availableRes.push(a)
		this.existingRes.splice(this.existingRes.indexOf(a), 1)
	}

	theFilter = this.checkFilter.bind(this) // kinda hacky

	checkFilter(it: Resource): boolean {
		let f = this.filter.trim()
		if (f.length == 0) return true
		let search = f.toLowerCase()
		return it.label.toLowerCase().includes(search) || it.id.toString().includes(search);
	}

	printInProgress = false

	printIt() {
		this.printInProgress = true
		this.api.printMultipleThings({
			body: this.existingRes.map(it => {
				return {
					type: it.type,
					id: it.id
				}
			}) as PrintMultipleDto
		}).subscribe({
			next: v => {
				this.printInProgress = false
				let ou = URL.createObjectURL(v)
				let opened = window.open(ou, "_blank")
				if (!opened) {
					alert("Konnte kein Fenster öffnen! Bitte erlaube für diese Webseite popups.")
				}
				URL.revokeObjectURL(ou)
			},
			error: v => {
				this.printInProgress = false
				handleDefaultError(v)
			}
		})
	}

	// ich bin glaub ich behindert. qr code scanner BEIM QR CODE GENERIEREN ist ein wenig sinnfrei, oder??
	// scan() {
	// 	let qrScanRef = this.qr.startScanning();
	// 	qrScanRef.onScanned.subscribe(v => {
	// 		try {
	// 			let id = parseId(v)
	// 			switch (id.kind) {
	// 				case IdKind.Crate:
	// 					let found = this.availableRes.find(it => it.type == ResType.Crate && it.id == id.id)
	// 					if (found) {
	// 						this.addItem(found)
	// 					}
	// 					break
	// 				case IdKind.Pallet:
	// 					let foundp = this.availableRes.find(it => it.type == ResType.Pallet && it.id == id.id)
	// 					if (foundp) {
	// 						this.addItem(foundp)
	// 					}
	// 					break
	// 				case IdKind.List:
	// 					this.snack.open("Listen können nicht hierdurch gedruckt werden", undefined, {
	// 						duration: 4000
	// 					})
	// 					break
	// 			}
	// 		} catch (e) {
	// 			if (e instanceof Error) {
	// 				this.snack.open(`Wahrscheinlich keine box: ${e.message}`, undefined, {
	// 					duration: 2000
	// 				})
	// 			}
	// 		}
	// 	})
	// }
}
