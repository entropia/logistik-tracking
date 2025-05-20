import {Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {QrScannerService} from '../../qr-scanner.service';
import {handleDefaultError} from '../../util/auth';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {PackingListDto} from '../../api/models/packing-list-dto';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatNoDataRow,
	MatRow,
	MatRowDef,
	MatTable,
	MatTableDataSource
} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatSort, MatSortHeader} from '@angular/material/sort';
import {EuroCrateDto} from '../../api/models/euro-crate-dto';
import {LocationTypeDto} from '../../api/models/location-type-dto';
import {LocationDto} from '../../api/models/location-dto';
import {MatBottomSheet} from '@angular/material/bottom-sheet';
import {Action, CrateSettingsSheetComponent} from '../crate-settings-sheet/crate-settings-sheet.component';
import {DeliveryStateEnumDto} from '../../api/models/delivery-state-enum-dto';
import {LogisticsLocationDto} from '../../api/models/logistics-location-dto';
import {MatButton, MatFabButton} from '@angular/material/button';
import {extractIdFromUrl, parseCrateId} from '../../util/qr-id-parser';
import {MatIcon} from '@angular/material/icon';
import {LocationComponent} from '../../location/location/location.component';
import {MatDialog} from '@angular/material/dialog';
import {AreYouSureComponent, Choice, ConfirmScreenConfig} from '../../are-you-sure/are-you-sure.component';

function stringifyLocation(location: LocationDto) {
	switch (location.locationType) {
		case LocationTypeDto.SomewhereElse:
			return location.somewhereElse!;
		case LocationTypeDto.Logistics:
			return location.logisticsLocation!;
		case LocationTypeDto.AtOperationCenter:
			return location.operationCenter!;
		default:
			throw new Error("invalid location type? "+location.locationType)
	}
}

function getDataForSort(original: (d: EuroCrateDto, a: string) => number | string, data: EuroCrateDto, active: string): number | string {
	if (active == "location") {
		let location = data.location;
		return stringifyLocation(location)
	} else return original(data, active)
}

function customFilterPredicate(data: EuroCrateDto, filter: string): boolean {
	const transformedFilter = filter.trim().toLowerCase();
	// Loops over the values in the array and returns true if any of them match the filter string
	if (stringifyLocation(data.location).toLowerCase().includes(transformedFilter)) return true;
	return Object.entries(data as {[key: string]: any}).some(value =>
		value[0] != "location" && `${value[1]}`.toLowerCase().includes(transformedFilter)
	);
}

@Component({
  selector: 'app-packliste-auslieferung',
	imports: [
		LocationComponent,
		MatButton, MatFormField, MatIcon, MatInput, MatTable, MatSort, MatColumnDef, MatHeaderCell, MatCell, MatHeaderCellDef, MatCellDef,
		MatSortHeader, MatHeaderRow, MatRow, MatNoDataRow, MatRowDef, MatHeaderRowDef, MatPaginator, MatProgressSpinner,
		MatLabel, MatFabButton
	],
  templateUrl: './packliste-auslieferung.component.html',
  styleUrl: './packliste-auslieferung.component.scss'
})
export class PacklisteAuslieferungComponent implements OnInit {

	@ViewChild("errorMessage")
	errMsg!: TemplateRef<any>;

	@ViewChild(MatPaginator, {static: false})
	set paginator(paginator: MatPaginator) {
	    this.dataSource.paginator = paginator
	}

	@ViewChild(MatSort, {static: false})
	set sort(sort: MatSort) {
	    this.dataSource.sort = sort
	}

	displayedColumns: string[] = ["operationCenter", "name","location","deliveryState"];

	@Input()
	id!: number;

	list?: PackingListDto;

	dataSource: MatTableDataSource<EuroCrateDto> = new MatTableDataSource<EuroCrateDto>([]);

	beginDelivery() {
		this.apiService.modifyPackingList({
			packingListId: this.id,
			body: {
				deliveryState: DeliveryStateEnumDto.InDelivery
			}
		}).subscribe({
			next: _ => {
				this.list!.deliveryState = DeliveryStateEnumDto.InDelivery
			},
			error: handleDefaultError
		})
	}

