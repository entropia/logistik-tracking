import {Component} from '@angular/core';
import {RouterLink} from "@angular/router";
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {RequiresAuthorityDirective} from '../util/requires-permission.directive';
import {AuthorityEnumDto} from '../api/models';

@Component({
	selector: 'app-navigation-header',
	imports: [
		RouterLink, MatIconModule, MatMenuModule, RequiresAuthorityDirective
	],
	templateUrl: './navigation-header.component.html',
	styleUrl: './navigation-header.component.scss'
})
export class NavigationHeaderComponent {

	protected readonly AuthorityEnumDto = AuthorityEnumDto;
}
