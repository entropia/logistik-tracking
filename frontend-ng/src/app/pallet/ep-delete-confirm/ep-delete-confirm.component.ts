import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {VeryBasicPackingListDto} from '../../api/models/very-basic-packing-list-dto';
import {RouterLink} from '@angular/router';
import {MatCheckbox} from '@angular/material/checkbox';
import {FormsModule} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {concatMap, delay, from} from 'rxjs';
import {map} from 'rxjs/operators';
import {ApiService} from '../../api/services/api.service';
import {handleDefaultError} from '../../util/auth';

type DeleteTrackedThing = VeryBasicPackingListDto & {
	deleted: boolean;
}

@Component({
  selector: 'app-ep-delete-confirm',
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		RouterLink,
		MatCheckbox,
		FormsModule,
		MatButton
	],
  templateUrl: './ep-delete-confirm.component.html',
  styleUrl: './ep-delete-confirm.component.scss'
})
export class EpDeleteConfirmComponent {
	dt: DeleteTrackedThing[]
	yeahImSure = false
	doingThings: boolean = false
	constructor(private diag: MatDialogRef<EpDeleteConfirmComponent>, @Inject(MAT_DIALOG_DATA) data: VeryBasicPackingListDto[],
				private api: ApiService) {
		this.dt = data.map(it => {
			return {
				...it,
				deleted: false
			}
		})
	}

	cancel() {
		this.diag.close(false)
	}

	delete() {
		this.doingThings = true
		from(this.dt).pipe(
			concatMap(it => {
				return this.api.deletePackingList({
					packingListId: it.packingListId
				}).pipe(delay(100), map(v => it))
			})
		).subscribe({
			next: v => {
				v.deleted = true
			},
			error: e => {
				this.doingThings = false
				handleDefaultError(e)
			},
			complete: () => {
				this.diag.close(true)
			}
		})
	}
}