	endDelivery() {
		if (this.list!.packedCrates!.some(it => it.deliveryState != DeliveryStateEnumDto.Delivered)) {
			this.diag.open<AreYouSureComponent, ConfirmScreenConfig, Choice>(AreYouSureComponent, {
				data: {
					body: this.errMsg,
					choices: [{
						title: "OK"
					}],
					title: "Fehler"
				}
			})
		}
		else {
			this.apiService.modifyPackingList({
				packingListId: this.id,
				body: {
					deliveryState: DeliveryStateEnumDto.Delivered
				}
			}).subscribe({
				next: _ => {
					this.list!.deliveryState = DeliveryStateEnumDto.Delivered
					this.list!.packedCrates!.forEach(it => it.deliveryState = DeliveryStateEnumDto.Delivered)
					this.snackbar.open("Fertig!", undefined, {
						duration: 4000
					})
				},
				error: handleDefaultError
			})
		}
	}

	constructor(
		private apiService: ApiService,
		private snackbar: MatSnackBar,
		private qr: QrScannerService,
		private bs: MatBottomSheet,
		private diag: MatDialog
	) {
		this.dataSource.sortingDataAccessor = getDataForSort.bind(getDataForSort, this.dataSource.sortingDataAccessor.bind(this.dataSource))
		this.dataSource.filterPredicate = customFilterPredicate.bind(customFilterPredicate)
	}

	ngOnInit() {
		this.apiService.getPackingList({
			packingListId: this.id
		}).subscribe({
			next: e => {
				this.list = e;
				this.dataSource.data = e.packedCrates!
			},
			error: handleDefaultError
		})
	}

	applyFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.dataSource.filter = filterValue.trim().toLowerCase();

		if (this.dataSource.paginator) {
			this.dataSource.paginator.firstPage();
		}
	}

	view(row: EuroCrateDto) {
		this.bs.open(CrateSettingsSheetComponent, {
			data: row
		}).afterDismissed().subscribe((v?: Action) => {
			if (v == Action.Delivered) {
				let nl = {
					locationType: LocationTypeDto.AtOperationCenter,
					operationCenter: row.operationCenter
				}
				this.apiService.modifyEuroCrate({
					id: row.internalId,
					body: {
						deliveryState: DeliveryStateEnumDto.Delivered,
						location: nl
					}
				}).subscribe({
					next: _ => {
						this.snackbar.open(`Kiste ${row.operationCenter}/${row.name} ausgeliefert!`, undefined, {
							duration: 4000
						})
						row.location = nl
						row.deliveryState = DeliveryStateEnumDto.Delivered
					},
					error: handleDefaultError
				})
			} else if (v == Action.ResetDelivery || v == Action.BroughtBackToLoc) {
				let nl: LocationDto = {
					locationType: LocationTypeDto.Logistics,
					logisticsLocation: LogisticsLocationDto.Loc
				}
				let ds = v == Action.ResetDelivery ? DeliveryStateEnumDto.InDelivery : DeliveryStateEnumDto.Delivered
				this.apiService.modifyEuroCrate({
					id: row.internalId,
					body: {
						deliveryState: ds,
						location: nl
					}
				}).subscribe({
					next: _ => {
						this.snackbar.open(`Kiste ${row.operationCenter}/${row.name} ${v == Action.ResetDelivery ? 'zurückgesetzt' : 'ausgeliefert'}!`, undefined, {
							duration: 4000
						})
						row.location = nl
						row.deliveryState = ds
					},
					error: handleDefaultError
				})
			}
		})
	}

	protected readonly DeliveryStateEnumDto = DeliveryStateEnumDto;

	doTheScan() {
		let rbr = this.qr.startScanning();
		rbr.onScanned.subscribe(v => {
			try {
				let id = parseCrateId(extractIdFromUrl(v))
				let the = this.list!.packedCrates!.find(it => it.internalId == id)
				if (the) {
					this.view(the)
					rbr.close()
				} else {
					this.snackbar.open(`Diese Kiste (${id}) ist nicht teil der Packliste!`, "OK", {
						duration: 5000
					})
				}
			} catch (e) {
				if (e instanceof Error) {
					this.snackbar.open(`Wahrscheinlich keine box: ${e.message}`, undefined, {
						duration: 2000
					})
				}
			}
		})
	}
}
