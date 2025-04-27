import {Component, OnInit, ViewChild} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {EuroPalletDto} from '../../api/models/euro-pallet-dto';
import {MatButton} from '@angular/material/button';
import {Router, RouterLink} from '@angular/router';
import {LocationComponent} from '../../util/location/location.component';
import {MatDialog} from '@angular/material/dialog';
import {CreateEuroPalletComponent} from '../create-euro-pallet/create-euro-pallet.component';
import {NewEuroPalletDto} from '../../api/models/new-euro-pallet-dto';
import {checkErrorAndAlertUser} from '../../util/auth';
import {AuthorityStatus, UserService} from '../../util/user.service';
import {AuthorityEnumDto} from '../../api/models/authority-enum-dto';

@Component({
	selector: 'app-euro-pallet',
	imports: [
		MatButton,
		LocationComponent,
		RouterLink
	],
	templateUrl: './euro-pallet.component.html',
	styleUrl: './euro-pallet.component.scss'
})
export class EuroPalletComponent implements OnInit {
	euroPallets?: EuroPalletDto[];

	@ViewChild("theButton", {static: false})
	set theBtn(e: MatButton) {
		this.users.hasAuthority(AuthorityEnumDto.ManageResources).then(yeah => {
			e.disabled = yeah != AuthorityStatus.HasIt
		})
	}

	constructor(
		private apiService: ApiService,
		private router: Router,
		private diag: MatDialog,
		private users: UserService
	) {
	}

	ngOnInit(): void {
		this.apiService.getAllEuroPallets().subscribe({
			next: euroPallets => {
				this.euroPallets = euroPallets;
			}
		})
	}

	createPallet() {
		this.diag.open<CreateEuroPalletComponent, any, NewEuroPalletDto>(CreateEuroPalletComponent)
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
					error: e => {
						console.error(e)
						if (!checkErrorAndAlertUser(e)) alert(`Failed to save pallet: ${e}`)
					}
				});
			})
	}

	printPallet(euroPallet: EuroPalletDto) {
		this.apiService.printEuroPallet({
			euroPalletId: euroPallet.euroPalletId
		}).subscribe({
			next: v => {
				let ou = URL.createObjectURL(v)
				window.open(ou, "_blank")
			},
			error: e => {
				console.error(e)
				if (!checkErrorAndAlertUser(e)) alert(`Failed to print pallet: ${e}`)
			}
	})
	}
}
