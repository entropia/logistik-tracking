import {Component, Input, ViewChild} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {PackingListDto} from '../../api/models/packing-list-dto';
import {RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {DeliveryStateEnumDto, EuroCrateDto} from '../../api/models';
import {MatFormField} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatButton} from '@angular/material/button';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {LocationComponent} from '../../util/location/location.component';
import {CrateAddWidgetComponent} from '../../util/crate-add-widget/crate-add-widget.component';

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
		LocationComponent,
		CrateAddWidgetComponent
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
				this.source.data = it.packedCrates!;
				// this.source.sort = this.sort!;
			},
			error: e => {
				alert("failed to load packing list! see console")
				console.error(e)
			}
		})
	}
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
		this.apiService.changeDeliveryStateOfPackingList({
			packingListId: this.selected!.packingListId,
			body: this.selected!
		}).subscribe({
			next: _ => {
				this.snackbar.open("Saved!", undefined, {
					duration: 3000
				})

			}
		})
	}

	protected readonly Object = Object;
	protected readonly DeliveryStateEnumDto = DeliveryStateEnumDto;

	displayedColumns: string[] = ['operationCenter', 'name', 'location'];
	source = new MatTableDataSource<EuroCrateDto>(undefined);

	addCrate() {
		// TODO
	}

	crateSubmitted($event: EuroCrateDto) {
// TODO
	}
}
