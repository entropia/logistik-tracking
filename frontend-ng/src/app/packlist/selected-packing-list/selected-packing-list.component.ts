import {Component, Input, ViewChild} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {PackingListDto} from '../../api/models/packing-list-dto';
import {RouterLink} from '@angular/router';
import {FormsModule, NgForm, NgModel} from '@angular/forms';
import {DeliveryStateEnumDto, EuroCrateDto} from '../../api/models';
import {MatFormField} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatButton, MatMiniFabButton} from '@angular/material/button';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {CrateAddWidgetComponent} from '../../util/crate-add-widget/crate-add-widget.component';
import {MatIcon} from '@angular/material/icon';
import {MatTooltip} from '@angular/material/tooltip';
import {forkJoin} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';

enum ItemStatus {
	KEEP,
	ADDED,
	REMOVED,
	TRANSFERRED
}

type TheItem = {
	status: ItemStatus,
	originalStatus: ItemStatus,
} & EuroCrateDto;

@Component({
	selector: 'app-selected-packing-list',
	imports: [
		RouterLink,
		FormsModule,
		MatFormField,
		MatSelect,
		MatOption,
		MatButton,
		MatTableModule,
		MatSortModule,
		CrateAddWidgetComponent,
		MatMiniFabButton,
		MatIcon,
		MatTooltip
	],
	templateUrl: './selected-packing-list.component.html',
	styleUrl: './selected-packing-list.component.scss'
})
export class SelectedPackingListComponent {
	@Input()
	set id(which: number) {
		this.apiService.getPackingList({
			packingListId: which
		}).subscribe({
			next: it => {
				this.selected = it;
				this.items = it.packedCrates!.map(it => {
					return {
						status: ItemStatus.KEEP,
						originalStatus: ItemStatus.KEEP,
						...it
					}
				})
				this.source.data = this.items;
				// this.source.sort = this.sort!;
			},
			error: e => {
				alert("failed to load packing list! see console")
				console.error(e)
			}
		})
	}

	@ViewChild("theForm", {static: false})
	theForm?: NgForm;

	@ViewChild("deliveryStateControl", {static:false})
	deliveryStateControl?: NgModel;

	items: TheItem[] = [];

	@ViewChild(MatSort, {static: false})
	set sort(sort: MatSort) {
		console.log(sort)
		this.source.sort = sort;
	}

	protected selected?: PackingListDto;
	constructor(
		private apiService: ApiService,
		private snackbar: MatSnackBar
	) {
	}

	saveIt() {
		let runs = []
		if (this.deliveryStateControl!.dirty) {
			runs.push(this.apiService.changeDeliveryStateOfPackingList({
				packingListId: this.selected!.packingListId,
				body: this.selected!
			}))
		}
		for(let item of this.items) {
			switch (item.status) {
				case ItemStatus.ADDED:
					runs.push(this.apiService.addEuroCrateToPackingList({
						packingListId: this.selected!.packingListId,
						operationCenter: item.operationCenter,
						euroCrateName: item.name,
						force: false
					}))
					item.status = ItemStatus.KEEP
					break;
				case ItemStatus.TRANSFERRED:
					runs.push(this.apiService.addEuroCrateToPackingList({
						packingListId: this.selected!.packingListId,
						operationCenter: item.operationCenter,
						euroCrateName: item.name,
						force: true
					}))
					item.status = ItemStatus.KEEP
					break;
				case ItemStatus.REMOVED:
					runs.push(this.apiService.removeEuroCrateFromPackingList({
						packingListId: this.selected!.packingListId,
						operationCenter: item.operationCenter,
						euroCrateName: item.name
					}))
					break;
			}
		}
		forkJoin(runs).subscribe({
			next: _ => {
				this.items = this.items.filter(f => f.status != ItemStatus.REMOVED)
				this.source.data = this.items;
				this.snackbar.open("Gespeichert!", undefined, {
					duration: 3000
				})
			},
			error: e => {
				if (e instanceof Error) {
					alert(`Failed to save! Check console: ${e.message}`)
				} else {
					alert("Failed to save! Check console")
				}
				console.error(e)
			}
		})
	}

	protected readonly Object = Object;
	protected readonly DeliveryStateEnumDto = DeliveryStateEnumDto;

	displayedColumns: string[] = ['status', 'operationCenter', 'name', 'actions'];
	source = new MatTableDataSource<TheItem>(undefined);

	crateSubmitted($event: EuroCrateDto) {
		let i;
		if ((i = this.items.findIndex(e => e.operationCenter == $event.operationCenter && e.name == $event.name)) != -1) {
			let existing = this.items[i]
			if (existing.status == ItemStatus.REMOVED) {
				existing.status = existing.originalStatus;
			}
		} else {
			this.apiService.getPackingListsOfCrate({
				euroCrateName: $event.name,
				operationCenter: $event.operationCenter
			}).subscribe({
				next: _ => {
					this.items.push({
						status: ItemStatus.TRANSFERRED,
						originalStatus: ItemStatus.TRANSFERRED,
						...$event
					})
					this.source.data = this.items;
				},
				error: err => {
					if (err instanceof HttpErrorResponse && err.status == 404) {
						// somewhat intended, now we know we dont have any list crate fighting us for this one
						this.items.push({
							status: ItemStatus.ADDED,
							originalStatus: ItemStatus.ADDED,
							...$event
						})
						this.source.data = this.items;
					} else {
						console.error(err)
						alert(`Unbekannter fehler beim suchen nach Packlisten zu der gegebenen Box! ${err}`)
					}
				},
			})

			this.theForm!.control.markAsDirty()
		}
	}

	remove(element: TheItem) {
		if (element.originalStatus == ItemStatus.ADDED || element.originalStatus == ItemStatus.TRANSFERRED) {
			this.items.splice(this.items.indexOf(element), 1)
			this.source.data = this.items;
		}
		else {
			element.status = ItemStatus.REMOVED;
			this.theForm!.control.markAsDirty()
		}
	}

	protected readonly ItemStatus = ItemStatus;
}
