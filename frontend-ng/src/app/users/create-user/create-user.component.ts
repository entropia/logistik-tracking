import {Component} from '@angular/core';
import {MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {FormsModule} from '@angular/forms';
import {UserDto} from '../../api/models/user-dto';
import {MatCheckbox} from '@angular/material/checkbox';
import bcrypt from 'bcryptjs';
import {MatOption, MatSelect} from '@angular/material/select';
import {AuthorityEnumDto} from '../../api/models';

@Component({
  selector: 'app-create-user',
	imports: [
		FormsModule,
		MatButton, MatFormField, MatInput, MatSelect, MatOption, MatCheckbox, MatDialogActions,
		MatDialogContent,
		MatDialogTitle,
		MatLabel
	],
  templateUrl: './create-user.component.html',
  styleUrl: './create-user.component.scss'
})
export class CreateUserComponent {
	nu: UserDto & {hashedPassword: string} = {
		username: "",
		hashedPassword: "",
		enabled: true,
		authorities: []
	}
	newPw: string = "";
	constructor(private dialogRef: MatDialogRef<CreateUserComponent>) {
	}

	cancel() {
		this.dialogRef.close(undefined)
	}

	save() {
		let salt = bcrypt.genSaltSync(12)
		this.nu.hashedPassword = bcrypt.hashSync(this.newPw, salt)
		this.dialogRef.close(this.nu)
	}

	protected readonly Object = Object;
	protected readonly AuthorityEnumDto = AuthorityEnumDto;
}
