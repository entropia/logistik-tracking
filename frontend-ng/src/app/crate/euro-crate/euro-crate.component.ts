import {Component, OnInit} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {EuroCrateDto} from '../../api/models/euro-crate-dto';
import {LocationComponent} from '../../location/location/location.component';
import {Router, RouterLink} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {CreateEuroCrateComponent} from '../create-euro-crate/create-euro-crate.component';
import {NewEuroCrateDto} from '../../api/models/new-euro-crate-dto';
import {MatButton} from '@angular/material/button';
import {handleDefaultError} from '../../util/auth';
import {RequiresAuthorityDirective} from '../../util/requires-permission.directive';
import {AuthorityEnumDto} from '../../api/models/authority-enum-dto';

@Component({
	selector: 'app-euro-crate',
	imports: [
		LocationComponent,
		RouterLink,
		MatButton,
		RequiresAuthorityDirective
	],
	templateUrl: './euro-crate.component.html',
	styleUrl: './euro-crate.component.scss'
})
export class EuroCrateComponent implements OnInit {
	protected crates?: EuroCrateDto[];

	constructor(
		private apiService: ApiService,
		private router: Router,
		private diag: MatDialog
	) {
	}

	ngOnInit() {
		this.apiService.getAllEuroCrates().subscribe({
			next: res => {
				this.crates = res;
			},
			error: handleDefaultError
		})
	}

	createCrate() {
		this.diag.open<CreateEuroCrateComponent, any, NewEuroCrateDto>(CreateEuroCrateComponent)
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
	}

	protected readonly AuthorityEnumDto = AuthorityEnumDto;
}
