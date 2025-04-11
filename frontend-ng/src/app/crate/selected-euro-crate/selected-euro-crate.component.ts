import {Component, OnInit} from '@angular/core';
import { ApiService } from '../../api/services';
import {OperationCenterDto} from '../../api/models/operation-center-dto';
import {EuroCrateDto} from '../../api/models/euro-crate-dto';
import {ActivatedRoute} from '@angular/router';
import {LocationComponent} from '../../util/location/location.component';

@Component({
	selector: 'app-selected-euro-crate',
	imports: [
		LocationComponent
	],
	templateUrl: './selected-euro-crate.component.html',
	styleUrl: './selected-euro-crate.component.scss'
})
export class SelectedEuroCrateComponent implements OnInit {
    crate?: EuroCrateDto;

	constructor(
		private apiService: ApiService,
		private route: ActivatedRoute
	) {
	}

	ngOnInit() {
		// hacky aber geht
		let name: string, oc: OperationCenterDto;
		let seen = false;
		this.route.params.forEach(it => {
			if (seen) return;
			seen = true;

			name = it["name"] as string
			oc = it["oc"] as OperationCenterDto

			this.apiService.getEuroCrate({
				euroCrateName: name!,
				operationCenter: oc!
			}).subscribe({
				next: ec => {
					this.crate = ec;
				},
				error: err => {
					alert("Failed to load crate. See console for error")
					console.error(err)
				}
			})
		})
	}

	getInfos(): string {
		let existing = this.crate?.information ?? "";
		existing = existing.trim()
		if (existing.length == 0) return "Keine Informationen";
		return existing;
	}
}
