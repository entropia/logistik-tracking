import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {ApiService} from '../../api/services/api.service';
import {checkErrorAndAlertUser} from '../../util/auth';
import {UserDto} from '../../api/models/user-dto';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {FormsModule} from '@angular/forms';
import {AuthorityEnumDto} from '../../api/models';
import {MatButtonModule} from '@angular/material/button';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-user-list',
	imports: [
		MatTableModule,
		MatSortModule,
		MatFormFieldModule,
		MatSelectModule,
		FormsModule,
		MatButtonModule,
		RouterLink
	],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent implements OnInit, AfterViewInit {
	@ViewChild(MatSort) sort!: MatSort;

	constructor(
		private api: ApiService
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
			error: err => {
				console.error(err)
				if (!checkErrorAndAlertUser(err)) alert(`Konnte nutzer nicht abrufen: ${err}`)
			}
		})
    }

	protected readonly AuthorityEnumDto = AuthorityEnumDto;
	protected readonly Object = Object;
}
