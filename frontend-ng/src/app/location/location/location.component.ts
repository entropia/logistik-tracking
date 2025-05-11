import {Component, Input} from '@angular/core';
import {LocationDto} from '../../api/models/location-dto';
import {LocationTypeDto} from '../../api/models';

export function formatLocationString(l: LocationDto) {
	switch (l.locationType) {
		case LocationTypeDto.SomewhereElse:
			return "Anderswo: "+l.somewhereElse!;
		case LocationTypeDto.Logistics:
			return "LOC: "+l.logisticsLocation!;
		case LocationTypeDto.AtOperationCenter:
			return "OC: "+l.operationCenter;
	}
}

@Component({
	selector: 'app-location',
	imports: [

	],
	templateUrl: './location.component.html',
	styleUrl: './location.component.scss'
})
export class LocationComponent {
	@Input() location!: LocationDto;

	protected readonly formatLocationString = formatLocationString;
}
