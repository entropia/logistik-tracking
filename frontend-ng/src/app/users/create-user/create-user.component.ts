import { Component } from '@angular/core';
import {MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {FormsModule} from '@angular/forms';
import {UserDto} from '../../api/models/user-dto';
import {MatCheckboxModule} from '@angular/material/checkbox';
import bcrypt from 'bcryptjs';
import {MatSelectModule} from '@angular/material/select';
import {AuthorityEnumDto} from '../../api/models';

@Component({
  selector: 'app-create-user',
	imports: [
		MatDialogModule,
		MatFormFieldModule,
		MatInputModule,
		MatButtonModule,
		FormsModule,
		MatCheckboxModule,
		MatSelectModule
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
