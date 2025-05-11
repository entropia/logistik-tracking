import {Component, OnDestroy, ViewChild} from '@angular/core';
import {ApiService} from '../api/services/api.service';
import {EuroCrateDto} from '../api/models/euro-crate-dto';
import {handleDefaultError} from '../util/auth';
import {EuroPalletDto} from '../api/models/euro-pallet-dto';
import {LocationTypeDto} from '../api/models/location-type-dto';
import {LogisticsLocationDto} from '../api/models/logistics-location-dto';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {formatLocationString, LocationComponent} from '../location/location/location.component';
import {RouterLink} from '@angular/router';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {FormsModule} from '@angular/forms';
import {DeliveryStateEnumDto} from '../api/models/delivery-state-enum-dto';
import {BasicPackingListDto} from '../api/models/basic-packing-list-dto';

function getDataForSortEC(original: (d: EuroCrateDto, a: string) => number | string, data: EuroCrateDto, active: string): number | string {
	if (active == "location") {
		return formatLocationString(data.location)
	} else return original(data, active)
}

function customFilterPredicateEC(data: EuroCrateDto | EuroPalletDto, filter: string): boolean {
	const transformedFilter = filter.trim().toLowerCase();
	// Loops over the values in the array and returns true if any of them match the filter string
	if (formatLocationString(data.location).toLowerCase().includes(transformedFilter)) return true;
	return Object.entries(data as {[key: string]: any}).some(value =>
		value[0] != "location" && `${value[1]}`.toLowerCase().includes(transformedFilter)
	);
}

function getDataForSortEP(original: (d: EuroPalletDto, a: string) => number | string, data: EuroPalletDto, active: string): number | string {
	if (active == "location") {
		return formatLocationString(data.location)
	} else return original(data, active)
}

@Component({
  selector: 'app-overview-page',
	imports: [
		MatTableModule,
		MatPaginatorModule,
		MatSortModule,
		LocationComponent,
		RouterLink,
		MatButtonToggleModule,
		FormsModule
	],
  templateUrl: './overview-page.component.html',
  styleUrl: './overview-page.component.scss'
})
export class OverviewPageComponent implements OnDestroy {
	constructor(
		private api: ApiService
	) {
		this.dsEuroCrates.sortingDataAccessor = getDataForSortEC.bind(getDataForSortEC, this.dsEuroCrates.sortingDataAccessor.bind(this.dsEuroCrates))
		this.dsEuroCrates.filterPredicate = customFilterPredicateEC.bind(customFilterPredicateEC)
		this.dsEuroPallet.sortingDataAccessor = getDataForSortEP.bind(getDataForSortEP, this.dsEuroPallet.sortingDataAccessor.bind(this.dsEuroPallet))
		this.dsEuroPallet.filterPredicate = customFilterPredicateEC.bind(customFilterPredicateEC)
		this.updateData();
		this.modifyRefreshTimer(this.refreshTimer)
	}

	viewColsEuroCrate = ["internalId", "operationCenter", "name", "location", "deliveryState"]
	viewColsEuroPallet = ["euroPalletId", "location", "packlisten"]
	viewColsLists = ["packingListId", "packingListName", "deliveryState"]

	euroCrates: EuroCrateDto[] = [];
	pallets: EuroPalletDto[] = [];

	refreshTimer: number = 5;

	@ViewChild("matSortCrates")
	set matSortCrates(matSortCrates: MatSort) {
	    this.dsEuroCrates.sort = matSortCrates
	}

	@ViewChild("matPaginatorCrates")
	set matPaginatorCrates(matPaginatorCrates: MatPaginator) {
	    this.dsEuroCrates.paginator =matPaginatorCrates;
	}

	@ViewChild("matSortPallet")
	set matSortPallet(matSortCrates: MatSort) {
		this.dsEuroPallet.sort = matSortCrates
	}

	@ViewChild("matPaginatorPallet")
	set matPaginatorPallet(matPaginatorCrates: MatPaginator) {
		this.dsEuroPallet.paginator =matPaginatorCrates;
	}

	@ViewChild("matSortLists")
	set matSortLists(matSortCrates: MatSort) {
		this.dsPacklisten.sort = matSortCrates
	}

	@ViewChild("matPaginatorLists")
	set matPaginatorListen(matPaginatorCrates: MatPaginator) {
		this.dsPacklisten.paginator =matPaginatorCrates;
	}


	dsEuroCrates = new MatTableDataSource<EuroCrateDto>()
	dsEuroPallet = new MatTableDataSource<EuroPalletDto>()
	dsPacklisten = new MatTableDataSource<BasicPackingListDto>()

	updateData() {
		this.api.getAllEuroCrates().subscribe({
			next: crates => {
				this.euroCrates = crates
				this.dsEuroCrates.data = crates
			},
			error: handleDefaultError
		})
		this.api.getAllEuroPallets().subscribe({
			next: pallets => {
				this.pallets = pallets
				this.dsEuroPallet.data = pallets
			},
			error: handleDefaultError
		})
		this.api.getAllPackingLists().subscribe({
			next: c => {
				this.dsPacklisten.data = c
			},
			error: handleDefaultError
		})
	}

	getAmountCratesInEntropia() {
		return this.euroCrates.filter(it => it.location.locationType == LocationTypeDto.Logistics && it.location.logisticsLocation == LogisticsLocationDto.Entropia).length
	}
	getAmountCratesInTransit() {
		return this.euroCrates.filter(it => it.location.locationType == LocationTypeDto.Logistics && it.location.logisticsLocation == LogisticsLocationDto.InTransport).length
	}
	getAmountCratesInGpnLimbo() {
		return this.euroCrates.filter(it => it.location.locationType == LocationTypeDto.Logistics && it.location.logisticsLocation == LogisticsLocationDto.Loc).length
	}

	prevTimeout?: number

	modifyRefreshTimer($event: number) {
		if (this.prevTimeout != undefined) {
			window.clearInterval(this.prevTimeout)
		}
		if ($event > 0) {
			this.prevTimeout = window.setInterval(() => {
				this.updateData()
			}, $event * 1000)
		} else {
			this.prevTimeout = undefined;
		}
	}
	ngOnDestroy() {
		if (this.prevTimeout != undefined) {
			window.clearInterval(this.prevTimeout)
		}
	}

	getPacklistenText(row: EuroPalletDto) {
		return this.dsPacklisten.data.filter(f => f.packedOn.euroPalletId == row.euroPalletId)
			// fertige listen nach hinten schieben
			.sort((a, b) => (a.deliveryState == DeliveryStateEnumDto.Delivered ? 1 : 0) - (b.deliveryState == DeliveryStateEnumDto.Delivered ? 1 : 0))
			.map(e => e.packingListName+" ("+e.packingListId+(e.deliveryState == DeliveryStateEnumDto.Delivered ? ", Ausg." : "")+")").join(", ");
	}
}
