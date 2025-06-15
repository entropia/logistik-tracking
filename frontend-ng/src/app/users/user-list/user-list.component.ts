import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {handleDefaultError} from '../../util/auth';
import {UserDto} from '../../api/models/user-dto';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
	MatTableDataSource
} from '@angular/material/table';
import {MatSort, MatSortHeader} from '@angular/material/sort';
import {FormsModule} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {Router, RouterLink} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'app-user-list',
	imports: [
		FormsModule,
		RouterLink,
		MatButton,
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
		MatRowDef,
		MatHeaderRowDef

	],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent implements OnInit, AfterViewInit {
	@ViewChild(MatSort) sort!: MatSort;

	constructor(
		private api: ApiService,
		private diag: MatDialog,
		private router: Router
	) {

	}

	ngAfterViewInit() {
		this.dataSource.sort = this.sort;
	}

	dataSource: MatTableDataSource<UserDto> = new MatTableDataSource<UserDto>([]);
	displayedColumns = ["username", "authorities"];

	ngOnInit(): void {
        this.api.getUsers().subscribe({
			next: it => {
				for (let userDto of it) {
					userDto.authorities = userDto.authorities.sort()
				}
				this.dataSource.data = it
			},
			error: handleDefaultError
		})
    }

	createUser() {
		import("../create-user/create-user.component").then(it => {
			this.diag.open<any, any, UserDto & {hashedPassword: string}>(it.CreateUserComponent)
				.afterClosed()
				.subscribe(value => {
					if (!value) return;
					this.api.createUser({
						body: value
					}).subscribe({
						next: u => {
							this.router.navigate(["users/", u.username])
						},
						error: handleDefaultError
					})
				})
		})
	}

	protected readonly Object = Object;
}
