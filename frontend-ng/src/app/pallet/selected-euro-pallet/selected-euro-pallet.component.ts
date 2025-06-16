import {Component, input, TemplateRef, ViewChild} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {LocationEditorComponent} from '../../location/location-editor/location-editor.component';
import {FormsModule, NgForm} from '@angular/forms';
import {ValidateLocationDirective} from '../../location/location-editor/location-validator';
import {LocationDto} from '../../api/models/location-dto';
import {MatError} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {MatSnackBar} from '@angular/material/snack-bar';
import {handleDefaultError} from '../../util/auth';
import {NewPackingListDto} from '../../api/models/new-packing-list-dto';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {RequiresAuthorityDirective} from '../../util/requires-permission.directive';
import {AuthorityEnumDto} from '../../api/models/authority-enum-dto';
import {AuthorityStatus, UserService} from '../../util/user.service';
import {PrintButtonComponent} from '../../util/print-button/print-button.component';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {switchMap} from 'rxjs/operators';
import {forkJoin} from 'rxjs';
import {openAreYouSureOverlay} from '../../are-you-sure/are-you-sure.component';
import {HttpErrorResponse} from '@angular/common/http';
import {MatIcon} from '@angular/material/icon';
import {VeryBasicPackingListDto} from '../../api/models/very-basic-packing-list-dto';
import {EpDeleteConfirmComponent} from '../ep-delete-confirm/ep-delete-confirm.component';

@Component({
	selector: 'app-selected-euro-pallet',
	imports: [
		LocationEditorComponent,
		FormsModule,
		ValidateLocationDirective,
		MatError,
		MatButton,

		RequiresAuthorityDirective,
		MatProgressSpinner,
		PrintButtonComponent,
		RouterLink,
		MatIcon
	],
	templateUrl: './selected-euro-pallet.component.html',
	styleUrl: './selected-euro-pallet.component.scss'
})
export class SelectedEuroPalletComponent {
	editingLocation?: LocationDto;

	canEdit: boolean = false;

	id = input.required<number>()
	selected = toSignal(toObservable(this.id).pipe(
		switchMap(uid => forkJoin({
			pallet: this.apiService.getEuroPallet({euroPalletId: uid}),
			relevantPls: this.apiService.getEuroPalletLists({euroPalletId: uid})
		}))
	), {initialValue: null})

	constructor(
		private apiService: ApiService,
		private snackbar: MatSnackBar,
		private router: Router,
		private diag: MatDialog,
		userService: UserService,
		private ac: ActivatedRoute
	) {
		userService.hasAuthority(AuthorityEnumDto.ModifyResources).then(does => {
			this.canEdit = does == AuthorityStatus.HasIt
		});
	}

	@ViewChild("deleteError")
	delErrorTempl!: TemplateRef<any>;
	deleteIt() {

		openAreYouSureOverlay<"cancel" | "delete">(this.diag, {
			body: this.delErrorTempl,
			choices: [{
				title: "Abbrechen",
				token: "cancel"
			}, {
				title: "Löschen",
				style: "color: #ea680b",
				token: "delete"
			}],
			title: "Palette löschen?"
		}).afterClosed().subscribe(result => {
			if (result == "delete") {
				this.tryDelete()
			}
		})
	}

	tryDelete() {
		this.apiService.deleteEuroPallet({
			euroPalletId: this.id()
		}).subscribe({
			next: () => {
				this.router.navigate([".."], {relativeTo: this.ac})
			},
			error: (e) => {
				if (e instanceof HttpErrorResponse && e.status == 409) {
					let whenDeleting_packingLists = e.error as VeryBasicPackingListDto[]
					this.diag.open<EpDeleteConfirmComponent, VeryBasicPackingListDto[], boolean>(EpDeleteConfirmComponent, {
						data: whenDeleting_packingLists
					}).afterClosed().subscribe({
						next: v => {
							if (v == true) {
								this.tryDelete()
							}
						}
					})
				}
			}
		})
	}

	saveIt(form: NgForm) {
		let pallet = this.selected()!.pallet;
		this.apiService.updateLastLocationOfEuroPallet({
			euroPalletId: pallet.euroPalletId,
			body: this.editingLocation!
		}).subscribe({
			next: _ => {
				this.snackbar.open("Saved!", undefined, {
					duration: 3000
				})
				form.control.markAsPristine()
			},
			error: handleDefaultError
		})
	}

	createPackingList() {
		let pallet = this.selected()!.pallet;
		import("../../packlist/create-packing-list/create-packing-list.component").then(it => {
			this.diag.open<any, any, NewPackingListDto>(it.CreatePackingListComponent, {
				data: {
					packedOnPallet: pallet.euroPalletId
				}
			}).afterClosed()
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
}
