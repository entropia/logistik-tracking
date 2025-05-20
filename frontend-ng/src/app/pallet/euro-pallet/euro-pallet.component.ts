import {Component, OnInit, ViewChild} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {EuroPalletDto} from '../../api/models/euro-pallet-dto';
import {MatButton, MatMiniFabButton} from '@angular/material/button';
import {Router, RouterLink} from '@angular/router';
import {LocationComponent} from '../../location/location/location.component';
import {MatDialog} from '@angular/material/dialog';
import {NewEuroPalletDto} from '../../api/models/new-euro-pallet-dto';
import {handleDefaultError} from '../../util/auth';
import {AuthorityEnumDto} from '../../api/models/authority-enum-dto';
import {RequiresAuthorityDirective} from '../../util/requires-permission.directive';
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
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatSort, MatSortHeader} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {AuthorityStatus, UserService} from '../../util/user.service';
import {MatIcon} from '@angular/material/icon';

@Component({
	selector: 'app-euro-pallet',
	imports: [
		LocationComponent,
		RouterLink,
		MatButton,
		RequiresAuthorityDirective,
		RequiresAuthorityDirective,

		MatMiniFabButton, RouterLink, MatFormField, MatIcon, MatInput, MatTable, MatSort, MatColumnDef, MatHeaderCell, MatCell, MatHeaderCellDef, MatCellDef,
		MatSortHeader, MatHeaderRow, MatRow, MatNoDataRow, MatRowDef, MatHeaderRowDef, MatPaginator,
		MatMiniFabButton,
		MatLabel
	],
	templateUrl: './euro-pallet.component.html',
	styleUrl: './euro-pallet.component.scss'
})
export class EuroPalletComponent implements OnInit {
	euroPallets?: EuroPalletDto[];
	protected displayedColumns = [
		'euroPalletId', 'location'
	]
	protected ds: MatTableDataSource<EuroPalletDto> = new MatTableDataSource<EuroPalletDto>();

	constructor(
		private apiService: ApiService,
		private router: Router,
		private diag: MatDialog,
		us: UserService
	) {
		us.hasAuthority(AuthorityEnumDto.Print).then(it => {
			if (it == AuthorityStatus.HasIt) this.displayedColumns.push("actions")
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

	ngOnInit(): void {
		this.apiService.getAllEuroPallets().subscribe({
			next: euroPallets => {
				this.euroPallets = euroPallets;
				this.ds.data = euroPallets;
			}
		})
	}

	applyFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.ds.filter = filterValue.trim().toLowerCase();

		if (this.ds.paginator) {
			this.ds.paginator.firstPage();
		}
	}

	createPallet() {
		import("../create-euro-pallet/create-euro-pallet.component").then(it => {
			this.diag.open<any, any, NewEuroPalletDto>(it.CreateEuroPalletComponent)
				.afterClosed()
				.subscribe(value => {
					if (!value) return;
					// console.log(value)
					this.apiService.createEuroPallet({
						body: value
					}).subscribe({
						next: pallet => {
							this.router.navigate(['euroPallet/' + pallet.euroPalletId])
								.catch(reason => {
									console.log("Failed to redirect to newly created pallet because: " + reason);
								});
						},
						error: handleDefaultError
					});
				})
		})

	}

	printPallet(euroPallet: EuroPalletDto) {
		this.apiService.printEuroPallet({
			euroPalletId: euroPallet.euroPalletId
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

	protected readonly AuthorityEnumDto = AuthorityEnumDto;
}
