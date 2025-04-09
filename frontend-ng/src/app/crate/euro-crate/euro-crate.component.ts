import {Component, OnInit} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {EuroCrateDto} from '../../api/models/euro-crate-dto';
import {LocationDto} from '../../api/models/location-dto';
import {LocationComponent} from '../../location/location.component';
import {RouterLink} from '@angular/router';

@Component({
	selector: 'app-euro-crate',
	imports: [
		LocationComponent,
		RouterLink
	],
	templateUrl: './euro-crate.component.html',
	styleUrl: './euro-crate.component.scss'
})
export class EuroCrateComponent implements OnInit {
	protected crates?: EuroCrateDto[];

	constructor(
		private apiService: ApiService
	) {
	}

	ngOnInit() {
		this.apiService.getAllEuroCrates().subscribe(res => {
			this.crates = res;
		})
	}
}
