import {Component, effect, input, TemplateRef, ViewChild} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {FormsModule, NgForm, NgModel} from '@angular/forms';
import {AuthorityEnumDto, DeliveryStateEnumDto, EuroCrateDto, LocationDto, LocationTypeDto, LogisticsLocationDto, PackingListPatchDto} from '../../api/models';
import {MatFormField, MatLabel} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatButton, MatMiniFabButton} from '@angular/material/button';
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
import {MatSort, MatSortHeader} from '@angular/material/sort';
import {CrateAddWidgetComponent} from '../../util/crate-add-widget/crate-add-widget.component';
import {MatIcon} from '@angular/material/icon';
import {MatTooltip} from '@angular/material/tooltip';
import {HttpErrorResponse} from '@angular/common/http';
import {QrScannerService} from '../../qr-scanner.service';
import {extractIdFromUrl, parseCrateId} from '../../util/qr-id-parser';
import {handleDefaultError} from '../../util/auth';
import {LocationEditorComponent} from '../../location/location-editor/location-editor.component';
import {ValidateLocationDirective} from '../../location/location-editor/location-validator';
import {forkJoin} from 'rxjs';
import {AuthorityStatus, UserService} from '../../util/user.service';
import {LocationComponent} from '../../location/location/location.component';
import {MatTab, MatTabGroup} from '@angular/material/tabs';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {PrintButtonComponent} from '../../util/print-button/print-button.component';
import {RequiresAuthorityDirective} from '../../util/requires-permission.directive';
import {openAreYouSureOverlay} from '../../are-you-sure/are-you-sure.component';
import {MatDialog} from '@angular/material/dialog';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {switchMap} from 'rxjs/operators';

enum ItemStatus {
	KEEP,
	ADDED,
	REMOVED,
	TRANSFERRED
}

type TheItem = {
	status: ItemStatus,
	originalStatus: ItemStatus,
} & EuroCrateDto;

@Component({
	selector: 'app-selected-packing-list',
	imports: [
		RouterLink,
		FormsModule,
		MatFormField,
		MatSelect,
		MatOption,
		MatButton,
		CrateAddWidgetComponent,
		MatMiniFabButton,
		MatIcon,
		MatTooltip,
		MatLabel,
		LocationEditorComponent,
		ValidateLocationDirective,
		LocationComponent,
		MatTabGroup,
		MatTab,
		MatProgressSpinner,
		MatTable,
		MatSort,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatSortHeader,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatRow,
		MatHeaderRowDef,
		MatRowDef,
		MatNoDataRow,
		PrintButtonComponent,
		RequiresAuthorityDirective
	],
	templateUrl: './selected-packing-list.component.html',
	styleUrl: './selected-packing-list.component.scss'
})
export class SelectedPackingListComponent {
	id = input.required<number>()
	selected = toSignal(toObservable(this.id).pipe(
		switchMap(uid => this.apiService.getPackingList({packingListId: uid}))
	), {initialValue: null})

	@ViewChild("theForm2", {static: false})
	theForm?: NgForm;

	@ViewChild("deliveryStateControl", {static:false})
	deliveryStateControl?: NgModel;

	items: TheItem[] = [];

	@ViewChild(MatSort, {static: false})
	set sort(sort: MatSort) {
		this.source.sort = sort;
	}

	canEdit = false

	// protected selected?: PackingListDto;
	constructor(
		private apiService: ApiService,
		private snackbar: MatSnackBar,
		private qr: QrScannerService,
		userService: UserService,
		private diag: MatDialog,
		private router: Router,
		private ac: ActivatedRoute
	) {
		userService.hasAuthority(AuthorityEnumDto.ModifyResources).then(does => {
			this.canEdit = does == AuthorityStatus.HasIt
			if (this.canEdit) {
				this.displayedColumns.push('actions')
			}
		})
		effect(() => {
			let c = this.selected()
			if (!c) return;
			this.items = c.packedCrates!.map(it => {
				return {
					status: ItemStatus.KEEP,
					originalStatus: ItemStatus.KEEP,
					...it
				}
			})
			this.source.data = this.items;
		});
	}

	saveInfo(f: NgForm) {
		let current = this.selected()!;
		let thePatch: PackingListPatchDto = {}
		if (this.deliveryStateControl!.dirty) {
			thePatch.deliveryState = current.deliveryState
		}
		this.apiService.modifyPackingList({
			packingListId: current.packingListId,
			body: thePatch
		}).subscribe({
			next: _ => {
				if (this.deliveryStateControl!.dirty) {
					// we set a new delivery state
					this.items.forEach(it => {
						it.deliveryState = current.deliveryState
					})
				}
				f.control.markAsPristine()
				this.source.data = this.items;
				this.snackbar.open("Gespeichert!", undefined, {
					duration: 3000
				})
			},
			error: handleDefaultError
		})
	}

	@ViewChild("redirectError")
	redirectErrorTmpl!: TemplateRef<any>;

