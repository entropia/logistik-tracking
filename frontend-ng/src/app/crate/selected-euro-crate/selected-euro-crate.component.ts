import {Component, Input, OnInit} from '@angular/core';
import { ApiService } from '../../api/services';
import {OperationCenterDto} from '../../api/models/operation-center-dto';
import {EuroCrateDto} from '../../api/models/euro-crate-dto';
import {ActivatedRoute, Route} from '@angular/router';

@Component({
	selector: 'app-selected-euro-crate',
	imports: [],
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
		let name: string, oc: OperationCenterDto;
		this.route.params.forEach(it => {
			name = it["name"] as string
			oc = it["oc"] as OperationCenterDto
		})
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
	}

}
