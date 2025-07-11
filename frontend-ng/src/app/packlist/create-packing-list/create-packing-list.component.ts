import {Component, Inject, OnInit} from '@angular/core';
import {MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NewPackingListDto} from '../../api/models/new-packing-list-dto';
import {ApiService} from '../../api/services/api.service';
import {DIALOG_DATA} from '@angular/cdk/dialog';
import {NgForOf} from '@angular/common';
import {MatOption, MatSelect} from '@angular/material/select';

@Component({
	selector: 'app-create-euro-pallet',
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,

		MatFormField,
		MatInput,
		FormsModule,
		ReactiveFormsModule,
		MatLabel,
		MatOption,
		MatSelect,
		NgForOf,
		MatButton,
	],
	templateUrl: './create-packing-list.component.html',
	styleUrl: './create-packing-list.component.scss'
})
export class CreatePackingListComponent implements OnInit {
	readonly form;
	euroPallets: { palletId: number, information: string }[] = [{
		palletId: -1,
		information: 'Paletten werden geladen...'
	}];

	constructor(
		private dialogRef: MatDialogRef<CreatePackingListComponent>,
		private apiService: ApiService,
		@Inject(DIALOG_DATA) data?: {
			packedOnPallet?: number
		}
	) {
		this.form = new FormGroup({
			name: new FormControl<string>("", { nonNullable: true }),
			packedOnPallet: new FormControl<number>(data?.packedOnPallet ?? -1, { nonNullable: true })
		});
	}

	ngOnInit(): void {
        this.apiService.getAllEuroPallets().subscribe({
			next: (euroPallets) => {
				this.euroPallets = euroPallets.map(pallet => ({
					palletId: pallet.euroPalletId,
					information: pallet.name
				}));
			}
		});
    }

	cancel() {
		this.dialogRef.close(null)
	}

	handleSubmit() {
		let packedOnPallet = this.form.value.packedOnPallet!;
		if (packedOnPallet == -1) {
			return;
		}

		let packingList: NewPackingListDto = {
			name: this.form.value.name!,
			packedOnPallet: packedOnPallet
		};
		this.dialogRef.close(packingList)
	}
}
