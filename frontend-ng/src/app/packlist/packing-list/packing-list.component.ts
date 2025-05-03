import {Component, OnInit} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {BasicPackingListDto} from '../../api/models/basic-packing-list-dto';
import {NgForOf} from '@angular/common';
import {Router, RouterLink} from '@angular/router';
import {MatButton} from '@angular/material/button';
import {MatDialog} from '@angular/material/dialog';
import {CreatePackingListComponent} from '../create-packing-list/create-packing-list.component';
import {NewPackingListDto} from '../../api/models/new-packing-list-dto';
import {RequiresAuthorityDirective} from '../../util/requires-permission.directive';
import {AuthorityEnumDto} from '../../api/models/authority-enum-dto';

@Component({
	selector: 'app-packing-list',
	imports: [
		NgForOf,
		RouterLink,
		MatButton,
		RequiresAuthorityDirective
	],
	templateUrl: './packing-list.component.html',
	styleUrl: './packing-list.component.scss'
})
export class PackingListComponent implements OnInit {
	protected packingLists?: BasicPackingListDto[];

	constructor(
		private apiService: ApiService,
		private router: Router,
		private diag: MatDialog
	) {
	}

	ngOnInit(): void {
		this.apiService.getAllPackingLists().subscribe({
			next: packingLists => {
				this.packingLists = packingLists;
			}
		})
	}

	createPackingList() {
		this.diag.open<CreatePackingListComponent, any, NewPackingListDto>(CreatePackingListComponent)
			.afterClosed()
			.subscribe(value => {
				if (!value) return;
				this.apiService.createPackingList({
					body: value
				}).subscribe({
					next: crate => {
						this.router.navigate(['packingList/' + crate.packingListId])
							.catch(reason => {
								console.log("Failed to redirect to newly created packing list because: " + reason);
							});
					}
				});
			});
	}

	protected readonly AuthorityEnumDto = AuthorityEnumDto;
}
