import {Component, OnInit} from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {MatIconModule} from '@angular/material/icon';
import {UserService} from '../util/user.service';
import {UserDto} from '../api/models/user-dto';
import {MatMenuModule} from '@angular/material/menu';
import {ApiConfiguration} from '../api/api-configuration';
import {RequiresAuthorityDirective} from '../util/requires-permission.directive';
import {AuthorityEnumDto} from '../api/models';

@Component({
	selector: 'app-navigation-header',
	imports: [
		RouterLink,
		MatIconModule,
		MatMenuModule,
		RequiresAuthorityDirective
	],
	templateUrl: './navigation-header.component.html',
	styleUrl: './navigation-header.component.scss'
})
export class NavigationHeaderComponent implements OnInit{
	actualU	?: UserDto;
	backendUrl: string;
constructor(
	private user: UserService,
	conf: ApiConfiguration,
	protected router: Router
) {
	this.backendUrl = conf.rootUrl
}

ngOnInit() {
	this.user.getUser().then(it => {
		this.actualU = it;
	})
}

	protected readonly AuthorityEnumDto = AuthorityEnumDto;
}
