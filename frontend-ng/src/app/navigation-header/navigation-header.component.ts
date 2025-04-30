import {Component, OnInit} from '@angular/core';
import {RouterLink} from "@angular/router";
import {MatIconModule} from '@angular/material/icon';
import {UserService} from '../util/user.service';
import {UserDto} from '../api/models/user-dto';
import {MatMenuModule} from '@angular/material/menu';
import {ApiConfiguration} from '../api/api-configuration';

@Component({
	selector: 'app-navigation-header',
	imports: [
		RouterLink,
		MatIconModule,
		MatMenuModule
	],
	templateUrl: './navigation-header.component.html',
	styleUrl: './navigation-header.component.scss'
})
export class NavigationHeaderComponent implements OnInit{
	actualU	?: UserDto;
	backendUrl: string;
constructor(
	private user: UserService,
	conf: ApiConfiguration
) {
	this.backendUrl = conf.rootUrl
}

ngOnInit() {
	this.user.getUser().then(it => {
		this.actualU = it;
	})
}

}
