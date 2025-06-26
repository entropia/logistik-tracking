import {Component, input, TemplateRef, ViewChild} from '@angular/core';
import {ApiService} from '../../api/services';
import {EuroCrateDto} from '../../api/models/euro-crate-dto';
import {FormsModule, NgForm, NgModel} from '@angular/forms';
import {LocationEditorComponent} from '../../location/location-editor/location-editor.component';
import {ValidateLocationDirective} from '../../location/location-editor/location-validator';
import {AuthorityEnumDto, DeliveryStateEnumDto, EuroCratePatchDto, LogisticsLocationDto, OperationCenterDto} from '../../api/models';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {MatButton} from '@angular/material/button';
import {handleDefaultError} from '../../util/auth';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {PrintButtonComponent} from '../../util/print-button/print-button.component';
import {RequiresAuthorityDirective} from '../../util/requires-permission.directive';
import {AuthorityStatus, UserService} from '../../util/user.service';
import {MatIcon} from '@angular/material/icon';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {catchError, switchMap} from 'rxjs/operators';
import {forkJoin, of} from 'rxjs';
import {openAreYouSureOverlay} from '../../are-you-sure/are-you-sure.component';

@Component({
	selector: 'app-selected-euro-crate',
	imports: [
		FormsModule,
		LocationEditorComponent,
		ValidateLocationDirective,
		MatError,
		MatFormField,
		MatLabel,
		MatSelect,
		MatOption,
		MatInput,
		MatButton,
		MatProgressSpinner,
		PrintButtonComponent,
		RequiresAuthorityDirective,
		MatIcon,
		RouterLink
	],
	templateUrl: './selected-euro-crate.component.html',
	styleUrl: './selected-euro-crate.component.scss'
})
export class SelectedEuroCrateComponent {
	canEdit: boolean = false;

	constructor(
		private apiService: ApiService,
		userService: UserService,
		private diag: MatDialog,
		private router: Router,
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
			title: "Kiste löschen?"
		}).afterClosed().subscribe(result => {
			if (result == "delete") {
				this.apiService.deleteEuroCrate({
					id: this.id()
				}).subscribe({
					next: () => {
						this.router.navigate([".."], {relativeTo: this.ac})
					},
					error: handleDefaultError
				})
			}
		})
	}

	id = input.required<number>()
	crate = toSignal(toObservable(this.id).pipe(
		switchMap(uid => forkJoin({
			crate: this.apiService.getEuroCrate({id: uid}),
			relatedPls: this.apiService.getPackingListsOfCrate({id: uid})
				.pipe(catchError(err => {
					console.error("couldnt fetch related packing list", err)
					return of(null);
				}))
		}))
	), {initialValue: null})


	saveIt(form: NgForm, oc: NgModel, locationMod: NgModel, deliStatusMod: NgModel, ftInfoMod: NgModel) {
		let theCrate = this.crate()!.crate;
		let patch: EuroCratePatchDto = {}
		if (oc.dirty) {
			patch.operationCenter = theCrate.operationCenter
		}
		if (locationMod.dirty) {
			patch.location = theCrate.location
		}
		if (deliStatusMod.dirty) {
			patch.deliveryState = theCrate.deliveryState
		}
		if (ftInfoMod.dirty) {
			patch.information = theCrate.information
		}
		this.apiService.modifyEuroCrate({
			id: this.id(),
			body: patch
		}).subscribe({
			next: _ => {
				form.control.markAsPristine()
			},
			error: handleDefaultError
		})
	}

	protected readonly LogisticsLocationDto = LogisticsLocationDto;
	protected readonly Object = Object;
	protected readonly DeliveryStateEnumDto = DeliveryStateEnumDto;
	protected readonly AuthorityEnumDto = AuthorityEnumDto;
	protected readonly OperationCenterDto = OperationCenterDto;

	dupeCrate() {
		import('../create-euro-crate/create-euro-crate.component').then(it => {
			this.diag.open<any, any, EuroCrateDto | undefined>(it.CreateEuroCrateComponent, {
					data: this.crate()?.crate
				})
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
						},
						error: handleDefaultError
					});
				})
		})
	}
}
