import {Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {UserDto} from '../../api/models/user-dto';
import {handleDefaultError} from '../../util/auth';
import {FormsModule, NgForm, NgModel} from '@angular/forms';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {AuthorityEnumDto} from '../../api/models';
import {MatButton} from '@angular/material/button';
import {MatCheckbox} from '@angular/material/checkbox';
import {ModifyUser$Params} from '../../api/fn/operations/modify-user';
import bcrypt from "bcryptjs";
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatListOption, MatSelectionList} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';
import {MatDialog} from '@angular/material/dialog';
import {openAreYouSureOverlay} from '../../are-you-sure/are-you-sure.component';
import {NgOptimizedImage} from '@angular/common';

const mapAuthToReadable = {
	MANAGE_USERS: "Nutzer Verwalten",
	CREATE_RESOURCES: "Ressourcen Erstellen",
	MODIFY_RESOURCES: "Ressourcen Ändern",
	DELETE_RESOURCES: "Ressourcen Löschen",
	PRINT: "Drucken"
}

@Component({
  selector: 'app-selected-user',
	imports: [
		FormsModule,
		MatCheckbox,
		MatProgressSpinner,
		MatFormField,
		MatInput,
		MatButton,
		MatLabel,
		MatSelectionList,
		MatListOption,
		MatIcon,
		NgOptimizedImage
	],
  templateUrl: './selected-user.component.html',
  styleUrl: './selected-user.component.scss'
})
export class SelectedUserComponent implements OnInit {
	@Input()
	id!: string;

	@ViewChild("confirmTemplate")
	template!: TemplateRef<unknown>;

	user?: UserDto;

	newPw: string = "";

	@ViewChild("theForm") theForm!: NgForm;

	constructor(
		private api: ApiService,
		private modal: MatSnackBar,
		private router: Router,
		private diag: MatDialog
	) {
	}

	ngOnInit() {
		this.api.getSpecificUser({
			name: this.id
		}).subscribe({
			next: it => {
				this.user = it;
			},
			error: handleDefaultError
		})
	}

	saveUser(authorities: NgModel, enabled: NgModel, newpw: NgModel) {
		let patch: ModifyUser$Params = {
			body: {
				username: this.id
			}
		}
		if (authorities.dirty) {
			patch.body.authorities = this.user!.authorities;
		}
		if (enabled.dirty) {
			patch.body.active = this.user!.enabled;
		}
		if (newpw.dirty) {
			let salt = bcrypt.genSaltSync(12)
			patch.body.hashedPassword = bcrypt.hashSync(this.newPw, salt)
		}
		this.api.modifyUser(patch)
			.subscribe({
				next: _ => {
					this.modal.open("Saved!", undefined, {
						duration: 4000
					})
					this.theForm.control.markAsPristine()
				},
				error: handleDefaultError
			})
	}

	protected readonly Object = Object;
	protected readonly AuthorityEnumDto = AuthorityEnumDto;
	protected readonly mapAuthToReadable = mapAuthToReadable;

	delete() {
		openAreYouSureOverlay<"cancel" | "delete">(this.diag, {
			body: this.template,
			choices: [{
				title: "Abbrechen",
				token: "cancel"
			}, {
				title: "Löschen",
				style: "color: #ea680b",
				token: "delete"
			}],
			title: "Nutzer löschen?"
		}).afterClosed().subscribe(result => {
			if (result == "delete") {
				this.api.deleteUser({
					username: this.id
				}).subscribe({
					next: _ => {
						this.modal.open("Deleted!", undefined, {
							duration: 4000
						})
						this.router.navigate(["/users"])
					}, error: handleDefaultError
				})
			}
		})
	}
}