	saveCrates(f: NgForm, passedCheck = false) {
		if (!passedCheck && this.items.some(it => it.status == ItemStatus.TRANSFERRED)) {
			openAreYouSureOverlay<"cancel" | "save">(this.diag, {
				body: this.redirectErrorTmpl,
				choices: [{
					title: "Abbrechen",
					style: "color: #ea680b",
					token: "cancel"
				}, {
					title: "Speichern",
					token: "save"
				}],
				title: "Kisten werden Bewegt"
			}).afterClosed().subscribe(it => {
				if (it == "save") {
					// redo it but we're sure this time
					this.saveCrates(f, true)
				}
			})
			return;
		}
		let thePatch: PackingListPatchDto = {
			addCrates: [],
			removeCrates: []
		}
		for(let item of this.items) {
			switch (item.status) {
				case ItemStatus.ADDED:
				case ItemStatus.TRANSFERRED:
					thePatch.addCrates!.push(item.internalId)
					// item.status = ItemStatus.KEEP
					break;
				case ItemStatus.REMOVED:
					thePatch.removeCrates!.push(item.internalId)
					break;
			}
		}
		let current = this.selected()!;
		this.apiService.modifyPackingList({
			packingListId: current.packingListId,
			body: thePatch
		}).subscribe({
			next: _ => {
				this.items = this.items.filter(f => f.status != ItemStatus.REMOVED)
				this.items.forEach(it => {
					if (it.status == ItemStatus.ADDED || it.status == ItemStatus.TRANSFERRED) {
						it.deliveryState = DeliveryStateEnumDto.Packing;
					}
					it.status = it.originalStatus = ItemStatus.KEEP;
				});
				f.control.markAsPristine()
				this.source.data = this.items;
				this.snackbar.open("Gespeichert!", undefined, {
					duration: 3000
				})
			},
			error: handleDefaultError
		})
	}

	protected readonly Object = Object;
	protected readonly DeliveryStateEnumDto = DeliveryStateEnumDto;

	displayedColumns: string[] = ['status', 'operationCenter', 'name', "location"];
	source = new MatTableDataSource<TheItem>(undefined);

	feedback(v: string) {
		this.snackbar.open(v, undefined, {
			duration: 5000
		})
		// wanted to do a phone vibration here but then i figured out ios safari doesnt support it
		// thanks apple
		// i guess we can do a sound cue for everyone and a vibration for android people?
	}

	crateSubmitted($event: EuroCrateDto, sendFeedback: boolean) {
		let i;
		if ((i = this.items.findIndex(e => e.operationCenter == $event.operationCenter && e.name == $event.name)) != -1) {
			let existing = this.items[i]
			if (existing.status == ItemStatus.REMOVED) {
				existing.status = existing.originalStatus;
				if (sendFeedback) {
					this.feedback(`/ Box ${$event.operationCenter}/${$event.name} zurückgesetzt (war entfernt)`)
				}
			} else {
				this.feedback(`= Box ${$event.operationCenter}/${$event.name} ungeändert`)
			}
		} else {
			this.apiService.getPackingListsOfCrate({
				id: $event.internalId
			}).subscribe({
				next: _ => {
					this.items.push({
						status: ItemStatus.TRANSFERRED,
						originalStatus: ItemStatus.TRANSFERRED,
						...$event
					})
					this.source.data = this.items;
					this.feedback(`-> Box ${$event.operationCenter}/${$event.name} hinzugefügt (wird bewegt)`)
				},
				error: err => {
					if (err instanceof HttpErrorResponse && err.status == 404) {
						// somewhat intended, now we know we dont have any list crate fighting us for this one
						this.items.push({
							status: ItemStatus.ADDED,
							originalStatus: ItemStatus.ADDED,
							...$event
						})
						this.source.data = this.items;
						this.feedback(`+ Box ${$event.operationCenter}/${$event.name} hinzugefügt (neu)`)
					} else {
						console.error(err)
						alert(`Unbekannter fehler beim suchen nach Packlisten zu der gegebenen Box! ${err}`)
					}
				},
			})

			this.theForm!.control.markAsDirty()
		}
	}

	remove(element: TheItem) {
		if (element.originalStatus == ItemStatus.ADDED || element.originalStatus == ItemStatus.TRANSFERRED) {
			this.items.splice(this.items.indexOf(element), 1)
			this.source.data = this.items;
		}
		else {
			element.status = ItemStatus.REMOVED;
			this.theForm!.control.markAsDirty()
		}
	}

	protected readonly ItemStatus = ItemStatus;

	scanMultiple() {
		let ref = this.qr.startScanning("Fertig");
		ref.onScanned.subscribe({
			next: v => {
				try {
					let id = parseCrateId(extractIdFromUrl(v))
					this.apiService.getEuroCrate({
						id
					}).subscribe({
						next: c => this.crateSubmitted(c, true),
						error: handleDefaultError
					})
				} catch (e) {
					this.feedback(`Wahrscheinlich keine Box: ${e}`)
					console.error(e)
				}
			}
		})
	}

	massOpLocation?: LocationDto;

	doMassOp(form: NgForm) {
		let current = this.selected()!;
		let theReal = this.massOpLocation!;
		forkJoin([
			this.apiService.modifyMultipleEcLocations({
				body: {
					location: theReal,
					ids: this.items.map(it => it.internalId)
				}
			}),
			this.apiService.updateLastLocationOfEuroPallet({
				body: theReal,
				euroPalletId: current.packedOn.euroPalletId
			})
		]).subscribe({
			next: _ => {
				this.feedback("Gespeichert!")
				form.reset()
				current.packedOn.location = theReal
				for (let item of this.items) {
					item.location = theReal
				}
			},
			error: handleDefaultError
		})
	}

	protected readonly LocationTypeDto = LocationTypeDto;
	protected readonly LogisticsLocationDto = LogisticsLocationDto;

	suggestLocation(param: LogisticsLocationDto) {
		this.massOpLocation = {
			locationType: LocationTypeDto.Logistics,
			logisticsLocation: param
		}
	}

	protected readonly AuthorityEnumDto = AuthorityEnumDto;

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
			title: "Packliste löschen?"
		}).afterClosed().subscribe(result => {
			if (result == "delete") {
				this.apiService.deletePackingList({
					packingListId: this.id()
				}).subscribe({
					next: () => {
						this.router.navigate([".."], {relativeTo: this.ac})
					},
					error: handleDefaultError
				})
			}
		})
	}
}
