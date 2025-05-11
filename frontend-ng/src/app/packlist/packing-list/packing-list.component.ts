import {Component, OnInit, ViewChild} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {BasicPackingListDto} from '../../api/models/basic-packing-list-dto';
import {Router, RouterLink} from '@angular/router';
import {MatButton, MatMiniFabButton} from '@angular/material/button';
import {MatDialog} from '@angular/material/dialog';
import {NewPackingListDto} from '../../api/models/new-packing-list-dto';
import {RequiresAuthorityDirective} from '../../util/requires-permission.directive';
import {AuthorityEnumDto} from '../../api/models/authority-enum-dto';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatInputModule} from '@angular/material/input';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {MatIconModule} from '@angular/material/icon';
import {AuthorityStatus, UserService} from '../../util/user.service';
import {handleDefaultError} from '../../util/auth';

@Component({
	selector: 'app-packing-list', imports: [
		MatButton, RequiresAuthorityDirective,
		MatFormFieldModule,
		MatTableModule,
		MatInputModule,
		MatSortModule,
		MatPaginatorModule,
		MatIconModule,
		MatMiniFabButton, RouterLink,

	], templateUrl: './packing-list.component.html', styleUrl: './packing-list.component.scss'
})
export class PackingListComponent implements OnInit {
	protected packingLists?: BasicPackingListDto[];
	protected displayedColumns = [
		'packingListId', 'packingListName', 'deliveryState'
	]
	protected ds: MatTableDataSource<BasicPackingListDto> = new MatTableDataSource<BasicPackingListDto>();

	constructor(private apiService: ApiService, private router: Router, private diag: MatDialog, user: UserService)
	{
		user.hasAuthority(AuthorityEnumDto.Print).then(it => {
			if (it == AuthorityStatus.HasIt) this.displayedColumns.push("actions")
		})
	}

	ngOnInit(): void {
		this.apiService.getAllPackingLists().subscribe({
			next: packingLists => {
				this.packingLists = packingLists;
				this.ds.data = packingLists;
			}
		})
	}

	@ViewChild(MatSort)
	set matSort(matSort: MatSort) {
		this.ds.sort = matSort;
	}

	@ViewChild(MatPaginator)
	set paginator(paginator: MatPaginator) {
		this.ds.paginator = paginator;
	}

	applyFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.ds.filter = filterValue.trim().toLowerCase();

		if (this.ds.paginator) {
			this.ds.paginator.firstPage();
		}
	}

	createPackingList() {
		import("../create-packing-list/create-packing-list.component").then(e => {
			this.diag.open<any, any, NewPackingListDto>(e.CreatePackingListComponent)
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
		})
	}

	protected readonly AuthorityEnumDto = AuthorityEnumDto;

	printPackingList(row: BasicPackingListDto) {
		this.apiService.printPackingList({
			packingListId: row.packingListId
		}).subscribe({
			next: v => {
				let ou = URL.createObjectURL(v)
				let opened = window.open(ou, "_blank")
				if (!opened) {
					alert("Konnte kein Fenster öffnen! Bitte erlaube für diese Webseite popups.")
				}
				URL.revokeObjectURL(ou)
			},
			error: handleDefaultError
		})
	}
}
