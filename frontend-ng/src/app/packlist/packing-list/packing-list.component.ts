import {Component, OnInit} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {BasicPackingListDto} from '../../api/models/basic-packing-list-dto';
import {NgForOf} from '@angular/common';
import {RouterLink} from '@angular/router';

@Component({
	selector: 'app-packing-list',
	imports: [
		NgForOf,
		RouterLink
	],
	templateUrl: './packing-list.component.html',
	styleUrl: './packing-list.component.scss'
})
export class PackingListComponent implements OnInit {
	protected packingLists?: BasicPackingListDto[];

	constructor(
		private apiService: ApiService
	) {
	}

	ngOnInit(): void {
		this.apiService.getAllPackingLists().subscribe({
			next: packingLists => {
				this.packingLists = packingLists;
			}
		})
	}
}
