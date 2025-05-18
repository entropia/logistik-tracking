import {Component, Inject} from '@angular/core';
import {MAT_BOTTOM_SHEET_DATA, MatBottomSheetRef} from '@angular/material/bottom-sheet';
import {MatListItem, MatListItemTitle, MatNavList} from '@angular/material/list';
import {MatLine} from '@angular/material/core';
import {EuroCrateDto} from '../../api/models/euro-crate-dto';

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
		MatListItemTitle,
		MatLine

	],
  templateUrl: './crate-settings-sheet.component.html',
  styleUrl: './crate-settings-sheet.component.scss'
})
export class CrateSettingsSheetComponent {
	constructor(
		private ref: MatBottomSheetRef<CrateSettingsSheetComponent>,
		@Inject(MAT_BOTTOM_SHEET_DATA) public data: EuroCrateDto
	) {
	}
	doAction(a?: Action) {
		this.ref.dismiss(a)
	}

	protected readonly Action = Action;
}
