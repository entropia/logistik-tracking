import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {UserDto} from '../../api/models/user-dto';
import {handleDefaultError} from '../../util/auth';
import {FormsModule, NgForm, NgModel} from '@angular/forms';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {AuthorityEnumDto} from '../../api/models';
import {MatButton} from '@angular/material/button';
import {MatCheckbox} from '@angular/material/checkbox';
import {ModifyUser$Params} from '../../api/fn/operations/modify-user';
import bcrypt from "bcryptjs";
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';
import {MatProgressSpinner} from '@angular/material/progress-spinner';

@Component({
  selector: 'app-selected-user',
	imports: [
		FormsModule,
		MatCheckbox,
		MatProgressSpinner,
		MatFormField,
		MatSelect,
		MatOption,
		MatInput,
		MatButton,
		MatLabel
	],
  templateUrl: './selected-user.component.html',
  styleUrl: './selected-user.component.scss'
})
export class SelectedUserComponent implements OnInit {
	@Input()
	id!: string;

	user?: UserDto;

	newPw: string = "";

	@ViewChild("theForm") theForm!: NgForm;

	constructor(
		private api: ApiService,
		private modal: MatSnackBar,
		private router: Router
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
			username: this.id,
			body: {}
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

	delete() {
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
}
