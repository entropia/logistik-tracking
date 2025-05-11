import {formatLocationString} from '../location/location/location.component';
import {LocationDto} from '../api/models/location-dto';
import {MatTableDataSource} from '@angular/material/table';

export type HasLocation = {
	location: LocationDto;
}

export function tableSorterRespectingLocation<T extends HasLocation>(original: (d: T, a: string) => number | string, data: T, active: string): number | string {
	if (active == "location") {
		return formatLocationString(data.location)
	} else return original(data, active)
}

export function tableFilterRespectingLocation<T extends HasLocation>(original: (data: T, f: string) => boolean, data: T, filter: string): boolean {
	const transformedFilter = filter.trim().toLowerCase();
	// Loops over the values in the array and returns true if any of them match the filter string
	if (formatLocationString(data.location).toLowerCase().includes(transformedFilter)) return true;
	// shitty hack aber muss leider so um den filter funktionieren zu lassen
	let withoutLocation = Object.fromEntries(Object.entries(data).filter(f => f[0] != "location")) as T
	return original(withoutLocation, filter)
}

export function applyFiltersTo<T extends HasLocation>(ds: MatTableDataSource<T>) {
	let original = ds.filterPredicate;
	ds.filterPredicate = (data: T, f: string) => tableFilterRespectingLocation<T>(original, data, f);
	let originalSD = ds.sortingDataAccessor;
	ds.sortingDataAccessor = (data: T, active: string) => tableSorterRespectingLocation<T>(originalSD, data, active)
}
