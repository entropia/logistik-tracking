import {Component, OnInit, ViewChild} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {EuroCrateDto} from '../../api/models/euro-crate-dto';
import {LocationComponent} from '../../location/location/location.component';
import {Router, RouterLink} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {NewEuroCrateDto} from '../../api/models/new-euro-crate-dto';
import {MatButton} from '@angular/material/button';
import {handleDefaultError} from '../../util/auth';
import {RequiresAuthorityDirective} from '../../util/requires-permission.directive';
import {AuthorityEnumDto} from '../../api/models/authority-enum-dto';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {applyFiltersTo} from '../../util/tables';
import {MatIcon} from '@angular/material/icon';

@Component({
	selector: 'app-euro-crate',
	imports: [
		LocationComponent,
		RouterLink,
		MatButton,
		RequiresAuthorityDirective,
		MatTableModule,
		MatFormFieldModule,
		MatInputModule,
		MatSortModule,
		MatPaginatorModule,
		MatIcon
	],
	templateUrl: './euro-crate.component.html',
	styleUrl: './euro-crate.component.scss'
})
export class EuroCrateComponent implements OnInit {
	protected displayedColumns = [
		'internalId', 'operationCenter', 'name', 'location'
	]
	protected crates?: EuroCrateDto[];
	protected ds: MatTableDataSource<EuroCrateDto> = new MatTableDataSource<EuroCrateDto>();

	constructor(
		private apiService: ApiService,
		private router: Router,
		private diag: MatDialog
	) {
		applyFiltersTo(this.ds)
	}

	@ViewChild(MatSort)
	set matSort(matSort: MatSort) {
	    this.ds.sort = matSort;
	}

	@ViewChild(MatPaginator)
	set paginator(paginator: MatPaginator) {
	    this.ds.paginator = paginator;
	}

	ngOnInit() {
		this.apiService.getAllEuroCrates().subscribe({
			next: res => {
				this.crates = res;
				this.ds.data = res;
			},
			error: handleDefaultError
		})
	}

	applyFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.ds.filter = filterValue.trim().toLowerCase();

		if (this.ds.paginator) {
			this.ds.paginator.firstPage();
		}
	}

	createCrate() {
		import('../create-euro-crate/create-euro-crate.component').then(it => {
			this.diag.open<any, any, NewEuroCrateDto>(it.CreateEuroCrateComponent)
				.afterClosed()
				.subscribe(value => {
					if (!value) return;
					this.apiService.createNewEuroCrate({
						body: value
					}).subscribe({
						next: crate => {
							this.router.navigate(['euroCrate/' + crate.internalId])
								.catch(reason => {
									console.log("Failed to redirect to newly created crate because: " + reason);
								});
						}
					});
				})
		})
	}

	protected readonly AuthorityEnumDto = AuthorityEnumDto;
}
