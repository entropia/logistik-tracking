import {Component} from '@angular/core';
import {MatBottomSheetRef} from '@angular/material/bottom-sheet';
import {MatListItem, MatNavList} from '@angular/material/list';
import {MatLine} from '@angular/material/core';

export enum Action {
	Delivered,
	ResetDelivery,
	BroughtBackToLoc
}

@Component({
  selector: 'app-crate-settings-sheet',
	imports: [
		MatNavList,
		MatListItem,
		MatLine

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
