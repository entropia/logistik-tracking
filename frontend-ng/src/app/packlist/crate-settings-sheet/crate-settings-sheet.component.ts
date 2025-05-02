import { Component } from '@angular/core';
import {MatBottomSheetRef} from '@angular/material/bottom-sheet';
import {MatListModule} from '@angular/material/list';
import {MatLineModule} from '@angular/material/core';

export enum Action {
	Delivered,
	ResetDelivery,
	BroughtBackToLoc
}

@Component({
  selector: 'app-crate-settings-sheet',
	imports: [
		MatListModule,
		MatLineModule
	],
  templateUrl: './crate-settings-sheet.component.html',
  styleUrl: './crate-settings-sheet.component.scss'
})
export class CrateSettingsSheetComponent {
	constructor(
		private ref: MatBottomSheetRef<CrateSettingsSheetComponent>
	) {
	}
	doAction(a?: Action) {
		this.ref.dismiss(a)
	}

	protected readonly Action = Action;
}
